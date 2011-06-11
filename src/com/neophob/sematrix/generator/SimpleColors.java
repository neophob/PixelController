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

package com.neophob.sematrix.generator;

import com.neophob.sematrix.resize.Resize.ResizeName;


public class SimpleColors extends Generator {

	private int rotate = 0;

	public SimpleColors(PixelControllerGenerator controller) {
		super(controller, GeneratorName.SIMPLECOLORS, ResizeName.QUALITY_RESIZE);
	}
	
	@Override
	public void update() {
		int col, ofs=0;
		for (int y=0; y<this.getInternalBufferYSize(); y++) {
			for (int x=0; x<this.getInternalBufferXSize(); x++) {			
				col = (int)((4*((y+rotate)%255)) << 16) | ((2*((x+rotate)%255)) << 8)  | ((x+y+rotate>>1)%255);
				ofs = y*this.getInternalBufferXSize()+x;				
				this.internalBuffer[ofs++]=col;
			}
		}
		rotate++;
	}

	
	@Override
	public void close() {
		// TODO Auto-generated method stub

	}
}
