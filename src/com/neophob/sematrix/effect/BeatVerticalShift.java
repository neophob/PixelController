package com.neophob.sematrix.effect;

import com.neophob.sematrix.input.Sound;

public class BeatVerticalShift extends Effect {

	int ammount=0;
	
	public BeatVerticalShift() {
		super(EffectName.BEAT_VERTICAL_SHIFT);
	}

	public int[] getBuffer(int[] buffer) {
		if (Sound.getInstance().isPang()) {
			ammount = (int)(Sound.getInstance().getVolumeNormalized()*internalBufferYSize);
		}
		return doVerticalShift(buffer, ammount);
	}
	
	/**
	 * shift a image buffer vertical
	 * @param ammount
	 * @param generator
	 * @return
	 */
	private int[] doVerticalShift(int[] buffer, int ammount) {
		int[] ret = new int[buffer.length];

		int idx=0;
		int ofs = ammount*internalBufferXSize;
		for (int i=ofs; i<buffer.length; i++) {
			ret[idx++] = buffer[i];	
		}
		for (int i=0; i<ofs; i++) {
			ret[idx++] = buffer[i];
		}
		
		return ret;
	}

}
