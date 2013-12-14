/**
 * Copyright (C) 2011-2013 Michael Vogt <michu@neophob.com>
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
package com.neophob.sematrix.core.visual.effect;

import com.neophob.sematrix.core.resize.PixelResize;
import com.neophob.sematrix.core.resize.Resize;
import com.neophob.sematrix.core.resize.Resize.ResizeName;
import com.neophob.sematrix.core.visual.MatrixData;

/**
 * rotate 90 degree effect.
 */
public class Rotate90 extends Effect {

	Resize r = new PixelResize();
	
	/**
	 * Instantiates a new inverter.
	 *
	 * @param controller the controller
	 */
	public Rotate90(MatrixData matrix) {
		super(matrix, EffectName.ROTATE90, ResizeName.QUALITY_RESIZE);
	}
	

	/* (non-Javadoc)
	 * @see com.neophob.sematrix.core.effect.Effect#getBuffer(int[])
	 */
	public int[] getBuffer(int[] buffer) {
		//easy rotating, if x and y has the same size
		if (internalBufferXSize==internalBufferYSize) {
			int[] ret = new int[buffer.length];
			int ofs=0;
			
			for (int x=0; x<internalBufferXSize; x++) {			
				for (int y=0; y<internalBufferYSize; y++) {
					ret[internalBufferXSize*y+internalBufferXSize-1-x] = buffer[ofs++];
				}
			}
			return ret;			
		}
		
		//rotate image
		int[] t1 = rotateNm(buffer);
		
		//resize output
		return r.resizeImage(t1, internalBufferYSize, internalBufferXSize, internalBufferXSize, internalBufferYSize);
    }
        	
        //src: http://www.geeksforgeeks.org/turn-an-image-by-90-degree/
        int[] rotateNm(int[] src) {
			int[] ret = new int[src.length];
			for (int y = 0; y < internalBufferYSize; y++) {
                for (int x = 0; x < internalBufferXSize; x++) {					
					ret[x * internalBufferYSize +(internalBufferYSize - y - 1)] = src[y * internalBufferXSize + x];
				}
			}
			return ret;
        }
}

