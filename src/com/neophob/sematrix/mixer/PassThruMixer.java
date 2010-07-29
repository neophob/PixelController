package com.neophob.sematrix.mixer;

import com.neophob.sematrix.glue.Visual;

public class PassThruMixer extends Mixer {

	public PassThruMixer() {
		super(MixerName.PASSTHRU);
	}

	public int[] getBuffer(Visual visual) {
		return visual.getEffect1Buffer();
	}

}
