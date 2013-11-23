/*
Rainbowduino V3 firmware capable of streaming 24bit RGB frames with up
to 35fps via the USB connector of the Rainbowduino V3 controller.

Author:   Markus Lang (m@rkus-lang.de)
Websites: http://programmers-pain.de/
          https://code.google.com/p/rainbowduino-v3-streaming-firmware/
          
This firmware is based on several Rainbowduino related firmwares like:
neorainbowduino:  http://code.google.com/p/neorainbowduino/
rainbowdash:      http://code.google.com/p/rainbowdash/
seeedstudio.com:  http://www.seeedstudio.com/wiki/Rainbowduino_v3.0

The Java part splits a full 8x8 RGB frame (3 colors * 64 LEDs = 192byte)
into four frame fragments (each 48byte) to get around the 64byte default
buffer size of the Arduino hardware serial implementation. Each fragment
will be extended with the HEADER bits and a frame fragment index to
be able to reconstruct the full frame in the correct order inside this
firmware. 

Splitting up the frame into fragments avoids running into data corruption
/ data loss if the Java part sends more bytes than the Arduino controller
can buffer. For every frame fragment the controller will send an ACK REPLY
message to the Java code so that the next fragment will be send.

The firmware is able to handle incomplete frames as well as CRC checksum
errors of the transferred LED color data so that it's able to signal those
error conditions to the Java API.

The LED update routine of this firmware is just a rewrite of the original
seeedstudio.com firmware (http://www.seeedstudio.com/wiki/Rainbowduino_v3.0)
including some changes regarding the interrupt handling to allow the
controller to update the LEDs and receiving incoming serial data at the
same time.
*/

// ports and bit values needed by the LED update routine
#define DDR_DATA   DDRB
#define DDR_CLK    DDRB
#define DDR_LINES  DDRD
#define PORT_DATA  PORTB
#define PORT_CLK   PORTB
#define PORT_LINES PORTD
#define BIT_DATA   0x01
#define BIT_CLK    0x02
#define BIT_LINES  0xF0

// general const variables
const unsigned char RAINBOWDUINO_LEDS = 64;
const unsigned char NUMBER_OF_COLORS = 3;
const unsigned char RED = 0;
const unsigned char GREEN = 1;
const unsigned char BLUE = 2;
const unsigned char NUMBER_OF_FRAME_FRAGMENTS = 4;
const int FRAME_FRAGMENT_LENGTH = ((RAINBOWDUINO_LEDS / NUMBER_OF_FRAME_FRAGMENTS) * NUMBER_OF_COLORS);

// serial protocol related const variables
const long BAUD_RATE = 115200;
const unsigned char HEADER_LENGTH = 3;
const unsigned char ACK_REPLY_LENGTH = 4;
const byte HEADER = 0x10;
const byte FRAME_FRAGMENTS[NUMBER_OF_FRAME_FRAGMENTS] = {0x20, 0x21, 0x22, 0x23};
// serial protocol state codes
const byte STATE_ACK = 0x30;
const byte STATE_FRAME_FRAGMENT_INDEX = 0x31;
const byte STATE_INCOMPLETE_FRAME = 0x32;

// byte array used to send back ack replies
// (HEAHDER, 2 bytes for returned value, ERROR CODE)
byte ackReply[ACK_REPLY_LENGTH] = {HEADER, 0, 0, 0};

// byte array used as frame buffers for the currently received and displayed frame
// (2 buffers, RGB colors, 8 rows, 8 columns)
unsigned char frameBuffers[2][NUMBER_OF_COLORS][8][8];
// the currently used frame buffer by the LED update routine
volatile unsigned char currentFrameBuffer;
// the current line the LED update routine will push color data for
volatile unsigned char currentLine;

// global variables used to parse the incoming serial data
byte serialData;             // stores the currently parsed byte
unsigned char headerCounter; // counts the number of found HEADER bytes
int frameFragmentPos;        // the relative position in the currently parsed frame fragment
int frameFragmentPosOffset;  // the offset to the relative position of the currently parsed frame fragment
int frameFragmentIndex;      // the index of the currently parsed frame fragment
int crc;                     // used to calculate the frame fragment crc value
unsigned char currentColor;  // used to store the current color index
unsigned char currentRow;    // used to store the current row index
unsigned char currentColumn; // used to store the current column index

void setup() {
  // initialize global variables used to update the LEDs
  currentFrameBuffer = 0;
  currentLine = 0;
  // initialize global variables used to parse the incoming serial data
  headerCounter = 0;
  frameFragmentPos = -1;
  frameFragmentPosOffset = -1;
  frameFragmentIndex = -1;
  crc = 0;
  currentColor = 0;
  currentRow = 0;
  currentColumn = 0;
  // setup serial communication
  Serial.begin(BAUD_RATE);
  // initialize frame buffers array
  for (unsigned char buffer = 0; buffer < 2; buffer++) {
    for (unsigned char color = 0; color < NUMBER_OF_COLORS; color++) {
      for (unsigned char row = 0; row < 8; row++) {
        for (unsigned char column = 0; column < 8; column++) {
          frameBuffers[buffer][color][row][column] = 0;
        }
      }
    }
  }
  // disable all internal interrupts
  cli();
  // initialize LED update routine and MY9221 state
  DDR_LINES  |=  BIT_LINES;
  PORT_LINES &= ~BIT_LINES;
  DDRD |= 0x04;
  DDR_DATA  |=  BIT_DATA;
  DDR_CLK   |=  BIT_CLK;
  PORT_DATA &= ~BIT_DATA;
  PORT_CLK  &= ~BIT_CLK;
  DDRB |= 0x20;
  // clear the display to get a clean state
  clearDisplay();
  // init TIMER 1 (trigger every ~1250us)
  TCCR1A = 0;
  TCCR1B = _BV(WGM13);
  ICR1 = 10000;
  TIMSK1 = _BV(TOIE1);
  TCNT1 = 0;
  TCCR1B |= _BV(CS10);
  // re-enable all internal interrupts
  sei();
}

void loop() {
  // check for available serial data and start parsing after the HEADER bytes have been found
  while (Serial.available() > 0) {
    serialData = Serial.read();
    // count the number of header bytes if we're not parsing a frame fragment right now
    if (frameFragmentPos == -1) {
      if (serialData == HEADER) {
        headerCounter++;
        // check if we've counted two HEADER bytes
        if (headerCounter == 2) {
          // set the frame fragment position and start the parsing
          frameFragmentPos = 0;
          headerCounter = 0;
        }
      } else {
        headerCounter = 0;
      }
      continue;
    }

    // check if we need to resolve the frame fragment index
    if (frameFragmentPos == 0 && frameFragmentIndex == -1) {
      for (unsigned char i = 0; i < NUMBER_OF_FRAME_FRAGMENTS; i++) {
        if (serialData == FRAME_FRAGMENTS[i]) {
          frameFragmentIndex = i;
          frameFragmentPosOffset = frameFragmentIndex * FRAME_FRAGMENT_LENGTH;
          break;
        }
      }
      // throw an error in case we couldn't parse the frame fragment index
      if (frameFragmentIndex == -1) {
        sendAckReply(STATE_FRAME_FRAGMENT_INDEX, serialData);
      }
      // throw an error in case the frame fragment index doesn't match the 
      // currentRow counter which indicates that we haven't received the 
      // frame fragments in the correct order. that can happen if you reset
      // the controller manually or if a frame fragment was lost during
      // transfer. therefore we'll skip the just received frame index incl.
      //  it's frame data.
      if (frameFragmentIndex * 2 != currentRow) {
        sendAckReply(STATE_INCOMPLETE_FRAME, frameFragmentIndex);
      }
      continue;        
    }

    // store the received byte in the frame buffer and calculate the crc value
    if (frameFragmentPos != -1 && frameFragmentPos < FRAME_FRAGMENT_LENGTH) {
      // store received color value in the currently used framebuffer
      int framePos = frameFragmentPosOffset + frameFragmentPos;
      int frameBufferPos = framePos / NUMBER_OF_COLORS;
      frameBuffers[currentFrameBuffer][currentColor][currentRow][currentColumn] = serialData;
      // reset currentColor pointer if a complete color cycle is done
      currentColor++;
      if (currentColor == NUMBER_OF_COLORS) {
        currentColor = 0;
        currentColumn++;
        // reset currentColumn pointer if a complete row is done
        if (currentColumn == 8) {
          currentColumn = 0;
          currentRow++;
          // reset currentRow pointer if a complete row cycle is done
          if (currentRow == 8) {
            currentRow = 0;
          }
        }
      }
      // calculate crc value for the currently parsed frame fragment
      crc = crc + serialData;
      // increment the frame fragment position
      frameFragmentPos++;
    }
    
    // check if we've parsed all bytes of the current frame fragment
    if (frameFragmentPos == FRAME_FRAGMENT_LENGTH) {
      // switch the currently used frame buffer if we've received the last frame fragment
      if (frameFragmentIndex == NUMBER_OF_FRAME_FRAGMENTS) {
        currentFrameBuffer = !currentFrameBuffer;
        // reset global variables that have been in use to parse the whole frame
        currentRow = 0;
        currentColumn = 0;
      }
      // let the java code know that we've parse an entire frame fragment
      sendAckReply(STATE_ACK, crc);
    }
  }
}

// send the ack reply message to the serial interface and reset the global parsing variables
void sendAckReply(byte stateCode, int value) {
  // update ack reply array
  ackReply[1] = value >> 8;
  ackReply[2] = value;
  ackReply[3] = stateCode;
  // send ack reply message
  Serial.write(ackReply, ACK_REPLY_LENGTH);

  //DO NOT FLUSH here or strange things will happen!
  //Serial.flush();
  
  // reset global variables that have been in use to parse the frame fragment
  frameFragmentPos = -1;
  frameFragmentPosOffset = -1;
  frameFragmentIndex = -1;
  currentColor = 0;
  crc = 0;
}

void send16BitData(unsigned int data) {
  for (unsigned char i = 0; i < 16; i++) {
    if (data & 0x8000) {
      PORT_DATA |=  BIT_DATA;
    } else {
      PORT_DATA &= ~BIT_DATA;
    }
    PORT_CLK ^= BIT_CLK;
    data <<= 1;
  }
}

void latchData() {
  PORT_DATA &= ~BIT_DATA;
  delayMicroseconds(10);
  PORT_LINES &= ~0x80;
  for (unsigned char i = 0; i < 8; i++) {
    PORT_DATA ^= BIT_DATA;
  }
}

void switchOnDrive(unsigned char line) {
  PORT_LINES &= ~BIT_LINES;
  PORT_LINES |= (line << 4);
  PORT_LINES |= 0x80;
}

void clearData() {
  PORT_DATA &= ~BIT_DATA;
  for (unsigned char i = 0; i < 192; i++) {
    PORT_CLK ^= BIT_CLK;
  }
}

void clearDisplay() {
  send16BitData(0);
  clearData();
  send16BitData(0);
  clearData();
  latchData();
}

ISR(TIMER1_OVF_vect) {
  // re-enable global interrupts, this needs some explanation:
  // ---------------------------------------------------------
  // to allow the internal interrupt of the Arduino framework to handle
  // incoming serial data we need to re-enable the global interrupts
  // inside this interrupt call of the LED update routine.
  // usually that's an stupid idea since the LED update rountine interrupt
  // could also be called a second time while this interrupt call is still
  // running - this would result in a hanging controller.
  // since we know that the interrupt is called in a 1250us interval and
  // the code in this method takes around ~650us to finish we still have
  // enough buffer to allow the internal interrupt to handle incoming serial
  // data without risking to block the controller. the time between the next
  // LED update rountine call is also sufficient to give the controller
  // enough time to parse the incoming serial data.
  // this setup was the easiest way out of the problem that the internal
  // interrupt and the interrupt of the LED update routine do otherwise
  // result in major data loss and data corruption if we wouldn't re-enable
  // the global interrupts here.
  sei();
  // determine the frame buffer row to be used for this interrupt call
  unsigned char row = 7 - currentLine;
  // clear the data of the former interrupt call to avoid flickering
  clearDisplay();
  // push data to the MY9221 ICs
  send16BitData(0);
  // push the blue color value of the current row
  for (char column = 0; column < 8; column++) {
    send16BitData(frameBuffers[currentFrameBuffer][BLUE][row][column]);
  }
  // push the green color value of the current row
  for (char column = 0; column < 4; column++) {
    send16BitData(frameBuffers[currentFrameBuffer][GREEN][row][column]);
  }
  send16BitData(0);
  for (char column = 4; column < 8; column++) {
    send16BitData(frameBuffers[currentFrameBuffer][GREEN][row][column]);
  }
  // push the red color value of the current row
  for (char column = 0; column < 8; column++) {
    send16BitData(frameBuffers[currentFrameBuffer][RED][row][column]);
  }
  // since the following code is timing-sensitive we have to disable
  // the global interrupts again to avoid ghosting / flickering of 
  // the other lines that shouldn't be active at all.
  cli();
  latchData();
  // activate current line
  switchOnDrive(currentLine);
  PORTD &= ~0x04;
  // increment current led row counter for the next interrupt call
  currentLine++;
  if (currentLine == 8) {
    currentLine = 0;
  }
}

