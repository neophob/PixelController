PixelInvaders Firmware for Arduino (like Teensy 2.0) compatible boards.

Details:
  neoLedLPD6803Spi: The SPI PixelInvaders firmware. If you bought a PixelInvaders DIY, this is the firmware you need.
  neoLedWS2801Spi: The SPI PixelInvaders firmware to use with WS2801 LED pixels.

libraries: This directory contains needed Arduino libraries. Depending which firmware you want to install on your Arduino/Teensy, you need other libraries:

Details:
  libraries/FastSPI_LED: needed for the neoLedWS2801Spi firmware
  libraries/Lpd6803: needed for the neoLedLPD6803 firmware
  libraries/neophob_lpd6803spi: needed for the neoLedLPD6803Spi firmware
  libraries/timer1: needed for the neoLedLPD6803 and neoLedLPD6803Spi firmware

Hint 1:
SPI limit you to use specific ports on a Arduino board and may not work if you use for example an Ethernet Shield.

Hint 2:
Use a Teensy 2.0 board for fast serial throughput. An Arduino *may* work but give you quite a slow experience!
