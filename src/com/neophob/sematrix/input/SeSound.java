package com.neophob.sematrix.input;

/**
 * 
 * @author michu
 *
 */
public interface SeSound {

	float getVolume();
	
	float getVolumeNormalized();
	
	boolean isKick();
	
	boolean isSnare();
	
	boolean isHat();
	
	boolean isPang();

	void shutdown();
}
