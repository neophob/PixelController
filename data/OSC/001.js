/*
pixelController Control
tested on android, motorola xoom, 1280 x 800
 */
loadedInterfaceName = "pixelControl001";
interfaceOrientation = "landscape";
pages = [[

/* --- data start --- */

/* Generator slider */
{
    "name":"SliderGen",
    "type":"MultiSlider",
    "x":0, "y":0,
    "width":.2, "height":.4,
    "numberOfSliders" : 2,
    "stroke": "#62627e",
    "color": "#c4c4fc",
},
{
    "name": "infoGen",
    "type": "Label",
    "x": .0, "y": .4,
    "width": .5, "height": .1,
    "value": "Generator",
    "verticalCenter": false,
    "align": "left",
},

/* Mixer slider */
{
    "name":"SliderMix",
    "type":"MultiSlider",
    "x":0, "y":0.5,
    "width":.20, "height":.4,
    "numberOfSliders" : 2,
    "stroke": "#7e7e62",
    "color": "#fcfcc4",
},
{
    "name": "infoMix",
    "type": "Label",
    "x": .0, "y": .9,
    "width": .5, "height": .1,
    "value": "Effect",
    "verticalCenter": false,
    "align": "left",
},


/* RGB Knobs */
{
    "name":"knobR",
    "type":"Knob",
    "x":0.5, "y":0,
    "radius":.14,
    "color": "#ff0000",
    "stroke": "#880000",
},
{
    "name":"knobG",
    "type":"Knob",
    "x":0.65, "y":0,
    "radius":.14,
    "color": "#00ff00",
    "stroke": "#008800",
},
{
    "name":"knobB",
    "type":"Knob",
    "x":0.8, "y":0,
    "radius":.14,
    "color": "#0000ff",
    "stroke": "#000088",    
},



/* --- data end --- */

]
];