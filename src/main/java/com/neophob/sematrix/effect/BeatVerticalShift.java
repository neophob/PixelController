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

import com.neophob.sematrix.glue.MatrixData;
import com.neophob.sematrix.input.Sound;
import com.neophob.sematrix.resize.Resize.ResizeName;

/**
 * The Class BeatVerticalShift.
 */
public class BeatVerticalShift extends Effect {

	/** The ammount. */
	private int ammount=0;
	
	/**
	 * Instantiates a new beat vertical shift.
	 *
	 * @param controller the controller
	 */
	public BeatVerticalShift(MatrixData matrix) {
		super(matrix, EffectName.BEAT_VERTICAL_SHIFT, ResizeName.QUALITY_RESIZE);
	}

	/* (non-Javadoc)
	 * @see com.neophob.sematrix.effect.Effect#getBuffer(int[])
	 */
	public int[] getBuffer(int[] buffer) {
		return doVerticalShift(buffer, ammount);
	}
	
	
    @Override
    public void update() {
        if (Sound.getInstance().isPang()) {
            ammount = (int)(Sound.getInstance().getVolumeNormalized()*internalBufferYSize);
        }
    }
    
	/**
	 * shift a image buffer vertical.
	 *
	 * @param buffer the buffer
	 * @param ammount the ammount
	 * @return the int[]
	 */
	private int[] doVerticalShift(int[] buffer, int ammount) {
		int[] ret = new int[buffer.length];

		int idx=0;
		int ofs = ammount*internalBufferXSize;
		for (int i=ofs; i<buffer.length; i++) {
			ret[idx++] = buffer[i];	
		}
		for (int i=0; i<ofs; i++) {
			ret[idx++] = buffer[i];
		}
		
		return ret;
	}

}
