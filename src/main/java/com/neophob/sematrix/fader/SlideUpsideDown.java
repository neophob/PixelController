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
 * The Class SlideUpsideDown.
 */
public class SlideUpsideDown extends Fader {

	/**
	 * Instantiates a new slide upside down.
	 */
	public SlideUpsideDown() {
		super(FaderName.SLIDE_UPSIDE_DOWN, 1500);
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
			
			newBuffer = Collector.getInstance().getVisual(this.newVisual).getBuffer();

			int ammount=(int)(internalBufferYSize*f)*internalBufferXSize;
			int totalSize=internalBufferYSize*internalBufferXSize;
			for (int y=0; y<ammount; y++) {
				ret[y]=newBuffer[totalSize-ammount+y];
			}
			int idx=0;
			for (int y=ammount; y<totalSize; y++) {
				ret[y]=buffer[idx++];
			}
			return ret;
			
		} catch (Exception e) {
			super.setDone();
			return buffer;
		}

	}

}
