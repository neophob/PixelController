/**
 * Copyright (C) 2011-2013 Michael Vogt <michu@neophob.com>
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


import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.apache.commons.lang3.time.DurationFormatUtils;

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
import com.neophob.sematrix.glue.PresetSettings;
import com.neophob.sematrix.glue.ShufflerOffset;
import com.neophob.sematrix.glue.Visual;
import com.neophob.sematrix.input.Sound;
import com.neophob.sematrix.jmx.TimeMeasureItemGlobal;
import com.neophob.sematrix.listener.KeyboardHandler;
import com.neophob.sematrix.mixer.Mixer.MixerName;
import com.neophob.sematrix.output.Output;
import com.neophob.sematrix.output.gui.helper.FileUtils;
import com.neophob.sematrix.output.gui.helper.Theme;
import com.neophob.sematrix.properties.ConfigConstant;
import com.neophob.sematrix.resize.Resize.ResizeName;

import controlP5.Button;
import controlP5.CheckBox;
import controlP5.ControlP5;
import controlP5.ControllerInterface;
import controlP5.DropdownList;
import controlP5.Label;
import controlP5.RadioButton;
import controlP5.Slider;
import controlP5.Tab;
import controlP5.Textfield;
import controlP5.Textlabel;
import controlP5.Toggle;


/**
 * Display the internal Visual buffers in full resolution
 * 
 * @author michu
 */
public class GeneratorGui extends PApplet implements GuiCallbackAction {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 2344499301021L;

    private static final int SELECTED_MARKER = 10;
    
    private static final int GENERIC_X_OFS = 5;
    private static final int GENERIC_Y_OFS = 8;

    private static final int NR_OF_WIDGETS = 4;
    private static final int WIDGET_BOARDER = 10;
    private static final int WIDGET_BAR_SIZE = 6;
    
    private static final String ALWAYS_VISIBLE_TAB = "global"; //$NON-NLS-1$

    /** The log. */
    private static final Logger LOG = Logger.getLogger(GeneratorGui.class.getName());

    /** The y. */
    private int windowWidth,windowHeight;

    /** The p image. */
    private PImage pImage=null;
    private PImage logo;

    private ControlP5 cp5;
    private DropdownList generatorListOne, effectListOne;
    private DropdownList generatorListTwo, effectListTwo;
    private DropdownList mixerList;
    private RadioButton selectedVisualList;
    private RadioButton selectedOutputs;
    private Button randomSelection, randomPresets;
    private RadioButton randomButtons;
    private Textfield textGenerator;

    private Slider brightnessControll;
    
    //Effect Tab    
    private Slider thresholdSlider, fxRotoSlider;	
    private DropdownList textureDeformOptions, zoomOptions;
    
    //Generator Tab
    private DropdownList blinkenLightsList, imageList, textwriterOption;	
    private Label passThroughMode;
    
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
    private Label presetInfo;
    private Textfield presetName;
    
    private CheckBox randomCheckbox;
    
    //info tab
    private List<Tab> allTabs = new ArrayList<Tab>();
    private Label currentFps;
    private Label currentVolume;
    private Label runtime;
    private Label sentFrames;
    private Label outputErrorCounter;
    private Label outputState;
    private Label oscStatistic;
    
    /** The target y size. */
    private int singleVisualXSize, singleVisualYSize;
    private int p5GuiYOffset;

    private int[] buffer = null;
    
    private boolean initialized = false;
    
    /**
     * Instantiates a new internal buffer.
     *
     * @param displayHoriz the display horiz
     * @param x the x
     * @param y the y
     * @param singleVisualXSize the target x size
     * @param singleVisualYSize the target y size 
     */
    public GeneratorGui(int windowWidth, int windowHeigth, int singleVisualXSize, int singleVisualYSize) {
    	super();        
        this.windowWidth = windowWidth;
        this.windowHeight = windowHeigth;
        this.singleVisualXSize = singleVisualXSize;
        this.singleVisualYSize = singleVisualYSize;
        this.p5GuiYOffset = this.singleVisualYSize + 110;        
    }

    /* (non-Javadoc)
     * @see processing.core.PApplet#setup()
     */
    public void setup() {
        size(windowWidth, windowHeight);         
    	LOG.log(Level.INFO, "Create GUI Window with size "+this.getWidth()+"/"+this.getHeight()); //$NON-NLS-1$ //$NON-NLS-2$

        frameRate(Collector.getInstance().getFps());
        smooth();
        background(0,0,0);		
        int i=0;
        
        cp5 = new ControlP5(this);
        cp5.setAutoDraw(false);
        
        //press alt and you can move gui elements arround. disable this *should* work but does not...
        cp5.setMoveable(false);

        //alt-h hide all controls - I don't want that!
        cp5.disableShortcuts();
        
        cp5.getTooltip().setDelay(200);
        P5EventListener listener = new P5EventListener(this);

        //selected visual
        Collector col = Collector.getInstance();
        int nrOfVisuals = col.getAllVisuals().size();
      
        selectedVisualList = cp5.addRadioButton(GuiElement.CURRENT_VISUAL.guiText(), getVisualCenter(col), p5GuiYOffset-58);
        selectedVisualList.setItemsPerRow(nrOfVisuals);
        selectedVisualList.setNoneSelectedAllowed(false);		
        for (i=0; i<nrOfVisuals; i++) {
            String s = Messages.getString("GeneratorGui.GUI_SELECTED_VISUAL")+(1+i);			 //$NON-NLS-1$
            Toggle t = cp5.addToggle(s, 0, 0, singleVisualXSize-1, 13);
            t.setCaptionLabel(s);
            selectedVisualList.addItem(t, i);			
            cp5.getTooltip().register(s, Messages.getString("GeneratorGui.GUI_SELECTED_VISUAL_TOOLTIP_PREFIX")+(1+i)+Messages.getString("GeneratorGui.GUI_SELECTED_VISUAL_TOOLTIP_POSTFIX"));			 //$NON-NLS-1$ //$NON-NLS-2$
        }
        selectedVisualList.moveTo(ALWAYS_VISIBLE_TAB);

        cp5.addTextlabel("gen1", Messages.getString("GeneratorGui.GUI_GENERATOR_LAYER_1"), GENERIC_X_OFS+3, 3+p5GuiYOffset).moveTo(ALWAYS_VISIBLE_TAB).getValueLabel(); //$NON-NLS-1$ //$NON-NLS-2$
        cp5.addTextlabel("gen2", Messages.getString("GeneratorGui.GUI_GENERATOR_LAYER_2"), GENERIC_X_OFS+3+3*Theme.DROPBOX_XOFS, 3+p5GuiYOffset).moveTo(ALWAYS_VISIBLE_TAB).getValueLabel(); //$NON-NLS-1$ //$NON-NLS-2$
        cp5.addTextlabel("fx1", Messages.getString("GeneratorGui.GUI_EFFECT_LAYER_1"), GENERIC_X_OFS+3+1*Theme.DROPBOX_XOFS, 3+p5GuiYOffset).moveTo(ALWAYS_VISIBLE_TAB).getValueLabel(); //$NON-NLS-1$ //$NON-NLS-2$
        cp5.addTextlabel("fx2", Messages.getString("GeneratorGui.GUI_EFFECT_LAYER_2"), GENERIC_X_OFS+3+4*Theme.DROPBOX_XOFS, 3+p5GuiYOffset).moveTo(ALWAYS_VISIBLE_TAB).getValueLabel(); //$NON-NLS-1$ //$NON-NLS-2$
        cp5.addTextlabel("mix2", Messages.getString("GeneratorGui.GUI_LAYER_MIXER"), GENERIC_X_OFS+3+2*Theme.DROPBOX_XOFS, 3+p5GuiYOffset).moveTo(ALWAYS_VISIBLE_TAB).getValueLabel(); //$NON-NLS-1$ //$NON-NLS-2$

        cp5.getTooltip().register("gen1", Messages.getString("GeneratorGui.GUI_TOOLTIP_GENERATOR_1")); //$NON-NLS-1$ //$NON-NLS-2$
        cp5.getTooltip().register("gen2", Messages.getString("GeneratorGui.GUI_TOOLTIP_GENERATOR_2")); //$NON-NLS-1$ //$NON-NLS-2$
        cp5.getTooltip().register("fx1", Messages.getString("GeneratorGui.GUI_TOOLTIP_EFFECT_1")); //$NON-NLS-1$ //$NON-NLS-2$
        cp5.getTooltip().register("fx2", Messages.getString("GeneratorGui.GUI_TOOLTIP_EFFECT_2")); //$NON-NLS-1$ //$NON-NLS-2$
        cp5.getTooltip().register("mix2", Messages.getString("GeneratorGui.GUI_TOOLTIP_MIXER")); //$NON-NLS-1$ //$NON-NLS-2$

        //Generator 
        generatorListOne = cp5.addDropdownList(GuiElement.GENERATOR_ONE_DROPDOWN.guiText(), 
        		GENERIC_X_OFS, p5GuiYOffset, Theme.DROPBOXLIST_LENGTH, 140);
        generatorListTwo = cp5.addDropdownList(GuiElement.GENERATOR_TWO_DROPDOWN.guiText(), 
        		GENERIC_X_OFS+3*Theme.DROPBOX_XOFS, p5GuiYOffset, Theme.DROPBOXLIST_LENGTH, 140);
        Theme.themeDropdownList(generatorListOne);
        Theme.themeDropdownList(generatorListTwo);
        i=0;
        for (GeneratorName gn: GeneratorName.values()) {
            generatorListOne.addItem(gn.guiText(), i);
            generatorListTwo.addItem(gn.guiText(), i);
            i++;
        }
        generatorListOne.setLabel(generatorListOne.getItem(1).getName());
        generatorListTwo.setLabel(generatorListTwo.getItem(1).getName());
        generatorListOne.moveTo(ALWAYS_VISIBLE_TAB);
        generatorListTwo.moveTo(ALWAYS_VISIBLE_TAB);
        generatorListOne.setHeight(Theme.DROPBOXLIST_LARGE_HEIGHT);
        generatorListTwo.setHeight(Theme.DROPBOXLIST_LARGE_HEIGHT);

        //Effect 
        effectListOne = cp5.addDropdownList(GuiElement.EFFECT_ONE_DROPDOWN.guiText(), 
        		GENERIC_X_OFS+1*Theme.DROPBOX_XOFS, p5GuiYOffset, Theme.DROPBOXLIST_LENGTH, 140);
        effectListTwo = cp5.addDropdownList(GuiElement.EFFECT_TWO_DROPDOWN.guiText(), 
        		GENERIC_X_OFS+4*Theme.DROPBOX_XOFS, p5GuiYOffset, Theme.DROPBOXLIST_LENGTH, 140);
        Theme.themeDropdownList(effectListOne);
        Theme.themeDropdownList(effectListTwo);
        i=0;
        for (EffectName gn: EffectName.values()) {
            effectListOne.addItem(gn.guiText(), i);
            effectListTwo.addItem(gn.guiText(), i);
            i++;
        }
        effectListOne.setLabel(effectListOne.getItem(0).getName());
        effectListTwo.setLabel(effectListTwo.getItem(0).getName());
        effectListOne.moveTo(ALWAYS_VISIBLE_TAB);
        effectListTwo.moveTo(ALWAYS_VISIBLE_TAB);
        effectListOne.setHeight(Theme.DROPBOXLIST_LARGE_HEIGHT);
        effectListTwo.setHeight(Theme.DROPBOXLIST_LARGE_HEIGHT);
        
        //Mixer 
        mixerList = cp5.addDropdownList(GuiElement.MIXER_DROPDOWN.guiText(), 
        		GENERIC_X_OFS+2*Theme.DROPBOX_XOFS, p5GuiYOffset, Theme.DROPBOXLIST_LENGTH, 140);
        Theme.themeDropdownList(mixerList);

        i=0;
        for (MixerName gn: MixerName.values()) {
            mixerList.addItem(gn.guiText(), i);
            i++;
        }
        mixerList.setLabel(mixerList.getItem(0).getName());
        mixerList.setHeight(Theme.DROPBOXLIST_LARGE_HEIGHT);
        mixerList.moveTo(ALWAYS_VISIBLE_TAB);


        //---------------------------------
        //TABS
        //---------------------------------

        final int yPosStartLabel = p5GuiYOffset+50;
        final int yPosStartDrowdown = p5GuiYOffset+36;

        cp5.getWindow().setPositionOfTabs(GENERIC_X_OFS, this.getHeight()-20);

        //there a default tab which is present all the time. rename this tab
        Tab generatorTab = cp5.getTab("default"); //$NON-NLS-1$
        allTabs.add(generatorTab);
        generatorTab.setLabel(Messages.getString("GeneratorGui.TAB_GENERATOR_EFFECT"));		 //$NON-NLS-1$
        Tab outputTab = cp5.addTab(Messages.getString("GeneratorGui.TAB_SINGLE_OUTPUT_MAPPING")); //$NON-NLS-1$
        allTabs.add(outputTab);
        Tab allOutputTab = null;
        
        //add all output mapping only if multiple output panels exist
        if (nrOfVisuals>2) {
            allOutputTab = cp5.addTab(Messages.getString("GeneratorGui.TAB_ALL_OUTPUT_MAPPING"));		 //$NON-NLS-1$
            allOutputTab.setColorForeground(0xffff0000);
            allTabs.add(allOutputTab);
        }

        Tab randomTab = cp5.addTab(Messages.getString("GeneratorGui.TAB_RANDOMIZE"));		 //$NON-NLS-1$
        allTabs.add(randomTab);
        Tab presetTab = cp5.addTab(Messages.getString("GeneratorGui.TAB_PRESETS")); //$NON-NLS-1$
        allTabs.add(presetTab);
        Tab infoTab = cp5.addTab(Messages.getString("GeneratorGui.TAB_INFO")); //$NON-NLS-1$
        allTabs.add(infoTab);
        Tab helpTab = cp5.addTab(Messages.getString("GeneratorGui.TAB_HELP")); //$NON-NLS-1$
        allTabs.add(helpTab);
        
        generatorTab.setColorForeground(0xffff0000);
        outputTab.setColorForeground(0xffff0000);
        randomTab.setColorForeground(0xffff0000);
        presetTab.setColorForeground(0xffff0000);
        helpTab.setColorForeground(0xffff0000);
        
        generatorTab.bringToFront();
        
        //-------------
        //Generic Options
        //-------------
        
        //freeze update 
        Toggle t1 = cp5.addToggle(GuiElement.BUTTON_TOGGLE_FREEZE.guiText(), 730, 2, 15, 15).moveTo(ALWAYS_VISIBLE_TAB);
        t1.setLabelVisible(false);
        cp5.addTextlabel("freezeUpdateTxt", Messages.getString("GeneratorGui.GUI_TOGGLE_FREEZE"), 745, 5).moveTo(ALWAYS_VISIBLE_TAB);
        cp5.getTooltip().register(GuiElement.BUTTON_TOGGLE_FREEZE.guiText(),Messages.getString("GeneratorGui.TOOLTIP_FREEZE")); //$NON-NLS-1$
        
        //toggle internal visuals
        Toggle t2 = cp5.addToggle(GuiElement.BUTTON_TOGGLE_INTERNAL_VISUALS.guiText(), 730, 20, 15, 15).moveTo(ALWAYS_VISIBLE_TAB);
        t2.setLabelVisible(false);
        cp5.addTextlabel("toggleIKnternalVisualsTxt", Messages.getString("GeneratorGui.GUI_TOGGLE_INTERNAL_BUFFER"), 745, 23).moveTo(ALWAYS_VISIBLE_TAB);; //$NON-NLS-1$
        cp5.getTooltip().register(GuiElement.BUTTON_TOGGLE_INTERNAL_VISUALS.guiText(),Messages.getString("GeneratorGui.TOOLTIP_GUI_TOGGLE_INTERNAL_BUFFER")); //$NON-NLS-1$

        
        //-------------
        //GENERATOR/EFFECT tab
        //-------------
        
        int genFxXOfs = 38+GENERIC_X_OFS;
        
        //EFFECTS OPTIONS
        //---------------
        int genElYOfs = yPosStartDrowdown+70;
        cp5.addTextlabel("genOptionsFx", Messages.getString("GeneratorGui.EFFECT_OPTIONS"), GENERIC_X_OFS, genElYOfs).moveTo(generatorTab).getValueLabel(); //$NON-NLS-1$ //$NON-NLS-2$        
        
        //threshold slider
        thresholdSlider = cp5.addSlider(GuiElement.THRESHOLD.guiText(), 
        		0, 255, 255, genFxXOfs, genElYOfs+60, 140, 14);
        thresholdSlider.setSliderMode(Slider.FIX);
        thresholdSlider.setGroup(generatorTab);	
        thresholdSlider.setDecimalPrecision(0);		

        //rotozoom slider
        fxRotoSlider = cp5.addSlider(GuiElement.FX_ROTOZOOMER.guiText(), 
                -127, 127, 0, genFxXOfs+2*Theme.DROPBOX_XOFS, genElYOfs+60, 140, 14);
        fxRotoSlider.setSliderMode(Slider.FIX);
        fxRotoSlider.setGroup(generatorTab);
        fxRotoSlider.setDecimalPrecision(0);
        fxRotoSlider.setCaptionLabel(Messages.getString("GeneratorGui.EFFECT_ROTOZOOM_SPEED")); //$NON-NLS-1$

        
        
        genElYOfs = yPosStartDrowdown+90;

        //texturedeform options
        cp5.addTextlabel("genTextdefOpt", Messages.getString("GeneratorGui.TEXTUREDDEFORM_OPTIONS"), genFxXOfs+3+0*Theme.DROPBOX_XOFS, genElYOfs+16).moveTo(generatorTab).getValueLabel(); //$NON-NLS-1$ //$NON-NLS-2$
        textureDeformOptions = cp5.addDropdownList(GuiElement.TEXTUREDEFORM_OPTIONS.guiText(), 
        		genFxXOfs+0*Theme.DROPBOX_XOFS, genElYOfs+11, Theme.DROPBOXLIST_LENGTH, 80);
        Theme.themeDropdownList(textureDeformOptions);		        
        textureDeformOptions.addItem(Messages.getString("GeneratorGui.TEXTUREDEFORM_ANAMORPHOSIS"), 1); //$NON-NLS-1$
        textureDeformOptions.addItem(Messages.getString("GeneratorGui.TEXTUREDEFORM_SPIRAL"), 2); //$NON-NLS-1$
        textureDeformOptions.addItem(Messages.getString("GeneratorGui.TEXTUREDEFORM_ROTATINGTUNNEL"), 3); //$NON-NLS-1$
        textureDeformOptions.addItem(Messages.getString("GeneratorGui.TEXTUREDEFORM_START"), 4); //$NON-NLS-1$
        textureDeformOptions.addItem(Messages.getString("GeneratorGui.TEXTUREDEFORM_TUNNEL"), 5); //$NON-NLS-1$
        textureDeformOptions.addItem(Messages.getString("GeneratorGui.TEXTUREDEFORM_FLOWER"), 6); //$NON-NLS-1$
        textureDeformOptions.addItem(Messages.getString("GeneratorGui.TEXTUREDEFORM_CLOUD"), 7); //$NON-NLS-1$
        textureDeformOptions.addItem(Messages.getString("GeneratorGui.TEXTUREDEFORM_PLANAR"), 8); //$NON-NLS-1$
        textureDeformOptions.addItem(Messages.getString("GeneratorGui.TEXTUREDEFORM_CIRCLE"), 9); //$NON-NLS-1$
        textureDeformOptions.addItem(Messages.getString("GeneratorGui.TEXTUREDEFORM_SPIRAL"), 10); //$NON-NLS-1$
        textureDeformOptions.addItem(Messages.getString("GeneratorGui.TEXTUREDEFORM_3D"), 11); //$NON-NLS-1$
        textureDeformOptions.setLabel(textureDeformOptions.getItem(1).getName());
        textureDeformOptions.setGroup(generatorTab);		

        
        cp5.addTextlabel("genZoomOpt", Messages.getString("GeneratorGui.ZOOM_OPTIONS"), genFxXOfs+3+1*Theme.DROPBOX_XOFS, genElYOfs+16).moveTo(generatorTab).getValueLabel(); //$NON-NLS-1$ //$NON-NLS-2$
        zoomOptions = cp5.addDropdownList(GuiElement.ZOOM_OPTIONS.guiText(), 
        		genFxXOfs+1*Theme.DROPBOX_XOFS, genElYOfs+11, Theme.DROPBOXLIST_LENGTH, 80);
        Theme.themeDropdownList(zoomOptions);		        
        zoomOptions.addItem(Messages.getString("GeneratorGui.ZOOM_IN"), 0); //$NON-NLS-1$
        zoomOptions.addItem(Messages.getString("GeneratorGui.ZOOM_OUT"), 1); //$NON-NLS-1$
        zoomOptions.addItem(Messages.getString("GeneratorGui.ZOOM_HORIZONTAL"), 2); //$NON-NLS-1$
        zoomOptions.addItem(Messages.getString("GeneratorGui.ZOOM_VERTICAL"), 3); //$NON-NLS-1$
        zoomOptions.setLabel(zoomOptions.getItem(0).getName());
        zoomOptions.setGroup(generatorTab);		

        
        //GENERATOR OPTIONS
        //-----------------
        
        genElYOfs = p5GuiYOffset+35;
        cp5.addTextlabel("genOptionsGen", Messages.getString("GeneratorGui.GENERATOR_OPTIONS"), GENERIC_X_OFS, genElYOfs).moveTo(generatorTab).getValueLabel(); //$NON-NLS-1$ //$NON-NLS-2$
        
        genElYOfs=yPosStartLabel+5;
        cp5.addTextlabel("genBlinken", Messages.getString("GeneratorGui.BLINKENLIGHT_LOAD"), genFxXOfs+3, genElYOfs+16).moveTo(generatorTab).getValueLabel(); //$NON-NLS-1$ //$NON-NLS-2$
        blinkenLightsList = cp5.addDropdownList(GuiElement.BLINKENLIGHTS_DROPDOWN.guiText(), 
        		genFxXOfs, genElYOfs+11, Theme.DROPBOXLIST_LENGTH, 140);
        Theme.themeDropdownList(blinkenLightsList);
        i=0;
        for (String s: FileUtils.findBlinkenFiles()) {
            blinkenLightsList.addItem(s, i);
            i++;
        }
        blinkenLightsList.setLabel(blinkenLightsList.getItem(1).getName());
        blinkenLightsList.setGroup(generatorTab);
        blinkenLightsList.setHeight(Theme.DROPBOXLIST_HEIGHT);

        //images
        cp5.addTextlabel("genImg", Messages.getString("GeneratorGui.IMAGE_LOAD"), genFxXOfs+3+1*Theme.DROPBOX_XOFS, genElYOfs+16).moveTo(generatorTab).getValueLabel(); //$NON-NLS-1$ //$NON-NLS-2$
        imageList = cp5.addDropdownList(GuiElement.IMAGE_DROPDOWN.guiText(), 
        		genFxXOfs+Theme.DROPBOX_XOFS, genElYOfs+11, Theme.DROPBOXLIST_LENGTH, 140);
        Theme.themeDropdownList(imageList);		
        i=0;
        for (String s: FileUtils.findImagesFiles()) {
            imageList.addItem(s, i);
            i++;
        }
        imageList.setLabel(imageList.getItem(1).getName());
        imageList.setGroup(generatorTab);		
        imageList.setHeight(Theme.DROPBOXLIST_HEIGHT);

        //colorscroll options
        cp5.addTextlabel("genColorScroll", Messages.getString("GeneratorGui.COLORSCROLL_OPTIONS"), genFxXOfs+3+2*Theme.DROPBOX_XOFS, genElYOfs+16).moveTo(generatorTab).getValueLabel(); //$NON-NLS-1$ //$NON-NLS-2$        
        colorScrollList = cp5.addDropdownList(GuiElement.COLORSCROLL_OPTIONS.guiText(), 
        		genFxXOfs+2*Theme.DROPBOX_XOFS, genElYOfs+11, Theme.DROPBOXLIST_LENGTH, 140);
        Theme.themeDropdownList(colorScrollList);		

        for (ScrollMode sm: ScrollMode.values()) {
            colorScrollList.addItem(sm.getDisplayName(), sm.getMode()); //$NON-NLS-1$ //$NON-NLS-2$
        }
        colorScrollList.setGroup(generatorTab);		
        colorScrollList.setHeight(Theme.DROPBOXLIST_HEIGHT);
        colorScrollList.setLabel(col.getPixelControllerGenerator().getScrollMode().getDisplayName());
        
        //add textfield options
        cp5.addTextlabel("genTextwriterOpt", Messages.getString("GeneratorGui.TEXTWRITER_OPTION"), genFxXOfs+3+3*Theme.DROPBOX_XOFS, genElYOfs+16).moveTo(generatorTab).getValueLabel(); //$NON-NLS-1$ //$NON-NLS-2$        
        textwriterOption = cp5.addDropdownList(GuiElement.TEXTWR_OPTION.guiText(), 
        		genFxXOfs+3*Theme.DROPBOX_XOFS, genElYOfs+11, Theme.DROPBOXLIST_LENGTH, 140);
        Theme.themeDropdownList(textwriterOption);
        textwriterOption.addItem(Messages.getString("GeneratorGui.TEXTWRITER_PINGPONG"), 0); //$NON-NLS-1$
        textwriterOption.addItem(Messages.getString("GeneratorGui.TEXTWRITER_LEFT"), 1); //$NON-NLS-1$
        textwriterOption.setLabel(textwriterOption.getItem(0).getName());
        textwriterOption.setGroup(generatorTab);
        textwriterOption.setHeight(Theme.DROPBOXLIST_HEIGHT);

        //add textfield
        textGenerator = cp5.addTextfield(GuiElement.TEXTFIELD, GuiElement.TEXTFIELD.guiText(), GuiElement.TEXTFIELD.guiText(), genFxXOfs+3+4*Theme.DROPBOX_XOFS, genElYOfs-6, Theme.DROPBOXLIST_LENGTH, 16); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                      
        passThroughMode = cp5.addTextlabel("passThroughMode", "", genFxXOfs, yPosStartDrowdown+55).moveTo(generatorTab).getValueLabel();
        passThroughMode.setColor(0xffff0000);
  
        //-----------------
        //Single Output tab
        //-----------------				

        //brightness control
        cp5.addTextlabel("brightnessControllTxt", Messages.getString("GeneratorGui.BRIGHTNESS"), 5, yPosStartDrowdown+95).moveTo(outputTab);

        brightnessControll = cp5.addSlider(GuiElement.BRIGHTNESS.guiText(), 0, 255, 255, 38, yPosStartDrowdown+110, 160, 14);
        brightnessControll.setSliderMode(Slider.FIX);
        brightnessControll.setGroup(outputTab);	
        brightnessControll.setDecimalPrecision(0);
        brightnessControll.setNumberOfTickMarks(11);
        brightnessControll.setRange(0, 100);
        brightnessControll.setLabelVisible(false);        

        int nrOfOutputs = Collector.getInstance().getAllOutputMappings().size();
        selectedOutputs = cp5.addRadioButton(GuiElement.CURRENT_OUTPUT.guiText(), GENERIC_X_OFS, yPosStartDrowdown);
        selectedOutputs.setItemsPerRow(nrOfOutputs);
        selectedOutputs.setNoneSelectedAllowed(false);		
        for (i=0; i<nrOfOutputs; i++) {
            String s = Messages.getString("GeneratorGui.OUTPUT_NR")+(1+i);			 //$NON-NLS-1$
            Toggle t = cp5.addToggle(s, 0, 0, singleVisualXSize, 13);
            t.setCaptionLabel(s);
            selectedOutputs.addItem(t, i);			
            cp5.getTooltip().register(s, Messages.getString("GeneratorGui.TOOLTIP_OUTPUT_PREFIX")+(1+i)+Messages.getString("GeneratorGui.TOOLTIP_OUTPUT_POSTFIX"));			 //$NON-NLS-1$ //$NON-NLS-2$
        }
        selectedOutputs.moveTo(outputTab);

        //visual
        cp5.addTextlabel("singleOutputVisual", Messages.getString("GeneratorGui.OUTPUT_VISUAL"), 38, yPosStartDrowdown+60).moveTo(outputTab).getValueLabel(); //$NON-NLS-1$ //$NON-NLS-2$
        dropdownOutputVisual = GeneratorGuiHelper.createVisualDropdown(cp5, 
                GuiElement.OUTPUT_SELECTED_VISUAL_DROPDOWN.guiText(), yPosStartDrowdown+10, nrOfVisuals); 
        dropdownOutputVisual.moveTo(outputTab);

        //Fader         
        cp5.addTextlabel("singleOutputTransition", Messages.getString("GeneratorGui.OUTPUT_TRANSITION"), 38+Theme.DROPBOX_XOFS*2, yPosStartDrowdown+60).moveTo(outputTab).getValueLabel(); //$NON-NLS-1$ //$NON-NLS-2$
        dropdownOutputFader = GeneratorGuiHelper.createFaderDropdown(cp5, 
                GuiElement.OUTPUT_FADER_DROPDOWN.guiText(), yPosStartDrowdown+10); 
        dropdownOutputFader.moveTo(outputTab);
        
        
        //--------------
        //All Output tab
        //--------------				
        
        if (allOutputTab!=null) {
            cp5.addTextlabel("allOutputTabLabel", Messages.getString("GeneratorGui.TEXT_CHANGE_ALL_OUTPUT_MAPPINGS"), 20, yPosStartDrowdown) //$NON-NLS-1$ //$NON-NLS-2$
            .moveTo(allOutputTab).getValueLabel();

            cp5.addTextlabel("allOutputVisual", Messages.getString("GeneratorGui.ALL_OUTPUT_VISUAL"), 38, yPosStartDrowdown+68).moveTo(allOutputTab).getValueLabel(); //$NON-NLS-1$ //$NON-NLS-2$
            allOutputTabVis = GeneratorGuiHelper.createVisualDropdown(cp5, 
                    GuiElement.OUTPUT_ALL_SELECTED_VISUAL_DROPDOWN.guiText(), yPosStartDrowdown+20, nrOfVisuals); 
            allOutputTabVis.moveTo(allOutputTab);

            //Fader         
            cp5.addTextlabel("allOutputTransition", Messages.getString("GeneratorGui.ALL_OUTPUT_TRANSITION"), 38+Theme.DROPBOX_XOFS*2, yPosStartDrowdown+68).moveTo(allOutputTab).getValueLabel(); //$NON-NLS-1$ //$NON-NLS-2$
            allOutputTabFader = GeneratorGuiHelper.createFaderDropdown(cp5, 
                    GuiElement.OUTPUT_ALL_FADER_DROPDOWN.guiText(), yPosStartDrowdown+20); 
            allOutputTabFader.moveTo(allOutputTab);        	        	
        }


        //palette dropdown list	
        cp5.addTextlabel("colSet", Messages.getString("GeneratorGui.SELECT_COLORSET"), GENERIC_X_OFS+5*Theme.DROPBOX_XOFS, p5GuiYOffset+3).moveTo(ALWAYS_VISIBLE_TAB).getValueLabel(); //$NON-NLS-1$ //$NON-NLS-2$

        colorSetList = cp5.addDropdownList(GuiElement.COLOR_SET_DROPDOWN.guiText(), 
        		GENERIC_X_OFS+5*Theme.DROPBOX_XOFS, p5GuiYOffset, Theme.DROPBOXLIST_LENGTH, 140);
        Theme.themeDropdownList(colorSetList);		
        i=0;
        for (ColorSet cs: Collector.getInstance().getColorSets()) {
            colorSetList.addItem(cs.getName(), i);
            i++;
        }		
        colorSetList.setLabel(colorSetList.getItem(1).getName());
        colorSetList.setHeight(Theme.DROPBOXLIST_LARGE_HEIGHT);
        colorSetList.moveTo(ALWAYS_VISIBLE_TAB);
        cp5.getTooltip().register("colSet", Messages.getString("GeneratorGui.TOOLTIP_COLORSET")); //$NON-NLS-1$ //$NON-NLS-2$

        
        //----------
        //RANDOM Tab
        //----------				

        Textlabel tRnd = cp5.addTextlabel("rndDesc",  //$NON-NLS-1$
        		Messages.getString("GeneratorGui.TEXT_RANDOM_MODE_SELECT_ELEMENTS"),  //$NON-NLS-1$
        		20, yPosStartDrowdown);
        tRnd.moveTo(randomTab).getValueLabel();
        
        
        randomCheckbox = cp5.addCheckBox(GuiElement.RANDOM_ELEMENT.guiText())
                .setPosition(35, 20+yPosStartDrowdown)
                .setSize(40, 20)
                .setColorForeground(color(120))
                .setColorActive(color(255))
                .setColorLabel(color(255))
                .setItemsPerRow(5)
                .setSpacingColumn(90)
		;
		
        for (ShufflerOffset so: ShufflerOffset.values()) {
            randomCheckbox.addItem(so.guiText(), i);
        }
        randomCheckbox.activateAll();
        randomCheckbox.moveTo(randomTab);


        //Button
        randomSelection = cp5.addButton(GuiElement.BUTTON_RANDOM_CONFIGURATION.guiText(), 0,
        		GENERIC_X_OFS+5*Theme.DROPBOX_XOFS, p5GuiYOffset+30, 110, 15);
        randomSelection.setCaptionLabel(Messages.getString("GeneratorGui.RANDOMIZE")); //$NON-NLS-1$
        randomSelection.moveTo(randomTab);
        cp5.getTooltip().register(GuiElement.BUTTON_RANDOM_CONFIGURATION.guiText(), Messages.getString("GeneratorGui.TOOLTIP_RANDOMIZE")); //$NON-NLS-1$

        randomPresets = cp5.addButton(GuiElement.BUTTON_RANDOM_PRESET.guiText(), 0,
        		GENERIC_X_OFS+5*Theme.DROPBOX_XOFS, p5GuiYOffset+55, 110, 15);
        randomPresets.setCaptionLabel(Messages.getString("GeneratorGui.RANDOM_PRESET")); //$NON-NLS-1$
        randomPresets.moveTo(randomTab);
        cp5.getTooltip().register(GuiElement.BUTTON_RANDOM_PRESET.guiText(),Messages.getString("GeneratorGui.TOOLTIP_RANDOM_PRESET")); //$NON-NLS-1$

        randomButtons = cp5.addRadioButton(GuiElement.BUTTONS_RANDOM_MODE.guiText())
                .setPosition(GENERIC_X_OFS+5*Theme.DROPBOX_XOFS, p5GuiYOffset+85)
                .setSize(45, 15)
                .setColorForeground(color(120))
                .setColorActive(color(255))
                .setColorLabel(color(255))
                .setItemsPerRow(1)
                .setSpacingColumn(26)
                .setNoneSelectedAllowed(true)
                .moveTo(randomTab);
        randomButtons.addItem(Messages.getString("GeneratorGui.RANDOM_MODE"), 0);
        randomButtons.addItem(Messages.getString("GeneratorGui.RANDOM_MODE_PRESET"), 1);
        
        //----------
        //PRESET Tab
        //----------

        presetButtons = cp5.addRadioButton(GuiElement.PRESET_BUTTONS.guiText())
                .setPosition(10, yPosStartDrowdown)
                .setSize(24, 14)
                .setColorForeground(color(120))
                .setColorActive(color(255))
                .setColorLabel(color(255))
                .setItemsPerRow(16)
                .setSpacingColumn(26)
                .setNoneSelectedAllowed(false);

        for (i=0; i<96+16; i++) {
            String label = ""+i; //$NON-NLS-1$
            if (i<10) {
                label = "0"+i; //$NON-NLS-1$
            }
            presetButtons.addItem(label, i);
        }
        presetButtons.activate(col.getSelectedPreset());
        presetButtons.moveTo(presetTab);                
        
        loadPreset = cp5.addButton(GuiElement.LOAD_PRESET.guiText(), 0,
        		GENERIC_X_OFS+2*Theme.DROPBOX_XOFS, yPosStartDrowdown+124, 100, 15);
        loadPreset.setCaptionLabel(GuiElement.LOAD_PRESET.guiText());
        loadPreset.moveTo(presetTab);
        cp5.getTooltip().register(GuiElement.LOAD_PRESET.guiText(),Messages.getString("GeneratorGui.TOOLTIP_LOAD_PRESET")); //$NON-NLS-1$

        savePreset = cp5.addButton(GuiElement.SAVE_PRESET.guiText(), 0,
        		GENERIC_X_OFS+3*Theme.DROPBOX_XOFS, yPosStartDrowdown+124, 100, 15);
        savePreset.setCaptionLabel(GuiElement.SAVE_PRESET.guiText());
        savePreset.moveTo(presetTab);
        cp5.getTooltip().register(GuiElement.SAVE_PRESET.guiText(),Messages.getString("GeneratorGui.TOOLTIP_SAVE_PRESET")); //$NON-NLS-1$

        presetName = cp5.addTextfield("presetName", 20, yPosStartDrowdown+124, Theme.DROPBOXLIST_LENGTH*2, 16).moveTo(presetTab); //$NON-NLS-1$
        presetInfo = cp5.addTextlabel("presetInfo", "", 160, yPosStartDrowdown+142).moveTo(presetTab).getValueLabel();         //$NON-NLS-1$ //$NON-NLS-2$
        
        updateCurrentPresetState();
        
        //-------------
        //Info tab
        //-------------
        
        int yposAdd = 18;
        int xposAdd = 200;

        //center it, we have 3 row which are 160 pixels wide
        int xOfs = (this.getWidth()-3*xposAdd)/2;
        int nfoYPos = yPosStartDrowdown+20;
        int nfoXPos = xOfs;

        cp5.addTextlabel("nfoFpsConf", Messages.getString("GeneratorGui.CONF_FPS")+col.getFps(), nfoXPos, nfoYPos).moveTo(infoTab).getValueLabel(); //$NON-NLS-1$ //$NON-NLS-2$
        nfoYPos+=yposAdd;
        currentFps = cp5.addTextlabel("nfoFpsCurrent", "", nfoXPos, nfoYPos).moveTo(infoTab).getValueLabel(); //$NON-NLS-1$ //$NON-NLS-2$
        nfoYPos+=yposAdd;
        runtime = cp5.addTextlabel("nfoRuntime", "", nfoXPos, nfoYPos).moveTo(infoTab).getValueLabel(); //$NON-NLS-1$ //$NON-NLS-2$
        nfoYPos+=yposAdd;
        cp5.addTextlabel("nfoSrvVersion", Messages.getString("GeneratorGui.SERVER_VERSION")+Collector.getInstance().getPixConStat().getVersion(), nfoXPos, nfoYPos).moveTo(infoTab).getValueLabel(); //$NON-NLS-1$ //$NON-NLS-2$
        nfoYPos+=yposAdd;
        currentVolume = cp5.addTextlabel("nfoVolumeCurrent", Messages.getString("GeneratorGui.CURRENT_VOLUME")+Sound.getInstance().getVolumeNormalized(), nfoXPos, nfoYPos).moveTo(infoTab).getValueLabel(); //$NON-NLS-1$ //$NON-NLS-2$
        nfoYPos+=yposAdd;
        cp5.addTextlabel("nfoWindowHeight", Messages.getString("GeneratorGui.INFO_WINDOW_HEIGHT")+this.getHeight(), nfoXPos, nfoYPos).moveTo(infoTab).getValueLabel(); //$NON-NLS-1$ //$NON-NLS-2$
        nfoYPos+=yposAdd;
        int ibsX= col.getPixelControllerGenerator().getGenerator(0).getInternalBufferXSize();
        int ibsY= col.getPixelControllerGenerator().getGenerator(0).getInternalBufferYSize();
        cp5.addTextlabel("nfoInternalBuffer", Messages.getString("GeneratorGui.INFO_INTERNAL_BUFFERSIZE")+ibsX+"/"+ibsY, nfoXPos, nfoYPos).moveTo(infoTab).getValueLabel(); //$NON-NLS-1$ //$NON-NLS-2$
        
        nfoXPos += xposAdd;
        nfoYPos = yPosStartDrowdown+20;
        Output output = col.getOutputDevice();
        if (output!=null) {
            String gammaText = WordUtils.capitalizeFully(StringUtils.replace(output.getGammaType().toString(), "_", " "));
            cp5.addTextlabel("nfoGamma", Messages.getString("GeneratorGui.GAMMA_CORRECTION")+gammaText, nfoXPos, nfoYPos).moveTo(infoTab).getValueLabel(); //$NON-NLS-1$ //$NON-NLS-2$        	
            nfoYPos+=yposAdd;
            cp5.addTextlabel("nfoBps", Messages.getString("GeneratorGui.OUTPUT_BPP")+output.getBpp(), nfoXPos, nfoYPos).moveTo(infoTab).getValueLabel(); //$NON-NLS-1$ //$NON-NLS-2$
            nfoYPos+=yposAdd;
        }
        sentFrames = cp5.addTextlabel("nfoSentFrames", "", nfoXPos, nfoYPos).moveTo(infoTab).getValueLabel(); //$NON-NLS-1$ //$NON-NLS-2$
        nfoYPos+=yposAdd;        
        outputErrorCounter = cp5.addTextlabel("nfoErrorFrames", "", nfoXPos, nfoYPos).moveTo(infoTab).getValueLabel(); //$NON-NLS-1$ //$NON-NLS-2$
        nfoYPos+=yposAdd;                            
        outputState = cp5.addTextlabel("nfoOutputState", "", nfoXPos, nfoYPos).moveTo(infoTab).getValueLabel(); //$NON-NLS-1$ //$NON-NLS-2$
        nfoYPos+=yposAdd;                  
        
        nfoXPos += xposAdd;
        nfoYPos = yPosStartDrowdown+20;
        String oscPort = col.getPh().getProperty(ConfigConstant.NET_OSC_LISTENING_PORT, ""); //$NON-NLS-1$ //$NON-NLS-2$
        cp5.addTextlabel("nfoOscPort", Messages.getString("GeneratorGui.OSC_PORT")+oscPort, nfoXPos, nfoYPos).moveTo(infoTab).getValueLabel(); //$NON-NLS-1$ //$NON-NLS-2$
        nfoYPos+=yposAdd;
        String tcpPort = col.getPh().getProperty(ConfigConstant.NET_LISTENING_PORT, ""); //$NON-NLS-1$ //$NON-NLS-2$
        cp5.addTextlabel("nfoTcpPort", Messages.getString("GeneratorGui.TCP_PORT")+tcpPort, nfoXPos, nfoYPos).moveTo(infoTab).getValueLabel(); //$NON-NLS-1$ //$NON-NLS-2$
        nfoYPos+=yposAdd;
        oscStatistic = cp5.addTextlabel("nfoOscStatistic", Messages.getString("GeneratorGui.OSC_STATISTIC"), nfoXPos, nfoYPos).moveTo(infoTab).getValueLabel(); 
        nfoYPos+=yposAdd;
        
        
        
        //-------------
        //Help tab
        //-------------
        
        int hlpYOfs = yPosStartDrowdown;
        int hlpXOfs1 = 20;
        int hlpXOfs2 = 240;
        int hlpYposAdd = 15;
        
        cp5.addTextlabel("hlpHeader1", Messages.getString("GeneratorGui.HLP_HEADER1"), hlpXOfs1, hlpYOfs).moveTo(helpTab).getValueLabel(); //$NON-NLS-1$ //$NON-NLS-2$
        hlpYOfs += hlpYposAdd/2;
        cp5.addTextlabel("hlpHeader2", Messages.getString("GeneratorGui.HLP_HEADER2"), hlpXOfs1, hlpYOfs).moveTo(helpTab).getValueLabel(); //$NON-NLS-1$ //$NON-NLS-2$
        hlpYOfs += hlpYposAdd;
        cp5.addTextlabel("hlpHeader3", Messages.getString("GeneratorGui.HLP_HEADER3"), hlpXOfs1, hlpYOfs).moveTo(helpTab).getValueLabel(); //$NON-NLS-1$ //$NON-NLS-2$
        hlpYOfs += hlpYposAdd;
        cp5.addTextlabel("hlpHeader4", Messages.getString("GeneratorGui.HLP_HEADER4"), hlpXOfs1, hlpYOfs).moveTo(helpTab).getValueLabel(); //$NON-NLS-1$ //$NON-NLS-2$
        hlpYOfs += hlpYposAdd;
        hlpYOfs += hlpYposAdd/2;
        cp5.addTextlabel("hlpKeyHeader", Messages.getString("GeneratorGui.HLP_KEYBINDING_HEADER"), hlpXOfs1, hlpYOfs).moveTo(helpTab).getValueLabel(); //$NON-NLS-1$ //$NON-NLS-2$
        
        hlpXOfs1 *=2 ;
        hlpYOfs += hlpYposAdd;
        cp5.addTextlabel("HLP_KEY_19", Messages.getString("GeneratorGui.HLP_KEY_19"), hlpXOfs1, hlpYOfs).moveTo(helpTab).getValueLabel(); //$NON-NLS-1$ //$NON-NLS-2$

        hlpYOfs += hlpYposAdd;
        cp5.addTextlabel("HLP_KEY_F", Messages.getString("GeneratorGui.HLP_KEY_F"), hlpXOfs1, hlpYOfs).moveTo(helpTab).getValueLabel(); //$NON-NLS-1$ //$NON-NLS-2$
        cp5.addTextlabel("HLP_KEY_G", Messages.getString("GeneratorGui.HLP_KEY_G"), hlpXOfs2, hlpYOfs).moveTo(helpTab).getValueLabel(); //$NON-NLS-1$ //$NON-NLS-2$

        hlpYOfs += hlpYposAdd;
        cp5.addTextlabel("HLP_KEY_W", Messages.getString("GeneratorGui.HLP_KEY_W"), hlpXOfs1, hlpYOfs).moveTo(helpTab).getValueLabel(); //$NON-NLS-1$ //$NON-NLS-2$
        cp5.addTextlabel("HLP_KEY_E", Messages.getString("GeneratorGui.HLP_KEY_E"), hlpXOfs2, hlpYOfs).moveTo(helpTab).getValueLabel(); //$NON-NLS-1$ //$NON-NLS-2$

        hlpYOfs += hlpYposAdd;
        cp5.addTextlabel("HLP_KEY_M", Messages.getString("GeneratorGui.HLP_KEY_M"), hlpXOfs1, hlpYOfs).moveTo(helpTab).getValueLabel(); //$NON-NLS-1$ //$NON-NLS-2$
        
        hlpYOfs += hlpYposAdd;
        cp5.addTextlabel("HLP_KEY_C", Messages.getString("GeneratorGui.HLP_KEY_C"), hlpXOfs1, hlpYOfs).moveTo(helpTab).getValueLabel(); //$NON-NLS-1$ //$NON-NLS-2$

        hlpYOfs += hlpYposAdd;
        cp5.addTextlabel("HLP_KEY_R", Messages.getString("GeneratorGui.HLP_KEY_R"), hlpXOfs1, hlpYOfs).moveTo(helpTab).getValueLabel(); //$NON-NLS-1$ //$NON-NLS-2$
        
        hlpYOfs += hlpYposAdd;
        cp5.addTextlabel("HLP_KEY_LEFT", Messages.getString("GeneratorGui.HLP_KEY_LEFT"), hlpXOfs1, hlpYOfs).moveTo(helpTab).getValueLabel(); //$NON-NLS-1$ //$NON-NLS-2$
        cp5.addTextlabel("HLP_KEY_RIGHT", Messages.getString("GeneratorGui.HLP_KEY_RIGHT"), hlpXOfs2, hlpYOfs).moveTo(helpTab).getValueLabel(); //$NON-NLS-1$ //$NON-NLS-2$
        //----------
        // LOGO
        //----------    

        try {
            logo = loadImage("gui"+File.separatorChar+"guilogo.jpg");   
            LOG.log(Level.INFO, "GUI logo loaded");
        } catch (Exception e) {
            LOG.log(Level.INFO, "Failed to load gui logo!",e);
        }
        

        //----------
        // MISC
        //----------    

        int xSizeForEachWidget = (windowWidth-2*GENERIC_X_OFS)/NR_OF_WIDGETS;        
        
        cp5.addTextlabel("frameDesc", Messages.getString("GeneratorGui.FRAME_PROGRESS"), GENERIC_X_OFS, GENERIC_Y_OFS).moveTo(ALWAYS_VISIBLE_TAB).getValueLabel(); //$NON-NLS-1$ //$NON-NLS-2$
        cp5.addTextlabel("sndDesc", Messages.getString("GeneratorGui.SOUND_DESC"), GENERIC_X_OFS+xSizeForEachWidget, GENERIC_Y_OFS).moveTo(ALWAYS_VISIBLE_TAB).getValueLabel(); //$NON-NLS-1$ //$NON-NLS-2$
        cp5.addTextlabel("sndVol", Messages.getString("GeneratorGui.INPUT_VOLUME"), GENERIC_X_OFS+xSizeForEachWidget*2, GENERIC_Y_OFS).moveTo(ALWAYS_VISIBLE_TAB).getValueLabel(); //$NON-NLS-1$ //$NON-NLS-2$
        cp5.addTextlabel("outputDevice", Messages.getString("GeneratorGui.OUTPUT_DEVICE"), GENERIC_X_OFS+xSizeForEachWidget*3, GENERIC_Y_OFS).moveTo(ALWAYS_VISIBLE_TAB).getValueLabel(); //$NON-NLS-1$ //$NON-NLS-2$
        cp5.addTextlabel("outputDeviceName", col.getOutputDeviceName(), 15+GENERIC_X_OFS+xSizeForEachWidget*3, 2+GENERIC_Y_OFS+10).moveTo(ALWAYS_VISIBLE_TAB).getValueLabel(); //$NON-NLS-1$
        
        //register event listener
        cp5.addListener(listener);

        //select first visual
        selectedVisualList.activate(0);
        selectedOutputs.activate(0);
        
        initialized = true;
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
        LOG.log(Level.INFO, "choose new preset "+val); //$NON-NLS-1$
        updateCurrentPresetState();
    }

    /**
     * this callback method is needed by the library but unused
     * @param val
     */
    public void CURRENT_VISUAL(int val) {
        //unused
    }
    
    
    /**
     * 
     * @param col
     * @return
     */
    private int getVisualCenter(Collector col) {
    	return (windowWidth - (col.getAllVisuals().size() * singleVisualXSize))/2;
    }
    
    
    /**
     * draw the whole internal buffer on screen.
     * this method is quite cpu intensive
     */
    public void draw() {
        long l = System.currentTimeMillis();
        Collector col = Collector.getInstance();
        int localX = getVisualCenter(col);
        int localY=40;

        long frames = col.getPixConStat().getFrameCount();
        
        //clear screen each 2nd frame and put logo on it
        if (frames%2==1) {
            background(0);        
            if (logo!=null) {
                image(logo, width-logo.width, height-logo.height);
            }
        }

        //draw internal buffer only if enabled
        if (col.isInternalVisualsVisible()) {
            //lazy init
            if (pImage==null) {
                //create an image out of the buffer
                pImage = col.getPapplet().createImage(singleVisualXSize, singleVisualYSize, PApplet.RGB );
            }

            //set used to find out if visual is on screen
            Set<Integer> outputId = new HashSet<Integer>();
            for (OutputMapping om: col.getAllOutputMappings()) {
                outputId.add(om.getVisualId());
            }
            
            //draw output buffer and marker
            int ofs=0;
            for (Visual v: col.getAllVisuals()) {

                //use always the pixel resize option to reduce cpu load
            	buffer = col.getMatrix().resizeBufferForDevice(v.getBuffer(), ResizeName.PIXEL_RESIZE, singleVisualXSize, singleVisualYSize);
            	
            	pImage.loadPixels();
            	System.arraycopy(buffer, 0, pImage.pixels, 0, singleVisualXSize*singleVisualYSize);
            	pImage.updatePixels();

            	//display the image
            	image(pImage, localX, localY);      		

            	//highlight current output
            	if (outputId.contains(ofs)) {
            		fill(20, 235, 20);
            	} else {
            		fill(235, 20, 20);
            	}	
            	rect(localX+5, localY+5, 10, 10);				

                localX += pImage.width;
                ofs++;
            }        	
        }        
        
        //beat detection
        displayWidgets(GENERIC_Y_OFS);
        
        //update more details, mostly info tab
        if (frames%12==1) {
            //INFO TAB
            int fps10 = (int)(col.getPixConStat().getCurrentFps()*10);
            currentFps.setText(Messages.getString("GeneratorGui.CURRENT_FPS")+fps10/10f); //$NON-NLS-1$
            String runningSince = DurationFormatUtils.formatDuration(System.currentTimeMillis() - col.getPixConStat().getStartTime(), "H:mm:ss");             //$NON-NLS-1$
            runtime.setText(Messages.getString("GeneratorGui.RUNNING_SINCE")+runningSince);          //$NON-NLS-1$
            sentFrames.setText(Messages.getString("GeneratorGui.SENT_FRAMES")+frames); //$NON-NLS-1$
            int snd1000 = (int)(1000f*Sound.getInstance().getVolumeNormalized());
            currentVolume.setText(Messages.getString("GeneratorGui.CURRENT_VOLUME")+(snd1000/1000f));
            
            Output output = col.getOutputDevice();
            if (output!=null) {
                String outputStateStr = WordUtils.capitalizeFully(output.getConnectionStatus());
                outputState.setText(outputStateStr);
                outputErrorCounter.setText(Messages.getString("GeneratorGui.IO_ERRORS")+output.getErrorCounter());             //$NON-NLS-1$            	
            }
            long recievedMB = col.getPixConStat().getRecievedOscBytes()/1024/1024;
            String oscStat  = Messages.getString("GeneratorGui.OSC_STATISTIC")+col.getPixConStat().getRecievedOscPakets()+"/"+recievedMB;
            oscStatistic.setText(oscStat);
            
            Visual v = col.getVisual(col.getCurrentVisual());
            if (v!=null) {		    
                if (v.getGenerator1().isPassThoughModeActive() || v.getGenerator2().isPassThoughModeActive()) {
                	passThroughMode.setText(Messages.getString("GeneratorGui.PASSTHROUGH_MODE"));
                } else {
                	passThroughMode.setText("");
                }
            }

        }
        
        //refresh gui from time to time
        if (col.isTriggerGuiRefresh() || frames++%50==2) {
            callbackRefreshWholeGui();
            col.setTriggerGuiRefresh(false);
        }
        
        //update gui
        cp5.draw(); 

        //track used time
        col.getPixConStat().trackTime(TimeMeasureItemGlobal.DEBUG_WINDOW, System.currentTimeMillis()-l);
    }

    
    /**
     * update preset stuff
     */
    public void updateCurrentPresetState() {
        Collector col = Collector.getInstance();
        PresetSettings preset = col.getPresets().get(col.getSelectedPreset());
        if (preset!=null) {
            String presetState;
            if (preset.isSlotUsed()) {
                presetState = Messages.getString("GeneratorGui.STR_TRUE"); //$NON-NLS-1$
            } else {
                presetState = Messages.getString("GeneratorGui.STR_FALSE"); //$NON-NLS-1$
            }

            presetInfo.setText(Messages.getString("GeneratorGui.VALID_ENTRY_EMPTY")+presetState); //$NON-NLS-1$
            presetName.setText(preset.getName());                
        } else {
            presetInfo.setText(Messages.getString("GeneratorGui.VALID_ENTRY_FALSE")); //$NON-NLS-1$
            presetName.setText("");                             //$NON-NLS-1$
        }
        
        col.setTriggerGuiRefresh(true);
    }
    
    /**
     * 
     * @param localY
     */
    private void displayWidgets(int localY) {
        int xSizeForEachWidget = (windowWidth-2*GENERIC_X_OFS)/NR_OF_WIDGETS;
        
        //display frame progress
        int frames = Collector.getInstance().getFrames() % (xSizeForEachWidget-WIDGET_BOARDER);        
        fill(0, 180, 234);
        rect(GENERIC_X_OFS, localY+SELECTED_MARKER+4, frames, WIDGET_BAR_SIZE);
        fill(2, 52, 77);
        rect(GENERIC_X_OFS+frames, localY+SELECTED_MARKER+4, xSizeForEachWidget-frames-WIDGET_BOARDER, WIDGET_BAR_SIZE);

        //draw sound stats
        Sound snd = Sound.getInstance();
        int xofs = GENERIC_X_OFS+xSizeForEachWidget;
        int xx = (xSizeForEachWidget-WIDGET_BOARDER*2)/3;

        colorSelect(snd.isKick());
        rect(xofs, localY+SELECTED_MARKER+4, xx, WIDGET_BAR_SIZE);

        xofs+=xx+WIDGET_BOARDER/2;
        colorSelect(snd.isSnare());
        rect(xofs, localY+SELECTED_MARKER+4, xx, WIDGET_BAR_SIZE);

        xofs+=xx+WIDGET_BOARDER/2;
        colorSelect(snd.isHat());        
        rect(xofs, localY+SELECTED_MARKER+4, xx, WIDGET_BAR_SIZE);        
        
        //Draw input volume
        int vol = (int)((xSizeForEachWidget-WIDGET_BOARDER)*snd.getVolumeNormalized());        
        fill(0, 180, 234);
        rect(GENERIC_X_OFS+2*xSizeForEachWidget, localY+SELECTED_MARKER+4, vol, WIDGET_BAR_SIZE);
        fill(2, 52, 77);
        rect(GENERIC_X_OFS+2*xSizeForEachWidget+vol, localY+SELECTED_MARKER+4, xSizeForEachWidget-WIDGET_BOARDER-vol, WIDGET_BAR_SIZE);
        
        //draw output device
        Boolean isConnected = Collector.getInstance().isOutputDeviceConnected();
        if (isConnected!=null) {
            //highlight current output
            if (isConnected) {
                fill(20, 235, 20);
            } else {
                fill(235, 20, 20);
            }   
            rect(3+GENERIC_X_OFS+3*xSizeForEachWidget, localY+SELECTED_MARKER, 10, 10);               
        }
        
    }


    /**
     * 
     * @param b
     */
    private void colorSelect(boolean b) {
        if (b) {
            fill(0, 180, 234);	
        } else {
            fill(2, 52, 77);	
        }		
    }

    /**
     * update only minimal parts of the gui
     */
    public Collector callbackRefreshMini() {
        //LOG.log(Level.INFO, "Refresh Partitial GUI");
        Collector col = Collector.getInstance();

        //get visual status			
        Visual v = col.getVisual(col.getCurrentVisual());
        if (v!=null) {		    
            generatorListOne.setLabel(generatorListOne.getItem(v.getGenerator1Idx()).getName());
            generatorListTwo.setLabel(generatorListTwo.getItem(v.getGenerator2Idx()).getName());
            effectListOne.setLabel(effectListOne.getItem(v.getEffect1Idx()).getName());
            effectListTwo.setLabel(effectListTwo.getItem(v.getEffect2Idx()).getName());
            mixerList.setLabel(mixerList.getItem(v.getMixerIdx()).getName());
            colorSetList.setLabel(v.getColorSet().getName());            
        }

        //get output status
        OutputMapping om = col.getOutputMappings(col.getCurrentOutput());
        dropdownOutputVisual.setLabel(dropdownOutputVisual.getItem(om.getVisualId()).getName());
        dropdownOutputFader.setLabel(dropdownOutputFader.getItem(om.getFader().getId()).getName());

        //do not set the current preset, the result would be an endless event loop, limitation of oscp5
        //see https://code.google.com/p/controlp5/issues/detail?id=79
        //presetButtons.activate(col.getSelectedPreset());
        
        return col;
    }

    /**
     * refresh whole gui
     */
    public void callbackRefreshWholeGui() {
        //LOG.log(Level.INFO, "Refresh Whole GUI");
        Collector col = this.callbackRefreshMini();		

        PixelControllerEffect pce = col.getPixelControllerEffect();
        thresholdSlider.changeValue(pce.getThresholdValue());
        brightnessControll.changeValue(col.getPixelControllerGenerator().getBrightness()*100);
        fxRotoSlider.changeValue(pce.getRotoZoomAngle());
        zoomOptions.setLabel(zoomOptions.getItem(pce.getZoomOption()).getName());                
        
        PixelControllerGenerator pcg = col.getPixelControllerGenerator();
        blinkenLightsList.setLabel(pcg.getFileBlinken()); 
        imageList.setLabel(pcg.getFileImageSimple());
        textwriterOption.setLabel(textwriterOption.getItem(pcg.getTextOption()).getName());
        
        // update current visual
        //TODO somethings fishy here...
        //selectedVisualList.activate(col.getCurrentVisual());
    }


    /**
     * mouse listener, used to close dropdown lists
     * 
     */
    public void mousePressed() {
    	
    	if (!initialized) {
    		return;
    	}
    	
        // print the current mouseoverlist on mouse pressed
        List <GuiElement> clickedOn = new ArrayList<GuiElement>();
        List<ControllerInterface<?>> lci = cp5.getWindow().getMouseOverList();
        for (ControllerInterface<?> ci: lci) {
            GuiElement ge = GuiElement.getGuiElement(ci.getName());
            if (ge!=null) {
                clickedOn.add(ge);				
            }
        }

        //close all open tabs
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
        if (!clickedOn.contains(GuiElement.TEXTWR_OPTION)) {
            textwriterOption.setOpen(false);
        }
        if (!clickedOn.contains(GuiElement.COLORSCROLL_OPTIONS)) {
            colorScrollList.setOpen(false);
        }
        if (!clickedOn.contains(GuiElement.ZOOM_OPTIONS)) {
            zoomOptions.setOpen(false);
        }

        if (allOutputTabVis!=null && !clickedOn.contains(GuiElement.OUTPUT_ALL_SELECTED_VISUAL_DROPDOWN)) {
            allOutputTabVis.setOpen(false);
        }
        
        if (allOutputTabFader!=null && !clickedOn.contains(GuiElement.OUTPUT_ALL_FADER_DROPDOWN)) {
            allOutputTabFader.setOpen(false);
        }

    }


    /**
     * Keyhandler
     * 
     * select visual by keypress
     */
    public void keyPressed() {
    	//ignore escape key
    	if (keyCode==ESC) {
    		key=0;
    	} else {
            KeyboardHandler.keyboardHandler(key, keyCode);    		
    	}
    }

    /* (non-Javadoc)
     * @see com.neophob.sematrix.output.gui.GuiCallbackAction#activeVisual(int)
     */
    @Override
    public void activeVisual(int n) {
        selectedVisualList.activate(n);
    }

	@Override
	public void refreshGui() {
		Collector.getInstance().setTriggerGuiRefresh(true);		
	}

	/**
	 * 
	 * @return the user specific preset name
	 */
    public String getCurrentPresetName() {
        return presetName.getText();
    }

    public boolean isTextfieldInEditMode() {
    	if (!initialized) {
    		return false;
    	}

    	return textGenerator.isFocus() || presetName.isFocus();
    }

	@Override
	public void selectPreviousTab() {
		Tab currentTab = cp5.getWindow().getCurrentTab();
		Tab lastTab=null;
		for (Tab t: allTabs) {
			if (t==currentTab && lastTab!=null) {
				lastTab.bringToFront();
				return;
			}
			lastTab = t;
		}
		//activate the last tab
		allTabs.get(allTabs.size()-1).bringToFront();
	}
	
	@Override
	public void selectNextTab() {
		boolean activateNextTab = false;		
		Tab currentTab = cp5.getWindow().getCurrentTab();		
		
		for (Tab t: allTabs) {
			if (activateNextTab) {
				//active next tab and return
				t.bringToFront();
				return;
			}
			
			if (t==currentTab) {
				activateNextTab = true;
			}
		}
		
		//active the first tab
		if (activateNextTab) {
			allTabs.get(0).bringToFront();
		}
	}

}
