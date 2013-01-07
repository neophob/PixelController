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
package com.neophob.sematrix.fader;

import com.neophob.sematrix.glue.Collector;


/**
 * The Class SlideLeftRight.
 */
public class SlideLeftRight extends Fader {

	/**
	 * Instantiates a new slide left right.
	 */
	public SlideLeftRight() {
		super(FaderName.SLIDE_LEFT_RIGHT, 1500);
	}

	/* (non-Javadoc)
	 * @see com.neophob.sematrix.fader.Fader#getBuffer(int[])
	 */
	@Override
	public int[] getBuffer(int[] buffer) {
		currentStep++;	

		try {
			if (super.isDone()) {
				return newBuffer;
			}

			int[] ret = new int[buffer.length];		
			float f = getCurrentStep();
			
			int ammount=(int)(internalBufferXSize*f);
			int ofs,x,idx=0;
			
			newBuffer = Collector.getInstance().getVisual(this.newVisual).getBuffer();

			int linesize=internalBufferXSize;
			for (int y=0; y<internalBufferYSize; y++) {
				ofs=internalBufferXSize*y;
				for (x=0; x<ammount; x++) {
					ret[idx++] = newBuffer[ofs+(linesize-ammount+x)];
				}
				for (x=ammount; x<internalBufferXSize; x++) {
					ret[idx++] = buffer[ofs+x];
				}
			}
			return ret;
		} catch (Exception e) {
			super.setDone();
			return buffer;
		}

	}

}
