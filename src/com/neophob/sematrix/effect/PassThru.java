package com.neophob.sematrix.effect;

import com.neophob.sematrix.generator.Generator;

public class PassThru extends Effect {

	public PassThru() {
		super(EffectName.PASSTHRU);
	}

	public int[] getBuffer(Generator generator) {
		return generator.getBuffer();
	}
	

}
