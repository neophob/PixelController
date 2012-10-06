/*
 * arduino serial-i2c-gateway, Copyright (C) 2010 michael vogt <michu@neophob.com>
 *  
 * based on 
 * -blinkm firmware by thingM
 * -"daft punk" firmware by Scott C / ThreeFN 
 *  
 * libraries to patch:
 * Wire: 
 *  	utility/twi.h: #define TWI_FREQ 400000L (was 100000L)
 *                    #define TWI_BUFFER_LENGTH 98 (was 32)
 *  	wire.h: #define BUFFER_LENGTH 98 (was 32)
 *
 * This file is part of neorainbowduino.
 *
 * neorainbowduino is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * neorainbowduino is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 * 	
 */

#include <Wire.h>
#include "Arduino.h"

extern "C" { 
#include "utility/twi.h"  // from Wire library, so we can do bus scanning
}


//to draw a frame we need arround 20ms to send an image. the serial baudrate is
//NOT the bottleneck. 
#define BAUD_RATE 115200

#define CLEARCOL 51 //00110011

//some magic numberes
#define CMD_START_BYTE  0x01
#define CMD_SENDFRAME 0x03
#define CMD_PING  0x04
#define CMD_INIT_RAINBOWDUINO 0x05
#define CMD_SCAN_I2C_BUS 0x06

//8ms is the minimum!
#define SERIAL_WAIT_TIME_IN_MS 8

//I2C definitions
#define START_OF_DATA 0x10
#define END_OF_DATA 0x20

//Static start+end address for i2x scan
#define START_I2C_SCAN 1
#define END_I2C_SCAN 101

//this should match RX_BUFFER_SIZE from HardwareSerial.cpp
byte serInStr[128]; 	 				 // array that will hold the serial input string

//counter for 2000 frames
//http://www.ftdichip.com/Support/Documents/AppNotes/AN232B-04_DataLatencyFlow.pdf
//there is a 16ms delay until the buffer is full, here are some measurements
//time is round trip time from/to java
//size  errorrate       frames>35ms  time for 2000frames  time/frame  time/frame worstcase
//5  -> rate: 0.0,      long: 156,   totalTime: 44250     22.13ms
//8  -> rate: 5.894106, long: 38,    totalTime: 41184     20.59ms     21.83ms
//16 -> rate: 7.092907, long: 4,     totalTime: 40155     20.07ms     21.48ms
//32 -> rate: 6.943056, long: 5,     totalTime: 39939     19.97ms     21.36ms
//62 -> rate: 22.97702, long: 7,     totalTime: 33739     16.89ms     20.58ms
//64 -> rate: 24.22577, long: 3,     totalTime: 33685     16.84ms     20.89ms
//-> I use 16b - not the fastest variant but more accurate

#define SERIALBUFFERSIZE 16
byte serialResonse[SERIALBUFFERSIZE];

byte g_errorCounter;

//send status back to library
static void sendAck() {
  serialResonse[0] = 'A';
  serialResonse[1] = 'K';
  serialResonse[2] = Serial.available();
  serialResonse[3] = g_errorCounter;  
  Serial.write(serialResonse, 4);

  //Clear bufer
 // Serial.flush();
}


//send an white image to the target rainbowduino
//contains red led's which describe its i2c addr
byte send_initial_image(byte i2caddr) {
  //clear whole buffer
  memset(serInStr, CLEARCOL, 128);

  //draw i2c addr as led pixels
  float tail = i2caddr/2.0f;
  byte tail2 = (byte)(tail);
  boolean useTail = (tail-(byte)(tail))!=0;			

  //buffer layout: 32b RED, 32b GREEN, 32b BLUE
  byte ofs=0;
  for (byte i=0; i<tail2; i++) {
    serInStr[ofs++]=255;
  }
  if (useTail) {
    serInStr[ofs++]=243;
  }
  
  return BlinkM_sendBuffer(i2caddr, serInStr);
}

// ripped from http://todbot.com/arduino/sketches/I2CScanner/I2CScanner.pde
// Scan the I2C bus between addresses from_addr and to_addr.
// On each address, call the callback function with the address and result.
// If result==0, address was found, otherwise, address wasn't found
// (can use result to potentially get other status on the I2C bus, see twi.c)
// Assumes Wire.begin() has already been called
// HINT: maximal 14 devices can be scanned!
void scanI2CBus() {
  memset(serialResonse, 255, SERIALBUFFERSIZE);
  serialResonse[0] = CMD_START_BYTE;
  serialResonse[1] = CMD_SCAN_I2C_BUS;

  byte rc,i=2;
  byte data = 0; // not used, just an address to feed to twi_writeTo()
  for (byte addr = START_I2C_SCAN; addr <= END_I2C_SCAN; addr++) {
  //rc 0 = success
    digitalWrite(13, HIGH);
    rc = twi_writeTo(addr, &data, 0, 1);
    digitalWrite(13, LOW);
    if (rc==0) {
      serialResonse[i]=addr;
      if (i<SERIALBUFFERSIZE) i++;
    }
    delayMicroseconds(64);
  }
  Serial.write(serialResonse, SERIALBUFFERSIZE);
  memset(serialResonse, 0, SERIALBUFFERSIZE);
}


void setup() {
  Wire.begin(1); // join i2c bus (address optional for master)
  
  pinMode(13, OUTPUT);
  memset(serialResonse, 0, SERIALBUFFERSIZE);

  //im your slave and wait for your commands, master!
  Serial.begin(BAUD_RATE); //Setup high speed Serial
  Serial.flush();
}


void loop() {
  //read the serial port and create a string out of what you read
  g_errorCounter=0;

  digitalWrite(13, LOW);
  // see if we got a proper command string yet
  if (readCommand(serInStr) == 0) {
    //no valid data found
    //sleep for 250us
    delayMicroseconds(250);
    return;
  }
  
  digitalWrite(13, HIGH);
  
  //i2c addres of device
  byte addr    = serInStr[1];
  //how many bytes we're sending
  byte sendlen = serInStr[2];
  //what kind of command we send
  byte type = serInStr[3];
  //parameter
  byte* cmd    = serInStr+5;

  switch (type) {
    case CMD_SENDFRAME:
    	//the size of an image must be exactly 96 bytes
        if (sendlen!=96) {
          g_errorCounter=100;
        } else {
          g_errorCounter = BlinkM_sendBuffer(addr, cmd);    
        }
        break;
    case CMD_PING:
        //just send the ack!
        break;
    case CMD_INIT_RAINBOWDUINO:
        //send initial image to rainbowduino
        g_errorCounter = send_initial_image(addr);
        break;
    case CMD_SCAN_I2C_BUS:
    	scanI2CBus();
    	break;
    default:
        //invalid command
        g_errorCounter=130; 
        break;
  }
        
  //send ack to library - command processed
  sendAck();
    
}



//send data via I2C to a client
static byte BlinkM_sendBuffer(byte addr, byte* cmd) {
    Wire.beginTransmission(addr);
    Wire.send(START_OF_DATA);
    Wire.send(cmd, 96);
    Wire.send(END_OF_DATA);
    return Wire.endTransmission();
}


//read a string from the serial and store it in an array
//you must supply the str array variable
//returns number of bytes read, or zero if fail
/* example ping command:
		cmdfull[0] = START_OF_CMD (marker);
		cmdfull[1] = addr;
		cmdfull[2] = 0x01; 
		cmdfull[3] = CMD_PING;
		cmdfull[4] = START_OF_DATA (marker);
		cmdfull[5] = 0x02;
		cmdfull[6] = END_OF_DATA (marker);
*/
#define HEADER_SIZE 5
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
    //failed to get data
    g_errorCounter=101;
    return 0;    
  }

//read header  
  i = SERIAL_WAIT_TIME_IN_MS;
  while (Serial.available() < HEADER_SIZE-1) {   // wait for the rest
    delay(1); 
    if (i-- == 0) {
      g_errorCounter=102;
      return 0;        // get out if takes too long
    }
  }
  for (i=1; i<HEADER_SIZE; i++) {
    str[i] = Serial.read();       // fill it up
  }
  
// --- START HEADER CHECK    
  //check if data is correct, 0x10 = START_OF_DATA
  if (str[4] != START_OF_DATA) {
    g_errorCounter=104;
    return 0;
  }
  
  //check sendlen, its possible that sendlen is 0!
  sendlen = str[2];
// --- END HEADER CHECK

  
//read data  
  i = SERIAL_WAIT_TIME_IN_MS;
  // wait for the final part, +1 for END_OF_DATA
  while (Serial.available() < sendlen+1) {
    delay(1); 
    if( i-- == 0 ) {
      g_errorCounter=105;
      return 0;
    }
  }

  for (i=HEADER_SIZE; i<HEADER_SIZE+sendlen+1; i++) {
    str[i] = Serial.read();       // fill it up
  }

  //check if data is correct, 0x20 = END_OF_DATA
  if (str[HEADER_SIZE+sendlen] != END_OF_DATA) {
    g_errorCounter=106;
    return 0;
  }

  //return data size (without meta data)
  return sendlen;
}


