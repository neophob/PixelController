package com.neophob.sematrix.effect;

import com.neophob.sematrix.generator.Generator;
import com.neophob.sematrix.input.Sound;

public class Voluminize extends Effect {

	public Voluminize() {
		super(EffectName.VOLUMINIZE);
	}

	public int[] getBuffer(Generator generator) {
		int[] buffer = generator.getBuffer();
		int[] ret = new int[buffer.length];
		
		short cr,cg,cb;
		int col;
		float volume = Sound.getInstance().getVolume();
		for (int i=0; i<buffer.length; i++){
			col = buffer[i];
    		cr=(short) (volume*((col>>16)&255));
    		cg=(short) (volume*((col>>8)&255));
    		cb=(short) (volume*( col&255));
    		
    		ret[i]= (cr << 16) | (cg << 8) | cb;
		}
		return ret;
	}
	

}
