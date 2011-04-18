package com.neophob.sematrix.glue;

import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.neophob.sematrix.effect.RotoZoom;
import com.neophob.sematrix.effect.Threshold;
import com.neophob.sematrix.effect.Tint;
import com.neophob.sematrix.effect.Effect.EffectName;
import com.neophob.sematrix.fader.Fader;
import com.neophob.sematrix.generator.Blinkenlights;
import com.neophob.sematrix.generator.Image;
import com.neophob.sematrix.generator.TextureDeformation;
import com.neophob.sematrix.generator.Generator.GeneratorName;
import com.neophob.sematrix.input.Sound;

/**
 * create random settings 
 * @author michu
 *
 */
public class Shuffler {

	private static Logger log = Logger.getLogger(Shuffler.class.getName());
	
	/**
	 * 
	 */
	private Shuffler() {
		//no instance allowed
	}

	/**
	 * heavy shuffler!
	 * used by manual RANDOMIZE 
	 */
	public static void manualShuffleStuff() {
		Collector col = Collector.getInstance(); 

		Random rand = new Random();

		if (col.getShufflerSelect(ShufflerOffset.GENERATOR_A)) {
			int size = col.getAllGenerators().size();
			for (Visual v: col.getAllVisuals()) {
				v.setGenerator1(rand.nextInt(size-1)+1);
			}
		}

		if (col.getShufflerSelect(ShufflerOffset.GENERATOR_B)) {
			int size = col.getAllGenerators().size();
			for (Visual v: col.getAllVisuals()) {
				v.setGenerator2(rand.nextInt(size));
			}

		}

		if (col.getShufflerSelect(ShufflerOffset.EFFECT_A)) {
			int size = col.getAllEffects().size();
			for (Visual v: col.getAllVisuals()) {
				v.setEffect1(rand.nextInt(size));
			}
		}

		if (col.getShufflerSelect(ShufflerOffset.EFFECT_B)) {
			int size = col.getAllEffects().size();
			for (Visual v: col.getAllVisuals()) {
				v.setEffect2(rand.nextInt(size));
			}
		}

		if (col.getShufflerSelect(ShufflerOffset.THRESHOLD_VALUE)) {
			int size = rand.nextInt()%255;
			col.setThresholdValue(size);
		}

		if (col.getShufflerSelect(ShufflerOffset.ROTOZOOMER)) {
			int angle = rand.nextInt()%255;
			col.setRotoZoomAngle(angle-127);
		} 

		if (col.getShufflerSelect(ShufflerOffset.MIXER)) {
			int size = col.getAllMixer().size();
			for (Visual v: col.getAllVisuals()) {
				if (v.getGenerator2Idx()==0) {
					//no 2nd generator - use passthru mixer
					v.setMixer(0);						
				} else {
					v.setMixer(rand.nextInt(size));						
				}
			}
		}

		if (col.getShufflerSelect(ShufflerOffset.BLINKEN)) {
			int nr = rand.nextInt(5);
			String fileToLoad="";
			switch (nr) {
			case 0:
				fileToLoad="torus.bml";
				break;
			case 1:
				fileToLoad="bnf_auge.bml";
				break;
			case 2:
				fileToLoad="bb-frogskin2.bml";
				break;
			case 3:
				fileToLoad="bb-rauten2.bml";
				break;
			case 4:
				fileToLoad="bb-spiral2fast.bml";
				break;
			}
			Blinkenlights blink = (Blinkenlights)col.getGenerator(GeneratorName.BLINKENLIGHTS);
			blink.loadFile(fileToLoad);
		}

		if (col.getShufflerSelect(ShufflerOffset.THRESHOLD_VALUE)) {
			col.setThresholdValue(rand.nextInt(255));
		}

		if (col.getShufflerSelect(ShufflerOffset.TINT)) {
			col.setRGB(
					rand.nextInt(255),
					rand.nextInt(255),
					rand.nextInt(255)
			);
		}
			
		if (col.getShufflerSelect(ShufflerOffset.TEXTURE_DEFORMATION)) {
			TextureDeformation df = (TextureDeformation)col.getGenerator(GeneratorName.TEXTURE_DEFORMATION);
			df.changeLUT(rand.nextInt(12));

			int nr = rand.nextInt(5);
			String fileToLoad="";
			switch (nr) {
			case 0:
				fileToLoad="1316.jpg";
				break;
			case 1:
				fileToLoad="ceiling.jpg";
				break;
			case 2:
				fileToLoad="circle.jpg";
				break;
			case 3:
				fileToLoad="gradient.jpg";
				break;
			case 4:
				fileToLoad="check.jpg";
				break;
			}
			df.loadFile(fileToLoad);
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
	 * used for randomized mode, rarely change stuff
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

		if (snare) {
			if (blah == 1 && col.getShufflerSelect(ShufflerOffset.GENERATOR_A)) {
				int size = col.getAllGenerators().size();
				for (Visual v: col.getAllVisuals()) {
					v.setGenerator1(rand.nextInt(size-1)+1);
				}
			}

			if (blah == 2 && col.getShufflerSelect(ShufflerOffset.GENERATOR_B)) {
				int size = col.getAllGenerators().size();
				for (Visual v: col.getAllVisuals()) {
					v.setGenerator2(rand.nextInt(size));
				}

			}

			if (blah == 3 && col.getShufflerSelect(ShufflerOffset.EFFECT_A)) {
				int size = col.getAllEffects().size();
				for (Visual v: col.getAllVisuals()) {
					v.setEffect1(rand.nextInt(size));
				}
			}

			if (blah == 4 && col.getShufflerSelect(ShufflerOffset.EFFECT_B)) {
				int size = col.getAllEffects().size();
				for (Visual v: col.getAllVisuals()) {
					v.setEffect2(rand.nextInt(size));
				}
			}

			if (blah == 14 && col.getShufflerSelect(ShufflerOffset.THRESHOLD_VALUE)) {
				int size = rand.nextInt()%255;
				Threshold t = (Threshold)col.getEffect(EffectName.THRESHOLD);
				t.setThreshold(size);
			}

		}

		if (hat) {
			if (blah == 5 && col.getShufflerSelect(ShufflerOffset.MIXER)) {
				int size = col.getAllMixer().size();
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
				int size = col.getFaderCount();
				for (OutputMapping om: col.getAllOutputMappings()) {
					Fader f=om.getFader();
					if (!f.isStarted()) {
						om.setFader(col.getFader(rand.nextInt(size)));	
					}
				}
			}

			if (blah == 11 && col.getShufflerSelect(ShufflerOffset.TINT)) {
				int r = rand.nextInt(256);
				int g = rand.nextInt(256);
				int b = rand.nextInt(256);
				Tint t = (Tint)col.getEffect(EffectName.TINT);
				t.setColor(r, g, b);
			}
			
	
			if (blah == 15 && col.getShufflerSelect(ShufflerOffset.ROTOZOOMER)) {
				int angle = rand.nextInt()%255;
				RotoZoom r = (RotoZoom)col.getEffect(EffectName.ROTOZOOM);
				r.setAngle(angle-128);
				
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

			if (blah == 8 && col.getShufflerSelect(ShufflerOffset.IMAGE)) {
				int nr = rand.nextInt(4);
				String fileToLoad="";
				switch (nr) {
				case 0:
					fileToLoad="circle.jpg";
					break;
				case 1:
					fileToLoad="half.jpg";
					break;
				case 2:
					fileToLoad="gradient.jpg";
					break;
				case 3:
					fileToLoad="check.jpg";
					break;
				}
				Image img = (Image)col.getGenerator(GeneratorName.IMAGE);
				img.loadFile(fileToLoad);
			}

			if (blah == 9 && col.getShufflerSelect(ShufflerOffset.BLINKEN)) {
				int nr = rand.nextInt(5);
				String fileToLoad="";
				switch (nr) {
				case 0:
					fileToLoad="torus.bml";
					break;
				case 1:
					fileToLoad="bnf_auge.bml";
					break;
				case 2:
					fileToLoad="bb-frogskin2.bml";
					break;
				case 3:
					fileToLoad="bb-rauten2.bml";
					break;
				case 4:
					fileToLoad="bb-spiral2fast.bml";
					break;
				}
				Blinkenlights blink = (Blinkenlights)col.getGenerator(GeneratorName.BLINKENLIGHTS);
				blink.loadFile(fileToLoad);
			}

			if (blah == 12 && col.getShufflerSelect(ShufflerOffset.TEXTURE_DEFORMATION)) {
				TextureDeformation df = (TextureDeformation)col.getGenerator(GeneratorName.TEXTURE_DEFORMATION);
				df.changeLUT(rand.nextInt(12));
			}

			if (blah == 13 && col.getShufflerSelect(ShufflerOffset.TEXTURE_DEFORMATION)) {
				TextureDeformation df = (TextureDeformation)col.getGenerator(GeneratorName.TEXTURE_DEFORMATION);
				int nr = rand.nextInt(5);
				String fileToLoad="";
				switch (nr) {
				case 0:
					fileToLoad="1316.jpg";
					break;
				case 1:
					fileToLoad="ceiling.jpg";
					break;
				case 2:
					fileToLoad="circle.jpg";
					break;
				case 3:
					fileToLoad="gradient.jpg";
					break;
				case 4:
					fileToLoad="check.jpg";
					break;
				}
				df.loadFile(fileToLoad);
			}

		}

	}
}
