# PixelInvaders Firmware for Arduino

## Firmware

 * `neoLedLPD6803Spi`: The SPI PixelInvaders firmware. If you bought a PixelInvaders DIY, this is the firmware you need. Works for Teensy 2.0, Teensy 2.0++ and Arduino's.
 * `ExpeditInvadersSpi`: This firmware drive one or multiple Expedit shelfs.
 * `neoLedWS2801Spi`: The SPI PixelInvaders firmware to use with WS2801 LED pixels.

## Libraries
The **libraries** directory contains all the needed Arduino libraries. Depending which firmware you want to install on your Arduino/Teensy, you need other libraries:

 * `libraries/FastSPI_LED2`: needed for the neoLedWS2801Spi firmware
 * `libraries/neophob_lpd6803spi`: needed for the neoLedLPD6803Spi firmware
 * `libraries/timer1`: needed for the neoLedLPD6803 and neoLedLPD6803Spi firmware

**Hint**:
SPI limit you to use specific ports on a Arduino board and may not work if you use for example an Ethernet Shield.

