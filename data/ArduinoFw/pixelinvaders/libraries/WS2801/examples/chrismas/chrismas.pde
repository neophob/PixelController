#include <SPI.h>

#include "WS2801.h"

/*****************************************************************************
Example sketch for driving WS2801 pixels
by michu@pixelinvaders.ch - www.pixelinvaders.ch
*****************************************************************************/

//TODO: scoll in both directions
//      color matcher

// Choose which 2 pins you will use for output.
// Can be any valid output pins.
// The colors of the wires may be totally different so
// BE SURE TO CHECK YOUR PIXELS TO SEE WHICH WIRES TO USE!
int dataPin = 2;       
int clockPin = 3;      
// Don't forget to connect the ground wire to Arduino ground, and the +5V wire to a +5V supply

static uint8_t DELAY = 50;

struct anim {
  uint8_t ofs;
  uint8_t pos;
  uint8_t del;
  uint8_t length;
  uint32_t col;
};

anim mover;
uint8_t clearColR;
uint8_t clearColG;
uint8_t clearColB;
uint32_t clearCol;

// Set the first variable to the NUMBER of pixels. 25 = 25 pixels in a row
WS2801 strip = WS2801(64, dataPin, clockPin);

void setup() {
  strip.begin();
  
  fadeToNewColor();
  newAnimation();
}


void loop() {
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
  delay(DELAY);
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
  mover.col = Color(random(200), random(200), random(250));  
}

//fade currentbackground color to next, random color
void fadeToNewColor() {
  uint8_t oldR = clearColR;
  uint8_t oldG = clearColG;
  uint8_t oldB = clearColB;

  clearColR = random(70);
  clearColG = random(70);
  clearColB = random(90);
  clearCol = Color(clearColR, clearColG, clearColB);
  
  int steps = 25;
  float stepsR = (clearColR-oldR)/(float)steps;
  float stepsG = (clearColG-oldG)/(float)steps;
  float stepsB = (clearColB-oldB)/(float)steps;

  for (int s=0; s<steps+1; s++) {
    uint8_t rr=oldR+stepsR*s;    
    uint8_t gg=oldG+stepsG*s;
    uint8_t bb=oldB+stepsB*s;
    uint32_t c = Color(rr, gg, bb);
    
    for (int i=0; i < strip.numPixels(); i++) {
      strip.setPixelColor(i, c);
    }
    strip.show(); 
    delay(DELAY);
  }
  
}

// Create a 24 bit color value from R,G,B
uint32_t Color(uint8_t r, uint8_t g, uint8_t b) {
  uint32_t c;
  c = r;
  c <<= 8;
  c |= g;
  c <<= 8;
  c |= b;
  return c;
}



