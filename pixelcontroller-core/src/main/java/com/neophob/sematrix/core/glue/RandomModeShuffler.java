package com.neophob.sematrix.core.glue;

import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.neophob.sematrix.core.sound.BeatToAnimation;
import com.neophob.sematrix.core.visual.OutputMapping;
import com.neophob.sematrix.core.visual.Visual;
import com.neophob.sematrix.core.visual.VisualState;
import com.neophob.sematrix.core.visual.effect.Effect.EffectName;
import com.neophob.sematrix.core.visual.fader.IFader;
import com.neophob.sematrix.core.visual.generator.Generator.GeneratorName;

public final class RandomModeShuffler {

    private static final Logger LOG = Logger.getLogger(RandomModeShuffler.class.getName());

    private RandomModeShuffler() {
        // no instance
    }

    /**
     * used for randomized mode, rarely change stuff.
     */
    public static boolean shuffleStuff(List<Boolean> shufflerSelect, boolean kick, boolean hat,
            boolean snare) {
        if (!hat && !kick && !snare) {
            return false;
        }

        if (shufflerSelect == null || shufflerSelect.size() < ShufflerOffset.values().length) {
            LOG.log(Level.WARNING, "Invalid shufflerSelect size: " + shufflerSelect);
            return false;
        }

        boolean changed = false;

        VisualState col = VisualState.getInstance();

        Random rand = new Random();
        int blah = rand.nextInt(18);

        if (snare) {
            if (blah == 1 && shufflerSelect.get(ShufflerOffset.GENERATOR_A.getOffset())) {
                int size = col.getPixelControllerGenerator().getSize();
                for (Visual v : col.getAllVisuals()) {
                    v.setGenerator1(rand.nextInt(size - 1) + 1);
                }
                changed = true;
            }

            if (blah == 2 && shufflerSelect.get(ShufflerOffset.GENERATOR_B.getOffset())) {
                int size = col.getPixelControllerGenerator().getSize();
                for (Visual v : col.getAllVisuals()) {
                    v.setGenerator2(rand.nextInt(size));
                }
                changed = true;
            }

            if (blah == 3 && shufflerSelect.get(ShufflerOffset.EFFECT_A.getOffset())) {
                int size = col.getPixelControllerEffect().getSize();
                for (Visual v : col.getAllVisuals()) {
                    v.setEffect1(rand.nextInt(size));
                }
                changed = true;
            }

            if (blah == 4 && shufflerSelect.get(ShufflerOffset.EFFECT_B.getOffset())) {
                int size = col.getPixelControllerEffect().getSize();
                for (Visual v : col.getAllVisuals()) {
                    v.setEffect2(rand.nextInt(size));
                }
                changed = true;
            }

            if (blah == 5 && shufflerSelect.get(ShufflerOffset.COLORSET.getOffset())) {
                int colorSets = col.getColorSets().size();
                for (Visual v : col.getAllVisuals()) {
                    v.setColorSet(rand.nextInt(colorSets));
                }
                changed = true;
            }

            if (blah == 6) {
                col.getPixelControllerEffect().getEffect(EffectName.THRESHOLD).shuffle();
                changed = true;
            }

        }

        if (hat) {
            if (blah == 7 && shufflerSelect.get(ShufflerOffset.MIXER.getOffset())) {
                int size = col.getPixelControllerMixer().getSize();
                for (Visual v : col.getAllVisuals()) {
                    if (v.getGenerator2Idx() == 0) {
                        // no 2nd generator - use passthru mixer
                        v.setMixer(0);
                    } else {
                        v.setMixer(rand.nextInt(size));
                    }
                }
                changed = true;
            }

            if (blah == 8 && shufflerSelect.get(ShufflerOffset.FADER_OUTPUT.getOffset())) {
                int size = col.getPixelControllerFader().getFaderCount();
                for (OutputMapping om : col.getAllOutputMappings()) {
                    IFader f = om.getFader();
                    if (!f.isStarted()) {
                        om.setFader(col.getPixelControllerFader()
                                .getVisualFader(rand.nextInt(size)));
                    }
                }
                changed = true;
            }

            if (blah == 9) {
                col.getPixelControllerEffect().getEffect(EffectName.ROTOZOOM).shuffle();
                changed = true;
            }

            if (blah == 10) {
                col.getPixelControllerEffect().getEffect(EffectName.ZOOM).shuffle();
                changed = true;
            }

            if (blah == 11 && shufflerSelect.get(ShufflerOffset.GENERATORSPEED.getOffset())) {
                col.setFpsSpeed(new Random().nextFloat() * 2.0f);
                changed = true;
            }

            if (blah == 12 && shufflerSelect.get(ShufflerOffset.BEAT_WORK_MODE.getOffset())) {
                BeatToAnimation bta = BeatToAnimation.values()[new Random().nextInt(BeatToAnimation
                        .values().length)];
                col.getPixelControllerGenerator().setBta(bta);
                changed = true;
            }

        }

        if (kick) {
            if (blah == 13 && shufflerSelect.get(ShufflerOffset.OUTPUT.getOffset())) {
                int nrOfVisuals = col.getAllVisuals().size();
                int screenNr = 0;
                for (OutputMapping om : col.getAllOutputMappings()) {
                    IFader f = om.getFader();
                    if (!f.isStarted()) {
                        // start fader only if not another one is started
                        f.startFade(rand.nextInt(nrOfVisuals), screenNr);
                    }
                    screenNr++;
                }
                changed = true;
            }

            if (blah == 14) {
                col.getPixelControllerGenerator().getGenerator(GeneratorName.IMAGE).shuffle();
                changed = true;
            }

            if (blah == 15) {
                col.getPixelControllerGenerator().getGenerator(GeneratorName.BLINKENLIGHTS)
                        .shuffle();
                changed = true;
            }

            if (blah == 16) {
                col.getPixelControllerEffect().getEffect(EffectName.TEXTURE_DEFORMATION).shuffle();
                changed = true;
            }

            if (blah == 17) {
                col.getPixelControllerGenerator().getGenerator(GeneratorName.COLOR_SCROLL)
                        .shuffle();
                changed = true;
            }

        }

        return changed;
    }
}
