/*
 * PixelInvaders serial-led-gateway v2.0, Copyright (C) 2011-2013 michael vogt <michu@neophob.com>
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

//send debug messages back via serial line
//#define DEBUG 1

//how many pixelinvaders panels are connected?
#define NR_OF_PANELS 2

//Teensy 2.0 has the LED on pin 11.
//Teensy++ 2.0 has the LED on pin 6
//Teensy 3.0 has the LED on pin 13
#define LED_PIN 11

// ======= END OF USER CONFIGURATION ======= 


//to draw a frame we need arround 20ms to send an image. the serial baudrate is
//NOT the bottleneck. 
#define BAUD_RATE 115200

#define PIXELS_PER_PANEL 64

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

//initialize pixels
Neophob_LPD6803 strip = Neophob_LPD6803(NR_OF_PANELS*PIXELS_PER_PANEL);

// --------------------------------------------
//     send status back to library
// --------------------------------------------
static void sendAck() {
  Serial.print("AK PXI");
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

  //first blink: init
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
  
  //second blink: init done
  delay(250);  
  digitalWrite(LED_PIN, HIGH);
  delay(250);
  digitalWrite(LED_PIN, LOW);    
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
  
  uint16_t ofs=0;
  uint16_t ledOffset = PIXELS_PER_PANEL*currentPacket;
  
  for (uint8_t i=0; i < nrOfPixels; i++) {
    strip.setPixelColor(ledOffset++, packetBuffer[ofs]<<8 | packetBuffer[ofs+1]);
    ofs+=2;
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

