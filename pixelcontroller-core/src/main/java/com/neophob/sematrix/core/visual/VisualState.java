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
package com.neophob.sematrix.core.visual;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.neophob.sematrix.core.glue.FileUtils;
import com.neophob.sematrix.core.glue.PixelControllerShufflerSelect;
import com.neophob.sematrix.core.glue.ShufflerOffset;
import com.neophob.sematrix.core.jmx.PixelControllerStatusMBean;
import com.neophob.sematrix.core.jmx.TimeMeasureItemGlobal;
import com.neophob.sematrix.core.preset.PresetService;
import com.neophob.sematrix.core.properties.ApplicationConfigurationHelper;
import com.neophob.sematrix.core.properties.ValidCommand;
import com.neophob.sematrix.core.resize.PixelControllerResize;
import com.neophob.sematrix.core.resize.Resize.ResizeName;
import com.neophob.sematrix.core.sound.ISound;
import com.neophob.sematrix.core.visual.color.IColorSet;
import com.neophob.sematrix.core.visual.effect.Effect;
import com.neophob.sematrix.core.visual.effect.Effect.EffectName;
import com.neophob.sematrix.core.visual.effect.PixelControllerEffect;
import com.neophob.sematrix.core.visual.fader.Fader.FaderName;
import com.neophob.sematrix.core.visual.fader.IFader;
import com.neophob.sematrix.core.visual.fader.PixelControllerFader;
import com.neophob.sematrix.core.visual.generator.Generator;
import com.neophob.sematrix.core.visual.generator.Generator.GeneratorName;
import com.neophob.sematrix.core.visual.generator.PixelControllerGenerator;
import com.neophob.sematrix.core.visual.mixer.Mixer;
import com.neophob.sematrix.core.visual.mixer.Mixer.MixerName;
import com.neophob.sematrix.core.visual.mixer.PixelControllerMixer;

/**
 * Visual State of PixelController
 * 
 * implements observable, other objects can register itself to receive update if
 * this state changes - concrete if notifyGuiUpdate() is called.
 * 
 */
public class VisualState extends Observable {

    private static final Logger LOG = Logger.getLogger(VisualState.class.getName());

    /** The Constant EMPTY_CHAR. */
    private static final String EMPTY_CHAR = " ";

    /** The singleton instance. */
    private static VisualState instance = new VisualState();

    /** The random mode. */
    private boolean randomMode = false;

    /** The random mode. */
    private boolean randomPresetMode = false;

    /** The initialized. */
    private boolean initialized;

    /** The matrix. */
    private MatrixData matrix;

    /** all input elements. */
    private List<Visual> allVisuals;

    /** fx to screen mapping. */
    private List<OutputMapping> ioMapping;

    /** The current visual. */
    private int currentVisual;

    /** The current output. */
    private int currentOutput;

    /** The pixel controller generator. */
    private PixelControllerGenerator pixelControllerGenerator;

    /** The pixel controller mixer. */
    private PixelControllerMixer pixelControllerMixer;

    /** The pixel controller effect. */
    private PixelControllerEffect pixelControllerEffect;

    /** The pixel controller resize. */
    private PixelControllerResize pixelControllerResize;

    /** The pixel controller shuffler select. */
    private PixelControllerShufflerSelect pixelControllerShufflerSelect;

    private PixelControllerFader pixelControllerFader;

    /** The is loading present. */
    private boolean isLoadingPresent = false;

    private List<IColorSet> colorSets;

    /** The random mode. */
    private boolean inPauseMode = false;

    private float brightness = 1.0f;

    // the fps multiplier
    private float fpsSpeed = 1.0f;

    private ISound sound;

    private PresetService presetService;

    /**
     * Instantiates a new collector.
     */
    private VisualState() {
        allVisuals = new CopyOnWriteArrayList<Visual>();

        ioMapping = new CopyOnWriteArrayList<OutputMapping>();
        initialized = false;
    }

    /**
     * initialize the collector.
     * 
     * @param papplet
     *            the PApplet
     * @param ph
     *            the PropertiesHelper
     */
    public synchronized void init(FileUtils fileUtils, ApplicationConfigurationHelper ph,
            ISound sound, List<IColorSet> colorSets, PresetService presetService) {

        if (initialized) {
            LOG.log(Level.WARNING, "Reinitialize collector, use for unit tests only");
        } else {
            LOG.log(Level.INFO, "Initialize collector");
        }

        this.colorSets = colorSets;
        this.sound = sound;
        this.presetService = presetService;

        int nrOfScreens = ph.getNrOfScreens();
        float fps = ph.parseFps();

        this.pixelControllerShufflerSelect = new PixelControllerShufflerSelect(sound,
                ph.getRandomModeLifetime());
        this.pixelControllerShufflerSelect.initAll();

        // create the device with specific size
        this.matrix = new MatrixData(ph.getDeviceXResolution(), ph.getDeviceYResolution());

        pixelControllerResize = new PixelControllerResize();
        pixelControllerResize.initAll();

        // create generators
        pixelControllerGenerator = new PixelControllerGenerator(ph, fileUtils, matrix, fps, sound,
                pixelControllerResize.getResize(ResizeName.PIXEL_RESIZE));
        pixelControllerGenerator.initAll();

        pixelControllerEffect = new PixelControllerEffect(matrix, sound,
                pixelControllerResize.getResize(ResizeName.PIXEL_RESIZE));
        pixelControllerEffect.initAll();

        pixelControllerMixer = new PixelControllerMixer(matrix, sound);
        pixelControllerMixer.initAll();

        pixelControllerFader = new PixelControllerFader(ph, matrix);

        // create visuals
        int additionalVisuals = 1 + ph.getNrOfAdditionalVisuals();
        LOG.log(Level.INFO, "Initialize " + (nrOfScreens + additionalVisuals) + " Visuals");
        try {
            Generator genPassThru = pixelControllerGenerator.getGenerator(GeneratorName.PASSTHRU);
            Effect effPassThru = pixelControllerEffect.getEffect(EffectName.PASSTHRU);
            Mixer mixPassThru = pixelControllerMixer.getMixer(MixerName.PASSTHRU);
            for (int i = 1; i < nrOfScreens + additionalVisuals + 1; i++) {
                Generator g = pixelControllerGenerator.getGenerator(GeneratorName.values()[i
                        % (GeneratorName.values().length)]);
                if (g == null) {
                    // its possible we select an inactive generator, in this
                    // case just ignore it...
                    additionalVisuals++;
                    LOG.log(Level.INFO, "Ignore null Visual, take next...");
                } else {
                    allVisuals.add(new Visual(g, genPassThru, effPassThru, effPassThru,
                            mixPassThru, colorSets.get(0)));
                }
            }

        } catch (IndexOutOfBoundsException e) {
            LOG.log(Level.SEVERE, "Failed to initialize Visual, maybe missing palette files?\n");
            throw new IllegalArgumentException(
                    "Failed to initialize Visuals, maybe missing palette files?");
        }

        // create an empty mapping
        ioMapping.clear();
        for (int n = 0; n < nrOfScreens; n++) {
            ioMapping.add(new OutputMapping(pixelControllerFader.getVisualFader(
                    FaderName.SWITCH.getId(), 1), n));
        }

        initialized = true;
    }

    /**
     * update the whole system: generators and effects
     * 
     * update the generators, if the sound is louder, update faster.
     */
    public void updateSystem(PixelControllerStatusMBean pixConStat) {
        // do not update system if presents are loading
        if (isLoadingPresent()) {
            return;
        }

        long l = System.currentTimeMillis();
        // update generator depending on the input sound
        pixelControllerGenerator.update();
        pixConStat.trackTime(TimeMeasureItemGlobal.GENERATOR, System.currentTimeMillis() - l);

        l = System.currentTimeMillis();
        pixelControllerEffect.update();
        pixConStat.trackTime(TimeMeasureItemGlobal.EFFECT, System.currentTimeMillis() - l);

        // cleanup faders
        l = System.currentTimeMillis();
        for (OutputMapping om : ioMapping) {
            IFader fader = om.getFader();
            if (fader != null && fader.isStarted() && fader.isDone()) {
                // fading is finished, cleanup
                fader.cleanUp();

                if (fader.getScreenOutput() >= 0) {
                    setOutputVisual(fader.getScreenOutput(), fader.getNewVisual());
                    LOG.log(Level.INFO,
                            "Cleanup {0}, new visual: {1}, output screen: {2}",
                            new Object[] { fader.getFaderName(), fader.getNewVisual(),
                                    fader.getScreenOutput() });
                } else {
                    LOG.log(Level.INFO, "Cleanup preset {0}, new visual: {1}",
                            new Object[] { fader.getFaderName(), fader.getNewVisual() });
                }
            }
        }
        pixConStat.trackTime(TimeMeasureItemGlobal.FADER, System.currentTimeMillis() - l);

        pixelControllerShufflerSelect.update();
    }

    /**
     * Gets the single instance of Collector.
     * 
     * @return single instance of Collector
     */
    public static VisualState getInstance() {
        return instance;
    }

    /**
     * which fx for screenOutput?.
     * 
     * @param screenOutput
     *            the screen output
     * @return fx nr.
     */
    public int getCurrentVisualForScreen(int screenOutput) {
        return ioMapping.get(screenOutput).getVisualId();
    }

    /**
     * define which visual is shown on which output, without fading.
     * 
     * @param screenOutput
     *            which screen nr
     * @param visualInput
     *            which visual
     */
    public void setOutputVisual(int screenOutput, int visualInput) {
        OutputMapping o = ioMapping.get(screenOutput);
        o.setVisualId(visualInput);
        ioMapping.set(screenOutput, o);
    }

    /**
     * get all screens with a specific visual used for crossfading.
     * 
     * @param oldVisual
     *            the old visual
     * @return the all screens with visual
     */
    public List<Integer> getAllScreensWithVisual(int oldVisual) {
        List<Integer> ret = new ArrayList<Integer>();
        int ofs = 0;
        for (OutputMapping o : ioMapping) {
            if (o.getVisualId() == oldVisual) {
                ret.add(ofs);
            }
            ofs++;
        }
        return ret;
    }

    /**
     * Checks if is random mode.
     * 
     * @return true, if is random mode
     */
    public boolean isRandomMode() {
        return randomMode;
    }

    /**
     * Sets the random mode.
     * 
     * @param randomMode
     *            the new random mode
     */
    public void setRandomMode(boolean randomMode) {
        this.randomMode = randomMode;
    }

    public boolean isRandomPresetMode() {
        return randomPresetMode;
    }

    public void setRandomPresetMode(boolean randomPresetMode) {
        this.randomPresetMode = randomPresetMode;
    }

    /*
     * MATRIX ======================================================
     */

    /**
     * Gets the matrix.
     * 
     * @return the matrix
     */
    public MatrixData getMatrix() {
        return matrix;
    }

    /**
     * Sets the matrix.
     * 
     * @param matrix
     *            the new matrix
     */
    public void setMatrix(MatrixData matrix) {
        this.matrix = matrix;
    }

    /*
     * VISUAL ======================================================
     */

    /**
     * Adds the visual.
     * 
     * @param visual
     *            the visual
     */
    public void addVisual(Visual visual) {
        allVisuals.add(visual);
    }

    /**
     * Gets the all visuals.
     * 
     * @return the all visuals
     */
    public List<Visual> getAllVisuals() {
        return allVisuals;
    }

    /**
     * Gets the visual.
     * 
     * @param index
     *            the index
     * @return the visual
     */
    public Visual getVisual(int index) {
        if (index >= 0 && index < allVisuals.size()) {
            return allVisuals.get(index);
        }
        return allVisuals.get(0);
    }

    /**
     * Sets the all visuals.
     * 
     * @param allVisuals
     *            the new all visuals
     */
    public void setAllVisuals(List<Visual> allVisuals) {
        this.allVisuals = allVisuals;
    }

    /*
     * OUTPUT MAPPING ======================================================
     */

    /**
     * Gets the all output mappings.
     * 
     * @return the all output mappings
     */
    public List<OutputMapping> getAllOutputMappings() {
        return ioMapping;
    }

    /**
     * Gets the output mappings.
     * 
     * @param index
     *            the index
     * @return the output mappings
     */
    public OutputMapping getOutputMappings(int index) {
        return ioMapping.get(index);
    }

    /**
     * Gets the current visual.
     * 
     * @return the current visual
     */
    public int getCurrentVisual() {
        return currentVisual;
    }

    /**
     * Sets the current visual.
     * 
     * @param currentVisual
     *            the new current visual
     */
    public void setCurrentVisual(int currentVisual) {
        if (currentVisual >= 0 && currentVisual < allVisuals.size()) {
            this.currentVisual = currentVisual;
        }
    }

    /**
     * 
     * @return
     */
    public int getCurrentOutput() {
        return currentOutput;
    }

    /**
     * 
     * @param currentOutput
     */
    public void setCurrentOutput(int currentOutput) {
        if (currentOutput >= 0 && currentOutput < ioMapping.size()) {
            this.currentOutput = currentOutput;
        }
    }

    /**
     * Checks if is loading present.
     * 
     * @return true, if is loading present
     */
    public synchronized boolean isLoadingPresent() {
        return isLoadingPresent;
    }

    /**
     * Sets the loading present.
     * 
     * @param isLoadingPresent
     *            the new loading present
     */
    public synchronized void setLoadingPresent(boolean isLoadingPresent) {
        this.isLoadingPresent = isLoadingPresent;
    }

    /**
     * Gets the shuffler select.
     * 
     * @param ofs
     *            the ofs
     * @return the shuffler select
     */
    public boolean getShufflerSelect(ShufflerOffset ofs) {
        return pixelControllerShufflerSelect.getShufflerSelect(ofs);
    }

    /**
     * Gets the pixel controller shuffler select.
     * 
     * @return the pixel controller shuffler select
     */
    public PixelControllerShufflerSelect getPixelControllerShufflerSelect() {
        return pixelControllerShufflerSelect;
    }

    /**
     * Gets the pixel controller mixer.
     * 
     * @return the pixel controller mixer
     */

    public PixelControllerMixer getPixelControllerMixer() {
        return pixelControllerMixer;
    }

    /**
     * Gets the pixel controller effect.
     * 
     * @return the pixel controller effect
     */
    public PixelControllerEffect getPixelControllerEffect() {
        return pixelControllerEffect;
    }

    /**
     * Gets the pixel controller generator.
     * 
     * @return the pixel controller generator
     */
    public PixelControllerGenerator getPixelControllerGenerator() {
        return pixelControllerGenerator;
    }

    /**
     * Gets the pixel controller resize.
     * 
     * @return the pixel controller resize
     */
    public PixelControllerResize getPixelControllerResize() {
        return pixelControllerResize;
    }

    /**
     * 
     * @return
     */
    public PixelControllerFader getPixelControllerFader() {
        return pixelControllerFader;
    }

    /**
     * 
     * @return
     */
    public int getFrames() {
        return pixelControllerGenerator.getFrames();
    }

    /**
     * 
     * @return
     */
    public List<IColorSet> getColorSets() {
        return colorSets;
    }

    /**
     * 
     * @param colorSets
     */
    public void setColorSets(List<IColorSet> colorSets) {
        this.colorSets = colorSets;
    }

    /**
	 * 
	 */
    public void togglePauseMode() {
        if (inPauseMode) {
            inPauseMode = false;
        } else {
            inPauseMode = true;
        }
    }

    /**
     * 
     * @return
     */
    public boolean isInPauseMode() {
        return inPauseMode;
    }

    /**
     * sound implementation
     * 
     * @return
     */
    public ISound getSound() {
        return sound;
    }

    /**
     * @return the brightness
     */
    public float getBrightness() {
        return brightness;
    }

    public float getFpsSpeed() {
        return fpsSpeed;
    }

    public void setFpsSpeed(float fpsSpeed) {
        this.fpsSpeed = fpsSpeed;
    }

    /**
     * @param brightness
     *            the brightness to set
     */
    public void setBrightness(float brightness) {
        this.brightness = brightness;
    }

    public boolean isPassThroughModeEnabledForCurrentVisual() {
        return getVisual(currentVisual).isPassThroughModeEnabledForCurrentVisual();
    }

    private List<String> getSystemState() {
        List<String> ret = new ArrayList<String>();

        int brightnessInt = (int) (this.brightness * 100f);
        ret.add(ValidCommand.CHANGE_BRIGHTNESS + " " + brightnessInt);
        int generatorSpeed = (int) (this.fpsSpeed * 100f);
        ret.add(ValidCommand.GENERATOR_SPEED + " " + generatorSpeed);

        // add element status
        ret.addAll(pixelControllerEffect.getCurrentState());
        ret.addAll(pixelControllerGenerator.getCurrentState());
        ret.addAll(pixelControllerShufflerSelect.getCurrentState());

        ret.add(ValidCommand.CHANGE_PRESET + EMPTY_CHAR + presetService.getSelectedPreset());

        return ret;
    }

    /**
     * get current state of all visuals/outputs as string list - used to save
     * current settings.
     * 
     * @return the current status
     */
    public List<String> getCurrentStatus() {
        List<String> ret = new ArrayList<String>();

        // get visual status
        int n = 0;
        for (Visual v : allVisuals) {
            ret.add(ValidCommand.CURRENT_VISUAL + EMPTY_CHAR + n++);
            ret.add(ValidCommand.CHANGE_GENERATOR_A + EMPTY_CHAR + v.getGenerator1Idx());
            ret.add(ValidCommand.CHANGE_GENERATOR_B + EMPTY_CHAR + v.getGenerator2Idx());
            ret.add(ValidCommand.CHANGE_EFFECT_A + EMPTY_CHAR + v.getEffect1Idx());
            ret.add(ValidCommand.CHANGE_EFFECT_B + EMPTY_CHAR + v.getEffect2Idx());
            ret.add(ValidCommand.CHANGE_MIXER + EMPTY_CHAR + v.getMixerIdx());
            ret.add(ValidCommand.CURRENT_COLORSET + EMPTY_CHAR + v.getColorSet().getName());
        }

        // get output status
        int ofs = 0;
        for (OutputMapping om : ioMapping) {
            ret.add(ValidCommand.CURRENT_OUTPUT + EMPTY_CHAR + ofs);
            ret.add(ValidCommand.CHANGE_OUTPUT_FADER + EMPTY_CHAR + om.getFader().getId());
            ret.add(ValidCommand.CHANGE_OUTPUT_VISUAL + EMPTY_CHAR + om.getVisualId());
            ofs++;
        }

        if (inPauseMode) {
            ret.add(ValidCommand.FREEZE + EMPTY_CHAR);
        }

        ret.addAll(getSystemState());

        return ret;
    }

    /**
     * get the current state of the current visual/outputs - used to update the
     * gui
     * 
     * @return
     */
    public List<String> getGuiState() {
        List<String> ret = new ArrayList<String>();

        Visual v = allVisuals.get(currentVisual);
        ret.add(ValidCommand.CURRENT_VISUAL + EMPTY_CHAR + currentVisual);
        ret.add(ValidCommand.CHANGE_GENERATOR_A + EMPTY_CHAR + v.getGenerator1Idx());
        ret.add(ValidCommand.CHANGE_GENERATOR_B + EMPTY_CHAR + v.getGenerator2Idx());
        ret.add(ValidCommand.CHANGE_EFFECT_A + EMPTY_CHAR + v.getEffect1Idx());
        ret.add(ValidCommand.CHANGE_EFFECT_B + EMPTY_CHAR + v.getEffect2Idx());
        ret.add(ValidCommand.CHANGE_MIXER + EMPTY_CHAR + v.getMixerIdx());
        ret.add(ValidCommand.CURRENT_COLORSET + EMPTY_CHAR + v.getColorSet().getName());

        ret.add(ValidCommand.CHANGE_OUTPUT_FADER + EMPTY_CHAR
                + ioMapping.get(currentOutput).getFader().getId());
        ret.add(ValidCommand.CHANGE_OUTPUT_VISUAL + EMPTY_CHAR
                + ioMapping.get(currentOutput).getVisualId());
        ret.add(ValidCommand.CURRENT_OUTPUT + EMPTY_CHAR + currentOutput);
        ret.add(ValidCommand.FREEZE + EMPTY_CHAR + inPauseMode);
        ret.add(ValidCommand.GET_PASSTHROUGH_MODE + EMPTY_CHAR
                + v.isPassThroughModeEnabledForCurrentVisual());
        ret.addAll(getSystemState());

        return ret;
    }

    /**
	 * 
	 */
    public void notifyGuiUpdate() {
        setChanged();
        notifyObservers(getGuiState());
    }

}
