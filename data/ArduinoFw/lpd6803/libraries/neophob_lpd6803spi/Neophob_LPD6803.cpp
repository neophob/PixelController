/*********************************************************************************/
// Example to control LPD6803-based RGB LED Modules in a strand or strip via SPI
// by Michael Vogt / http://pixelinvaders.ch
// This Library is basically a copy and paste work and relies on work 
// of Adafruit-WS2801-Library and FastSPI Library 
/*********************************************************************************/

#include <TimerOne.h>
#include "SPI.h"
#include "Neophob_LPD6803.h"

//some local variables, ised in isr
static uint8_t isDirty;
static uint16_t prettyUglyCopyOfNumPixels;
static uint16_t *pixelDataCurrent;	//working pointer
static uint16_t *pixelData; //pointer to pixel buffer, we cannot access pixels form isr!
volatile unsigned char nState=1;

// Constructor for use with hardware SPI (specific clock/data pins):
Neophob_LPD6803::Neophob_LPD6803(uint16_t n) {
  prettyUglyCopyOfNumPixels = n;  
  numLEDs = n;  
  pixelData = (uint16_t *)malloc(n);
  isDirty = 0;    
  cpumax = 70;
  
  //clear buffer
  for (int i=0; i < numLEDs; i++) {
    setPixelColor(i,0);
  }
}
/*the SPI data register (SPDR) holds the byte which is about to be shifted out the MOSI line */
#define SPI_LOAD_BYTE(data) SPDR=data
/* Wait until last bytes is transmitted. */
#define SPI_WAIT_TILL_TRANSMITED while(!(SPSR & (1<<SPIF)))


//Interrupt routine.
//Frequency was set in setup(). Called once for every bit of data sent
//In your code, set global Sendmode to 0 to re-send the data to the pixels
//Otherwise it will just send clocks.
static void isr() {
  static uint16_t indx=0;

  if (nState==1) {
    //check update color, make sure the data has been validated 
    if (isDirty==1) { //must we update the pixel value
	  //SPI_LOAD_BYTE(0);
	  //SPI_WAIT_TILL_TRANSMITED; 
	  SPI.transfer(0);	  	  
      indx = 0;
      pixelDataCurrent = pixelData; //reset index
      nState = 0;
      isDirty = 0;
      return;
    }
    
    //just send out zeros all the time, used to validate updates and prepare updates
    SPI.transfer(0);
	//SPI_LOAD_BYTE(0);
	//SPI_WAIT_TILL_TRANSMITED; 
    return;
  }
  else { //feed out pixelbuffer
  	
  	//First shift in 32bit “0” as start frame, then shift in all data frame, start 
  	//frame and data frame both are shift by high-bit, every data is input on DCLK rising edge.
  	
    register uint16_t command;
    command = 0x8000 | *(pixelDataCurrent++);       //get current pixel
  	//SPI_LOAD_BYTE( (command>>8) & 0xFF);
    //SPI_WAIT_TILL_TRANSMITED;                      	//send 8bits
    //SPI_LOAD_BYTE( command & 0xFF);
    //SPI_WAIT_TILL_TRANSMITED;                      	//send 8bits again
    SPI.transfer( (command>>8) & 0xFF);
    SPI.transfer( command      & 0xFF);

    if(indx++ >= prettyUglyCopyOfNumPixels) { 
      nState = 1;
    }

    return;
  } 
}


// Activate hard/soft SPI as appropriate:
void Neophob_LPD6803::begin(uint8_t divider) {
  startSPI(divider);

  setCPUmax(cpumax);
  Timer1.attachInterrupt(isr);
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
  
  //SPI_A(0); //maybe, move at the end of the startSPI() method
}

uint16_t Neophob_LPD6803::numPixels(void) {
  return numLEDs;
}


void Neophob_LPD6803::show(void) {
  isDirty=1; //flag to trigger redraw
}


void Neophob_LPD6803::setPixelColor(uint16_t n, uint8_t r, uint8_t g, uint8_t b) {
  if (n > prettyUglyCopyOfNumPixels) return;
  
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

  pixelData[n] = data;
}

//---
void Neophob_LPD6803::setPixelColor(uint16_t n, uint16_t c) {
  if (n > prettyUglyCopyOfNumPixels) return;

    /* As a modest alternative to full double-buffering, the setPixel()
     function blocks until the serial output interrupt has moved past
	   the pixel being modified here.  If the animation-rendering loop
	   functions in reverse (high to low pixel index), then the two can
	   operate together relatively efficiently with only minimal blocking
	   and no second pixel buffer required. */
  while(nState==0); 

  pixelData[n] = 0x8000 | c; //the first bit of the color word must be set
}


