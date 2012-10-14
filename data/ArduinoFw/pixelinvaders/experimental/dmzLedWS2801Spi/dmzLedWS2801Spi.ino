/*
 * PixelInvaders serial-led-gateway, Copyright (C) 2011 michael vogt <michu@neophob.com>
 * Tested on Teensy and Arduino
 * 
 * ------------------------------------------------------------------------
 *
 * This is the SPI version, unlike software SPI which is configurable, hardware 
 * SPI works only on very specific pins. 
 *
 * On the Arduino Uno, Duemilanove, etc., clock = pin 13 and data = pin 11. 
 * For the Arduino Mega, clock = pin 52, data = pin 51. 
 * For the ATmega32u4 Breakout Board and Teensy, clock = pin B1, data = B2. 
 *
 * ------------------------------------------------------------------------
 *
 * This file is part of PixelController.
 *
 * PixelController is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * PixelController is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 * 	
 */

//the lpd6803 library needs the timer1 library
#include <TimerOne.h>
#include <SPI.h>
#include <WS2801.h>

// blink LED when drawing rainbow
int ledPin = 11;
int ledon = 0;
elapsedMillis LEDTime;
elapsedMillis nextdemo;
int whichdemo = random(9);

// one color for when demo is one color, no need to refresh every loop
int onecolor = 0;
  
//star LED
//how many pixels
#define NR_OF_PIXELS 64

//just a constant for the random selection
#define RND 255

struct anim {
  uint32_t currentCol;
  uint32_t endCol;
  //1 = increase to endCol, 2 = decrease to 0
  uint8_t pos;
  uint8_t ofs;
  uint8_t del;
  uint8_t length;
  uint32_t col;
  
};

anim stars[NR_OF_PIXELS];
static uint8_t DELAY = 34;
uint8_t currentR, currentG, currentB;
uint8_t endR, endG, endB;

//christmas LED
anim mover;
uint8_t clearColR;
uint8_t clearColG;
uint8_t clearColB;
uint32_t clearCol;

// Choose which 2 pins you will use for output.
// Can be any valid output pins.
// The colors of the wires may be totally different so
// BE SURE TO CHECK YOUR PIXELS TO SEE WHICH WIRES TO USE!
int dataPin = 2;
int clockPin = 3;

//to draw a frame we need arround 20ms to send an image. the serial baudrate is
//NOT the bottleneck. 
//#define BAUD_RATE 230400//
#define BAUD_RATE 115200

//--- protocol data start
#define CMD_START_BYTE 0x01
#define CMD_SENDFRAME 0x03
#define CMD_PING  0x04

#define START_OF_DATA 0x10 
#define END_OF_DATA 0x20

//frame size for specific color resolution
//32pixels * 2 byte per color (15bit - one bit wasted)
#define COLOR_5BIT_FRAME_SIZE 64
#define SERIAL_HEADER_SIZE 5
//--- protocol data end

//8ms is the minimum! else we dont get any data!
#define SERIAL_DELAY_LOOP 3
#define SERIAL_WAIT_DELAY 3

//define nr of Panels*2 here, 4 means 2 panels
#define NR_OF_PANELS 1
#define PIXELS_PER_PANEL 64

//this should match RX_BUFFER_SIZE from HardwareSerial.cpp
//array that will hold the serial input string
byte serInStr[COLOR_5BIT_FRAME_SIZE+SERIAL_HEADER_SIZE]; 	 				 

//initialize pixels
WS2801 strip = WS2801(PIXELS_PER_PANEL*NR_OF_PANELS, dataPin, clockPin);

#define SERIALBUFFERSIZE 4
byte serialResonse[SERIALBUFFERSIZE];

byte g_errorCounter;

int j=0,k=0;
byte serialDataRecv;


// --------------------------------------------
//     send status back to library
// --------------------------------------------
static void sendAck() {
  serialResonse[0] = 'A';
  serialResonse[1] = 'K';
  serialResonse[2] = Serial.available();
  serialResonse[3] = g_errorCounter;
  Serial.write(serialResonse, SERIALBUFFERSIZE);

  //comment out next line on arduino!
  //Serial.send_now();
}


unsigned int Color(byte r, byte g, byte b) {
  //Take the lowest 5 bits of each value and append them end to end
  return( ((unsigned int)g & 0x1F )<<10 | ((unsigned int)b & 0x1F)<<5 | (unsigned int)r & 0x1F);
}

// Create a 24 bit color value from R,G,B
uint32_t ColorWS2801(byte r, byte g, byte b)
{
  uint32_t c;
  c = r;
  c <<= 8;
  c |= g;
  c <<= 8;
  c |= b;
  return c;
}


// --------------------------------------------
//     Input a value 0 to 127 to get a color value.
//     The colours are a transition r - g -b - back to r
// --------------------------------------------
unsigned int WheelWS2801(byte WheelPos) {
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
    b=31- WheelPos % 32;  //blue down 
    r=WheelPos % 32;      //red up
    g=0;                  //green off
    break; 
  }
  return(ColorWS2801(r,g,b));
}


//Input a value 0 to 255 to get a color value.
//The colours are a transition r - g -b - back to r
uint32_t Wheel(byte WheelPos)
{
  if (WheelPos < 85) {
   return Color(WheelPos * 3, 255 - WheelPos * 3, 0);
  } else if (WheelPos < 170) {
   WheelPos -= 85;
   return Color(255 - WheelPos * 3, 0, WheelPos * 3);
  } else {
   WheelPos -= 170; 
   return Color(0, WheelPos * 3, 255 - WheelPos * 3);
  }
}

// --------------------------------------------
//     do some animation until serial data arrives
// --------------------------------------------
void Rainbow1() {
  delay(1);

  k++;
  if (k>50) {
    k=0;
    j++;
    if (j>96*3) {  // 3 cycles of all 96 colors in the wheel
      j=0; 
    }

    for (int i=0; i < strip.numPixels(); i++) {
      strip.setPixelColor(i, Wheel((i + j) % 96));
    }
    strip.show();    
  }
}


// --------------------------------------------
//     create initial image
// --------------------------------------------
void showInitImage() {
  for (int i=0; i < strip.numPixels(); i++) {
    strip.setPixelColor(i, Wheel( i % 96));
  }    
  // Update the strip, to start they are all 'off'
  strip.show();
}


// --------------------------------------------
//      setup
// --------------------------------------------
void setup() {
  memset(serialResonse, 0, SERIALBUFFERSIZE);

  //im your slave and wait for your commands, master!
  Serial.begin(BAUD_RATE); //Setup high speed Serial
  Serial.flush();

  strip.begin();
  showInitImage();      // display some colors

  serialDataRecv = 0;   //no serial data received yet  
  
  // Initialize the LED 
  pinMode(ledPin, OUTPUT);     
  digitalWrite(ledPin, LOW);  

  //star animations
  for (int i=0; i < strip.numPixels(); i++) {
    if (random(RND)==2) {
      initStar(i);
    } else {
      stars[i].pos = 0;
    }      
  }
  
  //christmas animations
  fadeToNewColor();
  newAnimation();
}

// --------------------------------------------
//      main loop
// --------------------------------------------
void loop() {
  g_errorCounter=0;

  // see if we got a proper command string yet
  if (readCommand(serInStr) == 0) {
    //nope, nothing arrived yet...
    if (g_errorCounter!=0 && g_errorCounter!=102) {
      sendAck();
    }

    if (serialDataRecv==0) { //if no serial data arrived yet, show the rainbow...
      if ( whichdemo==1 || whichdemo==3 || whichdemo==5 || whichdemo==8) {
        if (nextdemo >= 5000) {
          nextdemo = 0;
          onecolor = 0;
          whichdemo = random(9);
        }
      } else {
        if (nextdemo >= 30000) {
          nextdemo = 0;
          whichdemo = random(9);
        }    
      }    
      BlinkLED();
      if (whichdemo==0) { BlinkChristmas(); }
      if (whichdemo==1) { if (onecolor!=1) { colorWipe(Color(255, 0, 0), 50); onecolor = 1; } }
      if (whichdemo==2) { Rainbow1(); }      
      if (whichdemo==3) { if (onecolor!=1) { colorWipe(Color(0, 255, 0), 50); onecolor = 1; } }
      if (whichdemo==4) { Rainbow2(DELAY); }      
      if (whichdemo==5) { if (onecolor!=1) { colorWipefull(Color(random(255), random(255), random(255))); onecolor = 1; } } 
      if (whichdemo==6) { BlinkStars(); }      
      if (whichdemo==7) { rainbowCycle(DELAY); }      
      if (whichdemo==8) { if (onecolor!=1) { colorWipe(Color(0, 0, 255), 50); onecolor = 1; } }
    }    
    return;
  }

  //led offset
  byte ofs    = serInStr[1];
  //how many bytes we're sending
  byte sendlen = serInStr[2];
  //what kind of command we send
  byte type = serInStr[3];
  //get the image data
  byte* cmd    = serInStr+5;

  switch (type) {
  case CMD_SENDFRAME:
    //the size of an image must be exactly 64bytes for 8*4 pixels
    if (sendlen == COLOR_5BIT_FRAME_SIZE) {
      updatePixels(ofs, cmd);
    } 
    else {
      g_errorCounter=100;
    }
    break;

  case CMD_PING:
    //just send the ack!
    serialDataRecv = 1;        
    break;

  default:
    //invalid command
    g_errorCounter=130; 
    break;
  }

  //send ack to library - command processed
  sendAck();
}

// --------------------------------------------
//    update 32 bytes of the led matrix
//    ofs: which panel, 0 (ofs=0), 1 (ofs=32), 2 (ofs=64)...
// --------------------------------------------
void updatePixels(byte ofs, byte* buffer) {
  uint16_t currentLed = ofs*PIXELS_PER_PANEL;
  byte x=0;
  for (byte i=0; i < PIXELS_PER_PANEL; i++) {
    strip.setPixelColor(currentLed, buffer[x]<<8 | buffer[x+1]);
    x+=2;
    currentLed++;
  }  
  strip.show();   // write all the pixels out
}

/* 
 --------------------------------------------
 read serial command
 --------------------------------------------
 read a string from the serial and store it in an array
 you must supply the str array variable
 returns number of bytes read, or zero if fail
 
 example ping command:
 		cmdfull[0] = START_OF_CMD (marker);
 		cmdfull[1] = addr;
 		cmdfull[2] = 0x01; 
 		cmdfull[3] = CMD_PING;
 		cmdfull[4] = START_OF_DATA (marker);
 		cmdfull[5] = 0x02;
 		cmdfull[6] = END_OF_DATA (marker);
 */

byte readCommand(byte *str) {
  byte b,i,sendlen;

  //wait until we get a CMD_START_BYTE or queue is empty
  i=0;
  while (Serial.available()>0 && i==0) {
    b = Serial.read();
    if (b == CMD_START_BYTE) {
      i=1;
    }
  }

  if (i==0) {
    //failed to get data ignore it
    g_errorCounter = 102;
    return 0;    
  }

  //read header  
  i=1;
  b=SERIAL_DELAY_LOOP;
  while (i<SERIAL_HEADER_SIZE) {
    if (Serial.available()) {
      str[i++] = Serial.read();
    } 
    else {
      delay(SERIAL_WAIT_DELAY); 
      if (b-- == 0) {
        g_errorCounter = 103;
        return 0;        //no data available!
      }      
    }
  }

  // --- START HEADER CHECK    
  //check if data is correct, 0x10 = START_OF_DATA
  if (str[4] != START_OF_DATA) {
    g_errorCounter = 104;
    return 0;
  }

  //check sendlen, its possible that sendlen is 0!
  sendlen = str[2];  
  // --- END HEADER CHECK

  //read data  
  i=0;
  b=SERIAL_DELAY_LOOP;
  while (i<sendlen+1) {
    if (Serial.available()) {
      str[SERIAL_HEADER_SIZE+i++] = Serial.read();
    } 
    else {
      delay(SERIAL_WAIT_DELAY); 
      if (b-- == 0) {
        g_errorCounter = 105;
        return 0;        //no data available!
      }      
    }
  }

  //check if data is correct, 0x20 = END_OF_DATA
  if (str[SERIAL_HEADER_SIZE+sendlen] != END_OF_DATA) {
    g_errorCounter = 106;
    return 0;
  }

  //return data size (without meta data)
  return sendlen;
}

void BlinkLED() {
  if (LEDTime >= 1000) {
    LEDTime = 0;
    if (ledon) {
      ledon = 0;
      digitalWrite(ledPin, LOW);
    } else {
      ledon = 1;
      digitalWrite(ledPin, HIGH);
    }
  }
}

void BlinkStars() {
  for (int i=0; i < strip.numPixels(); i++) {
    
    // ====
    // INIT
    // ====
    if (stars[i].pos == 0) {
      if (random(RND)==2) {
        initStar(i);
      }
    } else

    // =======
    // FADE IN
    // =======
    if (stars[i].pos == 1) {
      
      //decrease color...
      uint32_t ccol = stars[i].currentCol;
      currentB = ccol & 0xff;
      ccol >>= 8;
      currentG = ccol & 0xff;
      ccol >>= 8;
      currentR = ccol & 0xff;

      uint32_t ecol = stars[i].endCol;
      endB = ecol & 0xff;
      ecol >>= 8;
      endG = ecol & 0xff;
      ecol >>= 8;
      endR = ecol & 0xff;
      
      if (currentR<endR) currentR+=2; else currentR=endR;
      if (currentG<endG) currentG+=2; else currentG=endG;
      if (currentB>endB) currentB+=2; else currentB=endB;
      
      if (currentR==endR && currentG==endG && currentB == endB) {
        stars[i].pos = 2;
      }

      stars[i].currentCol = ColorWS2801(currentR, currentG, currentB);      
    } else 

    // ========
    // FADE OUT
    // ========
    if (stars[i].pos == 2) {
      //decrease color...
      uint32_t ccol = stars[i].currentCol;
      currentB = ccol & 0xff;
      ccol >>= 8;
      currentG = ccol & 0xff;
      ccol >>= 8;
      currentR = ccol & 0xff;

      if (currentR>2) currentR-=3; else currentR=0;
      if (currentG>2) currentG-=3; else currentG=0;
      if (currentB>2) currentB-=3; else currentB=0;

      stars[i].currentCol = ColorWS2801(currentR, currentG, currentB);

      if (stars[i].currentCol == 0) {
        stars[i].pos = 0;
      }
    }    
    //update color
    strip.setPixelColor(i, stars[i].currentCol);
  }
  strip.show(); 
  delay(DELAY);  
}

//init new star
void initStar(int i) {
    uint8_t rnd = random(192);
    uint8_t rnd2 = random(64);
    stars[i].endCol = ColorWS2801(rnd+rnd2, rnd+rnd2, rnd2);
    stars[i].pos = 1;
    stars[i].currentCol=0;  
}


// christmas animations
void BlinkChristmas() {
  if ((mover.pos > 0 && mover.pos == mover.del) || mover.pos > strip.numPixels()) {
    newAnimation();
  }
  
  if (mover.pos > mover.length) {
    mover.del++;
  } else {
    mover.pos++;      
  }
    
  for (int i=0; i < strip.numPixels(); i++) {
    if (i>=mover.ofs+mover.del && i<mover.ofs+mover.pos) {
      strip.setPixelColor(i, mover.col);
    } else {
      strip.setPixelColor(i, clearCol);
    }      
  }
  
  strip.show(); 
  delay(DELAY);  
}

//init a new line animation
void newAnimation() {
  fadeToNewColor();
  
  mover.length = 0;
  while (mover.length<16) {
    mover.ofs = random(strip.numPixels()); 
    mover.length = random( strip.numPixels()-mover.ofs ); 
  }
  mover.pos = 0;
  mover.del = 0;
  mover.col = ColorWS2801(random(200), random(200), random(250));  
}

//fade currentbackground color to next, random color
void fadeToNewColor() {
  uint8_t oldR = clearColR;
  uint8_t oldG = clearColG;
  uint8_t oldB = clearColB;

  clearColR = random(70);
  clearColG = random(70);
  clearColB = random(90);
  clearCol = ColorWS2801(clearColR, clearColG, clearColB);
  
  int steps = 25;
  float stepsR = (clearColR-oldR)/(float)steps;
  float stepsG = (clearColG-oldG)/(float)steps;
  float stepsB = (clearColB-oldB)/(float)steps;

  for (int s=0; s<steps+1; s++) {
    uint8_t rr=oldR+stepsR*s;    
    uint8_t gg=oldG+stepsG*s;
    uint8_t bb=oldB+stepsB*s;
    uint32_t c = ColorWS2801(rr, gg, bb);
    
    for (int i=0; i < strip.numPixels(); i++) {
      strip.setPixelColor(i, c);
    }
    strip.show(); 
    delay(DELAY);
  }
}

// other rainbows
void Rainbow2(uint8_t wait) {
  int i, j;
   
  for (j=0; j < 256; j++) {     // 3 cycles of all 256 colors in the wheel
    for (i=0; i < strip.numPixels(); i++) {
      strip.setPixelColor(i, WheelWS2801( (i + j) % 255));
    }  
    strip.show();   // write all the pixels out
    delay(wait);
  }
}

// Slightly different, this one makes the rainbow wheel equally distributed 
// along the chain
void rainbowCycle(uint8_t wait) {
  int i, j;
  
  for (j=0; j < 256 * 5; j++) {     // 5 cycles of all 25 colors in the wheel
    for (i=0; i < strip.numPixels(); i++) {
      // tricky math! we use each pixel as a fraction of the full 96-color wheel
      // (thats the i / strip.numPixels() part)
      // Then add in j which makes the colors go around per pixel
      // the % 96 is to make the wheel cycle around
      strip.setPixelColor(i, WheelWS2801( ((i * 256 / strip.numPixels()) + j) % 256) );
    }  
    strip.show();   // write all the pixels out
    delay(wait);
  }
}

// fill the dots one after the other with said color
// good for testing purposes
void colorWipe(uint32_t c, uint8_t wait) {
  int i;
  
  for (i=0; i < strip.numPixels(); i++) {
      strip.setPixelColor(i, c);
      strip.show();
      delay(wait);
  }
}

void colorWipefull(uint32_t c) {
  int i;
  
  for (i=0; i < strip.numPixels(); i++) {
      strip.setPixelColor(i, c);
  }
  strip.show();
}

