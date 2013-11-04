#ifndef Rainbow_h
#define Rainbow_h

//Address of the device. Note: this must be changed and compiled for all unique Rainbowduinos
#define I2C_DEVICE_ADDRESS 0x05

//=============================================
//PORTC maps to Arduino analog pins 0 to 5. Pins 6 & 7 are only accessible on the Arduino Mini
//PORTC - The Port C Data Register - read/write

//pin 23 of the arduino maps to first MBI5169 (blue) SDI input
#define SH_BIT_SDI   0x01

//pin 24 of the arduino maps to the MBI5169 CLN input
#define SH_BIT_CLK   0x02

//pin 25 of the arduino maps to the MBI5169 LE input
#define SH_BIT_LE    0x04

//pin 26 of the arduino maps to the MBI5169 OE input
#define SH_BIT_OE    0x08

//============================================

//some handy hints, ripped form the arduino forum
//Setting a bit: byte |= 1 << bit;
//Clearing a bit: byte &= ~(1 << bit);
//Toggling a bit: byte ^= 1 << bit;
//Checking if a bit is set: if (byte & (1 << bit))
//Checking if a bit is cleared: if (~byte & (1 << bit)) OR if (!(byte & (1 << bit)))

//potential take too long! -> PORTC &=~0x02; PORTC|=0x02
//Clock input terminal for data shift on rising edge
#define CLK_RISING  {PORTC &=~ SH_BIT_CLK; PORTC |= SH_BIT_CLK;}

//Data strobe input terminal, Serial data is transfered to the respective latch when LE is high. 
//The data is latched when LE goes low.
#define LE_HIGH     {PORTC |= SH_BIT_LE;}
#define LE_LOW      {PORTC &=~ SH_BIT_LE;}

//Output Enabled, when (active) low, the output drivers are enabled; 
//when high, all output drivers are turned OFF (blanked).
#define ENABLE_OE   {PORTC &=~ SH_BIT_OE;}
#define DISABLE_OE  {PORTC |= SH_BIT_OE;}

#define SHIFT_DATA_1     {PORTC |= SH_BIT_SDI;}
//potential take too long! -> PORTC&=~0x01
#define SHIFT_DATA_0     {PORTC &=~ SH_BIT_SDI;}


#define open_line0      {PORTB=0x04;}
#define open_line1      {PORTB=0x02;}
#define open_line2      {PORTB=0x01;}
#define open_line3      {PORTD=0x80;}
#define open_line4      {PORTD=0x40;}
#define open_line5      {PORTD=0x20;}
#define open_line6      {PORTD=0x10;}
#define open_line7      {PORTD=0x08;}
#define CLOSE_ALL_LINE  {PORTD&=~0xf8; PORTB&=~0x07;}

#endif

