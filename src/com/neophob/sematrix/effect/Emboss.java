package com.neophob.sematrix.effect;


public class Emboss extends Effect {

	public Emboss() {
		super(EffectName.EMBOSS);
	}

	public int[] getBuffer(int[] buffer) {
		return BoxFilter.applyBoxFilter(6, 1, buffer, this.internalBufferXSize);
	}
	

}
