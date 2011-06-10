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

package com.neophob.sematrix.effect;

import com.neophob.sematrix.resize.Resize.ResizeName;


public class Inverter extends Effect {

	public Inverter() {
		super(EffectName.INVERTER, ResizeName.QUALITY_RESIZE);
	}

	public int[] getBuffer(int[] buffer) {
		int[] ret = new int[buffer.length];
		
		short cr,cg,cb;//,ca;
		int col;

		for (int i=0; i<buffer.length; i++){
			col = buffer[i];
    		//ca=(short) (255-((col>>24)&255));
    		cr=(short) (255-((col>>16)&255));
    		cg=(short) (255-((col>>8)&255));
    		cb=(short) (255-( col&255));
    		
    		ret[i]= /*(ca << 24) | */(cr << 16) | (cg << 8) | cb;
		}
		return ret;
	}
	

}
