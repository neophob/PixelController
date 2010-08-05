package com.neophob.sematrix.effect;

import com.neophob.sematrix.input.Sound;

public class BeatHorizShift extends Effect {

	int ammount=0;
	
	public BeatHorizShift() {
		super(EffectName.BEAT_HORIZONTAL_SHIFT);
	}

	public int[] getBuffer(int[] buffer) {
		if (Sound.getInstance().isPang()) {
			ammount = (int)(Math.random()*internalBufferXSize);
		}
		
		return doHorizShift(buffer, ammount);
	}
	
	private int[] doHorizShift(int[] buffer, int ammount) {
		int[] ret = new int[buffer.length];

		int x,idx=0,ofs;
		for (int y=0; y<internalBufferYSize; y++) {
			ofs=internalBufferXSize*y;
			for (x=ammount; x<internalBufferXSize; x++) {
				ret[idx++] = buffer[ofs+x];
			}
			for (x=0; x<ammount; x++) {
				ret[idx++] = buffer[ofs+x];
			}
		}
		return ret;
	}


}
