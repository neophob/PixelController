/*
PixelController OSC Interface for PixelInvaders (www.pixelinvaders.ch)
using Control (http://charlie-roberts.com/Control/)

Tested on Android, Motorola XOOM, 1280 x 800
javascript reference: http://www.javascriptkit.com/jsref/math.shtml

(c) by michael Vogt/neophob.com 2011
 */

loadedInterfaceName = "pixelControl002";
interfaceOrientation = "landscape";

generatornames=["Off", "Blinken", "Image", "Plasma", "ColScrol", "Fire", "Metaballs", "Pixel", "Textur", "Text", "ImgZoom", "Cell", "Plasma2", "Frequenzy", "Drops", "Capture", "ColFad"]
effectnames=["Off", "Inverter", "Rotozoom", "BeatS H", "BeatS V", "Volumn", "Tint", "Threshold", "Emboss", "Zoom"]
mixernames=["Off", "AddSat", "Multiply", "Mix", "Neg", "Checkboard", "Multiply", "Xor", "MinusHalf", "Either"]


/******* Constants appear on all pages *******/

constants = [

/* -- Refresh GUI Button */
{
     "name": "refreshButton",
     "type": "Button",
     "x": 0.88, "y": .7,
     "width": .1, "height": .1,
     "mode": "contact",
     "color": "#fc8000",
     "stroke": "#7e4000",
     "ontouchstart": "interfaceManager.refreshInterface()",
     "label": "Refresh GUI",
     "labelSize": "18",
},
/* -- Button for enabling Menu/Toolbar */
{
    "name": "tabButton",
    "type": "Button",
    "x": 0.88, "y": .81,
    "width": .1, "height": .1,
    "mode": "toggle",
    "color": "#fc8000",
    "stroke": "#7e4000",
    "isLocal": true,
    "ontouchstart": "if(this.value == this.max) { control.showToolbar(); } else { control.hideToolbar(); }",
    "label": "Menu",
    "labelSize": "18",
},
];

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
    "bounds": [0,.35,.25,.1],
    "value": "Generator: --",
    "verticalCenter": false,
    "size": "18",
    "align": "left",
},

/* Visual NR slider */
{
    "name":"VisualNr",
    "type":"Slider",
    "x":0.25, "y":0,
    "width":.075, "height":.45,
    "stroke": "#754545",
    "color": "#CC7777",
    "min": 0, "max": 2.9,
    "isXFader" : false,
    "isVertical" : true,
    "onvaluechange": "infoVisual.changeValue( 'Visual Nr: '+ Math.floor(this.value) );",
},
{
    "name": "infoVisual",
    "type": "Label",
    "bounds": [.25,.35,.25,.1],
    "value": "Visual: --",
    "verticalCenter": false,
    "size": "18",
    "align": "left",
},


/* Effect slider */
{
    "name":"SliderEffect",
    "type":"MultiSlider",
    "x":0, "y":0.5,
    "width":.15, "height":.40,
    "numberOfSliders" : 2,
    "min" : 0, "max" : 9.9,
    "stroke": "#7e7e62",
    "color": "#fcfcc4",
    "onvaluechange" : "infoEffect.changeValue( 'Effect: '+ effectnames[ Math.floor(SliderEffect.children[0].value) ] +'/'+ effectnames[ Math.floor(SliderEffect.children[1].value) ] );",
},
{
    "name": "infoEffect",
    "type": "Label",
    "bounds": [0,.8,.25,.1],    
    "value": "Effect: --",
    "verticalCenter": false,
    "size": "18",
    "align": "left",
},

/* Mixer slider */
{
    "name":"SliderMix",
    "type":"Slider",
    "bounds": [.25,.5,.075,.4],    
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
    "x": .25, "y": .8,
    "width": .25, "height": .1,
    "value": "Mixer: --",
    "verticalCenter": false,
    "size": "18",
    "align": "left",
},

/* RGB Knobs */
{
    "name":"knobR",
    "type":"Knob",
    "x":.5, "y":0,
    "radius":.145,
    "color": "#ff0000",
    "stroke": "#880000",
},
{
    "name":"knobG",
    "type":"Knob",
    "x":.65, "y":0,
    "radius":.145,
    "color": "#00ff00",
    "stroke": "#008800",
},
{
    "name":"knobB",
    "type":"Knob",
    "x":.8, "y":0,
    "radius":.145,
    "color": "#0000ff",
    "stroke": "#000088",   
},
{
    "name":"knobThreshold",
    "type":"Knob",
    "x":.65, "y":.25,
    "radius":.145,
    "stroke": "#7e7e62",
    "color": "#fcfcc4",
},
{
    "name":"knobRotozoom",
    "type":"Knob",
    "x":.8, "y":.25,
    "radius":.145,
    "stroke": "#7e7e62",
    "color": "#fcfcc4",
    "centerZero" : true, 
},
/* -- Activate Tint Effect on all Outputs Button */
{
     "name": "activateTintButton",
     "type": "Button",
     "x": 0.5, "y": .25,
     "width": .145, "height": .08,
     "mode": "contact",
     "stroke": "#7e7e62",
     "color": "#fcfcc4",
     "label": "ACTIVATE TINT FX",
     "labelSize": "14",
     "oninit": "activateTintButton.fillDiv.style.borderWidth = '2px';",
},
{
     "name": "activateTresholdButton",
     "type": "Button",
     "x": 0.5, "y": .33,
     "width": .145, "height": .08,
     "mode": "contact",
     "stroke": "#7e7e62",
     "color": "#fcfcc4",
     "label": "ACTIVATE THRESHOLD FX",
     "labelSize": "14",
     "oninit": "activateTresholdButton.fillDiv.style.borderWidth = '2px';",
},
{
     "name": "activateRotozoomButton",
     "type": "Button",
     "x": 0.5, "y": .41,
     "width": .145, "height": .08,
     "mode": "contact",
     "stroke": "#7e7e62",
     "color": "#fcfcc4",
     "label": "ACTIVATE ROTOZOOM FX",
     "labelSize": "14",
     "oninit": "activateRotozoomButton.fillDiv.style.borderWidth = '2px';",
},

/* -- RANDOM MODE Button */
{
     "name": "randomToggleButton",
     "type": "Button",
     "x": 0.5, "y": .5,
     "width": .145, "height": .08,
     "mode": "toggle",
     "color": "#fc8000",
     "stroke": "#7e4000",
     "label": "RANDOM MODE",
     "labelSize": "18",
     "oninit": "randomToggleButton.fillDiv.style.borderWidth = '2px';",          
},
/* -- RANDOM Buttons */
{
     "name": "fireRandomButton",
     "type": "Button",
     "x":.5, "y":.58,
     "width":.145, "height":.08,
     "mode": "contact",
     "color": "#fc8000",
     "stroke": "#7e4000",
     "label": "RANDOM",
     "labelSize": "18",
     "oninit": "fireRandomButton.fillDiv.style.borderWidth = '2px';",     
},
/* -- RANDOM PRESET Buttons */
{
     "name": "fireRandomPresentButton",
     "type": "Button",
     "x":.5, "y":.66,
     "width":.145, "height":.08,
     "mode": "contact",
     "color": "#fc8000",
     "stroke": "#7e4000",
     "label": "RANDOM PRESET",
     "labelSize": "18",
	 "oninit": "fireRandomPresentButton.fillDiv.style.borderWidth = '2px';",    
},
/* -- Visual 1 to ALL OUTPUTS Button */
{
     "name": "visualOneToAll",
     "type": "Button",
     "x": .65, "y": .5,
     "width": .15, "height": .08,
     "mode": "contact",
     "stroke": "#754545",
     "color": "#CC7777",
     "label": "VISUAL 1 TO ALL PANELS",
     "labelSize": "14",
	 "oninit": "visualOneToAll.fillDiv.style.borderWidth = '2px';",     
},
/* -- Visual 2 to ALL OUTPUTS Button */
{
     "name": "visualTwoToAll",
     "type": "Button",
     "x": .65, "y": .58,
     "width": .15, "height": .08,
     "mode": "contact",
     "stroke": "#754545",
     "color": "#CC7777",
     "label": "VISUAL 2 TO ALL PANELS", 
     "labelSize": "14",
	 "oninit": "visualTwoToAll.fillDiv.style.borderWidth = '2px';",
},
/* -- Visual 3 to ALL OUTPUTS Button */
{
     "name": "visualThreeToAll",
     "type": "Button",
     "x": .65, "y": .66,
     "width": .15, "height": .08,
     "mode": "contact",
     "stroke": "#754545",
     "color": "#CC7777",
     "label": "VISUAL 3 TO ALL PANELS", 
     "labelSize": "14",
	 "oninit": "visualThreeToAll.fillDiv.style.borderWidth = '2px';",
},
/* -- Visual 1 2 to 1 2 Button */
{
     "name": "visual1To1",
     "type": "Button",
     "x": .65, "y": .74,
     "width": .15, "height": .08,
     "mode": "contact",
     "stroke": "#754545",
     "color": "#CC7777",
     "label": "VISUAL 1 TO 1, 2 TO 2", 
     "labelSize": "14",
	 "oninit": "visual1To1.fillDiv.style.borderWidth = '2px';",
},

/* -- next page --*/
{
    "name": "nextB",
    "type": "Button",
    "bounds": [.88,.59,.1,.1], 
    "label": "Presets",
    "labelSize": "18",
    "mode": "contact",    
    "ontouchstart": "control.changePage(1);",
    "color": "#fc8000",
    "stroke": "#7e4000",
},

],

/********** PAGE 2 *************/
[

/* -- label -- */
{
    "name":"page2Label",
    "type":"Label",
    "value":"SELECTED PRESET ID: -",
    "size": "40",
    "bounds": [0,0,1,.1],
},

/* -- multibutton -- */
{
     "name" : "currentPresent",
     "type" : "MultiButton",
     "bounds": [0,.1,.85,.8],
     "rows" : 12, "columns" : 8,
     "mode" : "momentary",
     "color": "#fc8000",
     "stroke": "#7e4000",
     "onvaluechange": "page2Label.changeValue( 'SELECTED PRESET ID: ' + this.childID );",
},

/* -- next page --*/
{
    "name": "nextA",
    "type": "Button",
    "bounds": [.88,.59,.1,.1], 
    "label": "Control",
    "labelSize": "18",
    "mode": "contact",    
    "ontouchstart": "control.changePage(0);",
    "color": "#fc8000",
    "stroke": "#7e4000",
},

/* -- LOAD -- */
{
     "name" : "loadPresent",
     "type" : "Button",
     "bounds": [.88,.1,.1,.1],
     "mode" : "momentary",
     "color": "#fc8000",
     "stroke": "#7e4000",
     "label": "Load",
     "labelSize": "18",
     "oninit": "loadPresent.fillDiv.style.borderWidth = '2px';",
},
/* -- SAVE -- */
{
     "name" : "savePresent",
     "type" : "Button",
     "bounds": [.88,.2,.1,.1],
     "mode" : "momentary",
     "color": "#fc8000",
     "stroke": "#7e4000",
     "label": "Save",
     "labelSize": "18",     
     "oninit": "savePresent.fillDiv.style.borderWidth = '2px';",
},

],

];



