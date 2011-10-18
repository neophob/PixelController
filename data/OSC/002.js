/*
pixelController Control
tested on android, motorola xoom, 1280 x 800
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
},
{
    "name": "infoGen",
    "type": "Label",
    "x": .0, "y": .45,
    "width": .5, "height": .05,
    "value": "Generator",
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
},
{
    "name": "infoEffect",
    "type": "Label",
    "x": .0, "y": .90,
    "width": .25, "height": .1,
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
},
{
    "name": "infoMix",
    "type": "Label",
    "x": .25, "y": .9,
    "width": .25, "height": .1,
    "value": "Mixer",
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


/* Buttons */
{
     "name": "myButton",
     "type": "MultiButton",
     "x":.65, "y":.5,
     "width":.2, "height":.4,
     "rows":4, "columns":2,
     "mode": "momentary",
     "color": "#fc8000",
     "stroke": "#7e4000",    
},

/* --- data end --- */

]
];