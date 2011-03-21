package com.neophob.sematrix.fader;


/**
 * crossfader
 * 
 * @author michu
 *
 */
public class Crossfader extends Fader {

	/**
	 * 
	 */
	public Crossfader() {
		super(FaderName.CROSSFADE, 2500);
	}

	/**
	 * 
	 * @param time
	 */
	public Crossfader(int time) {
		super(FaderName.CROSSFADE, time);
	}

	@Override
	public int[] getBuffer(int[] buffer) {
		currentStep++;		
		
		try {
			int[] newBuffer = getNewBuffer();
			if (super.isDone()) {
				return newBuffer;
			}
			float f = getCurrentStep();
			
			return CrossfaderHelper.getBuffer(f, buffer, newBuffer);			
		} catch (Exception e) {
			super.setDone();
			return buffer;
		}
	}

}
