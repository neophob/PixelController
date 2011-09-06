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

package com.neophob.sematrix.glue;

import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.neophob.sematrix.effect.Effect.EffectName;
import com.neophob.sematrix.fader.Fader;
import com.neophob.sematrix.fader.PixelControllerFader;
import com.neophob.sematrix.generator.Generator.GeneratorName;
import com.neophob.sematrix.input.Sound;

/**
 * create random settings.
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
		//no instance allowed
	}

	/**
	 * load a prestored preset, randomly.
	 */
	public static void presentShuffler() {
		Collector col = Collector.getInstance();
		Random rand = new Random();
		
		LOG.log(Level.INFO, "Present Shuffler");
		
		boolean done=false;
		while (!done) {
			int idx = rand.nextInt(64);
			List<String> present = col.getPresent().get(idx).getPresent();
			if (present!=null && present.size()>0) { 
				col.setCurrentStatus(present);
				col.setSelectedPresent(idx);
				done = true;
			}
			
		}
	}
	
	/**
	 * heavy shuffler! shuffle the current selected visual
	 * used by manual RANDOMIZE.
	 */
	public static void manualShuffleStuff() {		
		Collector col = Collector.getInstance(); 

		Random rand = new Random();
		int currentVisual = col.getCurrentVisual();
		int totalNrGenerator = col.getPixelControllerGenerator().getSize();
		int totalNrEffect = col.getPixelControllerEffect().getSize();
		int totalNrMixer = col.getPixelControllerMixer().getSize();

		LOG.log(Level.INFO, "Manaual Shuffle for Visual {0}", currentVisual);

		if (col.getShufflerSelect(ShufflerOffset.GENERATOR_A)) {
			for (Visual v: col.getAllVisuals()) {
				//why -1 +1? the first effect is passthrough - so no effect
				v.setGenerator1(rand.nextInt(totalNrGenerator-1)+1);
			}
		}

		if (col.getShufflerSelect(ShufflerOffset.GENERATOR_B)) {
			for (Visual v: col.getAllVisuals()) {
				v.setGenerator2(rand.nextInt(totalNrGenerator-1)+1);
			}
		}

		if (col.getShufflerSelect(ShufflerOffset.EFFECT_A)) {
			for (Visual v: col.getAllVisuals()) {
				v.setEffect1(rand.nextInt(totalNrEffect));
			}
		}

		if (col.getShufflerSelect(ShufflerOffset.EFFECT_B)) {
			for (Visual v: col.getAllVisuals()) {
				v.setEffect2(rand.nextInt(totalNrEffect));
			}
		}
		
		if (col.getShufflerSelect(ShufflerOffset.MIXER)) {
			for (Visual v: col.getAllVisuals()) {
				if (v.getGenerator2Idx()==0) {
					//no 2nd generator - use passthru mixer
					v.setMixer(0);						
				} else {
					v.setMixer(rand.nextInt(totalNrMixer));						
				}
			}
		}

		for (RandomizeState r: col.getPixelControllerGenerator().getAllGenerators()) {
			r.shuffle();
		}
		for (RandomizeState r: col.getPixelControllerEffect().getAllEffects()) {
			r.shuffle();
		}
		
		if (col.getShufflerSelect(ShufflerOffset.OUTPUT)) {
			int nrOfVisuals = col.getAllVisuals().size();
			int screenNr = 0;
			for (OutputMapping om: col.getAllOutputMappings()) {
				Fader f=om.getFader();
				if (!f.isStarted()) {
					//start fader only if not another one is started
					f.startFade(rand.nextInt(nrOfVisuals), screenNr);
				}
				screenNr++;
			}
		}

	}

	/**
	 * used for randomized mode, rarely change stuff.
	 */
	public static void shuffleStuff() {
		boolean kick = Sound.getInstance().isKick();
		boolean hat = Sound.getInstance().isHat();
		boolean snare = Sound.getInstance().isSnare();

		if (!hat && !kick && !snare) {
			return;
		}

		Collector col = Collector.getInstance(); 

		Random rand = new Random();
		int blah = rand.nextInt(16);
		LOG.log(Level.INFO, "Automatic Shuffler {0}", blah);

		if (snare) {			
			if (blah == 1 && col.getShufflerSelect(ShufflerOffset.GENERATOR_A)) {
				int size = col.getPixelControllerGenerator().getSize();
				for (Visual v: col.getAllVisuals()) {
					v.setGenerator1(rand.nextInt(size-1)+1);
				}
			}

			if (blah == 2 && col.getShufflerSelect(ShufflerOffset.GENERATOR_B)) {
				int size = col.getPixelControllerGenerator().getSize();
				for (Visual v: col.getAllVisuals()) {
					v.setGenerator2(rand.nextInt(size));
				}

			}

			if (blah == 3 && col.getShufflerSelect(ShufflerOffset.EFFECT_A)) {
				int size = col.getPixelControllerEffect().getSize();
				for (Visual v: col.getAllVisuals()) {
					v.setEffect1(rand.nextInt(size));
				}
			}

			if (blah == 4 && col.getShufflerSelect(ShufflerOffset.EFFECT_B)) {
				int size = col.getPixelControllerEffect().getSize();
				for (Visual v: col.getAllVisuals()) {
					v.setEffect2(rand.nextInt(size));
				}
			}

			if (blah == 14) {
				col.getPixelControllerEffect().getEffect(EffectName.THRESHOLD).shuffle();
			}

		}

		if (hat) {
			if (blah == 5 && col.getShufflerSelect(ShufflerOffset.MIXER)) {
				int size = col.getPixelControllerMixer().getSize();
				for (Visual v: col.getAllVisuals()) {
					if (v.getGenerator2Idx()==0) {
						//no 2nd generator - use passthru mixer
						v.setMixer(0);						
					} else {
						v.setMixer(rand.nextInt(size));						
					}
				}
			}			

			if (blah == 6 && col.getShufflerSelect(ShufflerOffset.FADER_OUTPUT)) {
				int size = PixelControllerFader.getFaderCount();
				for (OutputMapping om: col.getAllOutputMappings()) {
					Fader f=om.getFader();
					if (!f.isStarted()) {
						om.setFader(
								PixelControllerFader.getFader(rand.nextInt(size))
						);	
					}
				}
			}

			if (blah == 11) {
				col.getPixelControllerEffect().getEffect(EffectName.TINT).shuffle();
			}
			
	
			if (blah == 15) {
				col.getPixelControllerEffect().getEffect(EffectName.ROTOZOOM).shuffle();
			}

		}


		if (kick) {
			if (blah == 7 && col.getShufflerSelect(ShufflerOffset.OUTPUT)) {
				int nrOfVisuals = col.getAllVisuals().size();
				int screenNr = 0;
				for (OutputMapping om: col.getAllOutputMappings()) {
					Fader f=om.getFader();
					if (!f.isStarted()) {
						//start fader only if not another one is started
						f.startFade(rand.nextInt(nrOfVisuals), screenNr);
					}
					screenNr++;
				}
			}

			if (blah == 8) {
				col.getPixelControllerGenerator().getGenerator(GeneratorName.IMAGE).shuffle();
			}

			if (blah == 9) {
				col.getPixelControllerGenerator().getGenerator(GeneratorName.BLINKENLIGHTS).shuffle();
			}

			if (blah == 12) {
				col.getPixelControllerGenerator().getGenerator(GeneratorName.TEXTURE_DEFORMATION).shuffle();
			}

		}

	}
}
