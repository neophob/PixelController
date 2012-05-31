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


/**
 * The Class Inverter.
 */
public class FlipX extends Effect {

	/**
	 * Instantiates a new inverter.
	 *
	 * @param controller the controller
	 */
	public FlipX(PixelControllerEffect controller) {
		super(controller, EffectName.FLIP_X, ResizeName.QUALITY_RESIZE);
	}

	/* (non-Javadoc)
	 * @see com.neophob.sematrix.effect.Effect#getBuffer(int[])
	 */
	public int[] getBuffer(int[] buffer) {
		int[] ret = new int[buffer.length];
		
		int ofs=0;
		
		for (int y=0; y<this.internalBufferYSize; y++){			
			for (int x=0; x<this.internalBufferXSize; x++){
				ret[ofs+x] = buffer[ofs+this.internalBufferXSize-x-1];			
			}
			ofs+=this.internalBufferXSize;
		}
		return ret;
	}
	

}
