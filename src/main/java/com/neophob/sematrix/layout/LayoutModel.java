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

package com.neophob.sematrix.layout;

/**
 * helper class used to layout panels.
 *
 * @author michu
 */
public class LayoutModel {
	
	/** The same fx on x. */
	private int sameFxOnX;
	
	/** The same fx on y. */
	private int sameFxOnY;
	
	/** The ofs x. */
	private int ofsX;
	
	/** The ofs y. */
	private int ofsY;
	
	/** The fx input. */
	private int fxInput;
	
	/** The screen fragment x. */
	private float screenFragmentX;
	
	/** The screen fragment y. */
	private float screenFragmentY;
	
	/** The x width. */
	private float xStart,xWidth;
	
	/** The y width. */
	private float yStart,yWidth;
	
	/**
	 * Instantiates a new layout model.
	 *
	 * @param sameFxOnX the same fx on x
	 * @param sameFxOnY the same fx on y
	 * @param ofsX the ofs x
	 * @param ofsY the ofs y
	 * @param fxInput the fx input
	 */
	public LayoutModel(int sameFxOnX, int sameFxOnY, int ofsX, int ofsY, int fxInput) {
		this.sameFxOnX = sameFxOnX;
		this.sameFxOnY = sameFxOnY;
		this.ofsX = ofsX;
		this.ofsY = ofsY;
		this.fxInput = fxInput;
		
		if (!screenDoesNotNeedStretching()) {
			screenFragmentX = 1.0f/sameFxOnX;
/*			if (sameFxOnX<2) {
				screenFragmentX = 1.0f;
			} else {
				screenFragmentX = 1.0f/sameFxOnX;				
			}*/
			
			if (sameFxOnY<2) {
				screenFragmentY = 1.0f;
			} else {
				screenFragmentY = 1.0f/sameFxOnY;				
			}
			
			xStart = ofsX*screenFragmentX;
			xWidth = screenFragmentX;
			yStart = ofsY*screenFragmentY;
			yWidth = screenFragmentY;
		}
	}
	
	/**
	 * Screen does not need stretching.
	 *
	 * @return true, if successful
	 */
	public boolean screenDoesNotNeedStretching() {
		return (sameFxOnX==1 && sameFxOnY==1);
	}
	
	
	/**
	 * Gets the x start.
	 *
	 * @param length the length
	 * @return the x start
	 */
	public int getxStart(int length) {
		return (int)(xStart*length);
	}

	/**
	 * Gets the x width.
	 *
	 * @param length the length
	 * @return the x width
	 */
	public int getxWidth(int length) {
		return (int)(xWidth*length);
	}

	/**
	 * Gets the y start.
	 *
	 * @param length the length
	 * @return the y start
	 */
	public int getyStart(int length) {
		return (int)(yStart*length);
	}

	/**
	 * Gets the y width.
	 *
	 * @param length the length
	 * @return the y width
	 */
	public int getyWidth(int length) {
		return (int)(yWidth*length);
	}

	/**
	 * Gets the same fx on x.
	 *
	 * @return the same fx on x
	 */
	public int getSameFxOnX() {
		return sameFxOnX;
	}
	
	/**
	 * Gets the same fx on y.
	 *
	 * @return the same fx on y
	 */
	public int getSameFxOnY() {
		return sameFxOnY;
	}
	
	/**
	 * Gets the ofs x.
	 *
	 * @return the ofs x
	 */
	public int getOfsX() {
		return ofsX;
	}
	
	/**
	 * Gets the ofs y.
	 *
	 * @return the ofs y
	 */
	public int getOfsY() {
		return ofsY;
	}
	
	/**
	 * Gets the fx input.
	 *
	 * @return the fx input
	 */
	public int getFxInput() {
		return fxInput;
	}
	
}
