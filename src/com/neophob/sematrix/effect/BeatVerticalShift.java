package com.neophob.sematrix.effect;

import com.neophob.sematrix.generator.Generator;
import com.neophob.sematrix.input.Sound;

public class BeatVerticalShift extends VerticalShift {

	int ammount=0;
	
	public BeatVerticalShift() {
		super(EffectName.RND_VERTICAL_SHIFT);
	}

	public int[] getBuffer(Generator generator) {
		if (Sound.getInstance().isPang()) {
			ammount = (int)(Math.random()*generator.getInternalBufferYSize());
		}
		return super.doVerticalShift(generator, ammount);
	}
	

}
