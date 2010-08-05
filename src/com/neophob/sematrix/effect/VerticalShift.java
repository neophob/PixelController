package com.neophob.sematrix.effect;

import com.neophob.sematrix.generator.Generator;

public class VerticalShift extends Effect {

	public VerticalShift(EffectName effectName) {
		super(effectName);
	}

	/**
	 * shift a image buffer vertical
	 * @param ammount
	 * @param generator
	 * @return
	 */
	protected int[] doVerticalShift(Generator generator, int ammount) {
		int[] buffer = generator.getBuffer();
		int[] ret = new int[buffer.length];

		int idx=0;
		int ofs = ammount*generator.getInternalBufferXSize();
		for (int i=ofs; i<buffer.length; i++) {
			ret[idx++] = buffer[i];	
		}
		for (int i=0; i<ofs; i++) {
			ret[idx++] = buffer[i];
		}
		
		return ret;
	}

	public int[] getBuffer(int[] buffer) {
		//subclass needs to implement this!
		return null;
	}
	

}
