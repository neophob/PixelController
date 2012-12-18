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
 *
 * WS2801 hack with random demo created by David M. Zendzian - dmz@zzservers.com - @dmz006 on twitter
 * WS2801 funtions liberated from PixelController WS2801 demo apps
 * 2012/10/18
 * 
 * 2012/10/23 - updated for drawing static pictures and spectrum analyzer
 *
 * Spectrum analyze and code for line drawing from : http://www.bliptronics.com/projects/ArduinoSpectrumAnalyzer.aspx
 * Spectrum analyze idea : http://nuewire.com/info-archive/msgeq7-by-j-skoba/
 * Spectrum analyze idea : http://www.cmiyc.com/projects/msgeq7-simple-spectrum-analyzer/
 * currently configured to randomly fill in values but can be connected to MSGEQ7 to display
 * real audio output
 *
 * 2012/10/27 - dmz - expanded serInStr by 1 to prevent data reading buffer overflow
 */

#include <TimerOne.h>
#include <SPI.h>
#include <WS2801.h>

// debugging
// 0 = off
// 1 = blink LED on pin 11
// 2 = print debugging output to serial
int debug = 0;

//For spectrum analyzer shield, these three pins are used.
//You can move pins 4 and 5, but you must cut the trace on the shield and re-route from the 2 jumpers. 
int spectrumReset=5;
int spectrumStrobe=4;
int spectrumAnalog=0;  //0 for left channel, 1 for right.

//This holds the 15 bit RGB values for each LED.
//You'll need one for each LED, we're using 25 LEDs here.
#define WIDTH 8                  //Width of our grid.
#define HEIGHT 8                //Height of our grid.
#define NUM_LEDS WIDTH * HEIGHT  // Set the number of LEDs in use here

// Spectrum analyzer read values will be kept here.
int Spectrum[8];  

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
#define NR_OF_PANELS 2
#define PIXELS_PER_PANEL 32

//this should match RX_BUFFER_SIZE from HardwareSerial.cpp
//array that will hold the serial input string
// dmz - 2012-10-27 - added +1 to prevent data-reading from overflowing buffer into strip data
byte serInStr[COLOR_5BIT_FRAME_SIZE+SERIAL_HEADER_SIZE+1]; 	 				 

//initialize pixels
// manually set pins but disables SPI
//WS2801 strip = WS2801(PIXELS_PER_PANEL*NR_OF_PANELS, dataPin, clockPin);
// using default SPI pins
WS2801 strip = WS2801(PIXELS_PER_PANEL*NR_OF_PANELS);

#define SERIALBUFFERSIZE 4
byte serialResonse[SERIALBUFFERSIZE];

byte g_errorCounter;

int j=0,k=0;

// number of Demos
#define NUMDEMOS 10

// blink LED when in demos and debug endabled
int ledPin = 11;
int ledon = 0;
long LEDTime;

// timers for the next demo, since last message, demo output delay and since last serial communication
long nextdemo;
long messagetimer;
long delaytimer;
long lastComm;

// initial demo - set to display picture
int whichdemo = 10;

// one color for when demo is one color, no need to refresh every loop
int onecolor = 0;
  
#define NUMPICTURES 5

// default picture - pixelmonster
int whichpicture = 2;

// pictures
// System generates 7 unique colors and is mapped to each of the -1, -2, etc
int pictures[NUMPICTURES][WIDTH][HEIGHT] = 
   { { {-1,-1,-1,-1,-1,-1,-1,-1},  // blank imge
       {-1,-1,-1,-1,-1,-1,-1,-1},
       {-1,-1,-1,-1,-1,-1,-1,-1},
       {-1,-1,-1,-1,-1,-1,-1,-1},
       {-1,-1,-1,-1,-1,-1,-1,-1},
       {-1,-1,-1,-1,-1,-1,-1,-1},
       {-1,-1,-1,-1,-1,-1,-1,-1},
       {-1,-1,-1,-1,-1,-1,-1,-1} },
     { {-1,-2,-1,-2,-2,-1,-2,-1},  // pixelinvader
       {-1,-1,-2,-2,-2,-2,-1,-1},
       {-1,-2,-3,-2,-2,-3,-2,-1},
       {-2,-2,-2,-2,-2,-2,-2,-2},
       {-2,-1,-2,-2,-2,-2,-1,-2},
       {-2,-1,-2,-1,-1,-2,-1,-2},
       {-1,-1,-2,-1,-1,-2,-1,-1},
       {-1,-2,-2,-1,-1,-2,-2,-1} },
     { {-1,-1,-2,-1,-2,-2,-1,-1},  // pixelmonser
       {-1,-1,-2,-2,-2,-2,-1,-1},
       {-1,-2,-1,-2,-2,-1,-2,-1},
       {-2,-1,-1,-1,-1,-1,-1,-2},
       {-2,-2,-1,-2,-2,-1,-2,-2},
       {-2,-2,-2,-2,-2,-2,-2,-2},
       {-1,-2,-2,-2,-2,-2,-2,-1},
       {-1,-2,-1,-2,-1,-2,-1,-1} },
     { {-1,-1,-2,-2,-2,-2,-2,-1},  // skull
       {-1,-2,-2,-2,-2,-2,-2,-2},
       {-1,-2,-2,-2,-2,-2,-2,-2},
       {-2,-2,-2,-2,-2,-2,-2,-2},
       {-2,-1,-1,-2,-1,-1,-2,-2},
       {-2,-2,-1,-2,-2,-1,-2,-2},
       {-1,-2,-2,-1,-2,-2,-1,-1},
       {-1,-1,-2,-2,-2,-2,-1,-1} },       
     { {-1,-1,-2,-2,-2,-2,-1,-1}, // smileyface
       {-1,-2,-1,-1,-1,-1,-2,-1},
       {-2,-1,-2,-1,-1,-2,-1,-2},
       {-2,-1,-1,-1,-1,-1,-1,-2},
       {-2,-1,-2,-1,-1,-2,-1,-2},
       {-2,-1,-1,-2,-2,-1,-1,-2},
       {-1,-2,-1,-1,-1,-1,-2,-1},
       {-1,-1,-2,-2,-2,-2,-1,-1} }
   };

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

anim stars[NUM_LEDS];
static uint8_t DELAY = 34;
uint8_t currentR, currentG, currentB;
uint8_t endR, endG, endB;

//christmas LED
anim mover;
uint8_t clearColR;
uint8_t clearColG;
uint8_t clearColB;
uint32_t clearCol;

// --------------------------------------------
//      setup
// --------------------------------------------
void setup() {
  memset(serialResonse, 0, SERIALBUFFERSIZE);

  //im your slave and wait for your commands, master!
  Serial.begin(BAUD_RATE); //Setup high speed Serial
  Serial.flush();
  
  strip.begin();

  // initialize the delaytimer and lastcomm > than default wait
  // to display demos right as system is enabled
  delaytimer = 1000;  
  lastComm = 60000;

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
  
  // spectrum_init();          //init the spectrum analyzer hardware.  Uncomment if enabled
}

// --------------------------------------------
//      main loop
// --------------------------------------------
void loop() {
  // should have an interrupt on the serial port so it does not depend on timing
  // Check if there is serial input and if so process it using the standard pixelinvader routines
  // Otherwise if it has been at least a minute since last serial communication then we are
  // free to process various demos
 if (Serial.available()) {
    ProcessSerial();
 } else if (lastComm > 60000) {    
    ProcessDemo(); 
 }
}

// Loop serial functions
void ProcessSerial() {
  g_errorCounter=0;
  
  if (readCommand(serInStr) == 0) {
    //nope, nothing arrived yet...
    if (g_errorCounter!=0 && g_errorCounter!=102) {
      sendAck();
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
    break;
  
  default:
    //invalid command
    g_errorCounter=130; 
    break;
  }    
  //send ack to library - command processed
  sendAck();      
}

void ProcessDemo() {
  // static color demos display for 5 seconds, then reset variables
  if ( whichdemo==1 || whichdemo==3 || whichdemo==5 || whichdemo==8 ) {
    if (nextdemo >= 5000) { 
      j = 0;
      k = 0;
      nextdemo = 0;
      onecolor = 0;
      delaytimer = 1000;
      whichdemo = random(NUMDEMOS);
      whichpicture = random(NUMPICTURES);
    }
  } else if ( whichdemo == 10 ) {
    // picture demo display for 15 seconds, then reset variables    
    if (nextdemo >= 15000) { 
      j = 0;
      k = 0;
      nextdemo = 0;
      onecolor = 0;
      delaytimer = 1000;
      whichdemo = random(NUMDEMOS);
      whichpicture = random(NUMPICTURES);
    }
  } else {
    // all other demos display for 30 seconds, then reset variables
    if (nextdemo >= 30000) {
      j = 0;
      k = 0;
      nextdemo = 0;
      onecolor = 0;
      delaytimer = 1000;          
      whichdemo = random(NUMDEMOS);
      whichpicture = random(NUMPICTURES);
    }    
  }  
  // If you want to display a random color occasionally using picture=0 comment out this line
  if (whichpicture==0) {whichpicture=2;}
  if (debug) {  
    // if it has been a second sense last debug message and debug mode = 2
    // update with any debugging messages you need
    if ((messagetimer > 1000) && (debug == 2)) {
      messagetimer = 0;
      Serial.print(" Demo: ");
      Serial.print(whichdemo);
      Serial.print(" | DemoTimer: ");
      Serial.print(nextdemo);
      Serial.print(" | Picture: ");
      Serial.println(whichpicture);      
      Serial.print("NumPixels: ");
      Serial.println(strip.numPixels());
    }
    BlinkLED();
  }

  // The demos...
  if (whichdemo==0) { BlinkChristmas(); }
  if (whichdemo==1) { if (onecolor!=1) { colorWipefull(Color(255, 0, 0)); onecolor = 1; } }
  if (whichdemo==2) { Rainbow1(); }      
  if (whichdemo==3) { if (onecolor!=1) { colorWipefull(Color(0, 255, 0)); onecolor = 1; } }
  if (whichdemo==4) { Rainbow2(DELAY); }      
  if (whichdemo==5) { if (onecolor!=1) { colorWipefull(Color(random(255), random(255), random(255))); onecolor = 1; } } 
  if (whichdemo==6) { BlinkStars(); }      
  if (whichdemo==7) { rainbowCycle(DELAY); }      
  if (whichdemo==8) { if (onecolor!=1) { colorWipefull(Color(0, 0, 255)); onecolor = 1; } }
  if (whichdemo==9) { ProcessAudio(DELAY*50); }        
  if (whichdemo==10) { ProcessPicture(); }        
}    

/* 
 --------------------------------------------
 read serial command
 --------------------------------------------
 read a string from the serial and store it in an array
 you must supply the str array variable
 returns number of bytes read, or zero if fail
 
 example ping command:
 		cmdfull[0] = START_OF_CMD (marker); -- 0x01
 		cmdfull[1] = addr;
 		cmdfull[2] = 0x01; 
 		cmdfull[3] = CMD_PING; -- 0x04
 		cmdfull[4] = START_OF_DATA (marker); -- 0x10
 		cmdfull[5] = 0x02;
 		cmdfull[6] = END_OF_DATA (marker); -- 0x20

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
      lastComm = 0;
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
      str[SERIAL_HEADER_SIZE+i++] = Serial.read();;
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

  // Reset lastComm timer
  lastComm = 0;
  
  //return data size (without meta data)
  return sendlen;
}

// --------------------------------------------
//     send status back to library
// --------------------------------------------
static void sendAck() {
  serialResonse[0] = 'A';
  serialResonse[1] = 'K';
  serialResonse[2] = Serial.available();
  serialResonse[3] = g_errorCounter;
  Serial.write(serialResonse, SERIALBUFFERSIZE);

#if defined (CORE_TEENSY_SERIAL)
  //Teensy supports send now
  Serial.send_now();
#endif
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

// The following demos were taken from:
//    The pixelinvader code referenced above
//    WS2801 libraries referenced above
//    Spectrum analyzer libraries referenced above
//    and other random ideas...please contribute...dmz@zzservers.com / @dmz006
// --------------------------------------------
//     create initial image
// --------------------------------------------
void showInitImage() {
  for (int i=0; i < strip.numPixels(); i++) {
    strip.setPixelColor(i, Wheel( i % 96));
  }    
  // Update the strip, to start they are all 'off'
  strip.show(); 
  
  delay(6000);
}

// functions below from WS2801 and other libraries for demo purposes
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

// if its been a second since LED state change, update LED (on/off) and reset variables
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

//init new star
void initStar(int i) {
    uint8_t rnd = random(192);
    uint8_t rnd2 = random(64);
    stars[i].endCol = ColorWS2801(rnd+rnd2, rnd+rnd2, rnd2);
    stars[i].pos = 1;
    stars[i].currentCol=0;  
}

void BlinkStars() {
  if (delaytimer > DELAY) {
    delaytimer = 0;
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
  }
}

// christmas animations
void BlinkChristmas() {
  if (delaytimer > DELAY) {
    delaytimer = 0;
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
  }
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

  if (delaytimer > DELAY) {
    delaytimer = 0;
    for (int s=0; s<steps+1; s++) {
      uint8_t rr=oldR+stepsR*s;    
      uint8_t gg=oldG+stepsG*s;
      uint8_t bb=oldB+stepsB*s;
      uint32_t c = ColorWS2801(rr, gg, bb);
      
      for (int i=0; i < strip.numPixels(); i++) {
        strip.setPixelColor(i, c);
      }
      strip.show(); 
    }
  }
}

// view translation 
// this function will translate through x,y coordinates updating random color 
// mainly used to test x,y graphing to be sure it works as intended
void ProcessTranslation() {
  int newcolor;
  int colordifference;
  
  if (delaytimer > DELAY*100) {
    delaytimer = 0;
    int colors[7];
    for (int i=0; i<7; i++) {
       newcolor = ColorWS2801(random(255),random(255),random(255));
       if (i>0) {
         if (newcolor > colors[i-1]) {
           colordifference = newcolor - colors[i-1];
         } else {
           colordifference = colors[i-1] - newcolor;
         }
         if (colordifference < 2000) { 
           newcolor = ColorWS2801(random(255),random(255),random(255));
         }
       } else { 
         colors[i] = newcolor; 
       }
    }  
    for (int y=0; y < HEIGHT; y++) {
      for (int x=0; x < WIDTH; x++) {
        if (pictures[whichpicture][x][y] < 0) {
          strip.setPixelColor(Translate(x,y), colors[(pictures[0][x][y]*-1)-1]);
        } else {
          strip.setPixelColor(Translate(x,y), pictures[0][x][y]);
        }
        strip.show(); 
        // delay(DELAY*10);
        //Serial.print(x); Serial.print(":"); Serial.print(y); Serial.print("=");Serial.println(Translate(x,y));
      }
    }      
  }
}

// View pictures
// generate 8 random colors, if the picture buffer has <0 #s from -1 -- -7
// replace with the appropriate random color and display picture
// if picture buffer > 0 it will just send that value as the color
void ProcessPicture() {
  int newcolor;
  int colordifference;
  
  if (delaytimer > DELAY*100) {
    delaytimer = 0;
    int colors[7];
    for (int i=0; i<7; i++) {
       newcolor = ColorWS2801(random(255),random(255),random(255));
       if (i>0) {
         if (newcolor > colors[i-1]) {
           colordifference = newcolor - colors[i-1];
         } else {
           colordifference = colors[i-1] - newcolor;
         }
         if (colordifference < 2000) { 
           newcolor = ColorWS2801(random(255),random(255),random(255));
         }
       } else { 
         colors[i] = newcolor; 
       }
    }  

    for (int y=0; y < HEIGHT; y++) {
      for (int x=0; x < WIDTH; x++) {
        if (pictures[whichpicture][y][x] < 0) {
          strip.setPixelColor(Translate(x,y), colors[(pictures[whichpicture][y][x]*-1)-1]);
        } else {
          strip.setPixelColor(Translate(x,y), pictures[whichpicture][y][x]);
        }
      }
    }
    strip.show();     
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
      // strip.setPixelColor(i, WheelWS2801((i + j) % 96));
    }
    strip.show();    
  }
}

// other rainbows
void Rainbow2(uint8_t wait) {
  int i;
   
  if (delaytimer > wait) {
    delaytimer = 0;
    if (j < 256) { j++; }
    else { j = 0; }  // 3 cycles of all 256 colors in the wheel
    for (i=0; i < strip.numPixels(); i++) {
      strip.setPixelColor(i, WheelWS2801( (i + j) % 255));
    }  
    strip.show();   // write all the pixels out
  }
}

// Slightly different, this one makes the rainbow wheel equally distributed 
// along the chain
void rainbowCycle(uint8_t wait) {
  int i;
  
  if (delaytimer > wait) {
    delaytimer = 0;
    if (j < 256 * 5) { j++; } 
    else { j = 0; } // 5 cycles of all 25 colors in the wheel
      for (i=0; i < strip.numPixels(); i++) {
        // tricky math! we use each pixel as a fraction of the full 96-color wheel
        // (thats the i / strip.numPixels() part)
        // Then add in j which makes the colors go around per pixel
        // the % 96 is to make the wheel cycle around
        strip.setPixelColor(i, WheelWS2801( ((i * 256 / strip.numPixels()) + j) % 256) );
      }  
      strip.show();   // write all the pixels out
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

//Translate x and y to a LED index number in an array.
//Assume LEDS are layed out in a zig zag manner eg for a 4x4: (gridHeight=4)
// 1  2  3  4
// 8  7  6  5
// 9 10 11 12
//16 15 14 13
unsigned int Translate(byte x, byte y) {
  if (y%2) {
    return(((y+1)*HEIGHT) - 1 - x);
  } else {
    return((y*HEIGHT) + x);    
  }
}

//Flip the Y value
int flipY(int y) {
  return(HEIGHT-y-1);
}

// Draw a line in defined color between two points
// Using Bresenham's line algorithm, optimized for no floating point.
void line(int x0,  int y0, int x1, int y1, uint32_t color)
{
     boolean steep;
     steep= abs(y1 - y0) > abs(x1 - x0);
     if (steep)
    {
         swap(&x0, &y0);
         swap(&x1, &y1);
    }
     if (x0 > x1)
    {
         swap(&x0, &x1);
         swap(&y0, &y1);
    }
     int deltax = x1 - x0;
     int deltay = abs(y1 - y0);
     int error = 0;
     int ystep;
     int y = y0;
     int x;
     if (y0 < y1) 
       ystep = 1; 
     else 
       ystep = -1;
     for (x=x0; x<=x1; x++) // from x0 to x1
       {
         if (steep)
          strip.setPixelColor((Translate(y,x)),color);
         else 
           strip.setPixelColor((Translate(x,y)),color);
         error = error + deltay;
         if (2 * error >= deltax)
           {
           y = y + ystep;
           error = error - deltax;
           }
       }
     strip.show();
}

////Swap the values of two variables, for use when drawing lines.
void swap(int * a, int * b)
{
  int temp;
  temp=*b;
  *b=*a;
  *a=temp;
}

void box(byte x0, byte y0, byte x1, byte y1, uint32_t color)
{
  line(x0,y0,x1,y0,color);
  line(x1,y0,x1,y1,color);
  line(x1,y1,x0,y1,color);
  line(x0,y1,x0,y0,color);  
}

void spectrum_init()
{
    //Setup pins to drive the spectrum analyzer. 
  pinMode(spectrumReset, OUTPUT);
  pinMode(spectrumStrobe, OUTPUT);

  //Init spectrum analyzer
  digitalWrite(spectrumStrobe,LOW);
    delay(1);
  digitalWrite(spectrumReset,HIGH);
    delay(1);
  digitalWrite(spectrumStrobe,HIGH);
    delay(1);
  digitalWrite(spectrumStrobe,LOW);
    delay(1);
  digitalWrite(spectrumReset,LOW);
    delay(5);
  // Reading the analyzer now will read the lowest frequency. 
}

void ProcessAudio(uint8_t wait) {
  if (delaytimer > wait) {
    delaytimer = 0;
    showSpectrum();
  }
}

// Read 7 band equalizer.
void readSpectrum()
{
 
  byte Band;
  for(Band=0;Band <7; Band++)
  {
    // Spectrum[Band] = (analogRead(spectrumAnalog) + analogRead(spectrumAnalog) ) >>1; //Read twice and take the average by dividing by 2
    Spectrum[Band] = (random(1000) + random(1000) ) >>1;
    digitalWrite(spectrumStrobe,HIGH);
    digitalWrite(spectrumStrobe,LOW);     
  }
}

void normalizeSpectrum()
{
 
  static unsigned int  Divisor = 20, ChangeTimer=0, scaledLevel;
  int totalBarSize=0; //, ReminderDivisor,
  byte Band, BarSize, MaxLevel;
  
  readSpectrum();
  
  MaxLevel=0;
  for(Band=0;Band<7;Band++)
  {
     scaledLevel = Spectrum[Band]/Divisor;	//Bands are read in as 10 bit values. Scale them down to be 0 - 5
     Spectrum[Band]=scaledLevel;
     if (scaledLevel > MaxLevel)  //Check if this value is the largest so far.
       MaxLevel = scaledLevel;    
     totalBarSize+=Spectrum[Band];
  }
  Spectrum[7] = totalBarSize / 7;
  //Is the level off the chart!?? If so, increase the divisor to make it small next read.
  if (MaxLevel >= (HEIGHT)+1)
  {
    Divisor=Divisor+1;
    ChangeTimer=0;
  }
  else //If the level is too low, make divisor smaller, increase the levels on next read! - but only if divisor is not too small. If too small we sample too much noise!
    if(MaxLevel < 20)
    {
      if(Divisor > 25)
        if(ChangeTimer++ > 20)
        {
          Divisor--;
          ChangeTimer=0;
        }
    }
    else
    {
      ChangeTimer=0; 
    }
}

void showSpectrum()
{
  //Not I don;t use any floating point numbers - all integers to keep it zippy. 
   normalizeSpectrum();
   byte Band, BarSize;
   static unsigned int  Divisor = 20, ChangeTimer=0; //, ReminderDivisor,
   unsigned int works, Remainder;  
        
  for(Band=0;Band < WIDTH;Band++)  {
    for(BarSize=0;BarSize < HEIGHT; BarSize++) { 
      //if(Spectrum[Band] > BarSize) strip.setPixelColor(Translate(Band,BarSize),ColorWS2801(255,0,0)); ///below the level  WheelWS2801(Band*10)
      if(Spectrum[Band] > BarSize) strip.setPixelColor(Translate(Band,flipY(BarSize)),ColorWS2801(255,0,0)); //below the level
      else if ( Spectrum[Band] == BarSize) strip.setPixelColor(Translate(Band,flipY(BarSize)),ColorWS2801(0,0,31));//at the level
      else strip.setPixelColor(Translate(Band,flipY(BarSize)),ColorWS2801(0,0,0)); //Above the level - Y flipped because 0,0 is top left in pixelinvader
    }
  }
  strip.show(); 
}



