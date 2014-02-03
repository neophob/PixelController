/**
 * Copyright (C) 2011-2014 Michael Vogt <michu@neophob.com>
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

import com.neophob.sematrix.core.resize.Resize.ResizeName;
import com.neophob.sematrix.core.visual.MatrixData;

/**
 * create a strobo effect.
 *
 * @author michu
 */
public class Strobo extends Effect {

	private int stro;
	private int count;
	
	/**
	 * Instantiates a new threshold.
	 *
	 * @param controller the controller
	 */
	public Strobo(MatrixData matrix) {
		super(matrix, EffectName.STROBO, ResizeName.QUALITY_RESIZE);
		this.stro = 0;
		this.count = 0;
	}

	/* (non-Javadoc)
	 * @see com.neophob.sematrix.core.effect.Effect#getBuffer(int[])
	 */
	public int[] getBuffer(int[] buffer) {
		if (stro==128) {
			return new int[buffer.length];
		}

		return buffer;
	}
		
	/* (non-Javadoc)
     * @see com.neophob.sematrix.core.effect.Effect#update()
     */
    @Override
	public void update() {
	    if (count++<2) {
	        return;
	    }
	    count = 0;
		stro^=128;
	}
}
