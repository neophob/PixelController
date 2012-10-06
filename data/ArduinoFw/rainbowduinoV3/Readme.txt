Rainbowduino V3 Firmware
------------------------

Arduino firmware for a Rainbowduino V3 controller of the
'rainbowduino-v3-streaming-firmware' project hosted at:
https://code.google.com/p/rainbowduino-v3-streaming-firmware/

Author: Markus Lang (m@rkus-lang.de)
Blog:   http://programmers-pain.de/



-----------------------------------------------------------------------------------------------------------------------------------------------
DETAILED RAINBOWDUINO INSTRUCTIONS:
-----------------------------------
If you own a Rainbowduino and an Arduino board and would like to run PixelController, read this 
how-to by Scott Wilson!


---------------------------- 
Prepare your computer: (MAC)
----------------------------
- Install the Arduino programming environment.
- Install the PureData extended programming environment.
- Connect Arduino to computer with a USB cable.
- Java is built into mac and does not need to be installed.
- Maven 2.2.1 is only needed if you plan on building the binaries yourself.
- Download a Pixelcontroller snapshot (I put this folder into my local user folder.)
	
	/users/USERNAME/pixelcontroller-1.0.3.x

notes: 'USERNAME' will be your user account name
	  'COMPUTERNAME' will be your computers name
	  'x' will be a version number


---------------------------
Patch Arduino Wire library:
---------------------------
- Close the Arduino Programmer if it is open.
- Open and patch the files twi.h an wire.h with the correct values.
- the files can be found within the Arduino application package. /Applications/Arduino 
- ctrl click (right click) on the Arduino.app and choose 'show package contents' from the popup menu.
- navigate to the wire library. /Contents/Resources/Java/Libraries/Wire
- open twi.h in a text editor and change the values as follows.

	#define TWI_FREQ 400000L 
 	#define TWI_BUFFER_LENGTH 98

- save and close twi.h
- open wire.h in a text editor and change the values as follows.

 	#define BUFFER_LENGTH 98

- save and close wire.h


----------------------------
Install 3rd Party libraries:
----------------------------
- Copy the following folders from the Pixelcontroller data folder to your local Arduino Library folder.
	
	/users/USERNAME/Documents/Arduino/Libraries/

	FlexiTimer2
	MSTimer2
	arduinoFw
	rainbowduinoFw
	
- Restart the Arduino programming environment.
- The new libraries now show up in the Arduino open menu drop down.


----------------------------- 
Upload Rainbowduino firmware:
------------------------------
- Open the 'Bare Minimum' example sketch.
- Select the correct port and card type from the 'tools' menu.
- Click upload to load the sketch onto the Arduino.

- Connect the Arduino to the Rainbowduino using the tx/rx method. (4 wires needed)
	GND-GND
	RESET-DTR
	tx-tx
	rx-rx

- Open the sketch 'Rainbow_V2_71.pde'
- Three Sketches will open on individual tabs.
	Rainbow_V2_71
	Rainbow.h
	data.c	

- Change the i2c address in the 'rainbow.h' sketch to 0x01

	//Address of the device. Note: this must be changed and compiled for all unique Rainbowduinos
#define I2C_DEVICE_ADDRESS 0x01

- Select the correct port and board/chip type and upload the sketch.
	Note: Even though I am using a Arduino UNO I must select a 328 board type in order for it to load to 
	the Rainbowduino.
- If your LED matrix is plugged into the Rainbowduino it should now shows a Pixelinvader image.


------------------------ 
Upload Arduino firmware:
------------------------
- Unplug the rx/tx wires from the Arduino.
- Open the Arduino sketch 'neo.pde'
- Select the correct ports and card type. 
- Upload the sketch to the Arduino card.


---------------
i2c Connection:
---------------
- Connect the Arduino to the Rainbowduino via the i2c method.
- For I2C, you need 3 cables (SDA,SCL,GND):
(GND) from Arduino to Rain�bow�duino I2C GND
Arduino ana�log input 4 = Rain�bow�duino I2C SDA
Arduino ana�log input 5 = Rain�bow�duino I2C SCL

- Additional Information: I2C Cable Length (Sum�mary from http://www.i2cchip.com/i2c_connector.html#Bus%20Length)
Use screened and twisted cable for runs between boxes. 
DON�T twist SDA and SCL together.  
If you need to pair them you can pair SDA+VDD and SCL+GND.
Don�t run the bus fast if you don�t need to.
Run the bus at higher currents.
Cable length maximum 8m.


----------------------
Setup the config file:
----------------------
- Because we changed the i2c address we need to make sure that the config file will map the images to the correct 
  Rainbowduinos.
- Open the file 'config.properties' in a text editor. (file is located in the Pixelcontroller 'data' directory).
- We are going to do three things to this file.
	1. Enable the 'Rainbowduino' output
	2. Adjust the layout address order
	3. Disable the 'null' output
	
- Enable the Rainbowduino output. Find the output section with the following code.

#=========================
#settings for rainbowduino
#=========================
#i2c destination address + layout definition
#layout.row1.i2c.addr=5,6
#layout.row2.i2c.addr=8,9

- Activate this output buy removing the # in front of the bottom three lines. it should look like this after editing.

#=========================
#settings for rainbowduino
#=========================
i2c destination address + layout definition
layout.row1.i2c.addr=5,6
layout.row2.i2c.addr=8,9

- Change the i2c address layout order.
- It should look like this after making the changes.

#=========================
#settings for rainbowduino
#=========================
i2c destination address + layout definition
layout.row1.i2c.addr=1,2
layout.row2.i2c.addr=3,4



- Disable the 'null output'. Find the null output section of code, it should look like this.

#=========================
#settings for null output
#=========================
nulloutput.devices.row1=2
nulloutput.devices.row2=2

- deactivate it by adding # in front of the last 2 lines of code. it should look like this.

#=========================
#settings for null output
#=========================
#nulloutput.devices.row1=2
#nulloutput.devices.row2=2


- save and close the 'config.properties' file.


--------------------
Run PixelController:
--------------------
- Open the PureData file 'pixelcontroller.pd'
- Open a new terminal window.
- In order to run the 'pixelcontroller.sh' shell script, terminal must be in the root of that file.
- A new Terminal window should have the following text in it.

	COMPUTERNAME:~ USERNAME$

- Type the following so terminal changes its directory to the pixelcontrollers root.

	cd /Users/USERNAME/PixelController-1.0.3.x

- The terminal window should change its path and should look like this.

	COMPUTERNAME:PixelController-1.0.3.x USERNAME$

- To run pixelcontroller.sh type the following and then press 'enter'
	
	bash pixelcontroller.sh

- the terminal window will start to scroll a bunch of text, when complete it should open two windows.

	1. debug buffer
	2. com.neophob.PixelController

- The PureData GUI should now be connected and controlling the Rainbowduino.

