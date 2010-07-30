package com.neophob.sematrix.glue;

import java.util.Random;

public class Shuffler {

	private Shuffler() {
		//no instance allowed
	}
	
	public static void shuffleStuff() {
		Random rand = new Random();
		int blah = rand.nextInt(50);
		
		if (blah == 22) {
			Collector.getInstance().getVisual(0).setGenerator1(rand.nextInt(7));
			Collector.getInstance().getVisual(1).setGenerator1(rand.nextInt(7));
			Collector.getInstance().getVisual(2).setGenerator1(rand.nextInt(7));
			Collector.getInstance().getVisual(3).setGenerator1(rand.nextInt(7));
			Collector.getInstance().getVisual(4).setGenerator1(rand.nextInt(7));				
		}

		if (blah == 24) {
			Collector.getInstance().getVisual(0).setGenerator2(rand.nextInt(7));
			Collector.getInstance().getVisual(1).setGenerator2(rand.nextInt(7));
			Collector.getInstance().getVisual(2).setGenerator2(rand.nextInt(7));
			Collector.getInstance().getVisual(3).setGenerator2(rand.nextInt(7));
			Collector.getInstance().getVisual(4).setGenerator2(rand.nextInt(7));
		}
		
		if (blah == 44) {
			Collector.getInstance().getVisual(0).setEffect1(rand.nextInt(5));
			Collector.getInstance().getVisual(1).setEffect1(rand.nextInt(5));
			Collector.getInstance().getVisual(2).setEffect1(rand.nextInt(5));
			Collector.getInstance().getVisual(3).setEffect1(rand.nextInt(5));
			Collector.getInstance().getVisual(4).setEffect1(rand.nextInt(5));
		}

		if (blah == 4) {
			Collector.getInstance().getVisual(0).setEffect2(rand.nextInt(5));
			Collector.getInstance().getVisual(1).setEffect2(rand.nextInt(5));
			Collector.getInstance().getVisual(2).setEffect2(rand.nextInt(5));
			Collector.getInstance().getVisual(3).setEffect2(rand.nextInt(5));
			Collector.getInstance().getVisual(4).setEffect2(rand.nextInt(5));
		}
		
		if (blah == 9) {
			Collector.getInstance().getVisual(0).setMixer(rand.nextInt(4));
			Collector.getInstance().getVisual(1).setMixer(rand.nextInt(4));
			Collector.getInstance().getVisual(2).setMixer(rand.nextInt(4));
			Collector.getInstance().getVisual(3).setMixer(rand.nextInt(4));
			Collector.getInstance().getVisual(4).setMixer(rand.nextInt(4));
		}
		
		if (blah == 11) {
			Collector.getInstance().getAllOutputMappings().get(0).setFader(Collector.getInstance().getFader(rand.nextInt(4)));					
			Collector.getInstance().getAllOutputMappings().get(1).setFader(Collector.getInstance().getFader(rand.nextInt(4)));		
		}
		
		if (blah == 19) {
			Collector.getInstance().getAllOutputMappings().get(0).getFader().startFade(rand.nextInt(5), 0);
			Collector.getInstance().getAllOutputMappings().get(1).getFader().startFade(rand.nextInt(5), 1);
		}

	}
}
