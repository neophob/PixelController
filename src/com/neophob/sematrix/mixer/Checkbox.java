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
import com.neophob.sematrix.glue.Collector;
import com.neophob.sematrix.glue.Visual;
import com.neophob.sematrix.resize.Resize.ResizeName;

/**
 * checkbox mixer
 * 
 * 
 * @author mvogt
 *
 */
public class Checkbox extends Mixer {

	private int pixelsPerLine;
	
	public Checkbox(PixelControllerMixer controller) {
		super(controller, MixerName.CHECKBOX, ResizeName.PIXEL_RESIZE);
		pixelsPerLine = Collector.getInstance().getMatrix().getDeviceXSize();
	}

	public int[] getBuffer(Visual visual) {
		
		if (visual.getEffect2() == null) {
			return visual.getEffect1Buffer();
		}

		Generator gen1 = visual.getGenerator1();
		int[] src1 = visual.getEffect1Buffer();
		int[] src2 = visual.getEffect2Buffer();
		int[] dst = new int [gen1.internalBuffer.length];
		int flp = gen1.getInternalBufferXSize()*pixelsPerLine;
		
		boolean flip=true;
		for (int i=0; i<src1.length; i++) {
			if (i%pixelsPerLine==0) {
				flip=!flip;
			}
			if (i%flp==0) {
				flip=!flip;
			}
			
			if (flip) {
				dst[i] = src2[i];
			} else {
				dst[i] = src1[i];
			}
			
		}

		return dst;
	}

}
