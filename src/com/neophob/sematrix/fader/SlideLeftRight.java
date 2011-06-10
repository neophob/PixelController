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

package com.neophob.sematrix.fader;

import com.neophob.sematrix.generator.Generator;
import com.neophob.sematrix.glue.Collector;

public class SlideLeftRight extends Fader {

	public SlideLeftRight() {
		super(FaderName.SLIDE_LEFT_RIGHT, 1500);
	}

	@Override
	public int[] getBuffer(int[] buffer) {
		currentStep++;	

		try {
			int[] newBuffer = getNewBuffer();
			if (super.isDone()) {
				return newBuffer;
			}

			int[] ret = new int[buffer.length];		
			float f = getCurrentStep();
			Generator g = Collector.getInstance().getGenerator(0);
			int ammount=(int)(g.getInternalBufferXSize()*f);
			int ofs,x,idx=0;

			int linesize=g.getInternalBufferXSize();
			for (int y=0; y<g.getInternalBufferYSize(); y++) {
				ofs=g.getInternalBufferXSize()*y;
				for (x=0; x<ammount; x++) {
					ret[idx++] = newBuffer[ofs+(linesize-ammount+x)];
				}
				for (x=ammount; x<g.getInternalBufferXSize(); x++) {
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
