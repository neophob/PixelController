[![Build Status](https://secure.travis-ci.org/neophob/PixelController.png)](http://travis-ci.org/neophob/PixelController)

# PixelController
**PixelController** - a matrix control project by Michael Vogt <michu at neophob.com>, (c) 2010-2012. The main goal of this application is to create an easy to use matrix controller software which creates stunning visuals!

**Primary Website**: http://www.pixelinvaders.ch

**My Blog**: http://www.neophob.com

**Facebook**: https://www.facebook.com/PixelInvaders


## HOWTO USE PIXELCONTROLLER
Prerequisite:
* Java Runtime, v1.6+

Run `PixelController.cmd` on Windows, `PixelController.command` on OSX and `PixelController.sh` on Linux to start the application.
Make sure your led matrix connected to you computer before the application is started and you **configured** your output hardware in 
the data/config.properties file.

## DEMO
Check out http://vimeo.com/27453711 and http://vimeo.com/32580251 to see PixelController in action 
on two PixelInvaders panels. 


## SUPPORTED HARDWARE
PixelController supports different (LED) matrix hardware devices:
* PixelInvaders 3d Panels (see Readme.PixelInvaders, http://www.pixelinvaders.ch)
* Seeedstudios Rainbowduino V2 (see Readme.rainbowduinoV2)
* Seeedstudios Rainbowduino V3
* ArtNet Devices, multiple universe are supported,510 Channels (170 RGB Pixels) per universe
* MiniDmx Devices (like the SEDU board of http://www.led-studien.de)
* Adavision (http://www.adafruit.com/products/611)
* Element Labs Stealth LED panel. No longer in production (http://cled.barcousa.com/support/STEALTH/STEALTH_Users_Guide.pdf)
* Generic UDP Devices (for example Raspberry Pi, check out the PixelPi Software)

Check out the `data/ArduinoFW` directory, all Arduino based firmware files are stored there.

## FRONTENDS
There are different frontends for PixelController:
* Native Java: the default frontend is started when PixelController starts.
* PixConCli: Command Line Interface for PixelController, works also remote. The CLI tool is called `PixConCli.cmd` on Windows and `PixConCli.sh` on Linux/OSX.
* PureData: PureData frontend (http://puredata.info/ download the extended Version), very flexible, extensible (OSC, MIDI). The PureData file is called `PixelController.pd`.

##INTERFACES
* OSC interface, default listening port 9876. Processing examples included how to communicate with PixelController via OSC protocol
* FUDI interface, default listening port 3448, used to communicate with PureData

## IT DOES NOT WORK!
Try to understand **WHAT** does not work, which component? is it the frontend? PixelController itself? or no output?

Here are some common errors:
* Did you forgot to **edit the configuration file** `config.properties`. Take a look at the config.examples subdirectory!
* Did you flash the **correct firmware** to you Arduino/Teensy? PixelInvaders should display an animated Rainbow
   if powered on (https://github.com/neophob/PixelController/tree/master/data/ArduinoFw/lpd6803/neoLedLPD6803)
* **PixelInvaders panels**: Make sure that the Panel shows an **animated rainbow pattern** when the panels are powered on (make sure 
  that you also power the Arduino/Teensy board). If you don't see a animated rainbow, make sure the directon of the modules is correct and that
  the Arduino/Teensy, Led Modules and PSU share common ground.
* **PixelInvaders panels**: A User reported that the PixelInvader firmware did not work on a new Arduino UNO r3 board. I think the reason
   for this is the big serial latency. However using a Arduino UNO r1 worked flawlessly. Technically this is not a big
   deal, as the timeout value cold be adjusted in the firmware.


## HOWTO BUILD PIXELCONTROLLER
Prerequisite:
* Maven v2.x (if you use Maven 3, make sure to read http://neophob.com/2011/11/maven-3-is-evil/ first!)
* JDK 1.6+

Then run 

    # mvn initialize
    to install the needed packages in your local repo and    
    # mvn clean package
    to build PixelController, the distribution directory is "target/assembly/PixelController-VERISON/".

Hint: if you're using eclipse and you see an error like this
`java.lang.NoClassDefFoundError: Could not initialize class gnu.io.RXTXVersionjava.lang.NoClassDefFoundError: Could not initialize class gnu.io.RXTXVersion`
make sure you add the lib/serial directory as "Native library location"

## ADD NEW HARDWARE SUPPORT
It should be pretty simple to add support for new hardware. All Output code should go into the com.neophob.sematrix.output package (`src/main/java/com/neophob/sematrix/output` directory). All you need to do in the Output class is, take an array of int's (one int is used to store the 24 bpp) and send this buffer to your output device (via serial port, ethernet, bluetooth...). Maybe you need to reduce the color depth, flip each second scanline due hardware wiring, such helper methods should go into the `OutputHelper.java` class.

As a string point, add your hardware in the `OutputDeviceEnum.java` class and have a look where the other entries are referenced. **Take a look at the existing Output classes**, this should help you!

## PERFORMANCE
With the JMX interface you can monitor the status of your PixelController instance in real time. This 
will provide you with useful data such as required time for each layer (generator, effect, mixer…), the 
frame rate of your instance, allowing you to diagnose problems or performance issues. To read the JMX 
data, you will need to use a JMX client or the PixConCli util.

Example how to use PixConCli:

    localhost:PixelController-1.3-SNAPSHOT michu$ ./PixConCli.sh -c JMX_STAT -p 1337
    Create an RMI connector client and connect it to the RMI connector server 127.0.0.1:1337
    Get an MBeanServerConnection...
    
    Generic:
    server version           : 1.1
    current fps              : 20,036 (100% of configured fps: 20)
    frame count              : 1771
    running since            : 0:01:28.980
    
    The following average times have been collected during the last 10.007 seconds:
       generator             : 0,310ms
       effect                : 0,000ms
       output schedule       : 0,140ms
       fader                 : 0,000ms
       debug window          : 15,210ms
       output prepare wait   : 0,005ms
       output update wait    : 0,005ms
       matrix emulator window: 0,440ms
    
    Ouput-specific average times for output #1: NULL (NullDevice)
       prepare               : 1,550ms
       update                : 0,000ms
    
    Close the connection to the server


## CREDITS
* **Michael Vogt**:       Project Lead, Main Developer    	
* **Markus Lang**:        Maven enhancements, Output enhancements, Performance enhancements, Rainbowduino V3 support
* **McGyver666**:         Contributor
* **Rainer Ostendorf**:   Artnet Output
* **Pesi**:               miniDMX Output, Tester
* **Scott Wilson**:       Arduino/Rainbowduino Howto
* **Noxx6**:              Bugfixes
* **Psykon**:             Example Visuals
* **okyeron**:            Stealth output device

