/*
 * PixelInvaders serial-led-gateway, Copyright (C) 2011-2013 michael vogt <michu@neophob.com>
 * Tested on Teensy and Arduino.
 *
 * This is the firmware you should use if you bought a PixelInvaders DIY Kit!
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
#include <Neophob_LPD6803.h>

// ======= START OF USER CONFIGURATION =======

//define nr of Panels*2 here, 4 means 2 panels
#define NR_OF_PANELS 4

// ======= END OF USER CONFIGURATION ======= 

#define PIXELS_PER_PANEL 32

//to draw a frame we need arround 20ms to send an image. the serial baudrate is
//NOT the bottleneck. 
#define BAUD_RATE 115200

//--- protocol data start
#define CMD_START_BYTE 0x01
#define CMD_SENDFRAME 0x03
#define CMD_PING  0x04
#define CMD_CONNECTION_CLOSED 0x05

#define START_OF_DATA 0x10 
#define END_OF_DATA 0x20

//frame size for specific color resolution
//32pixels * 2 byte per color (15bit - one bit wasted)
#define COLOR_5BIT_FRAME_SIZE 64
#define SERIAL_HEADER_SIZE 5
#define SERIAL_FOOTER_SIZE 1
#define SERIAL_PACKET_SIZE (COLOR_5BIT_FRAME_SIZE+SERIAL_HEADER_SIZE+SERIAL_FOOTER_SIZE)
//--- protocol data end

//8ms is the minimum! else we dont get any data!
#define SERIAL_DELAY_LOOP 3
#define SERIAL_WAIT_DELAY 3

//this should match RX_BUFFER_SIZE from HardwareSerial.cpp
//array that will hold the serial input string
uint8_t serInStr[SERIAL_PACKET_SIZE];

//initialize pixels
Neophob_LPD6803 strip = Neophob_LPD6803(NR_OF_PANELS*PIXELS_PER_PANEL);

#define SERIALBUFFERSIZE 4
uint8_t serialResonse[SERIALBUFFERSIZE];

uint8_t g_errorCounter;

int j=0,k=0;
uint8_t serialDataRecv;


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


// --------------------------------------------
//      setup
// --------------------------------------------
void setup() {
  memset(serialResonse, 0, SERIALBUFFERSIZE);

  //im your slave and wait for your commands, master!
  Serial.begin(BAUD_RATE); //Setup high speed Serial
  Serial.flush();
  Serial.setTimeout(8);

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
      rainbow();	
    }
    return;
  }

  //led offset
  uint8_t ofs = serInStr[1];
  //how many bytes we're sending
  uint8_t sendlen = serInStr[2];
  //what kind of command we send
  uint8_t type = serInStr[3];
  //get the image data
  uint8_t* cmd = serInStr+5;

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

  case CMD_CONNECTION_CLOSED:
    //pixelcontroller just stopped, display the rainbow!
    serialDataRecv = 0;
    Serial.flush();
    rainbow();
    return;

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
void updatePixels(uint8_t ofs, uint8_t* buffer) {
  uint16_t currentLed = PIXELS_PER_PANEL;

  currentLed *= ofs;
  uint8_t x=0;
  for (uint8_t i=0; i < PIXELS_PER_PANEL; i++) {
    strip.setPixelColor(currentLed++, buffer[x]<<8 | buffer[x+1]);
    x+=2;
  }  
  
  //update panel only if the whole panel was updated
  if (ofs%2==1) {
    strip.show();   // write all the pixels out
  }
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

uint8_t readCommand(byte *str) {
  uint8_t recvNr = Serial.readBytes((char*)str, SERIAL_PACKET_SIZE);
  if (recvNr==0) {
    g_errorCounter = 102;
    return 0;        //no data available!    
  }

  //check header
  if (str[4] != START_OF_DATA) {
    g_errorCounter = 104;
    return 0;
  }
  
  uint8_t sendlen = str[2];  
  
  //check footer
  if (str[SERIAL_HEADER_SIZE+sendlen] != END_OF_DATA) {
    g_errorCounter = 106;
    return 0;
  }

  if (sendlen>recvNr) {
    g_errorCounter = 109;
    return 0;
  }

  return sendlen;
}


