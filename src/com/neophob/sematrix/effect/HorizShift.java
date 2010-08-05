package com.neophob.sematrix.effect;

import com.neophob.sematrix.generator.Generator;

public class HorizShift extends Effect {

	public HorizShift(EffectName effectName) {
		super(effectName);
	}

	protected int[] doHorizShift(Generator generator, int ammount) {
		int[] buffer = generator.getBuffer();
		int[] ret = new int[buffer.length];

		int x,idx=0,ofs;
		for (int y=0; y<generator.getInternalBufferYSize(); y++) {
			ofs=generator.getInternalBufferXSize()*y;
			for (x=ammount; x<generator.getInternalBufferXSize(); x++) {
				ret[idx++] = buffer[ofs+x];
			}
			for (x=0; x<ammount; x++) {
				ret[idx++] = buffer[ofs+x];
			}
		}
		return ret;
	}
	
	public int[] getBuffer(int[] buffer) {
		//subclass needs to implement this!
		return null;
	}
	

}
