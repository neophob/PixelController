package com.neophob.sematrix.input;

import java.util.Random;

public class SoundDummy implements SeSound {

	private Random random;
	
	public SoundDummy() {
		random = new Random();
	}
	
	/**
	 * get current volume
	 * @return
	 */
	public float getVolume() {
		return random.nextFloat();
	}

	public float getVolumeNormalized() {
		return getVolume();
	}

	public boolean isKick() {
		return random.nextBoolean();
	}

	public boolean isSnare() {
		return random.nextBoolean();
	}

	public boolean isHat() {
		return random.nextBoolean();
	}
	
	public boolean isPang() {
		return random.nextBoolean();
	}
	
	public int getFftAvg() {		
		return 1;
	}
	
	public float getFftAvg(int i) {
		return 1.0f;
	}

	public void shutdown() {
	}
	
}
