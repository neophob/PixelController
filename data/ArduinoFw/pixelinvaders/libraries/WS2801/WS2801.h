#if (ARDUINO >= 100)
 #include <Arduino.h>
#else
 #include <WProgram.h>
 #include <pins_arduino.h>
#endif

class WS2801 {

 public:

  WS2801();
  WS2801(uint16_t n, uint8_t dpin, uint8_t cpin); // Configurable pins
  WS2801(uint16_t n); // Use SPI hardware; specific pins only
  void
    begin(void),
    show(void),
    setPixelColor(uint16_t n, uint8_t r, uint8_t g, uint8_t b),
    setPixelColor(uint16_t n, uint32_t c),
    updatePins(uint8_t dpin, uint8_t cpin), // Change pins, configurable
    updatePins(void); // Change pins, hardware SPI
  uint16_t
    numPixels(void);
  uint32_t
    getPixelColor(uint16_t n);
 private:

  uint16_t
    numLEDs;
  uint8_t
    *pixels, // Holds color values for each LED (3 bytes each)
    clkpin    , datapin,     // Clock & data pin numbers
    clkpinmask, datapinmask; // Clock & data PORT bitmasks
  volatile uint8_t
    *clkport  , *dataport;   // Clock & data PORT registers
  void
    alloc(uint16_t n),
    startSPI(void);
  boolean
    hardwareSPI, // If 'true', using hardware SPI
    begun;       // If 'true', begin() method was previously invoked
};