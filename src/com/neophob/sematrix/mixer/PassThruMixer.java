package com.neophob.sematrix.mixer;

import com.neophob.sematrix.glue.Visual;
import com.neophob.sematrix.resize.Resize.ResizeName;

public class PassThruMixer extends Mixer {

	public PassThruMixer() {
		super(MixerName.PASSTHRU, ResizeName.QUALITY_RESIZE);
	}

	public int[] getBuffer(Visual visual) {
		return visual.getEffect1Buffer();
	}

}
