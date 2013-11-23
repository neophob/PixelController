/*

           
            EXPEDIT REGAL#1 (149x149 cm)                           EXPEDIT REGAL#2                                       EXPEDIT REGAL#3
                15 16 17              45 46 47                         77 78 79           107 108 109                       139 *140 141          169 170 171
           +----------+----------+----------+----------+          +----------+----------+----------+----------+         +----------+----------+----------+----------+
           |          |          |          |          |          |          |          |          |          |         |          |          |          |          |
           | 14       | 18       | 44       | 48       |          | 76       |*80       | 106      | 110      |         | 138      | 142      | 168      | 171      |
           | 13  [04] | 19  [05] | 43  [12] | 49  [13] |          | 75  [20] | 81  [21] | 105 [28] | 111 [29] |         | 137 [36] | 143 [37] | 167 [44] | 172 [45] |       
           | 12       |*20       | 42       | 50       |          | 74       | 82       | 104      | 112      |         | 136      | 144      | 166      | 173      |
           +----------+----------+----------+----------+          +----------+----------+----------+----------+         +----------+----------+----------+----------+
           |          |          |          |          |          |          |          |          |          |         |          |          |          |          | 
           | 11       | 21       | 41       | 51       |          | 73       | 83       | 103      | 113      |         | 135      | 145      | 165      | 174      | 
           | 10  [03] | 22  [06] |*40  [11] | 52  [14] |          | 72  [19] | 84  [22] | 102 [27] | 114 [30] |         | 134 [35] | 146 [38] | 164 [43] | 175 [46] |       
           | 09       | 23       | 39       | 53       |          | 71       | 85       | 101      | 115      |         | 133      | 147      | 163      | 176      |
           +----------+----------+----------+----------+          +----------+----------+----------+----------+         +----------+----------+----------+----------+
           |          |          |          |          |          |          |          |          |          |         |          |          |          |          |
           | 08       | 24       | 38       | 54       |          | 70       | 86       |*100      | 116      |         | 132      | 148      | 162      | 177      |
           | 07  [02] | 25  [07] | 37  [10] | 55  [15] |          | 69  [18] | 87  [23] | 99  [26] | 117 [31] |         | 131 [34] | 149 [39] | 161 [42] | 178 [47] |      
           | 06       | 26       | 36       | 56       |          | 68       | 88       | 98       | 118      |         | 130      | 150      |*160      | 179      |
           +----------+----------+----------+----------+          +----------+----------+----------+----------+         +----------+----------+----------+----------+
           |          |          |          |          |          |          |          |          |          |         |          |          |          |          |
           | 05       | 27       | 35       | 57       |          | 67       | 89       | 97       | 119      |         | 129      | 151      | 159      |*180      |
           | 04  [01] | 28  [08] | 34  [09] | 58  [16] |          | 66  [17] | 90  [24] | 96  [25] |*120 [32] |         | 128 [33] | 152 [40] | 158 [41] | 181 [48] |
  00 01 02 | 03       | 29       | 33       | 59       |          | 65       | 91       | 95       | 121      |         | 127      | 153      | 157      | 182      |
   ARDUINO +----------+----------+----------+----------+          +----------+----------+----------+----------+         +----------+----------+----------+----------+           
                           30 31 32             *60 61 62 63 64                    92 93 94            122 123 124 125 126             154 155 156



*/


#include <TimerOne.h>
#include <SPI.h>
#include <Neophob_LPD6803.h>

// ======= START OF USER CONFIGURATION =======

//send debug messages back via serial line
//#define DEBUG 1

//how many expeditinvaders panels are connected?
#define NR_OF_PANELS 3

//Teensy 2.0 has the LED on pin 11.
//Teensy++ 2.0 has the LED on pin 6
//Teensy 3.0 has the LED on pin 13
#define LED_PIN 11

// ======= END OF USER CONFIGURATION ======= 


//to draw a frame we need arround 20ms to send an image. the serial baudrate is
//NOT the bottleneck. 
#define BAUD_RATE 115200

#define PIXELS_PER_PANEL 16

//define some tpm2 constants
#define TPM2NET_HEADER_SIZE 4
#define TPM2NET_HEADER_IDENT 0x9c
#define TPM2NET_CMD_DATAFRAME 0xda
#define TPM2NET_CMD_COMMAND 0xc0
#define TPM2NET_CMD_ANSWER 0xaa
#define TPM2NET_FOOTER_IDENT 0x36

//package size we expect. 
#define MAX_PACKED_SIZE 255

// buffers for receiving and sending data
uint8_t packetBuffer[MAX_PACKED_SIZE]; //buffer to hold incoming packet
uint16_t psize;
uint8_t currentPacket;
uint8_t totalPacket;

// rainbow animation stuff
int j=0,k=0;
uint8_t serialDataRecv;

//initialize pixels, each logical pixel has 3 physical pixels
Neophob_LPD6803 strip = Neophob_LPD6803(NR_OF_PANELS*PIXELS_PER_PANEL*3);


//table defines offset of a visible pixel. a visible pixel consists of 3 pixels
static PROGMEM prog_uchar pixelOfs[NR_OF_PANELS*PIXELS_PER_PANEL] = {
  //expedit 1
  3,6,9,12,18,21,24,27,33,36,39,42,48,51,54,57,
  
  //expedit 2
  65,68,71,74,80,83,86,89,95,98,101,104,110,113,116,119,

  //expedit 3
  127,130,133,136,142,145,148,151,157,160,163,166,171,174,177,180

};

// --------------------------------------------
//     send status back to library
// --------------------------------------------
static void sendAck() {
  Serial.print("AK EXI");
  Serial.print(NR_OF_PANELS, DEC);
#if defined (CORE_TEENSY_SERIAL)
  //Teensy supports send now
  Serial.send_now();
#endif
}


// --------------------------------------------
//      setup
// --------------------------------------------
void setup() {
  memset(packetBuffer, 0, MAX_PACKED_SIZE);
  
  //im your slave and wait for your commands, master!
  Serial.begin(BAUD_RATE); //Setup high speed Serial
  Serial.flush();
  Serial.setTimeout(20);

  digitalWrite(LED_PIN, HIGH);
  delay(250);
  digitalWrite(LED_PIN, LOW);  
  
  //SETUP SPI SPEED AND ISR ROUTINE
  //-------------------------------
  //The SPI setup is quite important to set up correctly

  //SPI SPEED REFERENCE  
  //strip.begin(SPI_CLOCK_DIV128);        // Start up the LED counterm 0.125MHz - 8uS
  //strip.begin(SPI_CLOCK_DIV64);        // Start up the LED counterm 0.25MHz - 4uS
  //strip.begin(SPI_CLOCK_DIV32);        // Start up the LED counterm 0.5MHz - 2uS
  //strip.begin(SPI_CLOCK_DIV16);        // Start up the LED counterm 1.0MHz - 1uS
  //strip.begin(SPI_CLOCK_DIV8);        // Start up the LED counterm 2.0MHz - 0.5uS
  //strip.begin(SPI_CLOCK_DIV4);        // Start up the LED counterm 4.0MHz - 0.25uS
  //strip.begin(SPI_CLOCK_DIV2);        // Start up the LED counterm 8.0MHz - 0.125uS

  //SETTING#1 - SPEEDY
  strip.setCPU(36);                    // call the isr routine each 36us to drive the pwm
  strip.begin(SPI_CLOCK_DIV32);        // Start up the LED counterm 0.5MHz - 2uS

  //SETTING#2 - CONSERVATIVE
  //  strip.setCPU(68);                    // call the isr routine each 68us to drive the pwm
  //  strip.begin(SPI_CLOCK_DIV64);        // Start up the LED counterm 0.25MHz - 4uS

  rainbow();      // display some colors
  serialDataRecv = 0;   //no serial data received yet  
}

uint8_t dataFrame;


// --------------------------------------------
//      main loop
// --------------------------------------------
void loop() {
  int16_t res = readCommand();  
  
  if (res > 0) {
    serialDataRecv = 1;
/*#ifdef DEBUG      
    Serial.print(" OK");
    Serial.print(psize, DEC);    
    Serial.print("/");
    Serial.print(currentPacket, DEC);    
#if defined (CORE_TEENSY_SERIAL)
    Serial.send_now();
#endif
#endif*/
    digitalWrite(LED_PIN, HIGH);
    updatePixels();
    digitalWrite(LED_PIN, LOW);    
  }
  else {
    //return error number
    if (res!=-1) {
      Serial.print(" ERR: ");
      Serial.print(res, DEC);
#if defined (CORE_TEENSY_SERIAL)      
      Serial.send_now();
#endif      
    }
  }

  if (serialDataRecv==0) { //if no serial data arrived yet, show the rainbow...
    rainbow();
  }
}

//********************************
// UPDATE PIXELS
//********************************
void updatePixels() {
  uint8_t nrOfPixels = psize/2;
  
  //get packet source offset
  uint16_t ofs=0;
  
  //panel offset
  uint16_t ledOffset = PIXELS_PER_PANEL*currentPacket;
  
  //track 3 module offset
  uint16_t effectiveOfs;
  
  for (uint8_t i=0; i < nrOfPixels; i++) {
    effectiveOfs = pixelOfs[ledOffset];
    strip.setPixelColor(effectiveOfs++, packetBuffer[ofs]<<8 | packetBuffer[ofs+1]);
    strip.setPixelColor(effectiveOfs++, packetBuffer[ofs]<<8 | packetBuffer[ofs+1]);
    strip.setPixelColor(effectiveOfs, packetBuffer[ofs]<<8 | packetBuffer[ofs+1]);
    ofs+=2;
    ledOffset++;
  }  

  //update panel content only once, even if we send multiple packets.
  //this can be done on the PixelController software
  if (currentPacket>=totalPacket-1) {  
    strip.show();   // write all the pixels out
#ifdef DEBUG      
    Serial.print(" OK");
    Serial.print(currentPacket, DEC);    
#if defined (CORE_TEENSY_SERIAL)
    Serial.send_now();
#endif
#endif    
  } else {
    
#ifdef DEBUG      
    Serial.print(" No update yet ");
    Serial.print(currentPacket, DEC);
    Serial.print(" / ");
    Serial.print(totalPacket-1, DEC);    
#if defined (CORE_TEENSY_SERIAL)
    Serial.send_now();
#endif
#endif    
    
  }
}


//********************************
// READ SERIAL PORT
//********************************
int16_t readCommand() {  
  uint8_t startChar = Serial.read();  
  if (startChar != TPM2NET_HEADER_IDENT) {
    return -1;
  }

  //uint8_t 
  dataFrame = Serial.read();
  if (dataFrame != TPM2NET_CMD_DATAFRAME && dataFrame != TPM2NET_CMD_COMMAND) {
    return -2;  
  }

  uint8_t s1 = Serial.read();
  uint8_t s2 = Serial.read();  
  psize = (s1<<8) + s2;
  //ignore payload size if a command packet is send
  if (dataFrame != TPM2NET_CMD_COMMAND && (psize < 6 || psize > MAX_PACKED_SIZE)) {
    return -3;
  }  

  currentPacket = Serial.read();  
  totalPacket = Serial.read();    
  if (totalPacket>NR_OF_PANELS || currentPacket>NR_OF_PANELS) {
    return -4;
  }
  
  //get remaining bytes
  uint16_t recvNr = Serial.readBytes((char *)packetBuffer, psize);
  if (recvNr!=psize) {
    Serial.print(" MissingData: ");
    Serial.print(recvNr, DEC);
    Serial.print("/");
    Serial.print(psize, DEC);    
    return -5;
  }  
  

  uint8_t endChar = Serial.read();
  if (endChar != TPM2NET_FOOTER_IDENT) {
    return -6;
  }

  //check for a ping request, the payload of the cmd is ignored
  if (dataFrame == TPM2NET_CMD_COMMAND) {
    sendAck();
    return -50;
  }
  
  return psize;
}




// --------------------------------------------
//     do some color magic
// --------------------------------------------
unsigned int Color(byte r, byte g, byte b) {
  //Take the lowest 5 bits of each value and append them end to end
  return( (((unsigned int)g & 0x1F )<<10) | (((unsigned int)b & 0x1F)<<5) | ((unsigned int)r & 0x1F));
}

// --------------------------------------------
//     Input a value 0 to 127 to get a color value.
//     The colours are a transition r - g -b - back to r
// --------------------------------------------
unsigned int Wheel(byte WheelPos) {
  byte r,g,b;
  switch(WheelPos >> 5) {
  case 0:
    r=31- WheelPos % 32;   //Red down
    g=WheelPos % 32;      // Green up
    b=0;                  //blue off
    break; 
  case 1:
    g=31- WheelPos % 32;  //green down
    b=WheelPos % 32;      //blue up
    r=0;                  //red off
    break;   
  case 2:
  default:
    b=31- WheelPos % 32;  //blue down 
    r=WheelPos % 32;      //red up
    g=0;                  //green off
    break; 
  }
  return(Color(r,g,b));
}

// --------------------------------------------
//     do some animation until serial data arrives
// --------------------------------------------
void rainbow() {
  delay(1);

  k++;
  if (k>50) {
    k=0;
    j++;
    if (j>96*3) {  // 3 cycles of all 96 colors in the wheel
      j=0; 
    }

    for (unsigned int i=0; i < strip.numPixels(); i++) {
      strip.setPixelColor(i, Wheel((i + j) % 96));
    }
    strip.show();    
  }
}

