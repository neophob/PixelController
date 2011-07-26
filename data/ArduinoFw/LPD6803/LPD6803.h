#include <WProgram.h>

class LPD6803 {
 private:
  uint8_t cpumax;

 public:
  LPD6803(uint16_t n, uint8_t dpin, uint8_t cpin);
  void begin();
  void show();
  void doSwapBuffersAsap(uint16_t idx);
  void setPixelColor(uint16_t n, uint8_t r, uint8_t g, uint8_t b);
  void setPixelColor(uint16_t n, uint16_t c);
  void setCPUmax(uint8_t m);
  uint16_t numPixels(void);
};
