package com.neophob.sematrix.glue;

import java.util.Random;

import com.neophob.sematrix.generator.Blinkenlights;
import com.neophob.sematrix.generator.Image;
import com.neophob.sematrix.generator.Generator.GeneratorName;
import com.neophob.sematrix.input.Sound;

public class Shuffler {

	private Shuffler() {
		//no instance allowed
	}
	
	public static void shuffleStuff() {
		boolean kick = Sound.getInstance().isKick();
		boolean hat = Sound.getInstance().isHat();
		boolean snare = Sound.getInstance().isSnare();
		
		if (!hat && !kick && !snare) {
			return;
		}
		
		Random rand = new Random();
		int blah = rand.nextInt(10);
		
		if (snare) {
			if (blah == 1) {
				int size = Collector.getInstance().getAllGenerators().size();
				for (Visual v: Collector.getInstance().getAllVisuals()) {
					v.setGenerator1(rand.nextInt(size-1)+1);
				}
			}

			if (blah == 2) {
				int size = Collector.getInstance().getAllGenerators().size();
				for (Visual v: Collector.getInstance().getAllVisuals()) {
					v.setGenerator2(rand.nextInt(size));
				}

			}
			
			if (blah == 3) {
				int size = Collector.getInstance().getAllEffects().size();
				for (Visual v: Collector.getInstance().getAllVisuals()) {
					v.setEffect1(rand.nextInt(size));
				}
			}

			if (blah == 4) {
				int size = Collector.getInstance().getAllEffects().size();
				for (Visual v: Collector.getInstance().getAllVisuals()) {
					v.setEffect2(rand.nextInt(size));
				}
			}
			
		}
		
		if (hat) {
			if (blah == 5) {
				int size = Collector.getInstance().getAllMixer().size();
				for (Visual v: Collector.getInstance().getAllVisuals()) {
					v.setMixer(rand.nextInt(size));
				}
			}			

			if (blah == 6) {
				int size = Collector.getInstance().getFaderCount();
				Collector.getInstance().getAllOutputMappings().get(0).setFader(Collector.getInstance().getFader(rand.nextInt(size)));					
				Collector.getInstance().getAllOutputMappings().get(1).setFader(Collector.getInstance().getFader(rand.nextInt(size)));		
			}			
		}
		
		
		if (kick) {
			if (blah == 7) {
				int size = Collector.getInstance().getAllVisuals().size();
				Collector.getInstance().getAllOutputMappings().get(0).getFader().startFade(rand.nextInt(size), 0);
				Collector.getInstance().getAllOutputMappings().get(1).getFader().startFade(rand.nextInt(size), 1);
			}

			if (blah == 8) {
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
				Image img = (Image)Collector.getInstance().getGenerator(GeneratorName.IMAGE);
				img.loadFile(fileToLoad);
			}
			
			if (blah == 9) {
				int nr = rand.nextInt(4);
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
				Blinkenlights blink = (Blinkenlights)Collector.getInstance().getGenerator(GeneratorName.BLINKENLIGHTS);
				blink.loadFile(fileToLoad);
			}
	
		}
		
	}
}
