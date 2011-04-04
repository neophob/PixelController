package com.neophob.sematrix.effect;

import com.neophob.sematrix.resize.Resize.ResizeName;


public class PassThru extends Effect {

	public PassThru() {
		super(EffectName.PASSTHRU, ResizeName.QUALITY_RESIZE);
	}

	public int[] getBuffer(int[] buffer) {
		return buffer;
	}
	

}
