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

package com.neophob.sematrix.resize;

import java.awt.image.BufferedImage;

import com.neophob.sematrix.resize.util.ScalrOld;

/**
 * This filter is more like an area average filter.
 *
 * @author michu
 */
public class QualityResize extends Resize {

	/**
	 * Instantiates a new quality resize.
	 *
	 * @param controller the controller
	 */
	public QualityResize(PixelControllerResize controller) {
		super(controller, ResizeName.QUALITY_RESIZE);
	}
	
	/* (non-Javadoc)
	 * @see com.neophob.sematrix.resize.Resize#getBuffer(int[], int, int, int, int)
	 */
	public int[] getBuffer(int[] buffer, int deviceXSize, int deviceYSize, int currentXSize, int currentYSize) {
		BufferedImage bi = createImage(buffer, currentXSize, currentYSize);
		
		bi = ScalrOld.resize(bi, ScalrOld.Method.QUALITY, deviceXSize, deviceYSize);

		int[] ret = getPixelsFromImage(bi, deviceXSize, deviceYSize);
		
		//destroy image
		bi.flush();
		
		return ret;
	}
}
