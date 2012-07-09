#if (ARDUINO >= 100)
 #include <Arduino.h>
#else
 #include <WProgram.h>
 #include <pins_arduino.h>
#endif


class Neophob_LPD6803 {

 public:
  // Use SPI hardware; specific pins only:
  Neophob_LPD6803(uint16_t n);

  void
    begin(uint8_t divider),
    show(void),
    setPixelColor(uint16_t n, uint8_t r, uint8_t g, uint8_t b),
    setPixelColor(uint16_t n, uint16_t c),    
    setCPUmax(uint8_t max);
  uint16_t
    numPixels(void);

 private:

  uint16_t
    numLEDs;  
  uint8_t
    cpumax,
    clkpin    , datapin,     // Clock & data pin numbers
    clkpinmask, datapinmask; // Clock & data PORT bitmasks
  volatile uint8_t
    *clkport  , *dataport;   // Clock & data PORT registers
  void    
    startSPI(uint8_t divider),
    LedOut();
};