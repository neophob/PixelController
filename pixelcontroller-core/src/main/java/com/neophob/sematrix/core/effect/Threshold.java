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
package com.neophob.sematrix.core.effect;

import java.util.Random;

import com.neophob.sematrix.core.glue.Collector;
import com.neophob.sematrix.core.glue.MatrixData;
import com.neophob.sematrix.core.glue.ShufflerOffset;
import com.neophob.sematrix.core.resize.Resize.ResizeName;

/**
 * The Class Threshold.
 *
 * @author michu
 */
public class Threshold extends Effect {

	/** The threshold. */
	private int threshold;
	
	/**
	 * Instantiates a new threshold.
	 *
	 * @param controller the controller
	 */
	public Threshold(MatrixData matrix) {
		super(matrix, EffectName.THRESHOLD, ResizeName.QUALITY_RESIZE);
		this.threshold = 128;
	}

	/* (non-Javadoc)
	 * @see com.neophob.sematrix.core.effect.Effect#getBuffer(int[])
	 */
	public int[] getBuffer(int[] buffer) {
		int[] ret = new int[buffer.length];
		
		for (int i=0; i<buffer.length; i++){
    		if ((buffer[i]&255)<this.threshold) {
    		    ret[i]=128; 
    		} else {
    		    ret[i]=0;
    		}
		}
		return ret;
	}
	
	/* (non-Javadoc)
	 * @see com.neophob.sematrix.core.effect.Effect#shuffle()
	 */
	@Override
	public void shuffle() {
		if (Collector.getInstance().getShufflerSelect(ShufflerOffset.THRESHOLD_VALUE)) {
			this.threshold = (short)new Random().nextInt(255);
		}		
	}
	
	
	/**
	 * Sets the threshold.
	 *
	 * @param threshold the new threshold
	 */
	public void setThreshold(int threshold) {
		this.threshold = threshold%0xff;
	}	
	
	/**
	 * Gets the threshold.
	 *
	 * @return the threshold
	 */
	public int getThreshold() {
		return threshold;
	}
	
}
