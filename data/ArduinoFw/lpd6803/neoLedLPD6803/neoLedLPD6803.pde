/*
 * PixelInvaders serial-led-gateway, Copyright (C) 2011 michael vogt <michu@neophob.com>
 * Tested on Teensy and Arduino
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

#include <TimerOne.h>  
#include "LPD6803.h"

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

#define PIXELS_PER_PANEL 32
#define NR_OF_PANELS 2

//this should match RX_BUFFER_SIZE from HardwareSerial.cpp
//array that will hold the serial input string
byte serInStr[COLOR_5BIT_FRAME_SIZE+SERIAL_HEADER_SIZE]; 	 				 

// Choose which 2 pins you will use for output.
// Can be any valid output pins.
int dataPin = 2;       // 'green' wire
int clockPin = 3;      // 'blue' wire

//initialize pixels
LPD6803 strip = LPD6803(PIXELS_PER_PANEL*NR_OF_PANELS, dataPin, clockPin);

#define SERIALBUFFERSIZE 4
byte serialResonse[SERIALBUFFERSIZE];

byte g_errorCounter;

int j=0,k=0;
byte serialDataRecv;

//send status back to library
static void sendAck() {
  serialResonse[0] = 'A';
  serialResonse[1] = 'K';
  serialResonse[2] = Serial.available();
  serialResonse[3] = g_errorCounter;
  Serial.write(serialResonse, SERIALBUFFERSIZE);
  
  //comment out next line on arduino!
  Serial.send_now();
}


unsigned int Color(byte r, byte g, byte b) {
  //Take the lowest 5 bits of each value and append them end to end
  return( ((unsigned int)g & 0x1F )<<10 | ((unsigned int)b & 0x1F)<<5 | (unsigned int)r & 0x1F);
}

//Input a value 0 to 127 to get a color value.
//The colours are a transition r - g -b - back to r
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
      b=31- WheelPos % 32;  //blue down 
      r=WheelPos % 32;      //red up
      g=0;                  //green off
      break; 
  }
  return(Color(r,g,b));
}

//do some animation until serial data arrives
void rainbow() {
  int i;   
  for (i=0; i < strip.numPixels(); i++) {
     strip.setPixelColor(i, Wheel( (i + j) % 96));
  }
  strip.doSwapBuffersAsap(strip.numPixels());
  delay(1);
  
  k++;
  if (k>50) {
    k=0;
    j++;
    if (j>96*3) {  // 3 cycles of all 96 colors in the wheel
       j=0; 
    }
  }
}

//create initial image
void showInitImage() {
    for (int i=0; i < strip.numPixels(); i++) {
      strip.setPixelColor(i, Wheel( i % 96));
    }    
}


// --------------------------------------------
//      setup
// --------------------------------------------
void setup() {
  //pinMode(13, OUTPUT);
  
  memset(serialResonse, 0, SERIALBUFFERSIZE);

  //im your slave and wait for your commands, master!
  Serial.begin(BAUD_RATE); //Setup high speed Serial
  Serial.flush();

  strip.setCPUmax(50);  // start with 50% CPU usage. up this if the strand flickers or is slow

  // Start up the LED counter
  strip.begin();
  
  showInitImage();
  
  // Update the strip, to start they are all 'off'
  strip.show();
  
  serialDataRecv = 0;
}

// --------------------------------------------
//      loop
// --------------------------------------------
void loop() {
  //read the serial port and create a string out of what you read
  g_errorCounter=0;

  // see if we got a proper command string yet
  if (readCommand(serInStr) == 0) {
    if (g_errorCounter!=0 && g_errorCounter!=102) {
      sendAck();
    }
    
    if (serialDataRecv==0) {
    	  rainbow();    	
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
        } else {
	  g_errorCounter=100;
        }
        break;

    case CMD_PING:
    	serialDataRecv = 1;
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

// --------------------------------------------
//    update 32 bytes of the led matrix
//    ofs: which panel, 0, 64, 128...
// --------------------------------------------
void updatePixels(byte ofs, byte* buffer) {
  uint16_t currentLed = ofs*PIXELS_PER_PANEL;
  byte x=0;
  for (byte i=0; i < PIXELS_PER_PANEL; i++) {
    strip.setPixelColor(currentLed, buffer[x]<<8 | buffer[x+1]);
    x+=2;
    currentLed++;
  }  
  strip.doSwapBuffersAsap(ofs*PIXELS_PER_PANEL);   // write all the pixels out
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
/*  i = SERIAL_DELAY_LOOP;
  while (Serial.available() < SERIAL_HEADER_SIZE-1) {   // wait for the rest
    delay(SERIAL_WAIT_DELAY); 
    if (i-- == 0) {
      g_errorCounter = 103;
      return 0;        //no data available!
    }
  }
  for (i=1; i<SERIAL_HEADER_SIZE; i++) {
    str[i] = Serial.read();       // fill it up
  }*/
  i=1;
  b=SERIAL_DELAY_LOOP;
  while (i<SERIAL_HEADER_SIZE) {
    if (Serial.available()) {
      str[i++] = Serial.read();
    } else {
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
/*  i = SERIAL_DELAY_LOOP;
  // wait for the final part, +1 for END_OF_DATA
  while (Serial.available() < sendlen+1) {
    delay(SERIAL_WAIT_DELAY); 
    if( i-- == 0 ) {
      g_errorCounter = 105;
      return 0;
    }
  }
  for (i=SERIAL_HEADER_SIZE; i<SERIAL_HEADER_SIZE+sendlen+1; i++) {
    str[i] = Serial.read();       // fill it up
  }*/
  
  i=0;
  b=SERIAL_DELAY_LOOP;
  while (i<sendlen+1) {
    if (Serial.available()) {
      str[SERIAL_HEADER_SIZE+i++] = Serial.read();
    } else {
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

