package com.neophob.sematrix.mixer;

import com.neophob.sematrix.generator.Generator;
import com.neophob.sematrix.glue.Visual;

public class AddSat extends Mixer {

	public AddSat() {
		super(MixerName.ADDSAT);
	}

	public int[] getBuffer(Visual visual) {
		
		if (visual.getEffect2() == null) {
			return visual.getEffect1Buffer();
		}

		Generator gen1 = visual.getGenerator1();
		int[] src1 = visual.getEffect1Buffer();
		int[] src2 = visual.getEffect2Buffer();
		int[] dst = new int [gen1.internalBuffer.length];
		short r,g,b,a;
		int col_s, col_d;
		
		for (int i=0; i<src1.length; i++) {
			col_s = src1[i];
			a = (short) ((col_s>>24) & 255);
			r = (short) ((col_s>>16) & 255);
			g = (short) ((col_s>>8)  & 255);
			b = (short) ( col_s      & 255);
			col_d = src2[i];
			a += (short) ((col_d>>24) & 255);
			r += (short) ((col_d>>16) & 255);
			g += (short) ((col_d>>8)  & 255);
			b += (short) ( col_d      & 255);

			if (a > 255) a = 255;
			if (r > 255) r = 255;
			if (g > 255) g = 255;
			if (b > 255) b = 255;
			
			dst[i] = (int)(a << 24) | (r << 16) | (g << 8) | b;
		}

		return dst;
	}

}
