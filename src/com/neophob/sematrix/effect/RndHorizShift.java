package com.neophob.sematrix.effect;

import com.neophob.sematrix.generator.Generator;

public class RndHorizShift extends HorizShift {

	int ammount=0;
	
	public RndHorizShift() {
		super(EffectName.RND_HORIZONTAL_SHIFT);
	}

	public int[] getBuffer(Generator generator) {
		if (Math.random()>0.9f) {
			ammount = (int)(Math.random()*generator.getInternalBufferXSize());
		}
		
		return super.doHorizShift(generator, ammount);
	}
	

}
