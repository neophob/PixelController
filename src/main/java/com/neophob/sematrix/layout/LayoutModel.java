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
 * helper class used to layout panels
 * @author michu
 *
 */
public class LayoutModel {
	private int sameFxOnX;
	private int sameFxOnY;
	
	private int ofsX;
	private int ofsY;
	
	private int fxInput;
	
	private float screenFragmentX;
	private float screenFragmentY;
	
	private float xStart,xWidth;
	private float yStart,yWidth;
	
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
	
	public boolean screenDoesNotNeedStretching() {
		return (sameFxOnX==1 && sameFxOnY==1);
	}
	
	
	public int getxStart(int length) {
		return (int)(xStart*length);
	}

	public int getxWidth(int length) {
		return (int)(xWidth*length);
	}

	public int getyStart(int length) {
		return (int)(yStart*length);
	}

	public int getyWidth(int length) {
		return (int)(yWidth*length);
	}

	public int getSameFxOnX() {
		return sameFxOnX;
	}
	
	public int getSameFxOnY() {
		return sameFxOnY;
	}
	
	public int getOfsX() {
		return ofsX;
	}
	
	public int getOfsY() {
		return ofsY;
	}
	
	public int getFxInput() {
		return fxInput;
	}
	
}
