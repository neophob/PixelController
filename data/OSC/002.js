/*
PixelController OSC Interface for PixelInvaders (www.pixelinvaders.ch)
using Control (http://charlie-roberts.com/Control/)

Tested on Android, Motorola XOOM, 1280 x 800
javascript reference: http://www.javascriptkit.com/jsref/math.shtml

(c) by michael Vogt/neophob.com 2011
 */

loadedInterfaceName = "pixelControl002";
interfaceOrientation = "landscape";

generatornames=["Off", "Blinken", "Image", "Plasma", "ColScrol", "Fire", "Metaballs", "Pixel", "Textur", "Text", "ImgZoom", "Cell", "Plsama2", "Frequenzy", "Drops", "Capture", "ColFad"]
effectnames=["Off", "Inverter", "Rotozoom", "BeatS H", "BeatS V", "Volumn", "Tint", "Threshold", "Emboss", "Zoom"]
mixernames=["Off", "AddSat", "Multiply", "Mix", "Neg", "Checkboard", "Multiply", "Xor", "MinusHalf", "Either"]

pages = [
/********** PAGE 1 *************/
[

/* Generator slider */
{
    "name":"SliderGen",
    "type":"MultiSlider",
    "x":0, "y":0,
    "width":.15, "height":.45,
    "numberOfSliders" : 2,
    "min" : 0, "max" : 16.9,
    "stroke": "#62627e",
    "color": "#c4c4fc",
    "onvaluechange" : "infoGen.changeValue( 'Generator: '+generatornames[ Math.floor(SliderGen.children[0].value) ] +'/'+ generatornames[ Math.floor(SliderGen.children[1].value) ] );", 
},
{
    "name": "infoGen",
    "type": "Label",
    "x": .0, "y": .45,
    "width": .25, "height": .05,
    "value": "Generator: --",
    "verticalCenter": false,
    "align": "left",
},

/* Visual NR slider */
{
    "name":"VisualNr",
    "type":"Slider",
    "x":0.25, "y":0,
    "width":.075, "height":.45,
    "stroke": "#454545",
    "color": "#999999",
    "min": 0, "max": 4.9,
    "isXFader" : false,
    "isVertical" : true,
    "onvaluechange": "infoVisual.changeValue( 'Visual: '+ Math.floor(this.value) );",
},
{
    "name": "infoVisual",
    "type": "Label",
    "x": .25, "y": .45,
    "width": .25, "height": .1,
    "value": "Visual: --",
    "verticalCenter": false,
    "align": "left",
},


/* Effect slider */
{
    "name":"SliderEffect",
    "type":"MultiSlider",
    "x":0, "y":0.5,
    "width":.15, "height":.40,
    "numberOfSliders" : 2,
    "stroke": "#7e7e62",
    "min" : 0, "max" : 9.9,
    "color": "#fcfcc4",
    "onvaluechange" : "infoEffect.changeValue( 'Effect: '+ effectnames[ Math.floor(SliderEffect.children[0].value) ] +'/'+ effectnames[ Math.floor(SliderEffect.children[1].value) ] );",
},
{
    "name": "infoEffect",
    "type": "Label",
    "x": .0, "y": .9,
    "width": .25, "height": .1,
    "value": "Effect: --",
    "verticalCenter": false,
    "align": "left",
},

/* Mixer slider */
{
    "name":"SliderMix",
    "type":"Slider",
    "x":0.25, "y":0.5,
    "width":.075, "height":.4,
    "stroke": "#454545",
    "color": "#999999",
    "min": 0, "max": 9.9,
    "isXFader" : false,
    "isVertical" : true,
    "onvaluechange": "infoMix.changeValue( 'Mixer: '+mixernames[Math.floor(this.value)] );",
},
{
    "name": "infoMix",
    "type": "Label",
    "x": .25, "y": .9,
    "width": .25, "height": .1,
    "value": "Mixer: --",
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
     "label": "ACTIVATE TINT FX",
     "oninit": "activateTintButton.fillDiv.style.borderWidth = '2px';",
},

/* -- RANDOM MODE Button */
{
     "name": "randomToggleButton",
     "type": "Button",
     "x": 0.5, "y": .5,
     "width": .1, "height": .1,
     "mode": "toggle",
     "color": "#fc8000",
     "stroke": "#7e4000",
     "label": "RANDOM MODE",
     "oninit": "randomToggleButton.fillDiv.style.borderWidth = '2px';",          
},
/* -- RANDOM Buttons */
{
     "name": "fireRandomButton",
     "type": "Button",
     "x":.5, "y":.6,
     "width":.1, "height":.1,
     "mode": "momentary",
     "color": "#fc8000",
     "stroke": "#7e4000",
     "label": "RANDOM",
     "oninit": "fireRandomButton.fillDiv.style.borderWidth = '2px';",     
},
/* -- RANDOM PRESENT Buttons */
{
     "name": "fireRandomPresentButton",
     "type": "Button",
     "x":.5, "y":.7,
     "width":.1, "height":.1,
     "mode": "momentary",
     "color": "#fc8000",
     "stroke": "#7e4000",
     "label": "RND PRESENT", 
	 "oninit": "fireRandomPresentButton.fillDiv.style.borderWidth = '2px';",    
},
/* -- Visual 1 to ALL OUTPUTS Button */
{
     "name": "visualOneToAll",
     "type": "Button",
     "x": .65, "y": .5,
     "width": .15, "height": .1,
     "mode": "momentary",
     "color": "#fc8000",
     "stroke": "#7e4000",
     "label": "VISUAL 1 TO ALL PANELS",
	 "oninit": "visualOneToAll.fillDiv.style.borderWidth = '2px';",     
},
/* -- Visual 2 to ALL OUTPUTS Button */
{
     "name": "visualTwoToAll",
     "type": "Button",
     "x": .65, "y": .6,
     "width": .15, "height": .1,
     "mode": "momentary",
     "color": "#fc8000",
     "stroke": "#7e4000",
     "label": "VISUAL 2 TO ALL PANELS", 
	 "oninit": "visualTwoToAll.fillDiv.style.borderWidth = '2px';",
},
/* -- Refresh GUI Button */
{
     "name": "refreshButton",
     "type": "Button",
     "x": 0.88, "y": .8,
     "width": .1, "height": .1,
     "mode": "momentary",
     "color": "#fc8000",
     "stroke": "#7e4000",
     "ontouchstart": "interfaceManager.refreshInterface()",
     "label": "REFRESH GUI",
},


]

/********** PAGE 2 *************/

];



