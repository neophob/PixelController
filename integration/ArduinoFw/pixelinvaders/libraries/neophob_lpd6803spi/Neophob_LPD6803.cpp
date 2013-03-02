/*********************************************************************************/
// Example to control LPD6803-based RGB LED Modules in a strand or strip via SPI
// by Michael Vogt / http://pixelinvaders.ch
// This Library is basically a copy and paste work and relies on work 
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
volatile static uint8_t *pixelData; //pointer to pixel buffer, we cannot access pixels form isr!

volatile static uint8_t nState=1;

// Constructor for use with hardware SPI (specific clock/data pins):
Neophob_LPD6803::Neophob_LPD6803(uint16_t n) {
  numLEDs = n;  
  pixelData = (uint8_t *)malloc(numLEDs*2);
  cpumax = 70;

  //clear buffer
  for (unsigned int i=0; i < numLEDs*2; i++) {
    setPixelColor(i,0);
  }
}

static void isr2() {
  SPI_WAIT_TILL_TRANSMITED;  
  SPI_LOAD_BYTE(0);
}


// Activate hard/soft SPI as appropriate:
void Neophob_LPD6803::begin(uint8_t divider) {
  startSPI(divider);
  setCPUmax(cpumax);
  Timer1.attachInterrupt(isr2);
}

void Neophob_LPD6803::setCPUmax(uint8_t max) {
  cpumax = max;

  // each clock out takes 20 microseconds max
  long time = 100;
  time *= 20;   // 20 microseconds per
  time /= max;    // how long between timers
  Timer1.initialize(time);
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
  unsigned int i;
  nState = 0;

  Timer1.stop();
  //header
  for (i=0; i<4; i++) {
    SPI_WAIT_TILL_TRANSMITED;
    SPI_LOAD_BYTE(0);
  }

  //data
  for (i=0; i<numLEDs*2; ) {
      SPI_WAIT_TILL_TRANSMITED;
      SPI_LOAD_BYTE(pixelData[i++]);

      SPI_WAIT_TILL_TRANSMITED;
      SPI_LOAD_BYTE(pixelData[i++]);      
  }

  //tail
  for (i=0; i<numLEDs; i++) {
    SPI_WAIT_TILL_TRANSMITED;    
    SPI_LOAD_BYTE(0);
  }
  
  Timer1.resume();
  nState = 1;
}


void Neophob_LPD6803::setPixelColor(uint16_t n, uint8_t r, uint8_t g, uint8_t b) {
  if (n > numLEDs*2) return;
  
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

//  pixelData[n] = data;
}

//---
void Neophob_LPD6803::setPixelColor(uint16_t n, uint16_t c) {
  if (n > numLEDs*2) return;

    /* As a modest alternative to full double-buffering, the setPixel()
     function blocks until the serial output interrupt has moved past
     the pixel being modified here.  If the animation-rendering loop
     functions in reverse (high to low pixel index), then the two can
     operate together relatively efficiently with only minimal blocking
     and no second pixel buffer required. */
  while(nState==0); 
  n*=2;
  uint16_t col = 0x8000 | c;
//  pixelData[n] = 0x8000 | c; //the first bit of the color word must be set
  pixelData[n++]=col>>8;
  pixelData[n]=col&0xff;
}


