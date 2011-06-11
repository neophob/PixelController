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

import com.neophob.sematrix.input.Sound;
import com.neophob.sematrix.resize.Resize.ResizeName;

public class Voluminize extends Effect {

	public Voluminize(PixelControllerEffect controller) {
		super(controller, EffectName.VOLUMINIZE, ResizeName.QUALITY_RESIZE);
	}

	public int[] getBuffer(int[] buffer) {
		int[] ret = new int[buffer.length];
		
		short cr,cg,cb;
		int col;
		float volume = Sound.getInstance().getVolumeNormalized();
		for (int i=0; i<buffer.length; i++){
			col = buffer[i];
    		cr=(short) (volume*((col>>16)&255));
    		cg=(short) (volume*((col>>8)&255));
    		cb=(short) (volume*( col&255));
    		
    		ret[i]= (cr << 16) | (cg << 8) | cb;
		}
		return ret;
	}
	

}
