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
package com.neophob.sematrix.gui.guibuilder;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.apache.commons.lang3.time.DurationFormatUtils;

import processing.core.PApplet;
import processing.core.PImage;

import com.neophob.PixelControllerP5;
import com.neophob.sematrix.core.glue.FileUtils;
import com.neophob.sematrix.core.glue.ShufflerOffset;
import com.neophob.sematrix.core.glue.impl.FileUtilsLocalImpl;
import com.neophob.sematrix.core.output.IOutput;
import com.neophob.sematrix.core.preset.PresetService;
import com.neophob.sematrix.core.preset.PresetSettings;
import com.neophob.sematrix.core.properties.ConfigConstant;
import com.neophob.sematrix.core.properties.ValidCommand;
import com.neophob.sematrix.core.resize.IResize;
import com.neophob.sematrix.core.resize.PixelControllerResize;
import com.neophob.sematrix.core.resize.Resize.ResizeName;
import com.neophob.sematrix.core.sound.BeatToAnimation;
import com.neophob.sematrix.core.sound.ISound;
import com.neophob.sematrix.core.visual.MatrixData;
import com.neophob.sematrix.core.visual.OutputMapping;
import com.neophob.sematrix.core.visual.color.IColorSet;
import com.neophob.sematrix.core.visual.effect.Effect.EffectName;
import com.neophob.sematrix.core.visual.generator.ColorScroll.ScrollMode;
import com.neophob.sematrix.core.visual.generator.Generator.GeneratorName;
import com.neophob.sematrix.core.visual.mixer.Mixer.MixerName;
import com.neophob.sematrix.gui.callback.GuiUpdateFeedback;
import com.neophob.sematrix.gui.guibuilder.eventhandler.KeyboardHandler;
import com.neophob.sematrix.gui.guibuilder.eventhandler.P5EventListener;
import com.neophob.sematrix.gui.i18n.Messages;
import com.neophob.sematrix.gui.model.GuiElement;
import com.neophob.sematrix.gui.model.WindowSizeCalculator;
import com.neophob.sematrix.gui.service.PixConServer;

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

    private static final int MINIMAL_VISUAL_WIDTH = 120;

    private static final int GENERIC_X_OFS = 5;
    private static final int GENERIC_Y_OFS = 8;

    private static final int NR_OF_WIDGETS = 4;
    private static final int WIDGET_BOARDER = 10;
    private static final int WIDGET_BAR_SIZE = 6;

    private static final String ALWAYS_VISIBLE_TAB = "global"; //$NON-NLS-1$

    /** The log. */
    private static final Logger LOG = Logger.getLogger(GeneratorGui.class.getName());

    /** The y. */
    private int windowWidth, windowHeight;

    /** The p image. */
    private PImage pImage = null;
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
    private Toggle freeze;

    // Effect Tab
    private Slider thresholdSlider, fxRotoSlider;
    private DropdownList textureDeformOptions, zoomOptions;

    // Generator Tab
    private DropdownList blinkenLightsList, imageList, textwriterOption, beatWorkmode;
    private Label passThroughMode;
    private Slider generatorSpeedSlider;

    // Output Tab
    private DropdownList dropdownOutputVisual;
    private DropdownList dropdownOutputFader;

    // All Output Tab
    private DropdownList allOutputTabVis;
    private DropdownList allOutputTabFader;
    private DropdownList colorScrollList;
    private DropdownList colorSetList;

    // preset tab
    private RadioButton presetButtons;
    private Button loadPreset, savePreset;
    private Label presetInfo;
    private Textfield presetName;

    private CheckBox randomCheckbox;

    // info tab
    private List<Tab> allTabs = new ArrayList<Tab>();
    private Label currentFps, configuredFps;
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

    private Messages messages;

    private long frames = 0;

    private int coreFps;

    private PixConServer pixConServer;
    private IResize resize;
    private int nrOfVisuals;
    private P5EventListener listener;

    /**
     * Instantiates a new internal buffer.
     * 
     * @param displayHoriz
     *            the display horiz
     * @param x
     *            the x
     * @param y
     *            the y
     * @param singleVisualXSize
     *            the target x size
     * @param singleVisualYSize
     *            the target y size
     */
    public GeneratorGui(PixConServer pixelController, WindowSizeCalculator wsc) {
        super();
        this.pixConServer = pixelController;
        this.windowWidth = wsc.getWindowWidth();
        this.windowHeight = wsc.getWindowHeight();
        this.singleVisualXSize = wsc.getSingleVisualWidth();
        this.singleVisualYSize = wsc.getSingleVisualHeight();
        this.p5GuiYOffset = this.singleVisualYSize + 110;

        PixelControllerResize pcr = new PixelControllerResize();
        pcr.initAll();
        resize = pcr.getResize(ResizeName.PIXEL_RESIZE);
        messages = new Messages();
    }

    /*
     * (non-Javadoc)
     * 
     * @see processing.core.PApplet#setup()
     */
    public void setup() {
        size(windowWidth, windowHeight);
        LOG.log(Level.INFO,
                "Create GUI Window with size " + this.getWidth() + "/" + this.getHeight()); //$NON-NLS-1$ //$NON-NLS-2$

        frameRate(PixelControllerP5.FPS);
        smooth();
        background(0, 0, 0);
        int i = 0;

        cp5 = new ControlP5(this);
        cp5.setAutoDraw(false);

        // press alt and you can move gui elements arround. disable this
        // *should* work but does not...
        cp5.setMoveable(false);

        // alt-h hide all controls - I don't want that!
        cp5.disableShortcuts();

        cp5.getTooltip().setDelay(200);
        this.listener = new P5EventListener(pixConServer, this);

        // selected visual
        this.nrOfVisuals = pixConServer.getNrOfVisuals();

        int w = singleVisualXSize < MINIMAL_VISUAL_WIDTH ? MINIMAL_VISUAL_WIDTH - 1
                : singleVisualXSize - 1;
        selectedVisualList = cp5.addRadioButton(GuiElement.CURRENT_VISUAL.guiText(),
                getVisualCenter(), p5GuiYOffset - 58);
        selectedVisualList.setItemsPerRow(nrOfVisuals);
        selectedVisualList.setNoneSelectedAllowed(false);
        for (i = 0; i < nrOfVisuals; i++) {
            String s = messages.getString("GeneratorGui.GUI_SELECTED_VISUAL") + (1 + i); //$NON-NLS-1$
            Toggle t = cp5.addToggle(s, 0, 0, w, 13);
            t.setCaptionLabel(s);
            selectedVisualList.addItem(t, i);
            cp5.getTooltip()
                    .register(
                            s,
                            messages.getString("GeneratorGui.GUI_SELECTED_VISUAL_TOOLTIP_PREFIX") + (1 + i) + messages.getString("GeneratorGui.GUI_SELECTED_VISUAL_TOOLTIP_POSTFIX")); //$NON-NLS-1$ //$NON-NLS-2$
        }
        selectedVisualList.moveTo(ALWAYS_VISIBLE_TAB);

        cp5.addTextlabel(
                "gen1", messages.getString("GeneratorGui.GUI_GENERATOR_LAYER_1"), GENERIC_X_OFS + 3, 3 + p5GuiYOffset).moveTo(ALWAYS_VISIBLE_TAB).getValueLabel(); //$NON-NLS-1$ //$NON-NLS-2$
        cp5.addTextlabel(
                "gen2", messages.getString("GeneratorGui.GUI_GENERATOR_LAYER_2"), GENERIC_X_OFS + 3 + 3 * Theme.DROPBOX_XOFS, 3 + p5GuiYOffset).moveTo(ALWAYS_VISIBLE_TAB).getValueLabel(); //$NON-NLS-1$ //$NON-NLS-2$
        cp5.addTextlabel(
                "fx1", messages.getString("GeneratorGui.GUI_EFFECT_LAYER_1"), GENERIC_X_OFS + 3 + 1 * Theme.DROPBOX_XOFS, 3 + p5GuiYOffset).moveTo(ALWAYS_VISIBLE_TAB).getValueLabel(); //$NON-NLS-1$ //$NON-NLS-2$
        cp5.addTextlabel(
                "fx2", messages.getString("GeneratorGui.GUI_EFFECT_LAYER_2"), GENERIC_X_OFS + 3 + 4 * Theme.DROPBOX_XOFS, 3 + p5GuiYOffset).moveTo(ALWAYS_VISIBLE_TAB).getValueLabel(); //$NON-NLS-1$ //$NON-NLS-2$
        cp5.addTextlabel(
                "mix2", messages.getString("GeneratorGui.GUI_LAYER_MIXER"), GENERIC_X_OFS + 3 + 2 * Theme.DROPBOX_XOFS, 3 + p5GuiYOffset).moveTo(ALWAYS_VISIBLE_TAB).getValueLabel(); //$NON-NLS-1$ //$NON-NLS-2$

        cp5.getTooltip().register(
                "gen1", messages.getString("GeneratorGui.GUI_TOOLTIP_GENERATOR_1")); //$NON-NLS-1$ //$NON-NLS-2$
        cp5.getTooltip().register(
                "gen2", messages.getString("GeneratorGui.GUI_TOOLTIP_GENERATOR_2")); //$NON-NLS-1$ //$NON-NLS-2$
        cp5.getTooltip().register("fx1", messages.getString("GeneratorGui.GUI_TOOLTIP_EFFECT_1")); //$NON-NLS-1$ //$NON-NLS-2$
        cp5.getTooltip().register("fx2", messages.getString("GeneratorGui.GUI_TOOLTIP_EFFECT_2")); //$NON-NLS-1$ //$NON-NLS-2$
        cp5.getTooltip().register("mix2", messages.getString("GeneratorGui.GUI_TOOLTIP_MIXER")); //$NON-NLS-1$ //$NON-NLS-2$

        // Generator
        generatorListOne = cp5.addDropdownList(GuiElement.GENERATOR_ONE_DROPDOWN.guiText(),
                GENERIC_X_OFS, p5GuiYOffset, Theme.DROPBOXLIST_LENGTH, 140);
        generatorListTwo = cp5
                .addDropdownList(GuiElement.GENERATOR_TWO_DROPDOWN.guiText(), GENERIC_X_OFS + 3
                        * Theme.DROPBOX_XOFS, p5GuiYOffset, Theme.DROPBOXLIST_LENGTH, 140);
        Theme.themeDropdownList(generatorListOne);
        Theme.themeDropdownList(generatorListTwo);

        for (GeneratorName gn : GeneratorName.values()) {
            generatorListOne.addItem(gn.guiText(), gn.getId());
            generatorListTwo.addItem(gn.guiText(), gn.getId());
        }

        generatorListOne.setLabel(generatorListOne.getItem(1).getName());
        generatorListTwo.setLabel(generatorListTwo.getItem(1).getName());
        generatorListOne.moveTo(ALWAYS_VISIBLE_TAB);
        generatorListTwo.moveTo(ALWAYS_VISIBLE_TAB);
        generatorListOne.setHeight(Theme.DROPBOXLIST_LARGE_HEIGHT);
        generatorListTwo.setHeight(Theme.DROPBOXLIST_LARGE_HEIGHT);

        // Effect
        effectListOne = cp5.addDropdownList(GuiElement.EFFECT_ONE_DROPDOWN.guiText(), GENERIC_X_OFS
                + 1 * Theme.DROPBOX_XOFS, p5GuiYOffset, Theme.DROPBOXLIST_LENGTH, 140);
        effectListTwo = cp5.addDropdownList(GuiElement.EFFECT_TWO_DROPDOWN.guiText(), GENERIC_X_OFS
                + 4 * Theme.DROPBOX_XOFS, p5GuiYOffset, Theme.DROPBOXLIST_LENGTH, 140);
        Theme.themeDropdownList(effectListOne);
        Theme.themeDropdownList(effectListTwo);

        for (EffectName gn : EffectName.values()) {
            effectListOne.addItem(gn.guiText(), gn.getId());
            effectListTwo.addItem(gn.guiText(), gn.getId());
        }
        effectListOne.setLabel(effectListOne.getItem(0).getName());
        effectListTwo.setLabel(effectListTwo.getItem(0).getName());
        effectListOne.moveTo(ALWAYS_VISIBLE_TAB);
        effectListTwo.moveTo(ALWAYS_VISIBLE_TAB);
        effectListOne.setHeight(Theme.DROPBOXLIST_LARGE_HEIGHT);
        effectListTwo.setHeight(Theme.DROPBOXLIST_LARGE_HEIGHT);

        // Mixer
        mixerList = cp5.addDropdownList(GuiElement.MIXER_DROPDOWN.guiText(), GENERIC_X_OFS + 2
                * Theme.DROPBOX_XOFS, p5GuiYOffset, Theme.DROPBOXLIST_LENGTH, 140);
        Theme.themeDropdownList(mixerList);

        for (MixerName gn : MixerName.values()) {
            mixerList.addItem(gn.guiText(), gn.getId());
        }
        mixerList.setLabel(mixerList.getItem(0).getName());
        mixerList.setHeight(Theme.DROPBOXLIST_LARGE_HEIGHT);
        mixerList.moveTo(ALWAYS_VISIBLE_TAB);

        // ---------------------------------
        // TABS
        // ---------------------------------

        final int yPosStartLabel = p5GuiYOffset + 50;
        final int yPosStartDrowdown = p5GuiYOffset + 36;

        cp5.getWindow().setPositionOfTabs(GENERIC_X_OFS, this.getHeight() - 20);

        // there a default tab which is present all the time. rename this tab
        Tab generatorTab = cp5.getTab("default"); //$NON-NLS-1$
        allTabs.add(generatorTab);
        generatorTab.setLabel(messages.getString("GeneratorGui.TAB_GENERATOR_EFFECT")); //$NON-NLS-1$
        Tab outputTab = cp5.addTab(messages.getString("GeneratorGui.TAB_SINGLE_OUTPUT_MAPPING")); //$NON-NLS-1$
        allTabs.add(outputTab);
        Tab allOutputTab = null;

        // add all output mapping only if multiple output panels exist
        if (nrOfVisuals > 2) {
            allOutputTab = cp5.addTab(messages.getString("GeneratorGui.TAB_ALL_OUTPUT_MAPPING")); //$NON-NLS-1$
            allOutputTab.setColorForeground(0xffff0000);
            allTabs.add(allOutputTab);
        }

        Tab randomTab = cp5.addTab(messages.getString("GeneratorGui.TAB_RANDOMIZE")); //$NON-NLS-1$
        allTabs.add(randomTab);
        Tab presetTab = cp5.addTab(messages.getString("GeneratorGui.TAB_PRESETS")); //$NON-NLS-1$
        allTabs.add(presetTab);
        Tab infoTab = cp5.addTab(messages.getString("GeneratorGui.TAB_INFO")); //$NON-NLS-1$
        allTabs.add(infoTab);
        Tab helpTab = cp5.addTab(messages.getString("GeneratorGui.TAB_HELP")); //$NON-NLS-1$
        allTabs.add(helpTab);

        generatorTab.setColorForeground(0xffff0000);
        outputTab.setColorForeground(0xffff0000);
        randomTab.setColorForeground(0xffff0000);
        presetTab.setColorForeground(0xffff0000);
        helpTab.setColorForeground(0xffff0000);

        generatorTab.bringToFront();

        // -------------
        // Generic Options
        // -------------
        // generator speed slider
        generatorSpeedSlider = cp5.addSlider(GuiElement.GENERATOR_SPEED.guiText(), 0f, 2.0f, 1f,
                38 + GENERIC_X_OFS, p5GuiYOffset + 97, 140, 14);
        generatorSpeedSlider.setSliderMode(Slider.FIX);
        generatorSpeedSlider.setGroup(generatorTab);
        generatorSpeedSlider.setDecimalPrecision(0);
        generatorSpeedSlider.setRange(0, 200);
        generatorSpeedSlider.setLabelVisible(true);
        generatorSpeedSlider.setNumberOfTickMarks(21);

        // beat animation
        cp5.addTextlabel("beatWorkmode", messages.getString("GeneratorGui.BEAT_WORKMODE"),
                38 + GENERIC_X_OFS + 2 * Theme.DROPBOX_XOFS, p5GuiYOffset + 113 + 5)
                .moveTo(generatorTab).getValueLabel();
        beatWorkmode = cp5.addDropdownList(GuiElement.BEAT_WORKMODE.guiText(), 38 + GENERIC_X_OFS
                + 2 * Theme.DROPBOX_XOFS, p5GuiYOffset + 113, Theme.DROPBOXLIST_LENGTH, 140);
        Theme.themeDropdownList(beatWorkmode);
        for (BeatToAnimation bta : BeatToAnimation.values()) {
            beatWorkmode.addItem(bta.guiText(), bta.getId());
        }
        beatWorkmode.setLabel(beatWorkmode.getItem(0).getName());
        beatWorkmode.setGroup(generatorTab);
        beatWorkmode.setHeight(Theme.DROPBOXLIST_HEIGHT);

        // freeze update
        freeze = cp5.addToggle(GuiElement.BUTTON_TOGGLE_FREEZE.guiText(), 730, 2, 15, 15).moveTo(
                ALWAYS_VISIBLE_TAB);
        freeze.setLabelVisible(false);
        cp5.addTextlabel("freezeUpdateTxt", messages.getString("GeneratorGui.GUI_TOGGLE_FREEZE"),
                745, 5).moveTo(ALWAYS_VISIBLE_TAB);
        cp5.getTooltip().register(GuiElement.BUTTON_TOGGLE_FREEZE.guiText(),
                messages.getString("GeneratorGui.TOOLTIP_FREEZE")); //$NON-NLS-1$

        // toggle internal visuals
        Toggle t2 = cp5.addToggle(GuiElement.BUTTON_TOGGLE_INTERNAL_VISUALS.guiText(), 730, 20, 15,
                15).moveTo(ALWAYS_VISIBLE_TAB);
        t2.setLabelVisible(false);
        cp5.addTextlabel(
                "toggleIKnternalVisualsTxt", messages.getString("GeneratorGui.GUI_TOGGLE_INTERNAL_BUFFER"), 745, 23).moveTo(ALWAYS_VISIBLE_TAB);; //$NON-NLS-1$
        cp5.getTooltip().register(GuiElement.BUTTON_TOGGLE_INTERNAL_VISUALS.guiText(),
                messages.getString("GeneratorGui.TOOLTIP_GUI_TOGGLE_INTERNAL_BUFFER")); //$NON-NLS-1$

        // -------------
        // GENERATOR/EFFECT tab
        // -------------

        int genFxXOfs = 38 + GENERIC_X_OFS;

        // EFFECTS OPTIONS
        // ---------------
        int genElYOfs = yPosStartDrowdown + 105;
        cp5.addTextlabel(
                "genOptionsFx", messages.getString("GeneratorGui.EFFECT_OPTIONS"), GENERIC_X_OFS, genElYOfs).moveTo(generatorTab).getValueLabel(); //$NON-NLS-1$ //$NON-NLS-2$        

        // threshold slider
        thresholdSlider = cp5.addSlider(GuiElement.THRESHOLD.guiText(), 0, 255, 255, genFxXOfs,
                genElYOfs + 60, 140, 14);
        thresholdSlider.setSliderMode(Slider.FIX);
        thresholdSlider.setGroup(generatorTab);
        thresholdSlider.setDecimalPrecision(0);

        // rotozoom slider
        fxRotoSlider = cp5.addSlider(GuiElement.FX_ROTOZOOMER.guiText(), -127, 127, 0, genFxXOfs
                + 2 * Theme.DROPBOX_XOFS, genElYOfs + 60, 140, 14);
        fxRotoSlider.setSliderMode(Slider.FIX);
        fxRotoSlider.setGroup(generatorTab);
        fxRotoSlider.setDecimalPrecision(0);
        fxRotoSlider.setCaptionLabel(messages.getString("GeneratorGui.EFFECT_ROTOZOOM_SPEED")); //$NON-NLS-1$

        genElYOfs += 25;

        // texturedeform options
        cp5.addTextlabel(
                "genTextdefOpt", messages.getString("GeneratorGui.TEXTUREDDEFORM_OPTIONS"), genFxXOfs + 3 + 0 * Theme.DROPBOX_XOFS, genElYOfs + 16).moveTo(generatorTab).getValueLabel(); //$NON-NLS-1$ //$NON-NLS-2$
        textureDeformOptions = cp5.addDropdownList(GuiElement.TEXTUREDEFORM_OPTIONS.guiText(),
                genFxXOfs + 0 * Theme.DROPBOX_XOFS, genElYOfs + 11, Theme.DROPBOXLIST_LENGTH, 80);
        Theme.themeDropdownList(textureDeformOptions);
        textureDeformOptions.addItem(
                messages.getString("GeneratorGui.TEXTUREDEFORM_ANAMORPHOSIS"), 0); //$NON-NLS-1$
        textureDeformOptions.addItem(messages.getString("GeneratorGui.TEXTUREDEFORM_SPIRAL"), 1); //$NON-NLS-1$
        textureDeformOptions.addItem(
                messages.getString("GeneratorGui.TEXTUREDEFORM_ROTATINGTUNNEL"), 2); //$NON-NLS-1$
        textureDeformOptions.addItem(messages.getString("GeneratorGui.TEXTUREDEFORM_START"), 3); //$NON-NLS-1$
        textureDeformOptions.addItem(messages.getString("GeneratorGui.TEXTUREDEFORM_TUNNEL"), 4); //$NON-NLS-1$
        textureDeformOptions.addItem(messages.getString("GeneratorGui.TEXTUREDEFORM_FLOWER"), 5); //$NON-NLS-1$
        textureDeformOptions.addItem(messages.getString("GeneratorGui.TEXTUREDEFORM_CLOUD"), 6); //$NON-NLS-1$
        textureDeformOptions.addItem(messages.getString("GeneratorGui.TEXTUREDEFORM_PLANAR"), 7); //$NON-NLS-1$
        textureDeformOptions.addItem(messages.getString("GeneratorGui.TEXTUREDEFORM_CIRCLE"), 8); //$NON-NLS-1$
        textureDeformOptions.addItem(messages.getString("GeneratorGui.TEXTUREDEFORM_FLUSH"), 9); //$NON-NLS-1$
        textureDeformOptions.addItem(messages.getString("GeneratorGui.TEXTUREDEFORM_3D"), 10); //$NON-NLS-1$
        textureDeformOptions.setLabel(textureDeformOptions.getItem(0).getName());
        textureDeformOptions.setGroup(generatorTab);

        cp5.addTextlabel(
                "genZoomOpt", messages.getString("GeneratorGui.ZOOM_OPTIONS"), genFxXOfs + 3 + 1 * Theme.DROPBOX_XOFS, genElYOfs + 16).moveTo(generatorTab).getValueLabel(); //$NON-NLS-1$ //$NON-NLS-2$
        zoomOptions = cp5.addDropdownList(GuiElement.ZOOM_OPTIONS.guiText(), genFxXOfs + 1
                * Theme.DROPBOX_XOFS, genElYOfs + 11, Theme.DROPBOXLIST_LENGTH, 80);
        Theme.themeDropdownList(zoomOptions);
        zoomOptions.addItem(messages.getString("GeneratorGui.ZOOM_IN"), 0); //$NON-NLS-1$
        zoomOptions.addItem(messages.getString("GeneratorGui.ZOOM_OUT"), 1); //$NON-NLS-1$
        zoomOptions.addItem(messages.getString("GeneratorGui.ZOOM_HORIZONTAL"), 2); //$NON-NLS-1$
        zoomOptions.addItem(messages.getString("GeneratorGui.ZOOM_VERTICAL"), 3); //$NON-NLS-1$
        zoomOptions.setLabel(zoomOptions.getItem(0).getName());
        zoomOptions.setGroup(generatorTab);

        // GENERATOR OPTIONS
        // -----------------

        genElYOfs = p5GuiYOffset + 35;
        cp5.addTextlabel(
                "genOptionsGen", messages.getString("GeneratorGui.GENERATOR_OPTIONS"), GENERIC_X_OFS, genElYOfs).moveTo(generatorTab).getValueLabel(); //$NON-NLS-1$ //$NON-NLS-2$

        genElYOfs = yPosStartLabel + 5;

        // blinkenlights
        cp5.addTextlabel(
                "genBlinken", messages.getString("GeneratorGui.BLINKENLIGHT_LOAD"), genFxXOfs + 3, genElYOfs + 16).moveTo(generatorTab).getValueLabel(); //$NON-NLS-1$ //$NON-NLS-2$
        blinkenLightsList = cp5.addDropdownList(GuiElement.BLINKENLIGHTS_DROPDOWN.guiText(),
                genFxXOfs, genElYOfs + 11, Theme.DROPBOXLIST_LENGTH, 140);
        Theme.themeDropdownList(blinkenLightsList);
        FileUtils fu = pixConServer.getFileUtils();
        blinkenLightsList.addItems(fu.findBlinkenFiles());
        if (fu.findBlinkenFiles().length > 0) {
            blinkenLightsList.setLabel(blinkenLightsList.getItem(1).getName());
        }
        blinkenLightsList.setGroup(generatorTab);
        blinkenLightsList.setHeight(Theme.DROPBOXLIST_HEIGHT);

        // images
        cp5.addTextlabel(
                "genImg", messages.getString("GeneratorGui.IMAGE_LOAD"), genFxXOfs + 3 + 1 * Theme.DROPBOX_XOFS, genElYOfs + 16).moveTo(generatorTab).getValueLabel(); //$NON-NLS-1$ //$NON-NLS-2$
        imageList = cp5.addDropdownList(GuiElement.IMAGE_DROPDOWN.guiText(), genFxXOfs
                + Theme.DROPBOX_XOFS, genElYOfs + 11, Theme.DROPBOXLIST_LENGTH, 140);
        Theme.themeDropdownList(imageList);
        imageList.addItems(fu.findImagesFiles());
        if (fu.findImagesFiles().length > 0) {
            imageList.setLabel(imageList.getItem(1).getName());
        }
        imageList.setGroup(generatorTab);
        imageList.setHeight(Theme.DROPBOXLIST_HEIGHT);

        // colorscroll options
        cp5.addTextlabel(
                "genColorScroll", messages.getString("GeneratorGui.COLORSCROLL_OPTIONS"), genFxXOfs + 3 + 2 * Theme.DROPBOX_XOFS, genElYOfs + 16).moveTo(generatorTab).getValueLabel(); //$NON-NLS-1$ //$NON-NLS-2$        
        colorScrollList = cp5.addDropdownList(GuiElement.COLORSCROLL_OPTIONS.guiText(), genFxXOfs
                + 2 * Theme.DROPBOX_XOFS, genElYOfs + 11, Theme.DROPBOXLIST_LENGTH, 140);
        Theme.themeDropdownList(colorScrollList);

        for (ScrollMode sm : ScrollMode.values()) {
            colorScrollList.addItem(sm.getDisplayName(), sm.getMode()); //$NON-NLS-1$ //$NON-NLS-2$
        }
        colorScrollList.setGroup(generatorTab);
        colorScrollList.setHeight(Theme.DROPBOXLIST_HEIGHT);

        // add textfield options
        cp5.addTextlabel(
                "genTextwriterOpt", messages.getString("GeneratorGui.TEXTWRITER_OPTION"), genFxXOfs + 3 + 3 * Theme.DROPBOX_XOFS, genElYOfs + 16).moveTo(generatorTab).getValueLabel(); //$NON-NLS-1$ //$NON-NLS-2$        
        textwriterOption = cp5.addDropdownList(GuiElement.TEXTWR_OPTION.guiText(), genFxXOfs + 3
                * Theme.DROPBOX_XOFS, genElYOfs + 11, Theme.DROPBOXLIST_LENGTH, 140);
        Theme.themeDropdownList(textwriterOption);
        textwriterOption.addItem(messages.getString("GeneratorGui.TEXTWRITER_PINGPONG"), 0); //$NON-NLS-1$
        textwriterOption.addItem(messages.getString("GeneratorGui.TEXTWRITER_LEFT"), 1); //$NON-NLS-1$
        textwriterOption.setLabel(textwriterOption.getItem(0).getName());
        textwriterOption.setGroup(generatorTab);
        textwriterOption.setHeight(Theme.DROPBOXLIST_HEIGHT);

        // add textfield
        textGenerator = cp5.addTextfield(GuiElement.TEXTFIELD, GuiElement.TEXTFIELD.guiText(),
                GuiElement.TEXTFIELD.guiText(), genFxXOfs + 3 + 4 * Theme.DROPBOX_XOFS,
                genElYOfs - 6, Theme.DROPBOXLIST_LENGTH, 16); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

        passThroughMode = cp5
                .addTextlabel("passThroughMode", "", genFxXOfs, yPosStartDrowdown + 50)
                .moveTo(generatorTab).getValueLabel();
        passThroughMode.setColor(0xffff0000);

        // -----------------
        // Single Output tab
        // -----------------

        // brightness control
        cp5.addTextlabel("brightnessControllTxt", messages.getString("GeneratorGui.BRIGHTNESS"), 5,
                yPosStartDrowdown + 95).moveTo(outputTab);

        brightnessControll = cp5.addSlider(GuiElement.BRIGHTNESS.guiText(), 0, 255, 255, 38,
                yPosStartDrowdown + 110, 160, 14);
        brightnessControll.setSliderMode(Slider.FIX);
        brightnessControll.setGroup(outputTab);
        brightnessControll.setDecimalPrecision(0);
        brightnessControll.setNumberOfTickMarks(11);
        brightnessControll.setRange(0, 100);
        brightnessControll.setLabelVisible(false);

        int nrOfOutputs = pixConServer.getConfig().getNrOfScreens();
        selectedOutputs = cp5.addRadioButton(GuiElement.CURRENT_OUTPUT.guiText(), GENERIC_X_OFS,
                yPosStartDrowdown);
        selectedOutputs.setItemsPerRow(nrOfOutputs);
        selectedOutputs.setNoneSelectedAllowed(false);
        for (i = 0; i < nrOfOutputs; i++) {
            String s = messages.getString("GeneratorGui.OUTPUT_NR") + (1 + i); //$NON-NLS-1$
            Toggle t = cp5.addToggle(s, 0, 0, singleVisualXSize, 13);
            t.setCaptionLabel(s);
            selectedOutputs.addItem(t, i);
            cp5.getTooltip()
                    .register(
                            s,
                            messages.getString("GeneratorGui.TOOLTIP_OUTPUT_PREFIX") + (1 + i) + messages.getString("GeneratorGui.TOOLTIP_OUTPUT_POSTFIX")); //$NON-NLS-1$ //$NON-NLS-2$
        }
        selectedOutputs.moveTo(outputTab);

        // visual
        cp5.addTextlabel(
                "singleOutputVisual", messages.getString("GeneratorGui.OUTPUT_VISUAL"), 38, yPosStartDrowdown + 60).moveTo(outputTab).getValueLabel(); //$NON-NLS-1$ //$NON-NLS-2$
        dropdownOutputVisual = GeneratorGuiHelper.createVisualDropdown(cp5,
                GuiElement.OUTPUT_SELECTED_VISUAL_DROPDOWN.guiText(), yPosStartDrowdown + 10,
                nrOfVisuals);
        dropdownOutputVisual.moveTo(outputTab);

        // Fader
        cp5.addTextlabel(
                "singleOutputTransition", messages.getString("GeneratorGui.OUTPUT_TRANSITION"), 38 + Theme.DROPBOX_XOFS * 2, yPosStartDrowdown + 60).moveTo(outputTab).getValueLabel(); //$NON-NLS-1$ //$NON-NLS-2$
        dropdownOutputFader = GeneratorGuiHelper.createFaderDropdown(cp5,
                GuiElement.OUTPUT_FADER_DROPDOWN.guiText(), yPosStartDrowdown + 10);
        dropdownOutputFader.moveTo(outputTab);

        // --------------
        // All Output tab
        // --------------

        if (allOutputTab != null) {
            cp5.addTextlabel(
                    "allOutputTabLabel", messages.getString("GeneratorGui.TEXT_CHANGE_ALL_OUTPUT_MAPPINGS"), 20, yPosStartDrowdown) //$NON-NLS-1$ //$NON-NLS-2$
                    .moveTo(allOutputTab).getValueLabel();

            cp5.addTextlabel(
                    "allOutputVisual", messages.getString("GeneratorGui.ALL_OUTPUT_VISUAL"), 38, yPosStartDrowdown + 68).moveTo(allOutputTab).getValueLabel(); //$NON-NLS-1$ //$NON-NLS-2$
            allOutputTabVis = GeneratorGuiHelper.createVisualDropdown(cp5,
                    GuiElement.OUTPUT_ALL_SELECTED_VISUAL_DROPDOWN.guiText(),
                    yPosStartDrowdown + 20, nrOfVisuals);
            allOutputTabVis.moveTo(allOutputTab);

            // Fader
            cp5.addTextlabel(
                    "allOutputTransition", messages.getString("GeneratorGui.ALL_OUTPUT_TRANSITION"), 38 + Theme.DROPBOX_XOFS * 2, yPosStartDrowdown + 68).moveTo(allOutputTab).getValueLabel(); //$NON-NLS-1$ //$NON-NLS-2$
            allOutputTabFader = GeneratorGuiHelper.createFaderDropdown(cp5,
                    GuiElement.OUTPUT_ALL_FADER_DROPDOWN.guiText(), yPosStartDrowdown + 20);
            allOutputTabFader.moveTo(allOutputTab);
        }

        // palette dropdown list
        cp5.addTextlabel(
                "colSet", messages.getString("GeneratorGui.SELECT_COLORSET"), GENERIC_X_OFS + 5 * Theme.DROPBOX_XOFS, p5GuiYOffset + 3).moveTo(ALWAYS_VISIBLE_TAB).getValueLabel(); //$NON-NLS-1$ //$NON-NLS-2$

        colorSetList = cp5.addDropdownList(GuiElement.COLOR_SET_DROPDOWN.guiText(), GENERIC_X_OFS
                + 5 * Theme.DROPBOX_XOFS, p5GuiYOffset, Theme.DROPBOXLIST_LENGTH, 140);
        Theme.themeDropdownList(colorSetList);
        i = 0;
        for (IColorSet cs : pixConServer.getColorSets()) {
            colorSetList.addItem(cs.getName(), i);
            i++;
        }
        colorSetList.setLabel(colorSetList.getItem(1).getName());
        colorSetList.setHeight(Theme.DROPBOXLIST_LARGE_HEIGHT);
        colorSetList.moveTo(ALWAYS_VISIBLE_TAB);
        cp5.getTooltip().register("colSet", messages.getString("GeneratorGui.TOOLTIP_COLORSET")); //$NON-NLS-1$ //$NON-NLS-2$

        // ----------
        // RANDOM Tab
        // ----------

        Textlabel tRnd = cp5.addTextlabel("rndDesc", //$NON-NLS-1$
                messages.getString("GeneratorGui.TEXT_RANDOM_MODE_SELECT_ELEMENTS"), //$NON-NLS-1$
                20, yPosStartDrowdown);
        tRnd.moveTo(randomTab).getValueLabel();

        randomCheckbox = cp5.addCheckBox(GuiElement.RANDOM_ELEMENT.guiText())
                .setPosition(35, 20 + yPosStartDrowdown).setSize(40, 20)
                .setColorForeground(color(120)).setColorActive(color(255))
                .setColorLabel(color(255)).setItemsPerRow(5).setSpacingColumn(90);

        for (ShufflerOffset so : ShufflerOffset.values()) {
            randomCheckbox.addItem(so.guiText(), i);
        }
        randomCheckbox.activateAll();
        randomCheckbox.moveTo(randomTab);

        // Button
        randomSelection = cp5.addButton(GuiElement.BUTTON_RANDOM_CONFIGURATION.guiText(), 0,
                GENERIC_X_OFS + 5 * Theme.DROPBOX_XOFS, p5GuiYOffset + 30, 110, 15);
        randomSelection.setCaptionLabel(messages.getString("GeneratorGui.RANDOMIZE")); //$NON-NLS-1$
        randomSelection.moveTo(randomTab);
        cp5.getTooltip().register(GuiElement.BUTTON_RANDOM_CONFIGURATION.guiText(),
                messages.getString("GeneratorGui.TOOLTIP_RANDOMIZE")); //$NON-NLS-1$

        randomPresets = cp5.addButton(GuiElement.BUTTON_RANDOM_PRESET.guiText(), 0, GENERIC_X_OFS
                + 5 * Theme.DROPBOX_XOFS, p5GuiYOffset + 55, 110, 15);
        randomPresets.setCaptionLabel(messages.getString("GeneratorGui.RANDOM_PRESET")); //$NON-NLS-1$
        randomPresets.moveTo(randomTab);
        cp5.getTooltip().register(GuiElement.BUTTON_RANDOM_PRESET.guiText(),
                messages.getString("GeneratorGui.TOOLTIP_RANDOM_PRESET")); //$NON-NLS-1$

        randomButtons = cp5.addRadioButton(GuiElement.BUTTONS_RANDOM_MODE.guiText())
                .setPosition(GENERIC_X_OFS + 5 * Theme.DROPBOX_XOFS, p5GuiYOffset + 85)
                .setSize(45, 15).setColorForeground(color(120)).setColorActive(color(255))
                .setColorLabel(color(255)).setItemsPerRow(1).setSpacingColumn(26)
                .setNoneSelectedAllowed(true).moveTo(randomTab);
        randomButtons.addItem(messages.getString("GeneratorGui.RANDOM_MODE"), 0);
        randomButtons.addItem(messages.getString("GeneratorGui.RANDOM_MODE_PRESET"), 1);

        // ----------
        // PRESET Tab
        // ----------

        presetButtons = cp5.addRadioButton(GuiElement.PRESET_BUTTONS.guiText())
                .setPosition(10, yPosStartDrowdown).setSize(24, 14).setColorForeground(color(120))
                .setColorActive(color(255)).setColorLabel(color(255)).setItemsPerRow(16)
                .setSpacingColumn(26).setNoneSelectedAllowed(false);

        for (i = 0; i < 96 + 48; i++) {
            String label = "" + i; //$NON-NLS-1$
            if (i < 10) {
                label = "0" + i; //$NON-NLS-1$
            }
            presetButtons.addItem(label, i);
        }

        int defaultPreset = pixConServer.getConfig().loadPresetOnStart();
        if (defaultPreset < 0 || defaultPreset > PresetService.NR_OF_PRESET_SLOTS) {
            defaultPreset = 0;
        }
        presetButtons.activate(defaultPreset);
        presetButtons.moveTo(presetTab);

        loadPreset = cp5.addButton(GuiElement.LOAD_PRESET.guiText(), 0, GENERIC_X_OFS + 2
                * Theme.DROPBOX_XOFS, yPosStartDrowdown + 170, 100, 15);
        loadPreset.setCaptionLabel(GuiElement.LOAD_PRESET.guiText());
        loadPreset.moveTo(presetTab);
        cp5.getTooltip().register(GuiElement.LOAD_PRESET.guiText(),
                messages.getString("GeneratorGui.TOOLTIP_LOAD_PRESET")); //$NON-NLS-1$

        savePreset = cp5.addButton(GuiElement.SAVE_PRESET.guiText(), 0, GENERIC_X_OFS + 3
                * Theme.DROPBOX_XOFS, yPosStartDrowdown + 170, 100, 15);
        savePreset.setCaptionLabel(GuiElement.SAVE_PRESET.guiText());
        savePreset.moveTo(presetTab);
        cp5.getTooltip().register(GuiElement.SAVE_PRESET.guiText(),
                messages.getString("GeneratorGui.TOOLTIP_SAVE_PRESET")); //$NON-NLS-1$

        presetName = cp5
                .addTextfield(
                        "presetName", 20, yPosStartDrowdown + 170, Theme.DROPBOXLIST_LENGTH * 2, 16).moveTo(presetTab); //$NON-NLS-1$
        presetInfo = cp5
                .addTextlabel("presetInfo", "", 160, yPosStartDrowdown + 170 + 18).moveTo(presetTab).getValueLabel(); //$NON-NLS-1$ //$NON-NLS-2$

        updateCurrentPresetState();

        // -------------
        // Info tab
        // -------------

        int yposAdd = 18;
        int xposAdd = 200;

        // center it, we have 3 row which are 160 pixels wide
        int xOfs = (this.getWidth() - 3 * xposAdd) / 2;
        int nfoYPos = yPosStartDrowdown + 20;
        int nfoXPos = xOfs;

        coreFps = (int) (pixConServer.getConfig().parseFps() + 0.5);
        configuredFps = cp5
                .addTextlabel(
                        "nfoFpsConf", messages.getString("GeneratorGui.CONF_FPS") + coreFps, nfoXPos, nfoYPos).moveTo(infoTab).getValueLabel(); //$NON-NLS-1$ //$NON-NLS-2$
        nfoYPos += yposAdd;
        currentFps = cp5
                .addTextlabel("nfoFpsCurrent", "", nfoXPos, nfoYPos).moveTo(infoTab).getValueLabel(); //$NON-NLS-1$ //$NON-NLS-2$
        nfoYPos += yposAdd;
        runtime = cp5
                .addTextlabel("nfoRuntime", "", nfoXPos, nfoYPos).moveTo(infoTab).getValueLabel(); //$NON-NLS-1$ //$NON-NLS-2$
        nfoYPos += yposAdd;
        cp5.addTextlabel(
                "nfoSrvVersion", messages.getString("GeneratorGui.SERVER_VERSION") + pixConServer.getVersion(), nfoXPos, nfoYPos).moveTo(infoTab).getValueLabel(); //$NON-NLS-1$ //$NON-NLS-2$
        nfoYPos += yposAdd;
        float volNorm = pixConServer.getSoundImplementation().getVolumeNormalized();
        currentVolume = cp5
                .addTextlabel(
                        "nfoVolumeCurrent", messages.getString("GeneratorGui.CURRENT_VOLUME") + volNorm, nfoXPos, nfoYPos).moveTo(infoTab).getValueLabel(); //$NON-NLS-1$ //$NON-NLS-2$
        nfoYPos += yposAdd;
        cp5.addTextlabel(
                "nfoWindowHeight", messages.getString("GeneratorGui.INFO_WINDOW_HEIGHT") + this.getHeight(), nfoXPos, nfoYPos).moveTo(infoTab).getValueLabel(); //$NON-NLS-1$ //$NON-NLS-2$
        nfoYPos += yposAdd;
        MatrixData md = pixConServer.getMatrixData();
        cp5.addTextlabel(
                "nfoInternalBuffer",
                messages.getString("GeneratorGui.INFO_INTERNAL_BUFFERSIZE") + md.getBufferXSize()
                        + "/" + md.getBufferYSize(), nfoXPos, nfoYPos).moveTo(infoTab).getValueLabel(); //$NON-NLS-1$ //$NON-NLS-2$
        nfoYPos += yposAdd;

        Button saveScreenshot = cp5.addButton(GuiElement.SAVE_SCREENSHOT.guiText(), 0, nfoXPos,
                nfoYPos, 110, 15);
        saveScreenshot.setCaptionLabel(messages.getString("GeneratorGui.SAVE_SCREENSHOT")); //$NON-NLS-1$
        saveScreenshot.moveTo(infoTab);
        cp5.getTooltip().register(GuiElement.SAVE_SCREENSHOT.guiText(),
                messages.getString("GeneratorGui.TOOLTIP_SAVE_SCREENSHOT")); //$NON-NLS-1$

        nfoXPos += xposAdd;
        nfoYPos = yPosStartDrowdown + 20;
        IOutput output = pixConServer.getOutput();
        if (output != null) {
            String gammaText = WordUtils.capitalizeFully(StringUtils.replace(output.getGammaType()
                    .toString(), "_", " "));
            cp5.addTextlabel(
                    "nfoGamma", messages.getString("GeneratorGui.GAMMA_CORRECTION") + gammaText, nfoXPos, nfoYPos).moveTo(infoTab).getValueLabel(); //$NON-NLS-1$ //$NON-NLS-2$        	
            nfoYPos += yposAdd;
            cp5.addTextlabel(
                    "nfoBps", messages.getString("GeneratorGui.OUTPUT_BPP") + output.getBpp(), nfoXPos, nfoYPos).moveTo(infoTab).getValueLabel(); //$NON-NLS-1$ //$NON-NLS-2$
            nfoYPos += yposAdd;
        }
        sentFrames = cp5
                .addTextlabel("nfoSentFrames", "", nfoXPos, nfoYPos).moveTo(infoTab).getValueLabel(); //$NON-NLS-1$ //$NON-NLS-2$
        nfoYPos += yposAdd;
        outputErrorCounter = cp5
                .addTextlabel("nfoErrorFrames", "", nfoXPos, nfoYPos).moveTo(infoTab).getValueLabel(); //$NON-NLS-1$ //$NON-NLS-2$
        nfoYPos += yposAdd;
        outputState = cp5
                .addTextlabel("nfoOutputState", "", nfoXPos, nfoYPos).moveTo(infoTab).getValueLabel(); //$NON-NLS-1$ //$NON-NLS-2$
        nfoYPos += yposAdd;

        nfoXPos += xposAdd;
        nfoYPos = yPosStartDrowdown + 20;

        String oscPort = pixConServer.getConfig().getProperty(
                ConfigConstant.NET_OSC_LISTENING_PORT, ""); //$NON-NLS-1$ //$NON-NLS-2$
        cp5.addTextlabel(
                "nfoOscPort", messages.getString("GeneratorGui.OSC_PORT") + oscPort, nfoXPos, nfoYPos).moveTo(infoTab).getValueLabel(); //$NON-NLS-1$ //$NON-NLS-2$
        nfoYPos += yposAdd;
        oscStatistic = cp5
                .addTextlabel("nfoOscStatistic", messages.getString("GeneratorGui.OSC_STATISTIC"),
                        nfoXPos, nfoYPos).moveTo(infoTab).getValueLabel();
        nfoYPos += yposAdd;

        // -------------
        // Help tab
        // -------------

        int hlpYOfs = yPosStartDrowdown;
        int hlpXOfs1 = 20;
        int hlpXOfs2 = 240;
        int hlpYposAdd = 15;

        cp5.addTextlabel(
                "hlpHeader1", messages.getString("GeneratorGui.HLP_HEADER1"), hlpXOfs1, hlpYOfs).moveTo(helpTab).getValueLabel(); //$NON-NLS-1$ //$NON-NLS-2$
        hlpYOfs += hlpYposAdd / 2;
        cp5.addTextlabel(
                "hlpHeader2", messages.getString("GeneratorGui.HLP_HEADER2"), hlpXOfs1, hlpYOfs).moveTo(helpTab).getValueLabel(); //$NON-NLS-1$ //$NON-NLS-2$
        hlpYOfs += hlpYposAdd;
        cp5.addTextlabel(
                "hlpHeader3", messages.getString("GeneratorGui.HLP_HEADER3"), hlpXOfs1, hlpYOfs).moveTo(helpTab).getValueLabel(); //$NON-NLS-1$ //$NON-NLS-2$
        hlpYOfs += hlpYposAdd;
        cp5.addTextlabel(
                "hlpHeader4", messages.getString("GeneratorGui.HLP_HEADER4"), hlpXOfs1, hlpYOfs).moveTo(helpTab).getValueLabel(); //$NON-NLS-1$ //$NON-NLS-2$
        hlpYOfs += hlpYposAdd;
        hlpYOfs += hlpYposAdd / 2;
        cp5.addTextlabel(
                "hlpKeyHeader", messages.getString("GeneratorGui.HLP_KEYBINDING_HEADER"), hlpXOfs1, hlpYOfs).moveTo(helpTab).getValueLabel(); //$NON-NLS-1$ //$NON-NLS-2$

        hlpXOfs1 *= 2;
        hlpYOfs += hlpYposAdd;
        cp5.addTextlabel(
                "HLP_KEY_19", messages.getString("GeneratorGui.HLP_KEY_19"), hlpXOfs1, hlpYOfs).moveTo(helpTab).getValueLabel(); //$NON-NLS-1$ //$NON-NLS-2$

        hlpYOfs += hlpYposAdd;
        cp5.addTextlabel(
                "HLP_KEY_F", messages.getString("GeneratorGui.HLP_KEY_F"), hlpXOfs1, hlpYOfs).moveTo(helpTab).getValueLabel(); //$NON-NLS-1$ //$NON-NLS-2$
        cp5.addTextlabel(
                "HLP_KEY_G", messages.getString("GeneratorGui.HLP_KEY_G"), hlpXOfs2, hlpYOfs).moveTo(helpTab).getValueLabel(); //$NON-NLS-1$ //$NON-NLS-2$

        hlpYOfs += hlpYposAdd;
        cp5.addTextlabel(
                "HLP_KEY_W", messages.getString("GeneratorGui.HLP_KEY_W"), hlpXOfs1, hlpYOfs).moveTo(helpTab).getValueLabel(); //$NON-NLS-1$ //$NON-NLS-2$
        cp5.addTextlabel(
                "HLP_KEY_E", messages.getString("GeneratorGui.HLP_KEY_E"), hlpXOfs2, hlpYOfs).moveTo(helpTab).getValueLabel(); //$NON-NLS-1$ //$NON-NLS-2$

        hlpYOfs += hlpYposAdd;
        cp5.addTextlabel(
                "HLP_KEY_M", messages.getString("GeneratorGui.HLP_KEY_M"), hlpXOfs1, hlpYOfs).moveTo(helpTab).getValueLabel(); //$NON-NLS-1$ //$NON-NLS-2$

        hlpYOfs += hlpYposAdd;
        cp5.addTextlabel(
                "HLP_KEY_C", messages.getString("GeneratorGui.HLP_KEY_C"), hlpXOfs1, hlpYOfs).moveTo(helpTab).getValueLabel(); //$NON-NLS-1$ //$NON-NLS-2$

        hlpYOfs += hlpYposAdd;
        cp5.addTextlabel(
                "HLP_KEY_R", messages.getString("GeneratorGui.HLP_KEY_R"), hlpXOfs1, hlpYOfs).moveTo(helpTab).getValueLabel(); //$NON-NLS-1$ //$NON-NLS-2$

        hlpYOfs += hlpYposAdd;
        cp5.addTextlabel(
                "HLP_KEY_LEFT", messages.getString("GeneratorGui.HLP_KEY_LEFT"), hlpXOfs1, hlpYOfs).moveTo(helpTab).getValueLabel(); //$NON-NLS-1$ //$NON-NLS-2$
        cp5.addTextlabel(
                "HLP_KEY_RIGHT", messages.getString("GeneratorGui.HLP_KEY_RIGHT"), hlpXOfs2, hlpYOfs).moveTo(helpTab).getValueLabel(); //$NON-NLS-1$ //$NON-NLS-2$
        // ----------
        // LOGO
        // ----------

        try {
            logo = loadImage(new FileUtilsLocalImpl().getDataDir() + File.separator + "gui"
                    + File.separatorChar + "guilogo.jpg");
            LOG.log(Level.INFO, "GUI logo loaded");
        } catch (Exception e) {
            LOG.log(Level.INFO, "Failed to load gui logo!", e);
        }

        // ----------
        // MISC
        // ----------

        int xSizeForEachWidget = (windowWidth - 2 * GENERIC_X_OFS) / NR_OF_WIDGETS;

        cp5.addTextlabel(
                "frameDesc", messages.getString("GeneratorGui.FRAME_PROGRESS"), GENERIC_X_OFS, GENERIC_Y_OFS).moveTo(ALWAYS_VISIBLE_TAB).getValueLabel(); //$NON-NLS-1$ //$NON-NLS-2$
        cp5.addTextlabel(
                "sndDesc", messages.getString("GeneratorGui.SOUND_DESC"), GENERIC_X_OFS + xSizeForEachWidget, GENERIC_Y_OFS).moveTo(ALWAYS_VISIBLE_TAB).getValueLabel(); //$NON-NLS-1$ //$NON-NLS-2$
        cp5.addTextlabel(
                "sndVol", messages.getString("GeneratorGui.INPUT_VOLUME"), GENERIC_X_OFS + xSizeForEachWidget * 2, GENERIC_Y_OFS).moveTo(ALWAYS_VISIBLE_TAB).getValueLabel(); //$NON-NLS-1$ //$NON-NLS-2$
        cp5.addTextlabel(
                "outputDevice", messages.getString("GeneratorGui.OUTPUT_DEVICE"), GENERIC_X_OFS + xSizeForEachWidget * 3, GENERIC_Y_OFS).moveTo(ALWAYS_VISIBLE_TAB).getValueLabel(); //$NON-NLS-1$ //$NON-NLS-2$
        cp5.addTextlabel(
                "outputDeviceName", getOutputDeviceName(), 15 + GENERIC_X_OFS + xSizeForEachWidget * 3, 2 + GENERIC_Y_OFS + 10).moveTo(ALWAYS_VISIBLE_TAB).getValueLabel(); //$NON-NLS-1$

        // register event listener
        cp5.addListener(listener);

        // select first visual
        selectedVisualList.activate(0);
        selectedOutputs.activate(0);

        // register callback function
        GuiUpdateFeedback guf = new GuiUpdateFeedback(this);
        pixConServer.observeVisualState(guf);
        pixConServer.refreshGuiState();

        initialized = true;
    }

    public void RANDOM_ELEMENT(int val) {
        // unused
    }

    /**
     * this callback method is needed by the library but unused
     * 
     * @param val
     */
    public void CURRENT_OUTPUT(int val) {
        // unused
    }

    /**
     * this callback method is needed by the library but unused
     * 
     * @param val
     */
    public void PRESET_BUTTONS(int val) {
        LOG.log(Level.INFO, "choose new preset " + val); //$NON-NLS-1$
        updateCurrentPresetState();
    }

    /**
     * this callback method is needed by the library but unused
     * 
     * @param val
     */
    public void CURRENT_VISUAL(int val) {
        // unused
    }

    /**
     * 
     * @param col
     * @return
     */
    private int getVisualCenter() {
        if (singleVisualXSize < MINIMAL_VISUAL_WIDTH) {
            return (windowWidth - (this.nrOfVisuals * MINIMAL_VISUAL_WIDTH)) / 2;
        }
        return (windowWidth - (this.nrOfVisuals * singleVisualXSize)) / 2;
    }

    /**
     * draw the whole internal buffer on screen. this method is quite cpu
     * intensive
     */
    public void draw() {
        // background(0);
        long l = System.currentTimeMillis();
        int localX = getVisualCenter();
        int localY = 40;

        frames++;

        // clear screen each 2nd frame and put logo on it
        if (frames % 2 == 1) {
            background(0);
            if (logo != null) {
                image(logo, width - logo.width, height - logo.height);
            }
        }

        // draw internal buffer only if enabled
        if (listener.isInternalVisualVisible()) {
            // lazy init
            if (pImage == null) {
                // create an image out of the buffer

                pImage = this.createImage(singleVisualXSize, singleVisualYSize, PApplet.RGB);
            }

            // set used to find out if visual is on screen
            Set<Integer> outputId = new HashSet<Integer>();
            for (OutputMapping om : pixConServer.getAllOutputMappings()) {
                outputId.add(om.getVisualId());
            }

            MatrixData matrixData = pixConServer.getMatrixData();

            // draw output buffer and marker
            for (int ofs = 0; ofs < pixConServer.getNrOfVisuals(); ofs++) {
                // use always the pixel resize option to reduce cpu load

                buffer = resize.resizeImage(pixConServer.getVisualBuffer(ofs),
                        matrixData.getBufferXSize(), matrixData.getBufferYSize(),
                        singleVisualXSize, singleVisualYSize);

                pImage.loadPixels();
                System.arraycopy(buffer, 0, pImage.pixels, 0, singleVisualXSize * singleVisualYSize);
                pImage.updatePixels();

                // display the image
                image(pImage, localX, localY);

                // highlight current output
                if (outputId.contains(ofs)) {
                    fill(20, 235, 20);
                } else {
                    fill(235, 20, 20);
                }
                rect(localX + 5, localY + 5, 10, 10);

                // make sure the visuals are not too tight
                if (pImage.width < MINIMAL_VISUAL_WIDTH) {
                    localX += MINIMAL_VISUAL_WIDTH;
                } else {
                    localX += pImage.width;
                }
            }
        }

        // beat detection
        displayWidgets(GENERIC_Y_OFS);

        // update more details, mostly info tab
        if (frames % 10 == 1) {
            // INFO TAB
            int currentFps10 = (int) (coreFps * generatorSpeedSlider.getValue());
            configuredFps
                    .setText(messages.getString("GeneratorGui.CONF_FPS") + currentFps10 / 100f);
            int fps10 = (int) (pixConServer.getCurrentFps() * 10);
            currentFps.setText(messages.getString("GeneratorGui.CURRENT_FPS") + fps10 / 10f); //$NON-NLS-1$
            String runningSince = DurationFormatUtils.formatDuration(System.currentTimeMillis()
                    - pixConServer.getServerStartTime(), "H:mm:ss"); //$NON-NLS-1$
            runtime.setText(messages.getString("GeneratorGui.RUNNING_SINCE") + runningSince); //$NON-NLS-1$
            sentFrames.setText(messages.getString("GeneratorGui.SENT_FRAMES") + frames); //$NON-NLS-1$
            int snd1000 = (int) (1000f * pixConServer.getSoundImplementation()
                    .getVolumeNormalized());
            currentVolume.setText(messages.getString("GeneratorGui.CURRENT_VOLUME")
                    + (snd1000 / 1000f));

            IOutput output = pixConServer.getOutput();
            if (output != null) {
                String outputStateStr = WordUtils.capitalizeFully(output.getConnectionStatus());
                outputState.setText(outputStateStr);
                outputErrorCounter
                        .setText(messages.getString("GeneratorGui.IO_ERRORS") + output.getErrorCounter()); //$NON-NLS-1$            	
            }
            long recievedMB = pixConServer.getRecievedOscBytes() / 1024 / 1024;
            String oscStat = messages.getString("GeneratorGui.OSC_STATISTIC")
                    + pixConServer.getRecievedOscPackets() + "/" + recievedMB;
            oscStatistic.setText(oscStat);

            /*
             * if (pixConServer.isPassThroughModeEnabled()) {
             * passThroughMode.setText
             * (messages.getString("GeneratorGui.PASSTHROUGH_MODE")); } else {
             * passThroughMode.setText(""); }
             */
        }

        // update gui
        cp5.draw();

        // track used time
        pixConServer.updateNeededTimeForInternalWindow(System.currentTimeMillis() - l);
    }

    /**
     * update preset stuff
     */
    public void updateCurrentPresetState() {
        PresetSettings preset = pixConServer.getCurrentPresetSettings();
        if (preset != null) {
            String presetState;
            if (preset.isSlotUsed()) {
                presetState = messages.getString("GeneratorGui.STR_TRUE"); //$NON-NLS-1$
            } else {
                presetState = messages.getString("GeneratorGui.STR_FALSE"); //$NON-NLS-1$
            }

            presetInfo.setText(messages.getString("GeneratorGui.VALID_ENTRY_EMPTY") + presetState); //$NON-NLS-1$
            presetName.setText(preset.getName());
        } else {
            presetInfo.setText(messages.getString("GeneratorGui.VALID_ENTRY_FALSE")); //$NON-NLS-1$
            presetName.setText(""); //$NON-NLS-1$
        }

    }

    /**
     * 
     * @param localY
     */
    private void displayWidgets(int localY) {
        int xSizeForEachWidget = (windowWidth - 2 * GENERIC_X_OFS) / NR_OF_WIDGETS;

        // display frame progress
        long currentFrames = pixConServer.getFrameCount() % (xSizeForEachWidget - WIDGET_BOARDER);
        fill(0, 180, 234);
        rect(GENERIC_X_OFS, localY + SELECTED_MARKER + 4, currentFrames, WIDGET_BAR_SIZE);
        fill(2, 52, 77);
        rect(GENERIC_X_OFS + currentFrames, localY + SELECTED_MARKER + 4, xSizeForEachWidget
                - currentFrames - WIDGET_BOARDER, WIDGET_BAR_SIZE);

        // draw sound stats
        ISound snd = pixConServer.getSoundImplementation();
        int xofs = GENERIC_X_OFS + xSizeForEachWidget;
        int xx = (xSizeForEachWidget - WIDGET_BOARDER * 2) / 3;

        colorSelect(snd.isKick());
        rect(xofs, localY + SELECTED_MARKER + 4, xx, WIDGET_BAR_SIZE);

        xofs += xx + WIDGET_BOARDER / 2;
        colorSelect(snd.isSnare());
        rect(xofs, localY + SELECTED_MARKER + 4, xx, WIDGET_BAR_SIZE);

        xofs += xx + WIDGET_BOARDER / 2;
        colorSelect(snd.isHat());
        rect(xofs, localY + SELECTED_MARKER + 4, xx, WIDGET_BAR_SIZE);

        // Draw input volume
        int vol = (int) ((xSizeForEachWidget - WIDGET_BOARDER) * snd.getVolumeNormalized());
        fill(0, 180, 234);
        rect(GENERIC_X_OFS + 2 * xSizeForEachWidget, localY + SELECTED_MARKER + 4, vol,
                WIDGET_BAR_SIZE);
        fill(2, 52, 77);
        rect(GENERIC_X_OFS + 2 * xSizeForEachWidget + vol, localY + SELECTED_MARKER + 4,
                xSizeForEachWidget - WIDGET_BOARDER - vol, WIDGET_BAR_SIZE);

        // draw output device
        Boolean isConnected = isOutputDeviceConnected();
        if (isConnected != null) {
            // highlight current output
            if (isConnected) {
                fill(20, 235, 20);
            } else {
                fill(235, 20, 20);
            }
            rect(3 + GENERIC_X_OFS + 3 * xSizeForEachWidget, localY + SELECTED_MARKER, 10, 10);
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
     * mouse listener, used to close dropdown lists
     * 
     */
    public void mousePressed() {

        if (!initialized) {
            return;
        }

        // print the current mouseoverlist on mouse pressed
        List<GuiElement> clickedOn = new ArrayList<GuiElement>();
        List<ControllerInterface<?>> lci = cp5.getWindow().getMouseOverList();
        for (ControllerInterface<?> ci : lci) {
            GuiElement ge = GuiElement.getGuiElement(ci.getName());
            if (ge != null) {
                clickedOn.add(ge);
            }
        }

        // close all open tabs
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

        if (allOutputTabVis != null
                && !clickedOn.contains(GuiElement.OUTPUT_ALL_SELECTED_VISUAL_DROPDOWN)) {
            allOutputTabVis.setOpen(false);
        }

        if (allOutputTabFader != null && !clickedOn.contains(GuiElement.OUTPUT_ALL_FADER_DROPDOWN)) {
            allOutputTabFader.setOpen(false);
        }

    }

    /**
     * Keyhandler
     * 
     * select visual by keypress
     */
    public void keyPressed() {
        // ignore escape key
        if (keyCode == ESC) {
            key = 0;
        } else {
            KeyboardHandler.keyboardHandler(key, keyCode);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.neophob.sematrix.core.output.gui.GuiCallbackAction#activeVisual(int)
     */
    public void activeVisual(int n) {
        selectedVisualList.activate(n);
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

    public void selectPreviousTab() {
        Tab currentTab = cp5.getWindow().getCurrentTab();
        Tab lastTab = null;
        for (Tab t : allTabs) {
            if (t == currentTab && lastTab != null) {
                lastTab.bringToFront();
                return;
            }
            lastTab = t;
        }
        // activate the last tab
        allTabs.get(allTabs.size() - 1).bringToFront();
    }

    public void selectNextTab() {
        boolean activateNextTab = false;
        Tab currentTab = cp5.getWindow().getCurrentTab();

        for (Tab t : allTabs) {
            if (activateNextTab) {
                // active next tab and return
                t.bringToFront();
                return;
            }

            if (t == currentTab) {
                activateNextTab = true;
            }
        }

        // active the first tab
        if (activateNextTab) {
            allTabs.get(0).bringToFront();
        }
    }

    private String getOutputDeviceName() {
        IOutput output = pixConServer.getOutput();
        if (output == null) {
            return "";
        }
        return output.getType().toString();
    }

    /**
     * 
     * @return
     */
    private Boolean isOutputDeviceConnected() {
        IOutput output = pixConServer.getOutput();
        if (output == null || !output.isSupportConnectionState()) {
            return null;
        }

        return output.isSupportConnectionState() && output.isConnected();
    }

    @Override
    public void updateGuiElements(Map<String, String> diff) {
        for (Map.Entry<String, String> s : diff.entrySet()) {
            try {
                ValidCommand cmd = ValidCommand.valueOf(s.getKey());
                switch (cmd) {
                    case CHANGE_GENERATOR_A:
                        generatorListOne.setLabel(generatorListOne.getItem(
                                Integer.parseInt(s.getValue())).getName());
                        break;

                    case CHANGE_GENERATOR_B:
                        generatorListTwo.setLabel(generatorListTwo.getItem(
                                Integer.parseInt(s.getValue())).getName());
                        break;

                    case CHANGE_EFFECT_A:
                        effectListOne.setLabel(effectListOne
                                .getItem(Integer.parseInt(s.getValue())).getName());
                        break;

                    case CHANGE_EFFECT_B:
                        effectListTwo.setLabel(effectListTwo
                                .getItem(Integer.parseInt(s.getValue())).getName());
                        break;

                    case CURRENT_VISUAL:
                        // nothing todo
                        break;

                    case CHANGE_MIXER:
                        mixerList.setLabel(mixerList.getItem(Integer.parseInt(s.getValue()))
                                .getName());
                        break;

                    case BLINKEN:
                        blinkenLightsList.setLabel(s.getValue());
                        break;

                    case IMAGE:
                        imageList.setLabel(s.getValue());
                        break;

                    case CURRENT_COLORSET:
                        colorSetList.setLabel(s.getValue());
                        break;

                    case CHANGE_PRESET:
                        presetButtons.activate(Integer.parseInt(s.getValue()));
                        updateCurrentPresetState();
                        break;

                    case CHANGE_THRESHOLD_VALUE:
                        thresholdSlider.changeValue(Float.parseFloat(s.getValue()));
                        break;

                    case CHANGE_ROTOZOOM:
                        fxRotoSlider.changeValue(Float.parseFloat(s.getValue()));
                        break;

                    case COLOR_SCROLL_OPT:
                        colorScrollList.setLabel(colorScrollList.getItem(
                                Integer.parseInt(s.getValue())).getName());
                        break;

                    case ZOOMOPT:
                        zoomOptions.setLabel(zoomOptions.getItem(Integer.parseInt(s.getValue()))
                                .getName());
                        break;

                    case TEXTWR_OPTION:
                        textwriterOption.setLabel(textwriterOption.getItem(
                                Integer.parseInt(s.getValue())).getName());
                        break;

                    case TEXTDEF:
                        int i = Integer.parseInt(s.getValue());
                        textureDeformOptions.setLabel(textureDeformOptions.getItem(i).getName());
                        break;

                    case CHANGE_BRIGHTNESS:
                        brightnessControll.changeValue(Float.parseFloat(s.getValue()));
                        break;

                    case CHANGE_OUTPUT_FADER:
                        dropdownOutputFader.setLabel(dropdownOutputFader.getItem(
                                Integer.parseInt(s.getValue())).getName());
                        break;

                    case CHANGE_OUTPUT_VISUAL:
                        dropdownOutputVisual.setLabel(dropdownOutputVisual.getItem(
                                Integer.parseInt(s.getValue())).getName());
                        break;

                    case TEXTWR:
                        textGenerator.setText(s.getValue());
                        break;

                    case GENERATOR_SPEED:
                        generatorSpeedSlider.changeValue(Float.parseFloat(s.getValue()));
                        break;

                    case BEAT_WORKMODE:
                        beatWorkmode.setLabel(beatWorkmode.getItem(Integer.parseInt(s.getValue()))
                                .getName());
                        break;

                    case FREEZE:
                        if (s.getValue().equals("true")) {
                            freeze.setBroadcast(false);
                            freeze.setState(true);
                            freeze.setBroadcast(true);
                        } else {
                            freeze.setBroadcast(false);
                            freeze.setState(false);
                            freeze.setBroadcast(true);
                        }
                        break;

                    case CURRENT_OUTPUT:
                        int value = Integer.parseInt(s.getValue());
                        selectedOutputs.activate(value);
                        break;

                    case GET_PASSTHROUGH_MODE:
                        boolean b = Boolean.parseBoolean(s.getValue());
                        if (b) {
                            passThroughMode.setText(messages
                                    .getString("GeneratorGui.PASSTHROUGH_MODE"));
                        } else {
                            passThroughMode.setText("");
                        }
                        break;

                    default:
                        LOG.log(Level.WARNING,
                                "Not implemented: " + s.getKey() + " " + s.getValue());
                        break;
                }
            } catch (Exception e) {
                LOG.log(Level.WARNING, "Unknown entry: " + s.getKey(), e);
            }
        }

    }
}
