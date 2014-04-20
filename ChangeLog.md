# PixelController ChangeLog

## Changelog v2.1.0-RC1 to v2.1.0
    16 files changed, 35 insertions(+), 15 deletions(-)

* Add option to start PixelController in random preset mode
* Add TouchOSC Phone Layout for iPhone 5 (Thanks r26D)
* Add hint to start PixelController on RPI (as root)

## Changelog v2.0.0 to v2.1.0-RC1 (1. April 2014)
    632 files changed, 96745 insertions(+), 151436 deletions(-)
    
* Add client/server architecture. PixelController can started headless on a server, connect to the server with the client GUI (same look as the standalone version)
* Add RaspberryPi WS2801 output driver
* Add custom led matrix mapping tool, see [http://pixelinvaders.ch/pixelcontroller/](http://pixelinvaders.ch/pixelcontroller/) for more details
* Add Bonjour/Zeroconf support, PixelController register itself as "pixelcontroller.local"
* Add new ROTATE_ Generator/Effect/Mixer/Colorset OSC command
* Add Noise generator
* Add Gamma 3.0 color correction
* Add TouchOsc layout for phone and tablet
* Add simple performance test (`PixelController.sh -perf` or `PixelControllerRPi.sh -perf`)
* Add Random Mode with selectable time-life (option `randommode.lifetime.in.s`) (Issue #44)
* Add more Blinkenlights movie files
* Add new effect Posterize
* Add new effect Darken
* Add PixelInvaders firmware for Arduino UNO, thanks Yves Alter. This should fix all UNO related issues.
* Add new output rotate option: FLIPPEDY
* Remove support for stealth panels
* Fix decouple fps setting of PixelController from the GUI update speed (Issue #61)
* Fix replace Pixel and Quality image resize code with custom implementations (nearest neighbour and bilinear), major performance improvement
* Fix add missing float parameter to the OSC server, only int values were parsed
* Fix the framerate configuration can be a float number (ex. fps=0.1) if you need a really slow update rate
* Fix refresh GUI settings when random mode kicks in
* Fix generator speed changes the target fps (0..200%), much smoother (Issue #46, #52 and #59)
* Fix generator speed and brightness were not stored as part of the preset (Issue #62)
* Fix capture generator, crashed if recording window was too small
* Fix Metaball generator resolution for different sizes
* Fix more code cleanup
* Fix optimize CPU usage in heavy beat detection mode
* Fix optimize preset load time, load resource files (image and blinkenlight) only if generator is used in preset
* Fix reduce rotozoom effect speed
* Fix visual size in GUI
* Fix rename preset.led -> preset.properties, all PixelController extension files have now the same file extension.
* Fix update build process, put RPi specifiy resources (Serial and pi4j) dependencies into special directory, do not include junit dependencies.
* Fix speedup Blinkenlight parser, some frames were displayed too long
* Fix Multliply and Negative Multiply mixer, output didn't respect ColorSets correctly
* Fix Subsat mixer - was completly broken
* Fix output layout for all *FLIPPEDY entries - all FLIPPEDY action were broken.


## Changelog v1.5.1 to v2.0.0 (2. December 2013)  
    877 files changed, 230934 insertions(+), 227347 deletions(-)
    
* Modularize Project, rewrote large parts
* Add new command line version of PixelController - run on a headless server (like an RPi)
* Add make speed of generators configurable via GUI and save it as part of the preset (Issue #46)
* Add Beat Workmode, define how beat input should be interpreted. Options: Linear, Moderate Sound and Heavy Sound
* Fix Update GUI much faster (instant feedback)
* Fix GUI, sort images and blinkenlights file dropdown content case insensitive
* Fix remove obsolete config options (initial.image.simple, initial.blinken, initial.text) but load a preset during startup
* Fix rename config options (maximal.debug.window.xsize -> gui.window.maximal.width, update.generators.by.sound -> sound.analyze.input)
* Fix typo, rename data/presents.led -> data/presets.led
* Fix respect maximal window size defined in the config file
* Fix adjust matrix simulation window for large resolutions
* Fix effect Rotate90 - works now for non-square resolutions
* Fix improve error reporting
* Much faster startup time
* Display gamma correction on output matrix
* Removed TCP Server, used by the old PureData frontend and CLI client
* Update documentation, see readme.pdf


## Changelog v1.5.0 to v1.5.1 (12. November 2013)
    232 files changed, 12438 insertions(+), 12446 deletions(-)

* Add support for ExpeditInvaders output
* Add Fader effect if a preset is loaded, freeze current buffer and fade into new content
* Add Fader effect if current visual is randomized, freeze current buffer and fade into new content 
* Add config option to make fader time configurable
* Fix Checkbox Mixer, make checkbox size dependent on output
* Fix PixelImage, possible exception 
* Fix Geometrics and Cell generators, possible exception
* Fix rename Rainbowduino firmware to prevent crash
* Fix rainbowduinov3 firmware bug, the serial line could block
* Fix PixelInvaders, improve serial detection, *should* now works with regular Arduino boards
* Huge refactoring, improve unit testing


## Changelog v1.4.1 to v1.5.0 (22. October 2013)
    116 files changed, 12084 insertions(+), 5503 deletions(-)
    
* Add new Help Tab, display a short help how to use PixelController
* Add Zoom Effect, add Zoom options (zoom in, zoom out, vertical, horizontal)
* Add Textwriter option (PingPong scrolling, left scrolling)
* Add new Minimum and Maximum Mixer
* Add new keyboard shortcut to select tabs (arrow keys)
* Add new GUI option to save screenshot of all visuals
* Add missing option 'snake cabling' and 'mapping' for TPM2.Net device
* Add missing option 'snake cabling' and 'mapping' for Artnet/E1.31 devices
* Add multipanel support for Artnet/E1.31 output
* Fix use colorset name (instead of index) if a preset is saved
* Fix colorscroll GUI element initialization
* Fix Effect Rotate90 for non square output
* Fix Effect BeatHorizShift for exotic resolutions
* Fix Effect Rotozoom for exotic resolutions
* Fix Effect Texture deform for exotic resolutions
* Fix Mixer Checkbox for exotic resolutions
* Fix PixConCli JMX Query  
* Fix ColorSet, rarely visible smoothness bug
* Cleanup GUI, make grouping of controls more strict
* Reduce filesize of distribution file, remove unneeded files
* Remove support for Adavision product - does not exist anymore
* Update PixConCli, improve message handling
* Update Fire Generator, remove visible random pixels
* Update Config example files 
* Update PixelInvaders firmware, use the TPM2Net protocol to send data 
   Testcase: 2 PixelInvader Panels, ColorScroll Generator, no sound update
   Framerate old fw: 35fps, Framerate new fw: 140fps -> 400% faster!
   

## Changelog v1.4.0 to v1.4.1 (19. September 2013)
    58 files changed, 2376 insertions(+), 2490 deletions(-)
	
* Fix update TPM2.net implementation (add total packet bytes)
* Fix cleanup unused files
* Fix OSC listener, increased buffer size to 50kb so large image content can be sent via OSC
* Fix OSC address, expected: "/NAME" was: "NAME"
* Fix shuffle Colorset in Random mode, fix Issue#37
* Feature: disable beat detection if sound volume is too low
* Remove all references to the MD5 class, use Adler as caching mechanism (20x faster)
* Keep aspect ratio if internal buffer size is calculated


## Changelog v1.3.1 to v1.4.0 (4. July 2013)
    115 files changed, 2705 insertions(+), 1451 deletions(-)
	
* Add support for E1.31 devices
* Add more 8x8 icon images
* Add new effect, rotate 90 degrees
* Add option to hide internal Visuals to save CPU power
* Add new generator: VisualZero. use the first visual as generator, you can chain you effects.
* Add new random preset mode
* Add Pass through mode: ScreenCaputure (always) and OSC Generator (depending on input) are now 24bpp generators, you cannot use effects/mixer/colorsets
* Add option to adjust the color for each PixelInvaders panel
* Add new gamma option, GAMMA 2.2 
* Fix: Checkbox mixer for larger resolutions
* Fix: find .png files in GUI
* Fix: Do not change selected visual and output if a preset is loaded
* Fix: Handle missing config files
* Fix: if the Null output and another output is configured, ignore null output and start PixelController
* Fix: Preset numbering and loading Preset 0
* Fix: Load Presets: do not display non-existent files in the GUI
* Fix: Textwriter generator, use 8bpp as temp buffer to prevent artefacts
* Fix: Improve Add and Sub Mixer (rollover) 
* PixelInvaders Output: Use Adler32 as caching mechanism instead of MD5 - much faster.
                       md5 needs about 1ms to generate, adler32 needs about 0.2ms.
                         2 panels, 50fps, save: 80ms/s, 6 panels, 20fps, save: 96ms/s
* Rainbowduino Output: Cleanup init code. 
* Optimize PixelInvaders Firmware, use Serial.readBytes instead Serial.read() (see http://www.pjrc.com/teensy/benchmark_usb_serial_receive.html), PixelInvaders Firmware is now 20% faster.
* Add a lot of new presets and cleanup unused presets


## Changelog v1.3.0 to v1.3.1 (7. March 2013)
    82 files changed, 3416 insertions(+), 746 deletions(-)

* Updated PixelInvaders firmware, more speed, improved image quality
* Add two generic (OSC) input generators, see Processing examples (integration/Processing/OscSendImageData) how to use it. Hint: They get selected in random mode only if they are active (receiving data)
* Update OSC Server buffer size (32k) to accept image data
* Add OSC Statistics in GUI/JMX
* Artnet Output: respect color order (panel.color.order setting)
* Artnet Output: Add option to configure the Artnet broadcast address (was default 2.255.255.255)
* TPM2 Output: fix layout settings, for example ROTATE_180 caused an exception
* Serial Outputs (AdaVision, Rainbowduino v3, TPM2): fix serial port name issue, case sensitive (Issue #30)
* Add PixelInvaders Network device support using ser2net (for example using a Raspberry PI)
* Add output connection status for PixelInvaders panels
* Support Kinect for Xbox 360 via external Processing Sketch and OSC Generator
* Fix NPE during application launch and a key is pressed
* Fix NPE if using keyboard shortcuts to select a non existent generator (for example capture generator)
* Fix possible Freeze during Random Mode


## Changelog v1.2.x to v1.3.0 (31. January 2013)
    313 files changed, 65869 insertions(+), 11626 deletions(-)

* Add integrated GUI!
* Add Color-Sets, predefined color definition for all generators, effects and mixers
* Add support for UDP output device
* Add support for TPM2 serial output devices
* Add support for TPM2 net output devices
* Add support for Rainbowduino v3 output devices
* Add start scripts for OSX
* Add flipX and flipY effects
* Add gamma correction option
* support more output formats (not only RGB and RBG), thanks to noxx6
* Updated PixelInvaders SPI firmware (lpd6803 and ws2801), much faster than the bit banging version
* Update AdaVision driver
* Texturedeform is not a generator anymore but an effect
* Integrate OSC Server
* Reduce CPU load
* Fixed Blinkenlight video player race condition
* improved RXTX lib, fix some 64bit issues
* configuration fixes
* a lot of bugfixes
* Removed emboss effect, as the result was ugly
* Removed xor and MinusHalf mixer, as the result was ugly
* Removed image zoomer generator, as it looks ugly and was a duplicate.
* Add Manual


## Changelog v1.1 to v1.2 (29. November 2011)
    30 files changed, 840 insertions(+), 268 deletions(-)

* PixelInvaders Firmware, fixed strip.doSwapBuffersAsap(0)
* Support AdaVision


## Changelog v1.0.3 to v1.1 (8. November 2011)
    85 files changed, 4915 insertions(+), 3380 deletions(-)

* Add notification if random mode is selected or not
* more threads! the output gets calculated on the fly, this means wait until the visuals are generated. If they would run in separate thread, that would be faster...
* decoupling visual generation from output update, should increase performance
* New Generators: ColorScroll, ColorFade and ScreenCapture. Removed Simple Colors
* PixelInvaders firmware: update initial image in firmware
* PixelInvaders firmware: add autonomous firmware mode, run simple animation if no computer is connected
* ScreenCapture: make capturing area visible to user
* create GUI for tablet, Application Control (OSC + MIDI) (http://charlie-roberts.com/Control)
* manual mapping to support fancy led mapping (OnePanelResolutionAwareOutput class). define a configuration property like output.mapping=1,2,5,6,3,4,7,8 ...
* Log output to a file instead the command line
* Artnet output: make first universe id configurable
* possible to start PixelController with a preset

