/*
 * Element Stealth LED Panel serial-led-gateway, by Steven Noreyko and Christian Miller
 * Based on PixelInvaders gateway Copyright (C) 2011 michael vogt <michu@neophob.com>
 * Tested on Teensy
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	 See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *	
 */

#include <TimerOne.h>
#include "SPI.h"
#include "element_GFX.h"

Element_GFX panel;


// LED panel output pins
int led_latch = 24; // latch pin is 24/B4 = PB4 on Teensy
//int numLeds = 256;

#define PANEL_SIZE 256
#define NUM_PANELS 1


//to draw a frame we need arround 20ms to send an image. the serial baudrate is
//NOT the bottleneck. 
//#define BAUD_RATE 230400//
#define BAUD_RATE 115200

//--- protocol data start
#define CMD_START_BYTE 0x01
#define CMD_SENDFRAME 0x03
#define CMD_PING	0x04

#define START_OF_DATA 0x10 
#define END_OF_DATA 0x20

//frame size for specific color resolution
#define COLOR_5BIT_FRAME_SIZE 96
#define SERIAL_HEADER_SIZE 5
//--- protocol data end

//8ms is the minimum! else we dont get any data!
#define SERIAL_DELAY_LOOP 3
#define SERIAL_WAIT_DELAY 3

//define nr of Panels*2 here, 4 means 2 panels
#define NR_OF_PANELS 4
#define PIXELS_PER_PANEL 32

//this should match RX_BUFFER_SIZE from HardwareSerial.cpp
//array that will hold the serial input string
byte serInStr[COLOR_5BIT_FRAME_SIZE+SERIAL_HEADER_SIZE];					 

#define SERIALBUFFERSIZE 4
byte serialResonse[SERIALBUFFERSIZE];

// Set up some globals
byte g_errorCounter;

int j=0,k=0;
byte serialDataRecv;
uint16_t tmpBits;


// --------------------------------------------
//		 send status back to library
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

// --------------------------------------------
//		 Input a value 0 to 127 to get a color value.
//		 The colours are a transition r - g -b - back to r
// --------------------------------------------

// --------------------------------------------
//		 do some animation until serial data arrives
// --------------------------------------------

// rainbow colorwheel


// --------------------------------------------
//		 create initial image
// --------------------------------------------


// --------------------------------------------
//			setup
// --------------------------------------------
void setup() {
	SPI.begin();
	panel.constructor();
	panel.setRotation(0); // options are 0, 1, 2, 3

	memset(serialResonse, 0, SERIALBUFFERSIZE);

	//im your slave and wait for your commands, master!
	Serial.begin(BAUD_RATE); //Setup high speed Serial
	Serial.flush();

	//cpu use and SPI clock must be adjusted
//	strip.setCPUmax(50);	// start with 50% CPU usage. up this if the strand flickers or is slow	
//	strip.begin(SPI_CLOCK_DIV128);				// Start up the LED counterm 0.25MHz - 8uS
//	strip.begin(SPI_CLOCK_DIV64);				 // Start up the LED counterm 0.25MHz - 4uS
//	strip.begin(SPI_CLOCK_DIV32);				 // Start up the LED counterm 0.5MHz - 2uS
//	strip.begin(SPI_CLOCK_DIV16);				 // Start up the LED counterm 1.0MHz - 1uS
//	showInitImage();			// display some colors

	panel.fillScreen(make_color(64, 0, 0)); // flash red to show update is good
	panel.sendFrame();
	
	serialDataRecv = 0;		//no serial data received yet	 
}

// --------------------------------------------
//			main loop
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
			// rainbow();
			panel.fillScreen(make_color(0, 0, 0)); // blackout
			panel.sendFrame();
			
		}
		return;
	}

	//led bye offset address
	byte ofs		= serInStr[1];
	//how many bytes we're sending
	byte sendlen = serInStr[2];
	//what kind of command we send
	byte type = serInStr[3];
	//get the image data
	byte* cmd		 = serInStr+5;

	switch (type) {
	case CMD_SENDFRAME:
		//the size of an image must be exactly 96 bytes
		if (sendlen == COLOR_5BIT_FRAME_SIZE) {
			updatePixels(ofs, cmd);
			panel.sendFrame(); 
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
//		update 32 bytes of the led matrix
//		ofs: which panel, 0 (ofs=0), 1 (ofs=32), 2 (ofs=64)...
// --------------------------------------------

void updatePixels(byte ofs, byte* buffer) {
	int i, j, num;
	uint16_t currentLed = ofs*PIXELS_PER_PANEL; // this is offset * amount of bytes of data in each packet
	byte x=0;

	// draw 32 pixels per ofs packet chunk
	for (byte i=0; i < PIXELS_PER_PANEL; i++) {	
		//convert buffer to bytes
		byte rz = buffer[x];
		byte gz = buffer[x+1];
		byte bz = buffer[x+2];
		panel.drawPixelNum(currentLed, make_color(rz, gz, bz));
		x+=3;
		currentLed++;
	}	 
/*
	for (byte i=0; i < PIXELS_PER_PANEL; i++) {	
			//get 15 bit color
			tmpBits = buffer[x] << 8 | buffer[x+1];
			//convert it to 24 bit per color
			byte bz = tmpBits & 0x1F;
			byte gz = (tmpBits >> 5) & 0x1F;
			byte rz = (tmpBits >> 10) & 0x1F;
		panel.drawPixelNum(currentLed, make_color(rz<<2, gz<<2, bz<<2));
		x+=2;
		currentLed++;
	}	 
*/
/*	//this is working on 8x8 grid
		if (ofs == 0){
			for (i = 0; i < panel.width()/4; i++) {
				for (j = 0; j < panel.height()/2; j++){
					//get 15 bit color
					tmpBits = buffer[x] << 8 | buffer[x+1];
					//convert it to 24 bit per color
					byte bz = tmpBits & 0x1F;
					byte gz = (tmpBits >> 5) & 0x1F;
					byte rz= (tmpBits >> 10) & 0x1F;
					panel.drawPixel(i, j, make_color(rz<<3, gz<<3, bz<<3));
					x+=2;
				}
			}
		}
		if (ofs == 1){
			for (i = panel.width()/4; i < panel.width()/2; i++) {
				for (j = 0; j < panel.height()/2; j++){
					//get 15 bit color
					tmpBits = buffer[x] << 8 | buffer[x+1];
					//convert it to 24 bit per color
					byte bz = tmpBits & 0x1F;
					byte gz = (tmpBits >> 5) & 0x1F;
					byte rz= (tmpBits >> 10) & 0x1F;
					panel.drawPixel(i, j, make_color(rz<<3, gz<<3, bz<<3));
					x+=2;
				}
			}
		}
	}
*/

	
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
	unsigned char bytecount = 0;
	while (i<SERIAL_HEADER_SIZE) {
		if (Serial.available()) {
			str[i++] = Serial.read();
			bytecount++;
		} 
		else {
			delay(SERIAL_WAIT_DELAY); 
			if (b-- == 0) {
				g_errorCounter = 103;
				return 0;				 //no data available!
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
	unsigned char databytecount = 0;
	while (i<sendlen+1) {
		if (Serial.available() && databytecount < 30 ) {
			str[SERIAL_HEADER_SIZE+i++] = Serial.read();
		} 
		else {
			delay(SERIAL_WAIT_DELAY); 
			if (b-- == 0) {
				g_errorCounter = 105;
				return 0;				 //no data available!
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