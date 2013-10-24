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
package com.neophob.sematrix.glue;

import com.neophob.sematrix.fader.Fader;
import com.neophob.sematrix.fader.Fader.FaderName;
import com.neophob.sematrix.fader.PixelControllerFader;

/**
 * class used to store input/ouput mapping.
 * 
 * each visual is assigned to 0 or n screens, each screen h
 *
 * @author michu
 */
public class OutputMapping {

	/** the visual number (offset) */
	private int visualNumber;
	
	/** The fader. */
	private Fader fader;

	/**
	 * initialize the mapping.
	 *
	 * @param visualId the visual id
	 * @param screenNr the screen nr
	 */
	public OutputMapping(PixelControllerFader pixelControllerFader, int visualNumber) {
		this.fader = pixelControllerFader.getFader(FaderName.SWITCH);
		this.visualNumber = visualNumber;
	}

	/**
	 * Gets the visual id.
	 *
	 * @return the visual id
	 */
	public int getVisualId() {
		return visualNumber;
	}

	/**
	 * Sets the visual id.
	 *
	 * @param visualId the new visual id
	 */
	public void setVisualId(int visualId) {
		this.visualNumber = visualId;
	}

	/**
	 * Gets the fader.
	 *
	 * @return the fader
	 */
	public Fader getFader() {
		return fader;
	}

	/**
	 * Sets the fader.
	 *
	 * @param fader the new fader
	 */
	public void setFader(Fader fader) {
		this.fader = fader;
	}
	
}
