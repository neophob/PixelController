package com.neophob.sematrix.input;

public interface SeSound {

	public float getVolume();
	
	public float getVolumeNormalized();
	
	public boolean isKick();
	
	public boolean isSnare();
	
	public boolean isHat();
	
	public boolean isPang();

	public void shutdown();
}
