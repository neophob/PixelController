/*********************************************************************************/
// Example to control LPD6803-based RGB LED Modules in a strand or strip via SPI
// by Michael Vogt / http://pixelinvaders.ch
// v2.0 - 
// This Library is bsed on work 
// of Adafruit-WS2801-Library and FastSPI Library 
/*********************************************************************************/

#include <TimerOne.h>
#include "SPI.h"
#include "Neophob_LPD6803.h"


/*the SPI data register (SPDR) holds the byte which is about to be shifted out the MOSI line */
#define SPI_LOAD_BYTE(data) SPDR=data
/* Wait until last bytes is transmitted. */
#define SPI_WAIT_TILL_TRANSMITED while(!(SPSR & _BV(SPIF)))
//#define SPI_WAIT_TILL_TRANSMITED while(!(SPSR & (1<<SPIF)))


//some local variables, ised in isr
static uint8_t *pixelData; //pointer to pixel buffer, we cannot access pixels form isr!
static uint8_t nState=1;
static uint16_t numLEDs2;

// Constructor for use with hardware SPI (specific clock/data pins):
Neophob_LPD6803::Neophob_LPD6803(uint16_t n) {
  numLEDs = n;  
  numLEDs2 = numLEDs*2;
  pixelData = (uint8_t *)malloc(numLEDs2);

  //clear buffer
  for (unsigned int i=0; i < numLEDs; i++) {
    setPixelColor(i, 0);
  }
}

//just feed out the clock line to drive the pwm cycle
//hint: it works with only on clock pulse (even faster), however
//      there are visible patterns if you're using a long chain of modules.
//      those patters disapper if a second pulse is sent
static void isr2() {
  SPI_WAIT_TILL_TRANSMITED;  
  SPI_LOAD_BYTE(0);
  SPI_WAIT_TILL_TRANSMITED;  
  SPI_LOAD_BYTE(0);
}


// Activate hard/soft SPI as appropriate:
void Neophob_LPD6803::begin(uint8_t divider) {
  startSPI(divider);
  Timer1.attachInterrupt(isr2);
}

//call the clock shift out function each isrCallInMicroSec us
void Neophob_LPD6803::setCPU(long isrCallInMicroSec) {
  Timer1.initialize(isrCallInMicroSec);
}

// Enable SPI hardware and set up protocol details:
void Neophob_LPD6803::startSPI(uint8_t divider) {
  SPI.begin();
  SPI.setBitOrder(MSBFIRST);
  SPI.setDataMode(SPI_MODE0);
  SPI.setClockDivider(divider);  // 0.25 MHz  
//  SPI.setClockDivider(SPI_CLOCK_DIV2);  // 8 MHz
//  SPI.setClockDivider(SPI_CLOCK_DIV8);  // 2 MHz
// SPI.setClockDivider(SPI_CLOCK_DIV16);  // 1 MHz  
// SPI.setClockDivider(SPI_CLOCK_DIV32);  // 0.5 MHz  
//  SPI.setClockDivider(SPI_CLOCK_DIV64);  // 0.25 MHz  
  // LPD6803 can handle a data/PWM clock of up to 25 MHz, and 50 Ohm
  // resistors on SPI lines for impedance matching.  In practice and
  // at short distances, 2 MHz seemed to work reliably enough without
  // resistors, and 4 MHz was possible with a 220 Ohm resistor on the
  // SPI clock line only.  Your mileage may vary.  Experiment!

  SPI_LOAD_BYTE(0);
}

uint16_t Neophob_LPD6803::numPixels(void) {
  return numLEDs;
}


void Neophob_LPD6803::show(void) {
  //isDirty=1; //flag to trigger redraw
  unsigned int i;
  nState = 0;

  Timer1.stop();
  //header - omitted as the isr routing sends plenty of 0's
//  for (i=0; i<4; i++) {
//    SPI_WAIT_TILL_TRANSMITED;
//    SPI_LOAD_BYTE(0);
//  }

  //data
  for (i=0; i<numLEDs2; ) {
      SPI_WAIT_TILL_TRANSMITED;
      SPI_LOAD_BYTE(pixelData[i++]);

      SPI_WAIT_TILL_TRANSMITED;
      SPI_LOAD_BYTE(pixelData[i++]);      
  }

  //tail - omitted as the isr routing sends plenty of 0's
//  for (i=0; i<numLEDs; i++) {
//    SPI_WAIT_TILL_TRANSMITED;    
//    SPI_LOAD_BYTE(0);
//  }
  
  Timer1.resume();
  nState = 1;
}


void Neophob_LPD6803::setPixelColor(uint16_t n, uint8_t r, uint8_t g, uint8_t b) {
  if (n >= numLEDs2) return;
  
    /* As a modest alternative to full double-buffering, the setPixel()
     function blocks until the serial output interrupt has moved past
     the pixel being modified here.  If the animation-rendering loop
     functions in reverse (high to low pixel index), then the two can
     operate together relatively efficiently with only minimal blocking
     and no second pixel buffer required. */
  while(nState==0); 

  uint16_t data = g & 0x1F;
  data <<= 5;
  data |= b & 0x1F;
  data <<= 5;
  data |= r & 0x1F;
  data |= 0x8000; 

  uint16_t ofs = n*2;
  pixelData[ofs++]=data>>8;
  pixelData[ofs]=data&0xff;
}

//---
void Neophob_LPD6803::setPixelColor(uint16_t n, uint16_t c) {
  if (n >= numLEDs2) return;

    /* As a modest alternative to full double-buffering, the setPixel()
     function blocks until the serial output interrupt has moved past
     the pixel being modified here.  If the animation-rendering loop
     functions in reverse (high to low pixel index), then the two can
     operate together relatively efficiently with only minimal blocking
     and no second pixel buffer required. */
  while(nState==0); 

  uint16_t ofs = n*2;
  uint16_t col = 0x8000 | c;
  pixelData[ofs++]=col>>8;
  pixelData[ofs]=col&0xff;
}


