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
 * The Class Mix.
 */
public class Mix extends Mixer {

	/**
	 * Instantiates a new mix.
	 *
	 * @param controller the controller
	 */
	public Mix(PixelControllerMixer controller) {
		super(controller, MixerName.MIX, ResizeName.QUALITY_RESIZE);
	}

	/* (non-Javadoc)
	 * @see com.neophob.sematrix.mixer.Mixer#getBuffer(com.neophob.sematrix.glue.Visual)
	 */
	public int[] getBuffer(Visual visual) {
		if (visual.getEffect2() == null) {
			return visual.getEffect1Buffer();
		}

		short redSource, greenSource, blueSource;
		short redDest, greenDest, blueDest;
		int col;
				
		Generator gen1 = visual.getGenerator1();		
		int[] src1 = visual.getEffect1Buffer();
		int[] src2 = visual.getEffect2Buffer();
		int[] dst = new int [gen1.internalBuffer.length];

		for (int i=0; i<gen1.internalBuffer.length; i++){
			col = src1[i];
    		redSource=(short) ((col>>16)&255);
    		greenSource=(short) ((col>>8) &255);
    		blueSource=(short) ( col     &255);
    		
			col = src2[i];
    		redDest=(short) ((col>>16)&255);
    		greenDest=(short) ((col>>8) &255);
    		blueDest=(short) ( col     &255);
    		
    		redDest = (short) ((redDest + redSource) / 2);
    		greenDest = (short) ((greenDest + greenSource) / 2);
    		blueDest = (short) ((blueDest + blueSource) / 2);
    		
    		dst[i]=(int) ((redDest << 16) | (greenDest << 8) | blueDest);
          }
	
		return dst;
	}

}
