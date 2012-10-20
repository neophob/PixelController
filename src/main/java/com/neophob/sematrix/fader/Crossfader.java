/**
˚ * Copyright (C) 2011 Michael Vogt <michu@neophob.com>
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
 * crossfader.
 *
 * @author michu
 */
public class Crossfader extends Fader {

	/**
	 * Instantiates a new crossfader.
	 */
	public Crossfader() {
		super(FaderName.CROSSFADE, 1500);
	}

	/**
	 * Instantiates a new crossfader.
	 *
	 * @param time the time
	 */
	public Crossfader(int time) {
		super(FaderName.CROSSFADE, time);
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
			newBuffer = Collector.getInstance().getVisual(this.newVisual).getBuffer();
			return CrossfaderHelper.getBuffer(getCurrentStep(), buffer, newBuffer);			
		} catch (Exception e) {
			super.setDone();
			return buffer;
		}
	}

}
