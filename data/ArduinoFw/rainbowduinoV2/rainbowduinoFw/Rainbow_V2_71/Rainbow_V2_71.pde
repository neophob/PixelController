/*
 * rainbowduino firmware, Copyright (C) 2010-2011 michael vogt <michu@neophob.com>
 *  
 * based on 
 * -blinkm firmware by thingM
 * -"daft punk" firmware by Scott C / ThreeFN 
 * -rngtng firmware by Tobias Bielohlawek -> http://www.rngtng.com
 *  
 * needed libraries:
 * -FlexiTimer (http://github.com/wimleers/flexitimer2)
 *  
 * libraries to patch:
 * Wire: 
 *  	utility/twi.h: #define TWI_FREQ 400000L (was 100000L)
 *                     #define TWI_BUFFER_LENGTH 98 (was 32)
 *  	wire.h: #define BUFFER_LENGTH 98 (was 32)
 *
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
#include <FlexiTimer2.h>

#include "Rainbow.h"

/*
A variable should be declared volatile whenever its value can be changed by something beyond the control 
 of the code section in which it appears, such as a concurrently executing thread. In the Arduino, the 
 only place that this is likely to occur is in sections of code associated with interrupts, called an 
 interrupt service routine.
 */

extern unsigned char buffer[2][96];  //two buffers (backbuffer and frontbuffer)

//interrupt variables
byte g_line,g_level;

//read from bufCurr, write to !bufCurr
//volatile   //the display is flickerling, brightness is reduced
byte g_bufCurr;

//flag to blit image
volatile byte g_swapNow;
byte g_circle;

//data marker
#define START_OF_DATA 0x10
#define END_OF_DATA 0x20

//FPS
#define FPS 80.0f

#define BRIGHTNESS_LEVELS 16
#define LED_LINES 8
#define CIRCLE BRIGHTNESS_LEVELS*LED_LINES

void setup() {
  DDRD=0xff;        // Configure ports (see http://www.arduino.cc/en/Reference/PortManipulation): digital pins 0-7 as OUTPUT
  DDRC=0xff;        // analog pins 0-5 as OUTPUT
  DDRB=0xff;        // digital pins 8-13 as OUTPUT
  PORTD=0;          // Configure ports data register (see link above): digital pins 0-7 as READ
  PORTB=0;          // digital pins 8-13 as READ

  g_level = 0;
  g_line = 0;
  g_bufCurr = 0;
  g_swapNow = 0; 
  g_circle = 0;

  Wire.begin(I2C_DEVICE_ADDRESS); // join i2c bus as slave
  Wire.onReceive(receiveEvent);   // define the receive function for receiving data from master
  // Keep in mind:
  // While an interrupt routine is running, all other interrupts are blocked. As a result, timers will not work 
  // in interrupt routines and other functionality may not work as expected
  // -> if i2c data is receieved our led update timer WILL NOT WORK for a short time, the result
  // are display errors!

  //redraw screen 80 times/s
  FlexiTimer2::set(1, 1.0f/(128.f*FPS), displayNextLine);
  FlexiTimer2::start();                            //start interrupt code
}

//the mainloop - try to fetch data from the i2c bus and copy it into our buffer
void loop() {
  if (Wire.available()>97) { 
    
    byte b = Wire.receive();
    if (b != START_OF_DATA) {
      //handle error, read remaining data until end of data marker (if available)
      while (Wire.available()>0 && Wire.receive()!=END_OF_DATA) {}      
      return;
    }

    byte backbuffer = !g_bufCurr;
    b=0;
    //read image data (payload) - an image size is exactly 96 bytes
    while (b<96) { 
      buffer[backbuffer][b++] = Wire.receive();  //recieve whatever is available
    }

    //read end of data marker
    if (Wire.receive()==END_OF_DATA) {
        //set the 'we need to blit' flag
  	g_swapNow = 1;
    } 
  }
}



//=============HANDLERS======================================

//get data from master - HINT: this is a ISR call!
//HINT2: do not handle stuff here!! this will NOT work
//collect only data here and process it in the main loop!
void receiveEvent(int numBytes) {
  //do nothing here
}


//============INTERRUPTS======================================

// shift out led colors and swap buffer if needed (back buffer and front buffer) 
// function: draw whole image for brightness 0, then for brightness 1... this will 
//           create the brightness effect. 
//           so this interrupt needs to be called 128 times to draw all pixels (8 lines * 16 brightness levels) 
//           using a 10khz resolution means, we get 10000/128 = 78.125 frames/s
// TODO: try to implement an interlaced update at the same rate. 
void displayNextLine() { 
  draw_next_line();									// scan the next line in LED matrix level by level. 
  g_line+=2;	 								        // process all 8 lines of the led matrix 
  if(g_line==LED_LINES) {
    g_line=1;
  }
  if(g_line>LED_LINES) {								// when have scaned all LED's, back to line 0 and add the level 
    g_line=0; 
    g_level++;										// g_level controls the brightness of a pixel. 
    if (g_level>=BRIGHTNESS_LEVELS) {							// there are 16 levels of brightness (4bit) * 3 colors = 12bit resolution
      g_level=0; 
    } 
  }
  g_circle++;
  
  if (g_circle==CIRCLE) {							// check end of circle - swap only if we're finished drawing a full frame!

    if (g_swapNow==1) {
      g_swapNow = 0;
      g_bufCurr = !g_bufCurr;
    }
    g_circle = 0;
  }
}


// scan one line, open the scaning row
void draw_next_line() {
  DISABLE_OE						//disable MBI5168 output (matrix output blanked)
  //enable_row();				                //setup super source driver (trigger the VCC power lane)
  CLOSE_ALL_LINE					//super source driver, select all outputs off
  open_line(g_line);

  LE_HIGH							//enable serial input for the MBI5168
  shift_24_bit();	// feed the leds
  LE_LOW							//disable serial input for the MBI5168, latch the data
  
  ENABLE_OE							//enable MBI5168 output
}

//open correct output pins, used to setup the "super source driver"
//PB0 - VCC3, PB1 - VCC2, PB2 - VCC1
//PD3 - VCC8, PD4 - VCC7, PD5 - VCC6, PD6 - VCC5, PD7 - VCC4
void enable_row() {
  if (g_line < 3) {    // Open the line and close others
    PORTB = (PINB & 0xF8) | 0x04 >> g_line;
    PORTD =  PIND & 0x07;
  } else {
    PORTB =  PINB & 0xF8;
    PORTD = (PIND & 0x07) | 0x80 >> (g_line - 3);
  }
}

// display one line by the color level in buffer
void shift_24_bit() { 
  byte color,row,data0,data1,ofs; 

  for (color=0;color<3;color++) {	           	//Color format GRB
    ofs = color*32+g_line*4;				//calculate offset, each color need 32bytes
    			
    for (row=0;row<4;row++) {    
      
      data1=buffer[g_bufCurr][ofs]&0x0f;                //get pixel from buffer, one byte = two pixels
      data0=buffer[g_bufCurr][ofs]>>4;
      ofs++;

      if(data0>g_level) { 	//is current pixel visible for current level (=brightness)
        SHIFT_DATA_1		//send high to the MBI5168 serial input (SDI)
      } 
      else {
        SHIFT_DATA_0		//send low to the MBI5168 serial input (SDI)       
      }
      CLK_RISING		//send notice to the MBI5168 that serial data should be processed 

      if(data1>g_level) {
        SHIFT_DATA_1		//send high to the MBI5168 serial input (SDI)
      } 
      else {
        SHIFT_DATA_0		//send low to the MBI5168 serial input (SDI)
      }
      CLK_RISING		//send notice to the MBI5168 that serial data should be processed
    }     
  }
}

void open_line(unsigned char line) {    // open the scaning line 
  switch(line) {
  case 0: {
      open_line0
      break;
    }
  case 1: {
      open_line1
      break;
    }
  case 2: {
      open_line2
      break;
    }
  case 3: {
      open_line3
      break;
    }
  case 4: {
      open_line4
      break;
    }
  case 5: {
      open_line5
      break;
    }
  case 6: {
      open_line6
      break;
    }
  case 7: {
      open_line7
      break;
    }
  }
}
