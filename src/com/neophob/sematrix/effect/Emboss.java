package com.neophob.sematrix.effect;

import com.neophob.sematrix.resize.Resize.ResizeName;


public class Emboss extends Effect {

	public Emboss() {
		super(EffectName.EMBOSS, ResizeName.QUALITY_RESIZE);
	}

	public int[] getBuffer(int[] buffer) {
		return BoxFilter.applyBoxFilter(6, 1, buffer, this.internalBufferXSize);
	}
	

}
