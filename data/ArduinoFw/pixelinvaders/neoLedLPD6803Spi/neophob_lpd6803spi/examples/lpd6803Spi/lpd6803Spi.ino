#include <TimerOne.h>
#include <SPI.h>
#include "Neophob_LPD6803.h"

/*
Unlike software SPI which is configurable, hardware SPI works only on very specific pins. 

On the Arduino Uno, Duemilanove, etc., clock = pin 13 and data = pin 11. 
For the Arduino Mega, clock = pin 52, data = pin 51. 
For the ATmega32u4 Breakout Board and Teensy, clock = pin B1, data = B2. 

Alternately, on most boards you can use the 6-pin programming 
header for SPI output as well, in which case clock = pin 3 and data = pin 4.

src: http://forums.adafruit.com/viewtopic.php?f=47&t=24256 
thanks phil!
*/

#define LED_MODULES 20

Neophob_LPD6803 strip = Neophob_LPD6803(LED_MODULES);

void setup() {
  strip.setCPUmax(70);
  strip.begin();
  strip.show();
}


void loop() {
  colorWipe(Color(31, 0, 0), 50);
  colorWipe(Color(0, 31, 0), 50);
  colorWipe(Color(0, 0, 31), 50);
  
  rainbowCycle(50);
  
  rainbow(5);
}


// Slightly different, this one makes the rainbow wheel equally distributed 
// along the chain
void rainbowCycle(uint8_t wait) {
  int i, j;
  
  for (j=0; j < 96 * 5; j++) {     // 5 cycles of all 96 colors in the wheel
    for (i=0; i < strip.numPixels(); i++) {
      // tricky math! we use each pixel as a fraction of the full 96-color wheel
      // (thats the i / strip.numPixels() part)
      // Then add in j which makes the colors go around per pixel
      // the % 96 is to make the wheel cycle around
      strip.setPixelColor(i, Wheel( ((i * 96 / strip.numPixels()) + j) % 96) );
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


void rainbow(uint8_t wait) {
  int i, j;
   
  for (j=0; j < 96 * 3; j++) {     // 3 cycles of all 96 colors in the wheel
    for (i=0; i < strip.numPixels(); i++) {
      strip.setPixelColor(i, Wheel( (i + j) % 96));
    }  
    strip.show();   // write all the pixels out
    delay(wait);
  }
}

// Create a 15 bit color value from R,G,B
unsigned int Color(byte r, byte g, byte b)
{
  //Take the lowest 5 bits of each value and append them end to end
  return( ((unsigned int)g & 0x1F )<<10 | ((unsigned int)b & 0x1F)<<5 | (unsigned int)r & 0x1F);
}


//Input a value 0 to 127 to get a color value.
//The colours are a transition r - g -b - back to r
unsigned int Wheel(byte WheelPos)
{
  byte r,g,b;
  switch(WheelPos >> 5)
  {
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

