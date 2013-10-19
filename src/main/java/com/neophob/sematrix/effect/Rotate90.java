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
package com.neophob.sematrix.effect;

import com.neophob.sematrix.resize.Resize.ResizeName;


/**
 * rotate 90 degree effect.
 */
public class Rotate90 extends Effect {

	/**
	 * Instantiates a new inverter.
	 *
	 * @param controller the controller
	 */
	public Rotate90(PixelControllerEffect controller) {
		super(controller, EffectName.ROTATE90, ResizeName.QUALITY_RESIZE);
	}
	

	/* (non-Javadoc)
	 * @see com.neophob.sematrix.effect.Effect#getBuffer(int[])
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
		

		return this.rotoZoom(1f, buffer);					
	}
	
	/**
	 * Simplified RotoZoom effect
	 * 
	 * @param scaleP
	 * @param bufferSrc
	 * @return
	 */
	private int[] rotoZoom(float scaleP, int bufferSrc[]) {
		int[] tmp = new int[bufferSrc.length];
		int offs=0,soffs;
		float tx,ty;

        float sa=(float)(scaleP*1);		
		float txx=0-(internalBufferXSize/2.0f)*sa;

		for (int y=0; y<internalBufferYSize; y++) {		    
	        txx-=sa;
			
			ty=0;
			tx=txx;
			for (int x=0; x<internalBufferXSize; x++) {
				ty+=sa;				
				soffs = Math.abs((int)(tx)+(int)(ty)*internalBufferXSize);
			    tmp[offs++] = bufferSrc[soffs%(bufferSrc.length-1)];    			    
			}
		}

		return tmp;
	}
}
