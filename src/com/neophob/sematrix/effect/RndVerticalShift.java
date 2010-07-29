package com.neophob.sematrix.effect;

import com.neophob.sematrix.generator.Generator;

public class RndVerticalShift extends VerticalShift {

	int ammount=0;
	
	public RndVerticalShift() {
		super(EffectName.RND_VERTICAL_SHIFT);
	}

	public int[] getBuffer(Generator generator) {
		if (Math.random()>0.9f) {
			ammount = (int)(Math.random()*generator.getInternalBufferYSize());
		}
		return super.doVerticalShift(generator, ammount);
	}
	

}
