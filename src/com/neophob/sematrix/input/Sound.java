package com.neophob.sematrix.input;


public class Sound implements SeSound {

	private static Sound instance = new Sound();

	private SeSound implementation=null;
	
	private Sound() {
		
	}

	/**
	 * the setter
	 * @param implementation
	 */
	public synchronized void setImplementation(SeSound implementation) {
		this.implementation = implementation;
	}

	public static Sound getInstance() {
		return instance;
	}

	/**
	 * get current volume
	 * @return
	 */
	public float getVolume() {
		return implementation.getVolume();
	}

	public float getVolumeNormalized() {
		return implementation.getVolumeNormalized();
	}

	public boolean isKick() {
		return implementation.isKick();
	}

	public boolean isSnare() {
		return implementation.isSnare();
	}

	public boolean isHat() {
		return implementation.isHat();
	}
	
	public boolean isPang() {
		return implementation.isPang();
	}
	
	public void shutdown() {
		implementation.shutdown();
	}

	@Override
	public int getFftAvg() {		
		return implementation.getFftAvg();
	}
	
	public float getFftAvg(int i) {
		return implementation.getFftAvg(i);
	}

}
