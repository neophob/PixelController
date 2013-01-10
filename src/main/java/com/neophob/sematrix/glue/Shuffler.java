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
package com.neophob.sematrix.glue;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.neophob.sematrix.effect.Effect;
import com.neophob.sematrix.effect.Effect.EffectName;
import com.neophob.sematrix.fader.Fader;
import com.neophob.sematrix.fader.PixelControllerFader;
import com.neophob.sematrix.generator.Generator;
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
		
		int sanityCheck = 1000;
		boolean done=false;
		while (!done || sanityCheck--<1) {
			int idx = rand.nextInt(col.getPresent().size());
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
	 * 
	 * TODO: do not load data files from "in use" generators and effects
	 */
	public static void manualShuffleStuff() {	
	    long start = System.currentTimeMillis();
	    
		Collector col = Collector.getInstance(); 		
		int currentVisual = col.getCurrentVisual();
		Visual visual = col.getVisual(currentVisual);
		Random rand = new Random();
		
		LOG.log(Level.INFO, "Manual Shuffle for Visual {0}", currentVisual);
		
		int totalNrGenerator = col.getPixelControllerGenerator().getSize();
		int totalNrEffect = col.getPixelControllerEffect().getSize();
		int totalNrMixer = col.getPixelControllerMixer().getSize();

		if (col.getShufflerSelect(ShufflerOffset.GENERATOR_A)) {
			//why -1 +1? the first effect is passthrough - so no effect
		    visual.setGenerator1(rand.nextInt(totalNrGenerator-1)+1);
		}

		if (col.getShufflerSelect(ShufflerOffset.GENERATOR_B)) {			
		    visual.setGenerator2(rand.nextInt(totalNrGenerator-1)+1);			
		}

		if (col.getShufflerSelect(ShufflerOffset.EFFECT_A)) {
		    visual.setEffect1(rand.nextInt(totalNrEffect));
		}

		if (col.getShufflerSelect(ShufflerOffset.EFFECT_B)) {
		    visual.setEffect2(rand.nextInt(totalNrEffect));
		}
		
		if (col.getShufflerSelect(ShufflerOffset.MIXER)) {			
			if (visual.getGenerator2Idx()==0) {
				//no 2nd generator - use passthru mixer
			    visual.setMixer(0);						
			} else {
			    visual.setMixer(rand.nextInt(totalNrMixer));						
			}
		}

        //set used to find out if visual is on screen
        Set<Integer> activeGeneratorIds = new HashSet<Integer>();
        Set<Integer> activeEffectIds = new HashSet<Integer>();
        for (OutputMapping om: col.getAllOutputMappings()) {
            Visual v = col.getVisual(om.getVisualId());
            
            if (v.equals(visual)) {
            	continue;
            }
            
            activeEffectIds.add(v.getEffect1Idx());
            activeEffectIds.add(v.getEffect2Idx());
            
            activeGeneratorIds.add(v.getGenerator1Idx());
            activeGeneratorIds.add(v.getGenerator2Idx());
        }

        //shuffle only items which are NOT visible
        for (Generator g: col.getPixelControllerGenerator().getAllGenerators()) {
			if (!activeGeneratorIds.contains(g.getId())) {
				g.shuffle();
			}
		}
        
		for (Effect e: col.getPixelControllerEffect().getAllEffects()) {
			if (!activeEffectIds.contains(e.getId())) {
				e.shuffle();
			}
		}
		
		//do not shuffle output
		/*if (col.getShufflerSelect(ShufflerOffset.OUTPUT)) {
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
		}*/
		
		if (col.getShufflerSelect(ShufflerOffset.COLORSET)) {
			int colorSets = col.getColorSets().size();
			visual.setColorSet(rand.nextInt(colorSets));	
		}

		LOG.log(Level.INFO, "Shuffle finished in {0}ms", (System.currentTimeMillis()-start));
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
		int blah = rand.nextInt(17);
		//LOG.log(Level.INFO, "Automatic Shuffler {0}", blah);

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
				col.getPixelControllerEffect().getEffect(EffectName.TEXTURE_DEFORMATION).shuffle();
			}

			if (blah == 16) {
				col.getPixelControllerGenerator().getGenerator(GeneratorName.COLOR_SCROLL).shuffle();
			}

		}

	}
}
