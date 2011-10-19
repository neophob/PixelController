/*
PixelController OSC Interface for PixelInvaders (www.pixelinvaders.ch)
using Control (http://charlie-roberts.com/Control/)

Tested on Android, Motorola XOOM, 1280 x 800
javascript reference: http://www.javascriptkit.com/jsref/math.shtml

(c) by michael Vogt/neophob.com 2011
 */

loadedInterfaceName = "pixelControl002";
interfaceOrientation = "landscape";

pages = [[

/* --- data start --- */

/* Generator slider */
{
    "name":"SliderGen",
    "type":"MultiSlider",
    "x":0, "y":0,
    "width":.2, "height":.45,
    "numberOfSliders" : 2,
    "min" : 0, "max" : 16.9,
    "stroke": "#62627e",
    "color": "#c4c4fc",
    "onvaluechange" : "infoGen.changeValue( 'Generator: '+Math.floor(SliderGen.children[0].value) +'/'+Math.floor(SliderGen.children[1].value) );"    
},
{
    "name": "infoGen",
    "type": "Label",
    "x": .0, "y": .45,
    "width": .5, "height": .05,
    "value": "Generator: --",
    "verticalCenter": false,
    "align": "left",
},

/* Effect slider */
{
    "name":"SliderEffect",
    "type":"MultiSlider",
    "x":0, "y":0.5,
    "width":.2, "height":.40,
    "numberOfSliders" : 2,
    "stroke": "#7e7e62",
    "min" : 0, "max" : 9.9,
    "color": "#fcfcc4",
    "onvaluechange" : "infoEffect.changeValue( 'Effect: '+Math.floor(SliderEffect.children[0].value) +'/'+Math.floor(SliderEffect.children[1].value) );"
},
{
    "name": "infoEffect",
    "type": "Label",
    "x": .0, "y": .9,
    "width": .125, "height": .1,
    "value": "Effect",
    "verticalCenter": false,
    "align": "left",
},

/* Mixer slider */
{
    "name":"SliderMix",
    "type":"Slider",
    "x":0.25, "y":0.5,
    "width":.1, "height":.4,
    "stroke": "#454545",
    "color": "#999999",
    "min" : 0, "max" : 9.9,
    "isXFader" : false,
    "isVertical" : true,
    "onvaluechange" : "infoMixValue.changeValue( Math.floor(this.value) );"
},
{
    "name": "infoMix",
    "type": "Label",
    "x": .25, "y": .9,
    "width": .125, "height": .1,
    "value": "Mixer value:",
    "verticalCenter": false,
    "align": "left",
},
{
    "name": "infoMixValue",
    "type": "Label",
    "x": .375, "y": .9,
    "width": .125, "height": .1,
    "value": "--",
    "verticalCenter": false,
    "align": "left",
},

/* RGB Knobs */
{
    "name":"knobR",
    "type":"Knob",
    "x":0.5, "y":0,
    "radius":.145,
    "color": "#ff0000",
    "stroke": "#880000",
},
{
    "name":"knobG",
    "type":"Knob",
    "x":0.65, "y":0,
    "radius":.145,
    "color": "#00ff00",
    "stroke": "#008800",
},
{
    "name":"knobB",
    "type":"Knob",
    "x":0.8, "y":0,
    "radius":.145,
    "color": "#0000ff",
    "stroke": "#000088",    
},
/* -- Activate Tint Effect on all Outputs Button */
{
     "name": "activateTintButton",
     "type": "Button",
     "x": 0.5, "y": .25,
     "width": .1, "height": .1,
     "mode": "momentary",
     "color": "#fc8000",
     "stroke": "#7e4000",
},


/* -- Multi Buttons */
{
     "name": "myButton",
     "type": "MultiButton",
     "x":.5, "y":.5,
     "width":.1, "height":.4,
     "rows":4, "columns":1,
     "mode": "momentary",
     "color": "#fc8000",
     "stroke": "#7e4000",    
},
/* -- Random Button */
{
     "name": "randomButton",
     "type": "Button",
     "x": 0.75, "y": .5,
     "width": .1, "height": .1,
     "mode": "toggle",
     "color": "#fc8000",
     "stroke": "#7e4000",     
},
/* -- Refresh Button */
{
     "name": "refreshButton",
     "type": "Button",
     "x": 0.75, "y": .7,
     "width": .1, "height": .1,
     "mode": "momentary",
     "color": "#fc8000",
     "stroke": "#7e4000",
     "ontouchstart": "interfaceManager.refreshInterface()",
},
/* --- data end --- */

]
];
