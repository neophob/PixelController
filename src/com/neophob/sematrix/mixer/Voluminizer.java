package com.neophob.sematrix.mixer;

import com.neophob.sematrix.glue.Visual;

/**
 * mix src/dst accoring to volume of sound!
 * 
 * @author michu
 *
 */
public class Voluminizer extends Mixer {

	public Voluminizer() {
		super(MixerName.NEGATIVE_MULTIPLY);
	}

	public int[] getBuffer(Visual visual) {
		return null;
	}

}
