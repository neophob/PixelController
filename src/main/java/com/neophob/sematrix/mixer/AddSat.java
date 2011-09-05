/**
 * Copyright (C) 2011 Michael Vogt <michu@neophob.com>
 *
 * This file is part of PixelController.
 *
 * PixelController is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PixelController is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with PixelController.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.neophob.sematrix.mixer;

import com.neophob.sematrix.generator.Generator;
import com.neophob.sematrix.glue.Visual;
import com.neophob.sematrix.resize.Resize.ResizeName;

/**
 * The Class AddSat.
 */
public class AddSat extends Mixer {

	/**
	 * Instantiates a new adds the sat.
	 *
	 * @param controller the controller
	 */
	public AddSat(PixelControllerMixer controller) {
		super(controller, MixerName.ADDSAT, ResizeName.QUALITY_RESIZE);
	}

	/* (non-Javadoc)
	 * @see com.neophob.sematrix.mixer.Mixer#getBuffer(com.neophob.sematrix.glue.Visual)
	 */
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
