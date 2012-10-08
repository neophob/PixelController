
#ifndef _ELEMENT_GFX_H
#define _ELEMENT_GFX_H

#include "Adafruit_GFX.h"

// Remember - Adding panels changes HEIGHT
#define NUMPANELS (2)
#define ELEMENT_H (16 * NUMPANELS)
#define ELEMENT_W (16)
#define ELEMENT_LEDS (ELEMENT_W * ELEMENT_H)
#define ELEMENT_LATCH 24


class Element_GFX : public Adafruit_GFX
{
  public:
  void constructor();
  
  void transfer(uint8_t data);
  
  virtual void drawPixel(int16_t x, int16_t y, uint32_t color);
  virtual void drawPixelNum(int16_t num, uint32_t color);
  virtual void invertDisplay(boolean i);
  
  void sendFrame();
};

uint32_t make_color(uint8_t r, uint8_t g, uint8_t b);
uint32_t hsv_to_rgb(uint16_t hue, uint8_t sat, uint8_t value);

#endif
