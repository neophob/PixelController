
#ifndef _ELEMENT_GFX_H
#define _ELEMENT_GFX_H

#include "Adafruit_GFX.h"

// Remember - Adding panels changes HEIGHT
#define NUMPANELS (2)
#define ELEMENT_H (16 * NUMPANELS)
#define ELEMENT_W (16)
#define ELEMENT_LEDS (ELEMENT_W * ELEMENT_H)
#define ELEMENT_LATCH 27


class Element_GFX : public Adafruit_GFX
{
  public:
  void constructor();
  
  virtual void drawPixel(int16 x, int16 y, uint32 color);
  virtual void drawPixelNum(int16 num, uint32 color);
  virtual void invertDisplay(boolean i);
  
  void sendFrame();
};

uint32 make_color(uint8 r, uint8 g, uint8 b);
uint32 hsv_to_rgb(uint16 hue, uint8 sat, uint8 value);

#endif
