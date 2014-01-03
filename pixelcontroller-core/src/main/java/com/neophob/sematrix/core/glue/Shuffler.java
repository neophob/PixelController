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
package com.neophob.sematrix.core.glue;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.neophob.sematrix.core.preset.PresetService;
import com.neophob.sematrix.core.sound.BeatToAnimation;
import com.neophob.sematrix.core.sound.ISound;
import com.neophob.sematrix.core.visual.OutputMapping;
import com.neophob.sematrix.core.visual.Visual;
import com.neophob.sematrix.core.visual.VisualState;
import com.neophob.sematrix.core.visual.effect.Effect;
import com.neophob.sematrix.core.visual.generator.Generator;

/**
 * create random settings.
 * 
 * TODO USE MESSAGE PROCESSOR
 * 
 * @author michu
 */
public final class Shuffler {

    /** The log. */
    private static final Logger LOG = Logger.getLogger(Shuffler.class.getName());

    /**
     * Instantiates a new shuffler.
     */
    private Shuffler() {
        // no instance allowed
    }

    /**
     * used for randomized preset mode, rarely change stuff.
     */
    public static boolean randomPresentModeShuffler(ISound sound) {
        boolean kick = sound.isKick();
        boolean hat = sound.isHat();

        if (!kick && !hat) {
            return false;
        }

        Random rand = new Random();
        if (rand.nextInt(10) == 1) {
            return true;
        }

        return false;
    }

    /**
     * get a random and valid preset
     */
    public static int getRandomPreset(PresetService presetService) {
        Random rand = new Random();

        LOG.log(Level.INFO, "Present Shuffler");

        int sanityCheck = 1000;
        boolean done = false;
        int idx = 0;
        while (!done || sanityCheck-- < 1) {
            idx = rand.nextInt(presetService.getPresets().size());
            List<String> present = presetService.getPresets().get(idx).getPresent();
            if (present != null && !present.isEmpty()) {
                done = true;
            }
        }
        return idx;
    }

    /**
     * heavy shuffler! shuffle the current selected visual used by manual
     * RANDOMIZE.
     * 
     */
    public static void manualShuffleStuff(VisualState vs) {
        long start = System.currentTimeMillis();

        int currentVisual = vs.getCurrentVisual();
        Visual visual = vs.getVisual(currentVisual);
        Random rand = new Random();

        LOG.log(Level.INFO, "Manual Shuffle for Visual {0}", currentVisual);

        int totalNrGenerator = vs.getPixelControllerGenerator().getSize();
        // if (!vs.getPixelControllerGenerator().isCaptureGeneratorActive()) {
        // totalNrGenerator++;
        // }
        int totalNrEffect = vs.getPixelControllerEffect().getSize();
        int totalNrMixer = vs.getPixelControllerMixer().getSize();

        if (vs.getShufflerSelect(ShufflerOffset.GENERATOR_A)) {
            // make sure we only select inuse generators
            boolean isGeneratorInUse = false;
            while (!isGeneratorInUse) {
                // why -1 +1? the first effect is passthrough - so no effect
                visual.setGenerator1(rand.nextInt(totalNrGenerator - 1) + 1);
                isGeneratorInUse = visual.getGenerator1().isInUse();
            }
        }

        if (vs.getShufflerSelect(ShufflerOffset.GENERATOR_B)) {
            // make sure we only select inuse generators
            boolean isGeneratorInUse = false;
            while (!isGeneratorInUse) {
                // why -1 +1? the first effect is passthrough - so no effect
                visual.setGenerator2(rand.nextInt(totalNrGenerator - 1) + 1);
                isGeneratorInUse = visual.getGenerator2().isInUse();
            }
        }

        if (vs.getShufflerSelect(ShufflerOffset.EFFECT_A)) {
            visual.setEffect1(rand.nextInt(totalNrEffect));
        }

        if (vs.getShufflerSelect(ShufflerOffset.EFFECT_B)) {
            visual.setEffect2(rand.nextInt(totalNrEffect));
        }

        if (vs.getShufflerSelect(ShufflerOffset.MIXER)) {
            if (visual.getGenerator2Idx() == 0) {
                // no 2nd generator - use passthru mixer
                visual.setMixer(0);
            } else {
                visual.setMixer(rand.nextInt(totalNrMixer));
            }
        }

        // set used to find out if visual is on screen
        Set<Integer> activeGeneratorIds = new HashSet<Integer>();
        Set<Integer> activeEffectIds = new HashSet<Integer>();
        for (OutputMapping om : vs.getAllOutputMappings()) {
            Visual v = vs.getVisual(om.getVisualId());

            if (v.equals(visual)) {
                continue;
            }

            activeEffectIds.add(v.getEffect1Idx());
            activeEffectIds.add(v.getEffect2Idx());

            activeGeneratorIds.add(v.getGenerator1Idx());
            activeGeneratorIds.add(v.getGenerator2Idx());
        }

        // optimize, update blinkenlighst movie file only when visible
        boolean isBlinkenlightGeneratorVisible = false;
        if (visual.getGenerator1Idx() == Generator.GeneratorName.BLINKENLIGHTS.getId()
                || visual.getGenerator2Idx() == Generator.GeneratorName.BLINKENLIGHTS.getId()) {
            isBlinkenlightGeneratorVisible = true;
        }
        boolean isImageGeneratorVisible = false;
        if (visual.getGenerator1Idx() == Generator.GeneratorName.IMAGE.getId()
                || visual.getGenerator2Idx() == Generator.GeneratorName.IMAGE.getId()) {
            isImageGeneratorVisible = true;
        }

        // shuffle only items which are NOT visible
        for (Generator g : vs.getPixelControllerGenerator().getAllGenerators()) {
            if (!activeGeneratorIds.contains(g.getId())) {

                // optimize, loading a blinkenlights movie file is quite
                // expensive (takes a long time)
                // so load only a new movie file if the generator is active!
                if (g.getId() == Generator.GeneratorName.BLINKENLIGHTS.getId()) {
                    if (isBlinkenlightGeneratorVisible) {
                        g.shuffle();
                    }
                } else if (g.getId() == Generator.GeneratorName.IMAGE.getId()) {
                    if (isImageGeneratorVisible) {
                        g.shuffle();
                    }
                } else {
                    g.shuffle();
                }
            }
        }

        for (Effect e : vs.getPixelControllerEffect().getAllEffects()) {
            if (!activeEffectIds.contains(e.getId())) {
                e.shuffle();
            }
        }

        if (vs.getShufflerSelect(ShufflerOffset.COLORSET)) {
            int colorSets = vs.getColorSets().size();
            visual.setColorSet(rand.nextInt(colorSets));
        }

        if (vs.getShufflerSelect(ShufflerOffset.BEAT_WORK_MODE)) {
            BeatToAnimation bta = BeatToAnimation.values()[new Random().nextInt(BeatToAnimation
                    .values().length)];
            vs.getPixelControllerGenerator().setBta(bta);
        }

        if (vs.getShufflerSelect(ShufflerOffset.GENERATORSPEED)) {
            vs.setFpsSpeed(new Random().nextFloat() * 2.0f);
        }

        LOG.log(Level.INFO, "Shuffle finished in {0}ms", System.currentTimeMillis() - start);
    }

}
