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

import java.util.Random;

import processing.core.PConstants;

import com.neophob.sematrix.resize.Resize.ResizeName;

/**
 * @author mvogt
 * ripped from http://demo-effects.cvs.sourceforge.net/viewvc/demo-effects/demo-effects/FIRE/fire.c?revision=1.5&content-type=text%2Fplain
 */
public class Fire extends Generator implements PConstants {

	/* paletter */
	private int[] colors;
	
	private Random r;
	
	/* fire buffer, contains 0..255 */
	private int[] buffer;

	public Fire() {
		super(GeneratorName.FIRE, ResizeName.QUALITY_RESIZE);

		//Setup palette
		colors = new int[256];
		buffer = new int[internalBufferXSize*internalBufferYSize];
		for (int i = 0; i < 32; ++i) {
			/* black to blue, 32 values*/
			colors[i]=getColor(0, 0, i << 1);

			/* blue to red, 32 values*/
			colors[i + 32]=getColor(i << 3, 0, 64 - (i << 1));

			/*red to yellow, 32 values*/
			colors[i + 64]=getColor(255, i << 3, 0);

			/* yellow to white, 162 */
			colors[i + 96]=getColor(255, 255, i << 2);
			colors[i + 128]=getColor(255, 255, 64+(i << 2));
			colors[i + 160]=getColor(255, 255, 128+(i << 2));
			colors[i + 192]=getColor(255, 255, 192+i);
			colors[i + 224]=getColor(255, 255, 224+i);
		} 

		r = new Random();
	}

	private int getColor(int r, int g, int b) {
		return (r << 16) | (g << 8) | (b);
	}

	@Override
	public void update() {
		int j = this.getInternalBufferXSize() * (this.getInternalBufferYSize()- 1);

		int random;
		for (int i = 0; i < this.getInternalBufferXSize(); i++) {
			random = r.nextInt(16);
			/* the lower the value, the intense the fire, compensate a lower value with a higher decay value*/
			if (random > 8) {
				/*maximum heat*/
				this.buffer[j + i] = 255; 
			} else {
				this.buffer[j + i] = 0;
			}
		}  

		/* move fire upwards, start at bottom*/
		int temp;
		for (int index = 0; index < internalBufferYSize-1; index++) {
			for (int i = 0; i < internalBufferXSize; i++) {
				if (i == 0) {
					/* at the left border*/
					temp = buffer[j];
					temp += buffer[j + 1];
					temp += buffer[j - internalBufferXSize];
					temp /=3;
				} else 
					if (i == this.getInternalBufferXSize() - 1) {
						/* at the right border*/
						temp = buffer[j + i];
						temp += buffer[j - internalBufferXSize + i];
						temp += buffer[j + i - 1];
						temp /= 3;
					} else {
						temp = buffer[j + i];
						temp += buffer[j + i + 1];
						temp += buffer[j + i - 1];
						temp += buffer[j - internalBufferXSize + i];
						temp >>= 2;
					}
				if (temp > 1) {
					/* decay */
					temp --; 
				}
				this.buffer[j - internalBufferXSize + i] = temp;
				this.internalBuffer[j - internalBufferXSize + i] = colors[temp];
			}
			j -= this.getInternalBufferXSize();
		}      

	}

	@Override
	public void close() {
	}
}
