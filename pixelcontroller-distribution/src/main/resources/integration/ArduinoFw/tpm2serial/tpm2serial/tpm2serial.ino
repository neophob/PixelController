/*
 * PixelInvaders tpm2.net implementation, Copyright (C) 2013 michael vogt <michu@neophob.com>
 * 
 * If you like this, make sure you check out http://www.pixelinvaders.ch
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
 *
 */

#include <FastSPI_LED2.h>

//---- START USER CONFIG ----

#define DEBUG 1

//how many led pixels are connected
#define NUM_LEDS 256

// Teensy 3.0 and Arduino Uno have the LED on pin 13
#define LED_PIN = 13;

// The ouput pin the LEDs are connected to.
#define OUTPUT_PIN 6

//---- END USER CONFIG ----

#define BAUD_RATE 115200

//define some TPM constants
#define TPM2NET_HEADER_SIZE 6
#define TPM2NET_HEADER_IDENT 0x9c
#define TPM2NET_CMD_DATAFRAME 0xda
#define TPM2NET_CMD_COMMAND 0xc0
#define TPM2NET_CMD_ANSWER 0xaa
#define TPM2NET_FOOTER_IDENT 0x36

//3 byte per pixel or 24bit (RGB)
#define BPP 3

//package size we expect. 
#define MAX_PACKED_SIZE 520

#define PIXELS_PER_PACKET 170

// buffers for receiving and sending data
uint8_t packetBuffer[MAX_PACKED_SIZE]; //buffer to hold incoming packet
uint16_t frameSize;
uint8_t currentPacket;
uint8_t totalPacket;

CRGB leds[NUM_LEDS];

//********************************
// SETUP
//********************************
void setup() {  
  Serial.begin(BAUD_RATE);
  Serial.flush();
  Serial.setTimeout(20);
#ifdef DEBUG  
  Serial.println("HI");
#endif 

  pinMode(LED_PIN, OUTPUT);
  debugBlink(500);

  memset(packetBuffer, 0, MAX_PACKED_SIZE);

  LEDS.addLeds<WS2811, OUTPUT_PIN, RGB>(leds, NUM_LEDS);  //Connect NUM_LEDS on pin OUTPUT_PIN
  
  //Flickering issues?
  //...it turned out that as my PSU got hotter, the voltage was dropping towards the end of the LED strip.
  //Tried feeding power to both ends of the strip, only delayed the issue slightly.  Changed to a bigger PSU and fault went away 
  //(dual ends feeding power).

  // For safety (to prevent too high of a power draw), the test case defaults to
  // setting brightness to 50% brightness  
  LEDS.setBrightness(64);
  
  showInitImage(); // Display a default blank image
}

//********************************
// LOOP
//********************************
void loop() {  
  int16_t res = readCommand();
  if (res > 0) {
#ifdef DEBUG      
    Serial.print("FINE: ");
    Serial.print(frameSize, DEC);
    Serial.print("/");
    Serial.print(currentPacket, DEC);    
#if defined (CORE_TEENSY_SERIAL)    
    Serial.send_now();
#endif

#endif
    digitalWrite(LED_PIN, HIGH);
    updatePixels();
    digitalWrite(LED_PIN, LOW);
  }
#ifdef DEBUG      
  else {
    if (res!=-1) {
      Serial.print("ERR: ");
      Serial.println(res, DEC);
#if defined (CORE_TEENSY_SERIAL)          
      Serial.send_now();
#endif
      showError();
    }
  }
#endif  
}

//********************************
// UPDATE PIXELS
//********************************
void updatePixels() {
  uint8_t nrOfPixels = frameSize/3;
  
  uint16_t ofs=0;
  uint16_t ledOffset = PIXELS_PER_PACKET * currentPacket;
  
  for (uint16_t i=0; i<nrOfPixels; i++) {
    leds[i+ledOffset] = CRGB(packetBuffer[ofs++], packetBuffer[ofs++], packetBuffer[ofs++]);    
  }
  
  //update only if all data packets received
  if (currentPacket == totalPacket-1) {
#ifdef DEBUG      
    Serial.println("DRAW!");
#if defined (CORE_TEENSY_SERIAL)        
    Serial.send_now();
#endif    
#endif    
    LEDS.show();
  } else {
#ifdef DEBUG     
    Serial.print("NOTUPDATE: ");
    Serial.println(currentPacket, DEC);
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
  
  uint8_t tpmCommand = Serial.read();
  if (tpmCommand != TPM2NET_CMD_DATAFRAME) {
    return -2;  
  }
  
  // Get both 8 bit values for frameSize and assemble into a 16 bit integer.
  uint8_t s1 = Serial.read();
  uint8_t s2 = Serial.read();  
  frameSize = (s1<<8) + s2;
  if (frameSize < 6 || frameSize > MAX_PACKED_SIZE) {
    return -3;
  }  

  currentPacket = Serial.read();  
  totalPacket = Serial.read();    
  
  //get remaining bytes
  uint16_t recvNr = Serial.readBytes((char *)packetBuffer, frameSize);
  if (recvNr != frameSize) {
    return -5;
  }  

  uint8_t endChar = Serial.read();
  if (endChar != TPM2NET_FOOTER_IDENT) {
    return -6;
  }

  return frameSize;
}


//********************************
//     create initial image
//********************************
void showInitImage() {
  for (int i = 0 ; i < NUM_LEDS; i++) {
    leds[i] = CRGB(10, 10, 10);
  }
  LEDS.show();
}

void debugBlink(uint8_t t) {
  digitalWrite(LED_PIN, HIGH);
  delay(t);
  digitalWrite(LED_PIN, LOW);
}

void showError() {
  for (int i = 0 ; i < NUM_LEDS; i++) {
    leds[i] = CRGB(50, 0, 0);
  }
  LEDS.show();
}

