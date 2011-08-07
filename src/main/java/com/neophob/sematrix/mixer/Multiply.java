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

public class Multiply extends Mixer {

	public Multiply(PixelControllerMixer controller) {
		super(controller, MixerName.MULTIPLY, ResizeName.QUALITY_RESIZE);
	}

	public int[] getBuffer(Visual visual) {
		short cr_s,cg_s,cb_s;
		short cr_d,cg_d,cb_d;
		int col;
		
		if (visual.getEffect2() == null) {
			return visual.getEffect1Buffer();
		}
		
		Generator gen1 = visual.getGenerator1();		
		int[] src1 = visual.getEffect1Buffer();
		int[] src2 = visual.getEffect2Buffer();
		int[] dst = new int [gen1.internalBuffer.length];

		for (int i=0; i<gen1.internalBuffer.length; i++){
			col = src1[i];
    		cr_s=(short) ((col>>16)&255);
    		cg_s=(short) ((col>>8) &255);
    		cb_s=(short) ( col     &255);
    		
			col = src2[i];
    		cr_d=(short) ((col>>16)&255);
    		cg_d=(short) ((col>>8) &255);
    		cb_d=(short) ( col     &255);
    		
    		cr_d = (short) (cr_d * cr_s / 255);
    		cg_d = (short) (cg_d * cg_s / 255);
    		cb_d = (short) (cb_d * cb_s / 255);
    		
    		dst[i]=(int) ((cr_d << 16) | (cg_d << 8) | cb_d);
          }
	
		return dst;
	}

}
