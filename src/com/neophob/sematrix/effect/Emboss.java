package com.neophob.sematrix.effect;

import com.neophob.sematrix.resize.Resize.ResizeName;


public class Emboss extends Effect {

	public Emboss() {
		super(EffectName.EMBOSS, ResizeName.QUALITY_RESIZE);
	}

	public int[] getBuffer(int[] buffer) {
//		return BoxFilter.applyBoxFilter(8, 1, buffer, this.internalBufferXSize);
		int []a = BoxFilter.applyBoxFilter(8, 1, buffer, this.internalBufferXSize);
		a = BoxFilter.applyBoxFilter(4, 1, a, this.internalBufferXSize);
		return BoxFilter.applyBoxFilter(5, 1, a, this.internalBufferXSize);
	}
	

}
