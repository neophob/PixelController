# Roadmap PixelController

## Version 2.1
* remote setup, do not display duplicate entries but display whats missing.
* togle freeze
* finish / remove teensy3 firmware

## Version 2.2
* Update pixconcli
* PixelInvaders firmware: support Teensy 3 (IntervalTimer Library)
* PixelInvaders firmware: Support ambient color for the output panel(s)
* Support Multiple Output devices with different matrix sizes
* Simplify output configuration
* Add console to GUI

# Backlog

## Features

* Support both SPI output ports on RPI - automatic switch to second SPI port
* Independent preview visual, today generators and effects exists only once
* add slider fader if a preset is loaded   
* Support Artnet remote, should be independent from the artnet output, should work more like the osc input.
* Processing SDK
* current RMI API use targetip/port - but should use sockets. current limit: can not use same socket for client/server
* Remove Adler32 hash code, replace with something faster (xxhash, murmur2)
* Support multiple panels for tpm2serial output
* Support autodetection for tpm2 protocol
* Particle generator (rain,snow,fireworks...) remember: maybe rip from cocos2d particle system, should replace fire and metaballs
* Option to invert GUI colors
* Add OpenGL Shader Generator/Effects, using JOGL
* More GUI improvements (scaling, bigger fonts, save window position, option to bring both windows to front)
* Option to modify colorsets of all visuals
* add more fader (pixel fader for example)
* support more than 2 rows
* Option to save output of PixelController (byte stream) to a flat file (Offline usage)
* Option to use system audio as input source, currently its only mic input. 
* Syphon support https://code.google.com/p/syphon-implementations/
* OLA (http://www.opendmx.net/index.php/Open_Lighting_Architecture) input plugin see https://github.com/neophob/ola-to-tpm2net
* Game of life generator
* Option to load a 24bpp image in pass through mode
* new textscroller that displays only a character per timer
* option to configure strange mappings easy, use a gui? html5 frontend?
* GUI: Sort generator, effect and mixer dropdown lists by name, needs an update in the gui logic
* More layouts (Half/Half, 3/1...). Current layouts are hardware dependent. New layout shouldn't be. 

```
    +---+---+---+
    | a | b | c |
    +---+---+---+   
    | d | e | f |
    +---+---+---+   
     Example: 3/1 Layout defines 2 virtual panels, panel 1 using physical panel a,b,d,e panel 2 using physical panel c and f.
     Example: Fancy Layout defines 3 virtual panels, panel 1 using physical panel a,b,d,e, panel 2 using physical panel c panel 3 using physical panel f
```

## Design
* Create an API for the core (http://lcsd05.cs.tamu.edu/slides/keynote.pdf, http://theamiableapi.com/2012/01/16/java-api-design-checklist/)
* Switch to logback
* Use google-guice as DI container

## Bugfixes
* Do not freeze application if output fails (disconnect serial controller)
* verify all implemented image rotation for the output devices make sense
* Matrix device specific settings (bpp, cabling, mapping etc) should be inside the MatrixData class.
* Merge code of the output package, a lot of duplicate code 
* Resolution 1x96 is unhealthy for the gui...


# Rejected backlog features

* UDP output: support multiple output devices (probably not, alternative: use tpm2.net)
* Use Mozilla Rhino to create dynamic generators/effects -> using JDK1.6 Rhino is just too slow and cpu intensive (it needs about 40% cpu power with one Rhino js generator). v8 (java8) should a an alternative, however v8 is OS dependent.

