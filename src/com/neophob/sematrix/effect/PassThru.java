package com.neophob.sematrix.effect;


public class PassThru extends Effect {

	public PassThru() {
		super(EffectName.PASSTHRU);
	}

	public int[] getBuffer(int[] buffer) {
		return buffer;
	}
	

}
