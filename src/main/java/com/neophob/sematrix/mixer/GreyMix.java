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

import com.neophob.sematrix.color.ColorSet;
import com.neophob.sematrix.generator.Generator;
import com.neophob.sematrix.glue.Collector;
import com.neophob.sematrix.glue.Visual;
import com.neophob.sematrix.resize.Resize.ResizeName;

/**
 * convert rgb values as greyscale
 */
public class GreyMix extends Mixer {

	/**
	 * Instantiates a new xor.
	 *
	 * @param controller the controller
	 */
	public GreyMix(PixelControllerMixer controller) {
		super(controller, MixerName.GREYMIX, ResizeName.QUALITY_RESIZE);
	}

	/* (non-Javadoc)
	 * @see com.neophob.sematrix.mixer.Mixer#getBuffer(com.neophob.sematrix.glue.Visual)
	 */
	public int[] getBuffer(Visual visual) {		
		if (visual.getEffect2() == null) {
			return visual.getEffect1Buffer();
		}
		
		short r,g,b;
		int rgbColor;

		Generator gen1 = visual.getGenerator1();		
		int[] src1 = visual.getEffect1Buffer();
		int[] src2 = visual.getEffect2Buffer();
		int[] dst = new int [gen1.internalBuffer.length];

		ColorSet cs = Collector.getInstance().getActiveColorSet();
		
		for (int i=0; i<gen1.internalBuffer.length; i++){	
			rgbColor = src1[i];
			r = (short) ((rgbColor>>16) & 255);
			g = (short) ((rgbColor>>8)  & 255);
			b = (short) ( rgbColor      & 255);
			int val = (int)(r*0.3f+g*0.59f+b*0.11f);

			rgbColor = src2[i];
			r = (short) ((rgbColor>>16) & 255);
			g = (short) ((rgbColor>>8)  & 255);
			b = (short) ( rgbColor      & 255);
			val += (int)(r*0.3f+g*0.59f+b*0.11f);
			
			val/=2;
    		dst[i]=cs.getSmoothColor(val);
          }
	
		return dst;
	}

}
