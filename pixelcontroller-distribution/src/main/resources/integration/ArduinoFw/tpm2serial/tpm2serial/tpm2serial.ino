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
#define LED_PIN 13

// The ouput pin the LEDs are connected to.
#define OUTPUT_PIN 11

//---- END USER CONFIG ----

#define BAUD_RATE 115200

//define some TPM constants
#define TPM2NET_HEADER_SIZE 6
#define TPM2NET_HEADER_IDENT 0x9C
#define TPM2NET_CMD_DATAFRAME 0xDA
#define TPM2NET_CMD_COMMAND 0xC0
#define TPM2NET_CMD_ANSWER 0xAA
#define TPM2NET_FOOTER_IDENT 0x36

//3 byte per pixel or 24bit (RGB)
#define BPP 3

//package size we expect. 
#define MAX_PACKED_SIZE 520

#define PIXELS_PER_PACKET 170

// buffers for receiving and sending data
uint8_t frameData[MAX_PACKED_SIZE]; //buffer to hold incoming frame data
uint16_t frameSize;
uint8_t currentPacket;
uint8_t totalPacket;

CRGB leds[NUM_LEDS];

void setup() {  
  Serial.begin(BAUD_RATE);
  Serial.flush();
  Serial.setTimeout(20);
#ifdef DEBUG  
  Serial.println("HI");
#endif 

  pinMode(LED_PIN, OUTPUT);
  debugBlink(500);

  memset(frameData, 0, MAX_PACKED_SIZE);

  LEDS.addLeds<WS2811, OUTPUT_PIN, RGB>(leds, NUM_LEDS);  //Connect NUM_LEDS on pin OUTPUT_PIN

  //Flickering issues?
  //...it turned out that as my PSU got hotter, the voltage was dropping towards the end of the LED strip.
  //Tried feeding power to both ends of the strip, only delayed the issue slightly.  Changed to a bigger PSU and fault went away 
  //(dual ends feeding power).

  // For safety (to prevent too high of a power draw), the test case defaults to
  // setting brightness to 50% brightness  
  LEDS.setBrightness(128);

  showInitImage(); // Display a default blank image
}

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
      // Set all LEDs to red if bad data is received, lower the framerate to avoid.
      // TODO: Additional debugging needed to confirm the source of the bug.
      showError();
      Serial.print("ERR: ");
      Serial.println(res, DEC);
#if defined (CORE_TEENSY_SERIAL)          
      Serial.send_now();
#endif
    }
  }
#endif  
}

/*
 * Update all Pixels using the frameData.
 */
void updatePixels() {
  uint8_t nrOfPixels = frameSize/3;

  uint16_t ofs=0;
  uint16_t ledOffset = PIXELS_PER_PACKET * currentPacket;

  for (uint16_t i=0; i<nrOfPixels; i++) {
    leds[i+ledOffset] = CRGB(frameData[ofs], frameData[ofs+1], frameData[ofs+2]);
    ofs = ofs + 3;
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

/*
 * Read the next command packet from the serial port.
 */
int16_t readCommand() {  
  int startChar = serialWaitRead();
  if (startChar != TPM2NET_HEADER_IDENT) {
    return -1;
  }
  int tpmCommand = serialWaitRead();
  if (tpmCommand != TPM2NET_CMD_DATAFRAME) {
    return -2;
  }

  // Get both 8 bit values for frameSize and assemble into a 16 bit integer.
  int s1 = serialWaitRead();
  int s2 = serialWaitRead();
  frameSize = (s1 << 8) + s2;
  if (frameSize < 6 || frameSize > MAX_PACKED_SIZE) {
    return -3;
  }

  currentPacket = serialWaitRead();
  totalPacket = serialWaitRead();

  // Get the complete frame data
  uint16_t byteCount = Serial.readBytes((char *)frameData, frameSize);
  if (byteCount != frameSize) {
    return -5;
  }

  int endChar = serialWaitRead();
  if (endChar != TPM2NET_FOOTER_IDENT) {
    return -6;
  }

  return frameSize;
}

/*
 * Read a byte from Serial, but block until a byte is received to avoid Ardiuno latency problems.
 */
int serialWaitRead() {
  int inByte = Serial.read();

  // Loop until a byte is read.
  while (inByte == -1) {
    inByte = Serial.read();
  }

  return inByte;
}

/*
 * Create initial image.
 */
void showInitImage() {
  for (int i = 0 ; i < NUM_LEDS; i++) {
    leds[i] = CRGB(i&255, (i>>1)&255, (i>>2)&255);
  }
  LEDS.show();
}

/*
 * Blink the LED_PIN, generally used to confirm the connection.
 */
void debugBlink(uint8_t t) {
  digitalWrite(LED_PIN, HIGH);
  delay(t);
  digitalWrite(LED_PIN, LOW);
}

/*
 * Change all LEDs to red to represent an error status.
 */
void showError() {
  for (int i = 0 ; i < NUM_LEDS; i++) {
    leds[i] = CRGB(50, 0, 0);
  }
  LEDS.show();
}

