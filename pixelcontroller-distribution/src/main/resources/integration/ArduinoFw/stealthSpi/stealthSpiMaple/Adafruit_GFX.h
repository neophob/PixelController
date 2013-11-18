/***********************************
This is a our graphics core library, for all our displays. 
We'll be adapting all the
existing libaries to use this core to make updating, support 
and upgrading easier!

Adafruit invests time and resources providing this open source code, 
please support Adafruit and open-source hardware by purchasing 
products from Adafruit!

Written by Limor Fried/Ladyada  for Adafruit Industries.  
BSD license, check license.txt for more information
All text above must be included in any redistribution
****************************************/

#ifndef _ADAFRUIT_GFX_H
#define _ADAFRUIT_GFX_H

/*#if ARDUINO >= 100
 #include "Arduino.h"
 #include "Print.h"
#else*/
 #include "WProgram.h"
//#endif

#define swap(a, b) { int16 t = a; a = b; b = t; }

class Adafruit_GFX {// : public Print {
 public:

  Adafruit_GFX();
  virtual ~Adafruit_GFX();
  // i have no idea why we have to formally call the constructor. kinda sux
  void constructor(int16 w, int16 h);

  // this must be defined by the subclass
  virtual void drawPixel(int16 x, int16 y, uint32 color) = 0;
  virtual void invertDisplay(boolean i) = 0;

  // these are 'generic' drawing functions, so we can share them!
  virtual void drawLine(int16 x0, int16 y0, int16 x1, int16 y1, 
		uint32 color);
  virtual void drawFastVLine(int16 x, int16 y, int16 h, uint32 color);
  virtual void drawFastHLine(int16 x, int16 y, int16 w, uint32 color);
  virtual void drawRect(int16 x, int16 y, int16 w, int16 h, 
		uint32 color);
  virtual void fillRect(int16 x, int16 y, int16 w, int16 h, 
		uint32 color);
  virtual void fillScreen(uint32 color);

  void drawCircle(int16 x0, int16 y0, int16 r, uint32 color);
  void drawCircleHelper(int16 x0, int16 y0,
			int16 r, uint8 cornername, uint32 color);
  void fillCircle(int16 x0, int16 y0, int16 r, uint32 color);
  void fillCircleHelper(int16 x0, int16 y0, int16 r,
		      uint8 cornername, int16 delta, uint32 color);

  void drawTriangle(int16 x0, int16 y0, int16 x1, int16 y1,
		    int16 x2, int16 y2, uint32 color);
  void fillTriangle(int16 x0, int16 y0, int16 x1, int16 y1,
		    int16 x2, int16 y2, uint32 color);
  void drawRoundRect(int16 x0, int16 y0, int16 w, int16 h,
		     int16 radius, uint32 color);
  void fillRoundRect(int16 x0, int16 y0, int16 w, int16 h,
		     int16 radius, uint32 color);

  void drawBitmap(int16 x, int16 y, 
		  const uint8 *bitmap, int16 w, int16 h,
		  uint32 color);
  void drawChar(int16 x, int16 y, unsigned char c,
		uint32 color, uint32 bg, uint8 size);
//#if ARDUINO >= 100
//  virtual size_t write(uint8);
//#else
  virtual void   write(uint8);
//#endif
  void setCursor(int16 x, int16 y);
  void setTextColor(uint32 c);
  void setTextColor(uint32 c, uint32 bg);
  void setTextSize(uint8 s);
  void setTextWrap(boolean w);

  int16 height(void);
  int16 width(void);

  void setRotation(uint8 r);
  uint8 getRotation(void);

 protected:
  int16  WIDTH, HEIGHT;   // this is the 'raw' display w/h - never changes
  int16  _width, _height; // dependent on rotation
  int16  cursor_x, cursor_y;
  uint32 textcolor, textbgcolor;
  uint8  textsize;
  uint8  rotation;
  boolean  wrap; // If set, 'wrap' text at right edge of display
};

#endif
