/**
 * Copyright (C) 2011 Michael Vogt <michu@neophob.com>
 *
 * This file is part of PixelController.
 *
 * PixelController is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PixelController is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with PixelController.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.neophob.sematrix.output.gui;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import processing.core.PApplet;
import processing.core.PImage;

import com.neophob.sematrix.color.ColorSet;
import com.neophob.sematrix.effect.Effect.EffectName;
import com.neophob.sematrix.effect.PixelControllerEffect;
import com.neophob.sematrix.generator.ColorScroll.ScrollMode;
import com.neophob.sematrix.generator.Generator.GeneratorName;
import com.neophob.sematrix.generator.PixelControllerGenerator;
import com.neophob.sematrix.glue.Collector;
import com.neophob.sematrix.glue.OutputMapping;
import com.neophob.sematrix.glue.ShufflerOffset;
import com.neophob.sematrix.glue.Visual;
import com.neophob.sematrix.input.Sound;
import com.neophob.sematrix.jmx.TimeMeasureItemGlobal;
import com.neophob.sematrix.mixer.Mixer.MixerName;
import com.neophob.sematrix.output.gui.helper.FileUtils;
import com.neophob.sematrix.output.gui.helper.Theme;

import controlP5.Button;
import controlP5.CheckBox;
import controlP5.ControlP5;
import controlP5.ControllerInterface;
import controlP5.DropdownList;
import controlP5.RadioButton;
import controlP5.Slider;
import controlP5.Tab;
import controlP5.Textlabel;
import controlP5.Toggle;


/**
 * Display the internal Visual buffers in full resolution
 * 
 * @author michu
 */
public class GeneratorGui extends PApplet {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 2344499301021L;

    private static final int SELECTED_MARKER = 10;

    /** The log. */
    private static final Logger LOG = Logger.getLogger(GeneratorGui.class.getName());

    /** The y. */
    private int x,y;

    /** The p image. */
    private PImage pImage=null;

    private ControlP5 cp5;
    private DropdownList generatorListOne, effectListOne;
    private DropdownList generatorListTwo, effectListTwo;
    private DropdownList mixerList;
    private RadioButton selectedVisualList;
    private RadioButton selectedOutputs;
    private Button randomSelection, randomPresets;
    private Toggle toggleRandom;

    //Effect Tab    
    private Slider thresholdSlider;	

    //Generator Tab
    private DropdownList blinkenLightsList, imageList, textureDeformOptions;	
    private Button freezeUpdate;
    
    //Output Tab
    private DropdownList dropdownOutputVisual;
    private DropdownList dropdownOutputFader;    

    //All Output Tab
    private DropdownList allOutputTabVis;
    private DropdownList allOutputTabFader;
    private DropdownList colorScrollList;
    private DropdownList colorSetList;

    //preset tab
    private RadioButton presetButtons;
    private Button loadPreset, savePreset;
    
    private CheckBox randomCheckbox;
    
    /** The target y size. */
    private int targetXSize, targetYSize;
    private int p5GuiYOffset;

    /**
     * Instantiates a new internal buffer.
     *
     * @param displayHoriz the display horiz
     * @param x the x
     * @param y the y
     * @param targetXSize the target x size
     * @param targetYSize the target y size
     */
    public GeneratorGui(int x, int y, int targetXSize, int targetYSize) {
        this.x = x;
        this.y = y+SELECTED_MARKER;
        this.targetXSize = targetXSize;
        this.targetYSize = targetYSize;
        this.p5GuiYOffset = targetYSize + 80;		
    }




    private final String ALWAYS_VISIBLE_TAB = "global";

    /* (non-Javadoc)
     * @see processing.core.PApplet#setup()
     */
    public void setup() {
        LOG.log(Level.INFO, "create internal buffer with size "+x+"/"+y);
        size(x,y+100);
        noSmooth();
        frameRate(Collector.getInstance().getFps());
        background(0,0,0);		
        int i=0;        		

        cp5 = new ControlP5(this);
        cp5.setAutoDraw(false);
        cp5.getTooltip().setDelay(200);
        P5EventListener listener = new P5EventListener(this);

        //selected visual
        int nrOfVisuals = Collector.getInstance().getAllVisuals().size();
        selectedVisualList = cp5.addRadioButton(GuiElement.CURRENT_VISUAL.toString(), 0, p5GuiYOffset-58);
        selectedVisualList.setItemsPerRow(nrOfVisuals);
        selectedVisualList.setNoneSelectedAllowed(false);		
        for (i=0; i<nrOfVisuals; i++) {
            String s = "VISUAL #"+(1+i);			
            Toggle t = cp5.addToggle(s, 0, 0, targetXSize, 13);
            t.setCaptionLabel(s);
            selectedVisualList.addItem(t, i);			
            cp5.getTooltip().register(s, "Select Visual "+(1+i)+" to edit");			
        }
        selectedVisualList.moveTo(ALWAYS_VISIBLE_TAB);


        Textlabel tl = cp5.addTextlabel("logo", "PixelController", 540, p5GuiYOffset+145);//480
        tl.moveTo(ALWAYS_VISIBLE_TAB);
        tl.setFont(ControlP5.synt24);

        cp5.addTextlabel("gen1", "GENERATOR#1", 3, 3+p5GuiYOffset).moveTo(ALWAYS_VISIBLE_TAB).getValueLabel().setFont(ControlP5.standard58);
        cp5.addTextlabel("gen2", "GENERATOR#2", 3+3*Theme.DROPBOX_XOFS, 3+p5GuiYOffset).moveTo(ALWAYS_VISIBLE_TAB).getValueLabel().setFont(ControlP5.standard58);
        cp5.addTextlabel("fx1", "EFFECT#1", 3+1*Theme.DROPBOX_XOFS, 3+p5GuiYOffset).moveTo(ALWAYS_VISIBLE_TAB).getValueLabel().setFont(ControlP5.standard58);
        cp5.addTextlabel("fx2", "EFFECT#2", 3+4*Theme.DROPBOX_XOFS, 3+p5GuiYOffset).moveTo(ALWAYS_VISIBLE_TAB).getValueLabel().setFont(ControlP5.standard58);
        cp5.addTextlabel("mix2", "MIXER#2", 3+2*Theme.DROPBOX_XOFS, 3+p5GuiYOffset).moveTo(ALWAYS_VISIBLE_TAB).getValueLabel().setFont(ControlP5.standard58);


        //Generator 
        generatorListOne = cp5.addDropdownList(GuiElement.GENERATOR_ONE_DROPDOWN.toString(), 
                0, p5GuiYOffset, Theme.DROPBOXLIST_LENGTH, 140);
        generatorListTwo = cp5.addDropdownList(GuiElement.GENERATOR_TWO_DROPDOWN.toString(), 
                3*Theme.DROPBOX_XOFS, p5GuiYOffset, Theme.DROPBOXLIST_LENGTH, 140);
        Theme.themeDropdownList(generatorListOne);
        Theme.themeDropdownList(generatorListTwo);
        i=0;
        for (GeneratorName gn: GeneratorName.values()) {
            generatorListOne.addItem(gn.name(), i);
            generatorListTwo.addItem(gn.name(), i);
            i++;
        }
        generatorListOne.setLabel(generatorListOne.getItem(1).getName());
        generatorListTwo.setLabel(generatorListTwo.getItem(1).getName());
        generatorListOne.moveTo(ALWAYS_VISIBLE_TAB);
        generatorListTwo.moveTo(ALWAYS_VISIBLE_TAB);

        //Effect 
        effectListOne = cp5.addDropdownList(GuiElement.EFFECT_ONE_DROPDOWN.toString(), 
                1*Theme.DROPBOX_XOFS, p5GuiYOffset, Theme.DROPBOXLIST_LENGTH, 140);
        effectListTwo = cp5.addDropdownList(GuiElement.EFFECT_TWO_DROPDOWN.toString(), 
                4*Theme.DROPBOX_XOFS, p5GuiYOffset, Theme.DROPBOXLIST_LENGTH, 140);
        Theme.themeDropdownList(effectListOne);
        Theme.themeDropdownList(effectListTwo);
        i=0;
        for (EffectName gn: EffectName.values()) {
            effectListOne.addItem(gn.name(), i);
            effectListTwo.addItem(gn.name(), i);
            i++;
        }
        effectListOne.setLabel(effectListOne.getItem(0).getName());
        effectListTwo.setLabel(effectListTwo.getItem(0).getName());
        effectListOne.moveTo(ALWAYS_VISIBLE_TAB);
        effectListTwo.moveTo(ALWAYS_VISIBLE_TAB);

        //Mixer 
        mixerList = cp5.addDropdownList(GuiElement.MIXER_DROPDOWN.toString(), 
                2*Theme.DROPBOX_XOFS, p5GuiYOffset, Theme.DROPBOXLIST_LENGTH, 140);
        Theme.themeDropdownList(mixerList);

        i=0;
        for (MixerName gn: MixerName.values()) {
            mixerList.addItem(gn.name(), i);
            i++;
        }
        mixerList.setLabel(mixerList.getItem(0).getName());
        mixerList.moveTo(ALWAYS_VISIBLE_TAB);


        //---------------------------------
        //TABS
        //---------------------------------

        final int yPosStartLabel = p5GuiYOffset+50;
        final int yPosStartDrowdown = p5GuiYOffset+36;

        cp5.getWindow().setPositionOfTabs(0, p5GuiYOffset+152);		

        //there a default tab which is present all the time. rename this tab
        Tab generatorTab = cp5.getTab("default");
        generatorTab.setLabel("GENERATOR/EFFECT");		
        Tab outputTab = cp5.addTab("SINGLE OUTPUT MAPPING");
        Tab allOutputTab = cp5.addTab("ALL OUTPUT MAPPING");		
        Tab randomTab = cp5.addTab("RANDOM SELECTION");		
        Tab presetTab = cp5.addTab("PRESETS");

        generatorTab.setColorForeground(0xffff0000);
        outputTab.setColorForeground(0xffff0000);
        allOutputTab.setColorForeground(0xffff0000);
        randomTab.setColorForeground(0xffff0000);
        presetTab.setColorForeground(0xffff0000);

        //-------------
        //EFFECT tab
        //-------------
        thresholdSlider = cp5.addSlider(GuiElement.THRESHOLD.toString(), 
        		0, 255, 255, 0*Theme.DROPBOX_XOFS, yPosStartDrowdown+60, 160, 14);
        thresholdSlider.setSliderMode(Slider.FIX);
        thresholdSlider.setGroup(generatorTab);	
        thresholdSlider.setDecimalPrecision(0);		

        Slider fxRotoSlider = cp5.addSlider(GuiElement.FX_ROTOZOOMER.toString(), 
                -127, 127, 0, 2*Theme.DROPBOX_XOFS, yPosStartDrowdown+60, 160, 14);
        fxRotoSlider.setSliderMode(Slider.FIX);
        fxRotoSlider.setGroup(generatorTab);
        fxRotoSlider.setDecimalPrecision(0);

        
        //-------------
        //Generator tab
        //-------------
        
        cp5.addTextlabel("genBlinken", "LOAD BLINKENLIGHT FILE", 3, yPosStartLabel+3).moveTo(generatorTab).getValueLabel().setFont(ControlP5.standard58);
        String path = Collector.getInstance().getPapplet().sketchPath+"/data";		

        blinkenLightsList = cp5.addDropdownList(GuiElement.BLINKENLIGHTS_DROPDOWN.toString(), 
                0, yPosStartDrowdown+16, Theme.DROPBOXLIST_LENGTH, 140);
        Theme.themeDropdownList(blinkenLightsList);
        i=0;
        for (String s: FileUtils.findBlinkenFiles(path)) {
            blinkenLightsList.addItem(s, i);
            i++;
        }
        blinkenLightsList.setLabel(blinkenLightsList.getItem(1).getName());
        blinkenLightsList.setGroup(generatorTab);
        blinkenLightsList.setHeight(100);

        //images
        cp5.addTextlabel("genImg", "LOAD IMAGE FILE", 3+1*Theme.DROPBOX_XOFS, yPosStartLabel+3).moveTo(generatorTab).getValueLabel().setFont(ControlP5.standard58);

        imageList = cp5.addDropdownList(GuiElement.IMAGE_DROPDOWN.toString(), 
                Theme.DROPBOX_XOFS, yPosStartDrowdown+16, Theme.DROPBOXLIST_LENGTH, 140);
        Theme.themeDropdownList(imageList);		
        i=0;
        for (String s: FileUtils.findImagesFiles(path)) {
            imageList.addItem(s, i);
            i++;
        }
        imageList.setLabel(imageList.getItem(1).getName());
        imageList.setGroup(generatorTab);		
        imageList.setHeight(100);

        cp5.addTextlabel("genTextdefOpt", "TEXTUREDEFORM OPTION", 3+2*Theme.DROPBOX_XOFS, yPosStartLabel+3).moveTo(generatorTab).getValueLabel().setFont(ControlP5.standard58);

        //texturedeform options		
        textureDeformOptions = cp5.addDropdownList(GuiElement.TEXTUREDEFORM_OPTIONS.toString(), 
                2*Theme.DROPBOX_XOFS, yPosStartDrowdown+16, Theme.DROPBOXLIST_LENGTH, 140);
        Theme.themeDropdownList(textureDeformOptions);		

        textureDeformOptions.addItem("Anamorphosis", 1);
        textureDeformOptions.addItem("Spiral", 2);
        textureDeformOptions.addItem("Rotating Tunnel", 3);
        textureDeformOptions.addItem("Star", 4);
        textureDeformOptions.addItem("Tunnel", 5);
        textureDeformOptions.addItem("Flower", 6);
        textureDeformOptions.addItem("Cloud", 7);
        textureDeformOptions.addItem("Planar", 8);
        textureDeformOptions.addItem("Circle", 9);
        textureDeformOptions.addItem("Spiral", 10);
        textureDeformOptions.addItem("3D Ball", 11);

        textureDeformOptions.setLabel(textureDeformOptions.getItem(1).getName());
        textureDeformOptions.setGroup(generatorTab);		
        textureDeformOptions.setHeight(80);

        //colorscroll options
        cp5.addTextlabel("genColorScroll", "COLORSCROLL OPTIONS", 3+3*Theme.DROPBOX_XOFS, yPosStartLabel+3).moveTo(generatorTab).getValueLabel().setFont(ControlP5.standard58);

        colorScrollList= cp5.addDropdownList(GuiElement.COLORSCROLL_OPTIONS.toString(), 
                3*Theme.DROPBOX_XOFS, yPosStartDrowdown+16, Theme.DROPBOXLIST_LENGTH, 140);
        Theme.themeDropdownList(colorScrollList);		

        for (ScrollMode sm: ScrollMode.values()) {
            colorScrollList.addItem(sm.name().replace("_", " "), sm.getMode());
        }
        colorScrollList.setLabel(colorScrollList.getItem(0).getName());
        colorScrollList.setGroup(generatorTab);		
        colorScrollList.setHeight(100);

        //add textfield
        //Textfield textfield = 
        cp5.addTextfield("textfield", "TEXTFIELD", "TEXTFIELD", 3+4*Theme.DROPBOX_XOFS, yPosStartLabel-16+2, Theme.DROPBOXLIST_LENGTH, 16);

		freezeUpdate = cp5.addButton(GuiElement.BUTTON_TOGGLE_FREEZE.toString(), 0,
                5*Theme.DROPBOX_XOFS, p5GuiYOffset+30, 100, 15);
		freezeUpdate.setCaptionLabel("Toggle Freeze");
		freezeUpdate.setGroup(generatorTab);
        cp5.getTooltip().register(GuiElement.BUTTON_TOGGLE_FREEZE.toString(),"freeze update");

        //-----------------
        //Single Output tab
        //-----------------				
        int nrOfOutputs = Collector.getInstance().getAllOutputMappings().size();
        selectedOutputs = cp5.addRadioButton(GuiElement.CURRENT_OUTPUT.toString(), 0, yPosStartDrowdown);
        selectedOutputs.setItemsPerRow(nrOfOutputs);
        selectedOutputs.setNoneSelectedAllowed(false);		
        for (i=0; i<nrOfOutputs; i++) {
            String s = "OUTPUT #"+(1+i);			
            Toggle t = cp5.addToggle(s, 0, 0, targetXSize, 13);
            t.setCaptionLabel(s);
            selectedOutputs.addItem(t, i);			
            cp5.getTooltip().register(s, "Select Output "+(1+i)+" to edit");			
        }
        selectedOutputs.moveTo(outputTab);

        //visual
        dropdownOutputVisual = GeneratorGuiHelper.createVisualDropdown(cp5, 
                GuiElement.OUTPUT_SELECTED_VISUAL_DROPDOWN.toString(), yPosStartDrowdown, nrOfVisuals); 
        dropdownOutputVisual.moveTo(outputTab);

        //Fader         
        dropdownOutputFader = GeneratorGuiHelper.createFaderDropdown(cp5, 
                GuiElement.OUTPUT_FADER_DROPDOWN.toString(), yPosStartDrowdown); 
        dropdownOutputFader.moveTo(outputTab);

        //--------------
        //All Output tab
        //--------------				

        cp5.addTextlabel("allOutputTabLabel", "CHANGE ALL OUTPUT MAPPINGS", 20, yPosStartDrowdown)
        .moveTo(allOutputTab).getValueLabel().setFont(ControlP5.standard58);

        allOutputTabVis = GeneratorGuiHelper.createVisualDropdown(cp5, 
                GuiElement.OUTPUT_ALL_SELECTED_VISUAL_DROPDOWN.toString(), yPosStartDrowdown, nrOfVisuals); 
        allOutputTabVis.moveTo(allOutputTab);

        //Fader         
        allOutputTabFader = GeneratorGuiHelper.createFaderDropdown(cp5, 
                GuiElement.OUTPUT_ALL_FADER_DROPDOWN.toString(), yPosStartDrowdown); 
        allOutputTabFader.moveTo(allOutputTab);

        //palette dropdown list	
        cp5.addTextlabel("colSet", "SELECT COLORSET", 5*Theme.DROPBOX_XOFS, p5GuiYOffset+3).moveTo(ALWAYS_VISIBLE_TAB).getValueLabel().setFont(ControlP5.standard58);

        colorSetList = cp5.addDropdownList(GuiElement.COLOR_SET_DROPDOWN.toString(), 
                5*Theme.DROPBOX_XOFS, p5GuiYOffset, Theme.DROPBOXLIST_LENGTH, 140);
        Theme.themeDropdownList(colorSetList);		
        i=0;
        for (ColorSet cs: Collector.getInstance().getColorSets()) {
            colorSetList.addItem(cs.getName(), i);
            i++;
        }		
        colorSetList.setLabel(colorSetList.getItem(1).getName());
        colorSetList.setHeight(100);
        colorSetList.moveTo(ALWAYS_VISIBLE_TAB);

        //----------
        //RANDOM Tab
        //----------				

        Textlabel t2 = cp5.addTextlabel("rndDesc", 
        		"SELECT THE ELEMENTS THAT SHOULD BE CHANGED IN RANDOM MODE:", 
        		20, yPosStartDrowdown);
        t2.moveTo(randomTab).getValueLabel().setFont(ControlP5.standard58);
        
        
        randomCheckbox = cp5.addCheckBox(GuiElement.RANDOM_ELEMENT.toString())
                .setPosition(35, 20+yPosStartDrowdown)
                .setSize(40, 20)
                .setColorForeground(color(120))
                .setColorActive(color(255))
                .setColorLabel(color(255))
                .setItemsPerRow(5)
                .setSpacingColumn(90)
		;
		
        for (ShufflerOffset so: ShufflerOffset.values()) {
            randomCheckbox.addItem(so.name(), i);
        }
        randomCheckbox.activateAll();
        randomCheckbox.moveTo(randomTab);


        //Button
        randomSelection = cp5.addButton(GuiElement.BUTTON_RANDOM_CONFIGURATION.toString(), 0,
                5*Theme.DROPBOX_XOFS, p5GuiYOffset+30, 100, 15);
        randomSelection.setCaptionLabel("RANDOMIZE");
        randomSelection.moveTo(randomTab);
        cp5.getTooltip().register(GuiElement.BUTTON_RANDOM_CONFIGURATION.toString(),"cross your fingers, randomize everything");

        randomPresets = cp5.addButton(GuiElement.BUTTON_RANDOM_PRESENT.toString(), 0,
                5*Theme.DROPBOX_XOFS, p5GuiYOffset+55, 100, 15);
        randomPresets.setCaptionLabel("RANDOM PRESENT");
        randomPresets.moveTo(randomTab);
        cp5.getTooltip().register(GuiElement.BUTTON_RANDOM_PRESENT.toString(),"Load a random preset");

        toggleRandom = cp5.addToggle(GuiElement.BUTTON_TOGGLE_RANDOM_MODE.toString(), true,
                5*Theme.DROPBOX_XOFS, p5GuiYOffset+80, 100, 15);
        toggleRandom.setCaptionLabel("RANDOM MODE");
        toggleRandom.setState(false);
        toggleRandom.moveTo(randomTab);
        cp5.getTooltip().register(GuiElement.BUTTON_TOGGLE_RANDOM_MODE.toString(),"Toggle the random mode");		


        //----------
        //PRESET Tab
        //----------

        presetButtons = cp5.addRadioButton(GuiElement.PRESET_BUTTONS.toString())
                .setPosition(20, yPosStartDrowdown)
                .setSize(12, 12)
                .setColorForeground(color(120))
                .setColorActive(color(255))
                .setColorLabel(color(255))
                .setItemsPerRow(14)
                .setSpacingColumn(40)
                .setNoneSelectedAllowed(false);

        for (i=0; i<70; i++) {
            String label = ""+(i+1);
            if (i<9) {
                label = "0"+(i+1);
            }
            presetButtons.addItem(label, i);
        }
        presetButtons.activate(0);
        presetButtons.moveTo(presetTab);
        
        loadPreset = cp5.addButton(GuiElement.LOAD_PRESET.toString(), 0,
                2*Theme.DROPBOX_XOFS, yPosStartDrowdown+80, 100, 15);
        loadPreset.setCaptionLabel(GuiElement.LOAD_PRESET.toString());
        loadPreset.moveTo(presetTab);
        cp5.getTooltip().register(GuiElement.LOAD_PRESET.toString(),"Load a stored preset");

        savePreset = cp5.addButton(GuiElement.SAVE_PRESET.toString(), 0,
                3*Theme.DROPBOX_XOFS, yPosStartDrowdown+80, 100, 15);
        savePreset.setCaptionLabel(GuiElement.SAVE_PRESET.toString());
        savePreset.moveTo(presetTab);
        cp5.getTooltip().register(GuiElement.SAVE_PRESET.toString(),"Save a preset");

        
        //----------
        // MISC
        //----------    

        //register event listener
        cp5.addListener(listener);

        //select first visual
        selectedVisualList.activate(0);
        selectedOutputs.activate(0);
    }

    public void RANDOM_ELEMENT(int val) {
        //unused
    }
    
    /**
     * this callback method is needed by the library but unused
     * @param val
     */
    public void CURRENT_OUTPUT(int val) {
        //unused
    }
    
    /**
     * this callback method is needed by the library but unused
     * @param val
     */
    public void PRESET_BUTTONS(int val) {
        //unused
    }

    /**
     * this callback method is needed by the library but unused
     * @param val
     */
    public void CURRENT_VISUAL(int val) {
        //unused
    }

    
    /**
     * draw the whole internal buffer on screen.
     */
    public void draw() {
        long l = System.currentTimeMillis();

        drawSolidBackground();

        int localX=0, localY=0;
        int[] buffer;
        Collector col = Collector.getInstance();

        //set used to find out if visual is on screen
        Set<Integer> outputId = new HashSet<Integer>();
        for (OutputMapping om: col.getAllOutputMappings()) {
            outputId.add(om.getVisualId());
        }

        //draw output buffer and marker
        int ofs=0;
        for (Visual v: col.getAllVisuals()) {
            //get image
            buffer = col.getMatrix().resizeBufferForDevice(v.getBuffer(), v.getResizeOption(), targetXSize, targetYSize);

            if (pImage==null) {
                //create an image out of the buffer
                pImage = col.getPapplet().createImage(targetXSize, targetYSize, PApplet.RGB );				
            }
            pImage.loadPixels();
            System.arraycopy(buffer, 0, pImage.pixels, 0, targetXSize*targetYSize);
            pImage.updatePixels();

            //draw current output
            if (outputId.contains(ofs)) {
                fill(66,200,66);
            } else {
                fill(55,55,55);
            }	
            rect(localX, localY+targetYSize+2, targetXSize, SELECTED_MARKER);				

            //display the image
            image(pImage, localX, localY);
            localX += pImage.width;
            ofs++;
        }

        //display frame progress
        int frames = col.getFrames() % targetXSize;
        fill(200,200,200);
        rect(0, localY+targetYSize+SELECTED_MARKER+4, frames, 5);
        fill(55,55,55);
        rect(frames, localY+targetYSize+SELECTED_MARKER+4, targetXSize-frames, 5);

        //beat detection
        displaySoundStats(localY);

        cp5.draw();		
        col.getPixConStat().trackTime(TimeMeasureItemGlobal.DEBUG_WINDOW, System.currentTimeMillis()-l);
    }



    
    /**
     * draw nice gradient at the end of the screen
     */
    private void drawSolidBackground() {
    	fill(20);
    	rect(0,0,this.width,this.height);
    }


    /**
     * 
     * @param localY
     */
    private void displaySoundStats(int localY) {
        Sound snd = Sound.getInstance();

        int xofs = targetXSize+2;
        int xx = targetXSize/3-2;

        colorSelect(snd.isKick());
        rect(xofs, localY+targetYSize+SELECTED_MARKER+4, xx, 5);

        xofs+=xx+2;
        colorSelect(snd.isSnare());
        rect(xofs, localY+targetYSize+SELECTED_MARKER+4, xx, 5);

        xofs+=xx+2;
        colorSelect(snd.isHat());
        rect(xofs, localY+targetYSize+SELECTED_MARKER+4, xx, 5);		
    }


    /**
     * 
     * @param b
     */
    private void colorSelect(boolean b) {
        if (b) {
            fill(200,200,200);	
        } else {
            fill(55,55,55);	
        }		
    }

    /**
     * update only minimal parts of the gui
     */
    public Collector callbackRefreshMini() {
        LOG.log(Level.INFO, "Refresh Partitial GUI");
        Collector col = Collector.getInstance();

        //get visual status			
        Visual v = col.getVisual(col.getCurrentVisual());
        if (v!=null) {		    
            generatorListOne.setLabel(generatorListOne.getItem(v.getGenerator1Idx()).getName());
            generatorListTwo.setLabel(generatorListTwo.getItem(v.getGenerator2Idx()).getName());
            effectListOne.setLabel(effectListOne.getItem(v.getEffect1Idx()).getName());
            effectListTwo.setLabel(effectListTwo.getItem(v.getEffect2Idx()).getName());
            mixerList.setLabel(mixerList.getItem(v.getMixerIdx()).getName());			
        }

        //get output status
        OutputMapping om = col.getOutputMappings(col.getCurrentOutput()); 
        dropdownOutputVisual.setLabel(dropdownOutputVisual.getItem(om.getVisualId()).getName());
        dropdownOutputFader.setLabel(dropdownOutputFader.getItem(om.getFader().getId()).getName());

        ColorSet cs = col.getActiveColorSet();
        colorSetList.setLabel(cs.getName());

        return col;
    }

    /**
     * refresh whole gui
     */
    public void callbackRefreshWholeGui() {
        LOG.log(Level.INFO, "Refresh Whole GUI");
        Collector col = this.callbackRefreshMini();		

        PixelControllerEffect pce = col.getPixelControllerEffect();

        thresholdSlider.changeValue(pce.getThresholdValue());

        PixelControllerGenerator pcg = col.getPixelControllerGenerator();
        blinkenLightsList.setLabel(pcg.getFileBlinken()); 
        imageList.setLabel(pcg.getFileImageSimple());
    }


    /**
     * mouse listener, used to close dropdown lists
     * 
     */
    public void mousePressed() {
        // print the current mouseoverlist on mouse pressed
        List <GuiElement> clickedOn = new ArrayList<GuiElement>();
        List<ControllerInterface<?>> lci = cp5.getWindow().getMouseOverList();
        for (ControllerInterface<?> ci: lci) {
            GuiElement ge = GuiElement.getGuiElement(ci.getName());
            if (ge!=null) {
                clickedOn.add(ge);				
            }
        }

        if (!clickedOn.contains(GuiElement.GENERATOR_ONE_DROPDOWN)) {
            generatorListOne.setOpen(false);
        }
        if (!clickedOn.contains(GuiElement.GENERATOR_TWO_DROPDOWN)) {
            generatorListTwo.setOpen(false);
        }
        if (!clickedOn.contains(GuiElement.EFFECT_ONE_DROPDOWN)) {
            effectListOne.setOpen(false);
        }
        if (!clickedOn.contains(GuiElement.EFFECT_TWO_DROPDOWN)) {
            effectListTwo.setOpen(false);
        }
        if (!clickedOn.contains(GuiElement.MIXER_DROPDOWN)) {
            mixerList.setOpen(false);
        }
        if (!clickedOn.contains(GuiElement.COLOR_SET_DROPDOWN)) {
            colorSetList.setOpen(false);
        }

        if (!clickedOn.contains(GuiElement.BLINKENLIGHTS_DROPDOWN)) {
            blinkenLightsList.setOpen(false);
        }
        if (!clickedOn.contains(GuiElement.IMAGE_DROPDOWN)) {
            imageList.setOpen(false);
        }
        if (!clickedOn.contains(GuiElement.OUTPUT_FADER_DROPDOWN)) {
            dropdownOutputFader.setOpen(false);
        }
        if (!clickedOn.contains(GuiElement.OUTPUT_SELECTED_VISUAL_DROPDOWN)) {
            dropdownOutputVisual.setOpen(false);
        }
        if (!clickedOn.contains(GuiElement.TEXTUREDEFORM_OPTIONS)) {
            textureDeformOptions.setOpen(false);
        }
        if (!clickedOn.contains(GuiElement.COLORSCROLL_OPTIONS)) {
            colorScrollList.setOpen(false);
        }

        if (!clickedOn.contains(GuiElement.OUTPUT_ALL_SELECTED_VISUAL_DROPDOWN)) {
            allOutputTabVis.setOpen(false);
        }
        if (!clickedOn.contains(GuiElement.OUTPUT_ALL_FADER_DROPDOWN)) {
            allOutputTabFader.setOpen(false);
        }

    }


    /**
     * select visual by keypress
     */
    public void keyPressed() {
        if(key>='1' && key<'9') {
            // convert a key-number (48-52) to an int between 0 and 4
            int n = (int)key-49;
            selectedVisualList.activate(n);
        }
    }




}
