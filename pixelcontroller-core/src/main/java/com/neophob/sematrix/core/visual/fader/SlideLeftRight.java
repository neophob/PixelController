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
package com.neophob.sematrix.core.visual.fader;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.neophob.sematrix.core.visual.MatrixData;


/**
 * The Class SlideLeftRight.
 */
public class SlideLeftRight extends Fader {

	private static final Logger LOG = Logger.getLogger(SlideLeftRight.class.getName());

	/**
	 * Instantiates a new slide left right.
	 */
	public SlideLeftRight(MatrixData matrix, int fps) {
		this(matrix, DEFAULT_FADER_DURATION, fps);
	}

	/**
	 * 
	 * @param duration
	 */
	public SlideLeftRight(MatrixData matrix, int duration, int fps) {
		super(matrix, FaderName.SLIDE_LEFT_RIGHT, duration, fps);
	}

	/* (non-Javadoc)
	 * @see com.neophob.sematrix.core.fader.Fader#getBuffer(int[])
	 */
	@Override
	public int[] getBuffer(int[] visual1Buffer, int[] visual2Buffer) {
		currentStep++;	

		try {
			if (super.isDone()) {
				return visual2Buffer;
			}

			if (presetFader) {
				LOG.log(Level.SEVERE, "presetFader not supported!");
			}
			
			int[] ret = new int[visual1Buffer.length];		
			float f = getCurrentStep();
			
			int ammount=(int)(internalBufferXSize*f);
			int ofs,x,idx=0;
			
			int linesize=internalBufferXSize;
			for (int y=0; y<internalBufferYSize; y++) {
				ofs=internalBufferXSize*y;
				for (x=0; x<ammount; x++) {
					ret[idx++] = visual2Buffer[ofs+(linesize-ammount+x)];
				}
				for (x=ammount; x<internalBufferXSize; x++) {
					ret[idx++] = visual1Buffer[ofs+x];
				}
			}
			return ret;
		} catch (Exception e) {
			LOG.log(Level.SEVERE, "getBuffer failed, ignore error", e);
			super.setDone();
			return visual1Buffer;
		}

	}

}
