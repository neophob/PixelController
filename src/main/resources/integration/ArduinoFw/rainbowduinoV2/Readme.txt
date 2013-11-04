Rainbowduino V2 Firmware
------------------------

Content:
-3rdParty: 3rd party libraries the firmware needs, install them
-arduinoFw: If you want to drive your rainbowduino with an Arduino, this is the firmware for it
-teensyFw: If you want to drive your rainbowduino with an Teensy, this is the firmware for it
-rainbowduinoFw: The firmware for the rainbowduino itself. Make sure you change the i2c address for each device

---

needed libraries:
 -MsTimer2 (http://www.arduino.cc/playground/Main/MsTimer2)
 -FlexiTimer (http://github.com/wimleers/flexitimer2)
 
libraries to patch:
 Wire: 
 	utility/twi.h: #define TWI_FREQ 400000L (was 100000L)
 				   #define TWI_BUFFER_LENGTH 98 (was 32)
 	wire.h: #define BUFFER_LENGTH 98 (was 32)
 	

Hint: make sure you restart arduino after patching the files!
 	
 
---

Use the library:

    private Rainbowduino rainbowduino = null;
    private boolean ping;

    public YourClass() {
        rainbowduino = new Rainbowduino( papplet );
        rainbowduino.initPort();
        ping = rainbowduino.ping((byte)0);
    }
    
    public sendFrame(int i2cAddr) {
    	rainbowduino.sendRgbFrame((byte)i2cAddr, THE_8x8_BUFFER, false);
    }