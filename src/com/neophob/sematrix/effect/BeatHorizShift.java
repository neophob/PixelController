package com.neophob.sematrix.effect;

import com.neophob.sematrix.generator.Generator;
import com.neophob.sematrix.input.Sound;

public class BeatHorizShift extends HorizShift {

	int ammount=0;
	
	public BeatHorizShift() {
		super(EffectName.RND_HORIZONTAL_SHIFT);
	}

	public int[] getBuffer(Generator generator) {
		if (Sound.getInstance().isPang()) {
			ammount = (int)(Math.random()*generator.getInternalBufferXSize());
		}
		
		return super.doHorizShift(generator, ammount);
	}
	

}
