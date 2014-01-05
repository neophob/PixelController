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
package com.neophob.sematrix.core.listener;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.neophob.sematrix.core.glue.Shuffler;
import com.neophob.sematrix.core.glue.helper.ScreenshotHelper;
import com.neophob.sematrix.core.preset.PresetService;
import com.neophob.sematrix.core.properties.ValidCommand;
import com.neophob.sematrix.core.sound.BeatToAnimation;
import com.neophob.sematrix.core.visual.OutputMapping;
import com.neophob.sematrix.core.visual.Visual;
import com.neophob.sematrix.core.visual.VisualState;
import com.neophob.sematrix.core.visual.color.IColorSet;
import com.neophob.sematrix.core.visual.effect.Effect;
import com.neophob.sematrix.core.visual.effect.Effect.EffectName;
import com.neophob.sematrix.core.visual.effect.RotoZoom;
import com.neophob.sematrix.core.visual.fader.IFader;
import com.neophob.sematrix.core.visual.fader.TransitionManager;
import com.neophob.sematrix.core.visual.generator.Generator;
import com.neophob.sematrix.core.visual.mixer.Mixer;

/**
 * Messagebus of the application
 * 
 * reason to use a singleton here: - there exist ONE message processor -
 * processing the messages should be done synchronized
 */
public enum MessageProcessor {
    INSTANCE;

    /** The log. */
    private static final Logger LOG = Logger.getLogger(MessageProcessor.class.getName());

    /** The Constant IGNORE_COMMAND. */
    private static final String IGNORE_COMMAND = "Ignored command";

    private static final String OFF = "OFF";
    private static final String ON = "ON";

    private PresetService presetService;

    /**
     * Instantiates a new message processor.
     */
    private MessageProcessor() {
        // no instance allowed
    }

    /**
     * 
     * @param presetService
     */
    public void init(PresetService presetService) {
        this.presetService = presetService;
    }

    private int parseValue(String s) {
        return (int) Float.parseFloat(s);
    }

    private boolean ignoreCommand(ValidCommand cmd, String[] msg) {
        // filter out (double) touchOSC Messages, example push buttons
        if (msg.length == 2 && cmd.getNrOfParams() == 0) {
            if ("0.0".equals(msg[1])) {
                return true;
            }
        }

        // filter out (double) touchOSC Messages, example push buttons with
        // arguments, but ignore the low value
        if (msg.length == 2 && cmd.getNrOfParams() == 1) {
            if ("-1.0".equals(msg[1])) {
                return true;
            }
        }

        return false;
    }

    /**
     * process message from gui.
     * 
     * @param msg
     *            the msg
     * @param startFader
     *            the start fader
     * @return STATUS if we need to send updates back to the gui (loaded
     *         preferences)
     */
    public synchronized void processMsg(String[] msg, boolean startFader, byte[] blob) {
        if (msg == null || msg.length < 1) {
            return;
        }

        int msgLength = msg.length - 1;
        int tmp;
        VisualState col = VisualState.getInstance();

        try {
            ValidCommand cmd = ValidCommand.valueOf(msg[0]);
            if (ignoreCommand(cmd, msg)) {
                LOG.log(Level.INFO, "Ignore command, assume TouchOSC action");
                return;
            }

            Visual v;
            switch (cmd) {
                case CHANGE_GENERATOR_A:
                    try {
                        int nr = col.getCurrentVisual();
                        tmp = parseValue(msg[1]);
                        Generator g = col.getPixelControllerGenerator().getGenerator(tmp);
                        // silly check of generator exists
                        g.getId();
                        col.getVisual(nr).setGenerator1(g);
                    } catch (Exception e) {
                        LOG.log(Level.WARNING, IGNORE_COMMAND, e);
                    }
                    break;

                case CHANGE_GENERATOR_B:
                    try {
                        // the new method - used by the gui
                        int nr = col.getCurrentVisual();
                        tmp = parseValue(msg[1]);
                        Generator g = col.getPixelControllerGenerator().getGenerator(tmp);
                        g.getId();
                        col.getVisual(nr).setGenerator2(g);
                    } catch (Exception e) {
                        LOG.log(Level.WARNING, IGNORE_COMMAND, e);
                    }
                    break;

                case CHANGE_EFFECT_A:
                    try {
                        int nr = col.getCurrentVisual();
                        tmp = parseValue(msg[1]);
                        Effect e = col.getPixelControllerEffect().getEffect(tmp);
                        e.getId();
                        col.getVisual(nr).setEffect1(e);
                    } catch (Exception e) {
                        LOG.log(Level.WARNING, IGNORE_COMMAND, e);
                    }
                    break;

                case CHANGE_EFFECT_B:
                    try {
                        int nr = col.getCurrentVisual();
                        tmp = parseValue(msg[1]);
                        Effect e = col.getPixelControllerEffect().getEffect(tmp);
                        e.getId();
                        col.getVisual(nr).setEffect2(e);
                    } catch (Exception e) {
                        LOG.log(Level.WARNING, IGNORE_COMMAND, e);
                    }
                    break;

                case CHANGE_MIXER:
                    try {
                        // the new method - used by the gui
                        int nr = col.getCurrentVisual();
                        tmp = parseValue(msg[1]);
                        Mixer m = col.getPixelControllerMixer().getMixer(tmp);
                        m.getId();
                        col.getVisual(nr).setMixer(m);
                    } catch (Exception e) {
                        LOG.log(Level.WARNING, IGNORE_COMMAND, e);
                    }
                    break;

                case CHANGE_OUTPUT_VISUAL:
                    try {
                        int nr = col.getCurrentOutput();
                        int newFx = parseValue(msg[1]);
                        int oldFx = col.getFxInputForScreen(nr);
                        int nrOfVisual = col.getAllVisuals().size();
                        LOG.log(Level.INFO, "old fx: {0}, new fx {1}",
                                new Object[] { oldFx, newFx });
                        if (oldFx != newFx && newFx >= 0 && newFx < nrOfVisual) {
                            LOG.log(Level.INFO, "Change Output 0, old fx: {0}, new fx {1}",
                                    new Object[] { oldFx, newFx });
                            if (startFader) {
                                // start fader to change screen
                                col.getOutputMappings(nr).getFader().startFade(newFx, nr);
                            } else {
                                // do not fade if we load setting from present
                                col.mapInputToScreen(nr, newFx);
                            }
                        }
                    } catch (Exception e) {
                        LOG.log(Level.WARNING, IGNORE_COMMAND, e);
                    }
                    break;

                case CHANGE_ALL_OUTPUT_VISUAL:
                    try {
                        int newFx = parseValue(msg[1]);
                        int size = col.getAllOutputMappings().size();
                        int nrOfVisual = col.getAllVisuals().size();

                        if (newFx >= 0 && newFx < nrOfVisual) {
                            for (int i = 0; i < size; i++) {
                                int oldFx = col.getFxInputForScreen(i);
                                if (oldFx != newFx) {
                                    LOG.log(Level.INFO, "Change Output 0, old fx: {0}, new fx {1}",
                                            new Object[] { oldFx, newFx });
                                    if (startFader) {
                                        // start fader to change screen
                                        col.getOutputMappings(i).getFader().startFade(newFx, i);
                                    } else {
                                        // do not fade if we load setting from
                                        // present
                                        col.mapInputToScreen(i, newFx);
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        LOG.log(Level.WARNING, IGNORE_COMMAND, e);
                    }
                    break;

                case CHANGE_OUTPUT_FADER:
                    try {
                        int nr = col.getCurrentOutput();
                        tmp = parseValue(msg[1]);
                        // do not start a new fader while the old one is still
                        // running
                        if (!col.getOutputMappings(nr).getFader().isStarted()) {
                            IFader f = col.getPixelControllerFader().getVisualFader(tmp,
                                    col.getFpsSpeed());
                            if (f != null) {
                                col.getOutputMappings(nr).setFader(f);
                            }
                        }
                    } catch (Exception e) {
                        LOG.log(Level.WARNING, IGNORE_COMMAND, e);
                    }
                    break;

                case CHANGE_ALL_OUTPUT_FADER:
                    try {
                        tmp = parseValue(msg[1]);
                        for (OutputMapping om : col.getAllOutputMappings()) {
                            // do not start a new fader while the old one is
                            // still running
                            if (!om.getFader().isStarted()) {
                                IFader f = col.getPixelControllerFader().getVisualFader(tmp,
                                        col.getFpsSpeed());
                                if (f != null) {
                                    om.setFader(f);
                                }
                            }
                        }
                    } catch (Exception e) {
                        LOG.log(Level.WARNING, IGNORE_COMMAND, e);
                    }
                    break;

                case CHANGE_SHUFFLER_SELECT:
                    try {
                        int size = col.getPixelControllerShufflerSelect().getShufflerSelect()
                                .size();
                        if (size > msgLength) {
                            size = msgLength;
                        }
                        boolean b;
                        String str = "";
                        for (int i = 0; i < size; i++) {
                            b = false;
                            if ("1".equals(msg[i + 1])) {
                                b = true;
                                str += '1';
                            } else
                                str += '0';

                            col.getPixelControllerShufflerSelect().setShufflerSelect(i, b);
                        }
                        LOG.log(Level.INFO, "Shuffler select: " + str);
                    } catch (Exception e) {
                        LOG.log(Level.WARNING, IGNORE_COMMAND, e);
                    }
                    break;

                case CHANGE_ROTOZOOM:
                    try {
                        int val = parseValue(msg[1]);
                        RotoZoom r = (RotoZoom) col.getPixelControllerEffect().getEffect(
                                EffectName.ROTOZOOM);
                        r.setAngle(val);
                    } catch (Exception e) {
                        LOG.log(Level.WARNING, IGNORE_COMMAND, e);
                    }
                    break;

                case SAVE_PRESET:
                    try {
                        int idxs = presetService.getSelectedPreset();
                        List<String> present = col.getCurrentStatus();
                        presetService.getPresets().get(idxs).setPresent(present);
                        presetService.getPresets().get(idxs).setName(msg[1]);
                        presetService.savePresents();
                    } catch (Exception e) {
                        LOG.log(Level.WARNING, IGNORE_COMMAND, e);
                    }
                    break;

                case LOAD_PRESET:
                    try {
                        loadPreset(presetService.getSelectedPreset());
                        col.notifyGuiUpdate();
                    } catch (Exception e) {
                        LOG.log(Level.WARNING, IGNORE_COMMAND, e);
                    }
                    break;

                case CHANGE_PRESET:
                    try {
                        int a = parseValue(msg[1]);
                        presetService.setSelectedPreset(a);
                    } catch (Exception e) {
                        LOG.log(Level.WARNING, IGNORE_COMMAND, e);
                    }
                    break;

                case CHANGE_THRESHOLD_VALUE:
                    try {
                        int a = parseValue(msg[1]);
                        if (a > 255) {
                            a = 255;
                        }
                        if (a < 0) {
                            a = 0;
                        }
                        col.getPixelControllerEffect().setThresholdValue(a);
                    } catch (Exception e) {
                        LOG.log(Level.WARNING, IGNORE_COMMAND, e);
                    }
                    break;

                case BLINKEN:
                    try {
                        String fileToLoad = msg[1];
                        col.getPixelControllerGenerator().setFileBlinken(fileToLoad);
                    } catch (Exception e) {
                        LOG.log(Level.WARNING, IGNORE_COMMAND, e);
                    }
                    break;

                case IMAGE:
                    try {
                        String fileToLoad = msg[1];
                        col.getPixelControllerGenerator().setFileImageSimple(fileToLoad);
                    } catch (Exception e) {
                        LOG.log(Level.WARNING, IGNORE_COMMAND, e);
                    }
                    break;

                case COLOR_SCROLL_OPT:
                    try {
                        int dir = parseValue(msg[1]);
                        col.getPixelControllerGenerator().setColorScrollingDirection(dir);
                    } catch (Exception e) {
                        LOG.log(Level.WARNING, IGNORE_COMMAND, e);
                    }
                    break;

                case TEXTDEF:
                    try {
                        int lut = parseValue(msg[1]);
                        col.getPixelControllerEffect().setTextureDeformationLut(lut);
                    } catch (Exception e) {
                        LOG.log(Level.WARNING, IGNORE_COMMAND, e);
                    }
                    break;

                case ZOOMOPT:
                    try {
                        int zoomMode = parseValue(msg[1]);
                        col.getPixelControllerEffect().setZoomOption(zoomMode);
                    } catch (Exception e) {
                        LOG.log(Level.WARNING, IGNORE_COMMAND, e);
                    }
                    break;

                case TEXTWR:
                    try {
                        if (msg.length < 2 || msg[1] == null) {
                            col.getPixelControllerGenerator().setText("");
                        } else {
                            col.getPixelControllerGenerator().setText(msg[1]);
                        }
                    } catch (Exception e) {
                        LOG.log(Level.WARNING, IGNORE_COMMAND, e);
                    }
                    break;

                case TEXTWR_OPTION:
                    try {
                        int scollerNr = parseValue(msg[1]);
                        col.getPixelControllerGenerator().setTextOption(scollerNr);
                    } catch (Exception e) {
                        LOG.log(Level.WARNING, IGNORE_COMMAND, e);
                    }
                    break;

                case RANDOM: // enable or disable random mode
                    try {
                        String onOrOff = msg[1];
                        if (ON.equalsIgnoreCase(onOrOff)) {
                            col.setRandomPresetMode(false);
                            col.setRandomMode(true);
                            LOG.log(Level.INFO, "Random Mode enabled");
                        }
                        if (OFF.equalsIgnoreCase(onOrOff)) {
                            col.setRandomPresetMode(false);
                            col.setRandomMode(false);
                            LOG.log(Level.INFO, "Random Mode disabled");
                        }
                    } catch (Exception e) {
                        LOG.log(Level.WARNING, IGNORE_COMMAND, e);
                    }
                    break;

                case RANDOM_PRESET_MODE:
                    try {
                        String onOrOff = msg[1];
                        if (ON.equalsIgnoreCase(onOrOff)) {
                            col.setRandomMode(false);
                            col.setRandomPresetMode(true);
                            LOG.log(Level.INFO, "Random Preset Mode enabled");
                        }
                        if (OFF.equalsIgnoreCase(onOrOff)) {
                            col.setRandomMode(false);
                            col.setRandomPresetMode(false);
                            LOG.log(Level.INFO, "Random Preset Mode disabled");
                        }
                    } catch (Exception e) {
                        LOG.log(Level.WARNING, IGNORE_COMMAND, e);
                    }
                    break;

                // one shot randomizer
                case RANDOMIZE:
                    try {
                        // save current visual buffer
                        TransitionManager transition = new TransitionManager(col);
                        Shuffler.manualShuffleStuff(col);
                        transition.startCrossfader();
                        col.notifyGuiUpdate();
                    } catch (Exception e) {
                        LOG.log(Level.WARNING, IGNORE_COMMAND, e);
                    }
                    break;

                // one shot randomizer, use a pre-stored present
                case PRESET_RANDOM:
                    try {
                        // save current visual buffer
                        TransitionManager transition = new TransitionManager(col);
                        int currentPreset = Shuffler.getRandomPreset(presetService);
                        loadPreset(currentPreset);
                        presetService.setSelectedPreset(currentPreset);
                        transition.startCrossfader();
                        col.notifyGuiUpdate();
                    } catch (Exception e) {
                        LOG.log(Level.WARNING, IGNORE_COMMAND, e);
                    }
                    break;

                case CURRENT_VISUAL:
                    // change the selected visual, need to update
                    // some of the gui elements
                    try {
                        int a = parseValue(msg[1]);
                        col.setCurrentVisual(a);
                        col.notifyGuiUpdate();
                    } catch (Exception e) {
                        LOG.log(Level.WARNING, IGNORE_COMMAND, e);
                    }
                    break;

                case CURRENT_OUTPUT:
                    // change the selected output, need to update
                    // some of the gui elements
                    try {
                        int a = parseValue(msg[1]);
                        col.setCurrentOutput(a);
                    } catch (Exception e) {
                        LOG.log(Level.WARNING, IGNORE_COMMAND, e);
                    }
                    break;

                case CHANGE_BRIGHTNESS:
                    try {
                        int a = parseValue(msg[1]);
                        if (a < 0 || a > 100) {
                            LOG.log(Level.WARNING, IGNORE_COMMAND + ", invalid brightness value: "
                                    + a);
                            break;
                        } else {
                            float f = a / 100f;
                            col.setBrightness(f);
                        }
                    } catch (Exception e) {
                        LOG.log(Level.WARNING, IGNORE_COMMAND, e);
                    }
                    break;

                case GENERATOR_SPEED:
                    try {
                        int fpsAdjustment = (int) Float.parseFloat(msg[1]);
                        if (fpsAdjustment < 0 || fpsAdjustment > 200) {
                            LOG.log(Level.WARNING, IGNORE_COMMAND
                                    + ", invalid fps adjustment value: " + fpsAdjustment);
                            break;
                        } else {
                            float f = fpsAdjustment / 100f;
                            col.setFpsSpeed(f);
                        }
                    } catch (Exception e) {
                        LOG.log(Level.WARNING, IGNORE_COMMAND, e);
                    }
                    break;

                // create a screenshot of all current buffers
                case SCREENSHOT:
                    ScreenshotHelper.saveScreenshot(col.getFrames(), col.getAllVisuals());
                    LOG.log(Level.INFO, "Saved some screenshots");
                    break;

                // change current colorset
                case CURRENT_COLORSET:
                    int nr = col.getCurrentVisual();
                    try {
                        // old method, reference colorset by index
                        int newColorSetIndex = parseValue(msg[1]);
                        col.getVisual(nr).setColorSet(newColorSetIndex);
                        break;
                    } catch (Exception e) {
                        // ignore
                    }

                    try {
                        // now try to reference colorset by name
                        col.getVisual(nr).setColorSet(msg[1]);
                    } catch (Exception e) {
                        LOG.log(Level.WARNING, IGNORE_COMMAND, e);
                    }

                    break;

                // pause output, needed to create screenshots or take an image
                // of the output
                case FREEZE:
                    col.togglePauseMode();
                    break;

                case OSC_GENERATOR1:
                    col.getPixelControllerGenerator().getOscListener1().updateBuffer(blob);
                    break;

                case OSC_GENERATOR2:
                    col.getPixelControllerGenerator().getOscListener2().updateBuffer(blob);
                    break;

                case ROTATE_COLORSET:
                    v = col.getVisual(col.getCurrentVisual());
                    String colorSetName = v.getColorSet().getName();

                    boolean takeNext = false;
                    IColorSet nextColorSet = col.getColorSets().get(0);
                    for (IColorSet cs : col.getColorSets()) {
                        if (takeNext) {
                            nextColorSet = cs;
                            break;
                        }
                        if (cs.getName().equals(colorSetName)) {
                            takeNext = true;
                        }
                    }
                    v.setColorSet(nextColorSet.getName());
                    break;

                case ROTATE_GENERATOR_A:
                    v = col.getVisual(col.getCurrentVisual());
                    int currentGenerator = v.getGenerator1Idx();
                    int nrOfGenerators = 1 + col.getPixelControllerGenerator().getSize();
                    int count = nrOfGenerators;
                    Generator g = null;
                    while (count >= 0 && g == null) {
                        currentGenerator++;
                        g = col.getPixelControllerGenerator().getGenerator(
                                currentGenerator % nrOfGenerators);
                    }
                    if (g != null && g.getName() != null) {
                        v.setGenerator1(currentGenerator % nrOfGenerators);
                    }
                    break;

                case ROTATE_GENERATOR_B:
                    v = col.getVisual(col.getCurrentVisual());
                    currentGenerator = v.getGenerator2Idx();
                    nrOfGenerators = 1 + col.getPixelControllerGenerator().getSize();
                    count = nrOfGenerators;
                    g = null;
                    while (count >= 0 && g == null) {
                        currentGenerator++;
                        g = col.getPixelControllerGenerator().getGenerator(
                                currentGenerator % nrOfGenerators);
                    }
                    if (g != null && g.getName() != null) {
                        v.setGenerator2(currentGenerator % nrOfGenerators);
                    }
                    break;

                case ROTATE_EFFECT_A:
                    v = col.getVisual(col.getCurrentVisual());
                    int currentEffect = v.getEffect1Idx();
                    int nrOfEffects = col.getPixelControllerEffect().getSize();
                    currentEffect++;
                    v.setEffect1(currentEffect % nrOfEffects);
                    break;

                case ROTATE_EFFECT_B:
                    v = col.getVisual(col.getCurrentVisual());
                    currentEffect = v.getEffect2Idx();
                    nrOfEffects = col.getPixelControllerEffect().getSize();
                    currentEffect++;
                    v.setEffect2(currentEffect % nrOfEffects);
                    break;

                case ROTATE_MIXER:
                    v = col.getVisual(col.getCurrentVisual());
                    int currentMixer = v.getMixerIdx();
                    int nrOfMixerss = col.getPixelControllerMixer().getSize();
                    currentMixer++;
                    v.setMixer(currentMixer % nrOfMixerss);
                    break;

                case BEAT_WORKMODE:
                    try {
                        int workmodeId = parseValue(msg[1]);
                        for (BeatToAnimation bta : BeatToAnimation.values()) {
                            if (bta.getId() == workmodeId) {
                                col.getPixelControllerGenerator().setBta(bta);
                                LOG.log(Level.INFO, "Select beat workmode " + bta);
                            }
                        }

                    } catch (Exception e) {
                        LOG.log(Level.WARNING, IGNORE_COMMAND, e);
                    }

                    break;

                // unkown message
                default:
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < msg.length; i++) {
                        sb.append(msg[i]);
                        sb.append("; ");
                    }
                    LOG.log(Level.INFO, "Ignored command <{0}>", sb);
                    break;
            }

            col.notifyGuiUpdate();

        } catch (IllegalArgumentException e) {
            LOG.log(Level.INFO, "Unknown attribute ignored <{0}>", new Object[] { msg[0] });
        }

    }

    private List<String> removeObsoleteCommands(List<String> preset) {
        if (!preset.contains("CHANGE_GENERATOR_B 1") && !preset.contains("CHANGE_GENERATOR_A 1")) {
            LOG.log(Level.INFO, "No Blinkengenerator found, remove loading blinken resource");
            int ofs = 0;
            for (String s : preset) {
                if (s.startsWith("BLINKEN")) {
                    break;
                }
                ofs++;
            }
            preset.remove(ofs);
        }
        if (!preset.contains("CHANGE_GENERATOR_B 2") && !preset.contains("CHANGE_GENERATOR_A 2")) {
            LOG.log(Level.INFO, "No Imagegenerator found, remove loading image resource");
            int ofs = 0;
            for (String s : preset) {
                if (s.startsWith("IMAGE")) {
                    break;
                }
                ofs++;
            }
            preset.remove(ofs);
        }
        return preset;
    }

    /**
     * 
     * @param nr
     */
    private void loadPreset(int nr) {
        VisualState col = VisualState.getInstance();
        col.setLoadingPresent(true);
        // save current selections
        int currentVisual = col.getCurrentVisual();
        int currentOutput = col.getCurrentOutput();

        List<String> preset = presetService.getPresets().get(nr).getPresent();
        if (preset != null) {
            preset = removeObsoleteCommands(new ArrayList<String>(preset));

            // load preset
            col.setCurrentStatus(preset);

            // Restore current Selection
            col.setCurrentVisual(currentVisual);
            col.setCurrentOutput(currentOutput);
        }
        col.setLoadingPresent(false);
    }

}
