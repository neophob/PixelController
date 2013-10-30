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
import com.neophob.sematrix.input.ISound;
import com.neophob.sematrix.resize.Resize.ResizeName;

/**
 * The Class BeatHorizShift.
 */
public class BeatHorizShift extends Effect {

	/** The ammount. */
	private int ammount=0;
	
	private ISound sound;
	
	/**
	 * Instantiates a new beat horiz shift.
	 *
	 * @param controller the controller
	 */
	public BeatHorizShift(MatrixData matrix, ISound sound) {
		super(matrix, EffectName.BEAT_HORIZONTAL_SHIFT, ResizeName.QUALITY_RESIZE);
		this.sound = sound;
	}

	/* (non-Javadoc)
	 * @see com.neophob.sematrix.effect.Effect#getBuffer(int[])
	 */
	public int[] getBuffer(int[] buffer) {
		return doHorizShift(buffer, ammount);
	}
	
	
    @Override
    public void update() {
        if (sound.isPang()) {
            ammount = (int)(sound.getVolumeNormalized()*internalBufferYSize);
        }
    }
	
	/**
	 * Do horiz shift.
	 *
	 * @param buffer the buffer
	 * @param ammount the ammount
	 * @return the int[]
	 */
	private int[] doHorizShift(int[] buffer, int ammount) {		
		int[] ret = new int[buffer.length];
		ammount %= internalBufferXSize;

		int x,idx=0,ofs;
		for (int y=0; y<internalBufferYSize; y++) {
			ofs=internalBufferXSize*y;
			for (x=ammount; x<internalBufferXSize; x++) {
				ret[idx++] = buffer[ofs+x];
			}
			for (x=0; x<ammount; x++) {
				ret[idx++] = buffer[ofs+x];
			}
		}
		return ret;
	}


}
