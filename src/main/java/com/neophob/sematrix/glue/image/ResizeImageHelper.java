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

package com.neophob.sematrix.glue.image;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

/**
 * The Class ResizeImageHelper.
 *
 * @author michu
 */
public final class ResizeImageHelper {

	/**
	 * Instantiates a new resize image helper.
	 */
	private ResizeImageHelper() {
		//no instance allowed, util class
	}


	/**
	 * Creates the image.
	 *
	 * @param buffer the buffer
	 * @param currentXSize the current x size
	 * @param currentYSize the current y size
	 * @return the buffered image
	 */
	public static BufferedImage createImage(int[] buffer, int currentXSize, int currentYSize) {
		BufferedImage bi = new BufferedImage(currentXSize, currentYSize, BufferedImage.TYPE_INT_RGB);
		//bi.setRGB(0, 0, currentXSize, currentYSize, buffer, 0, currentXSize);
		WritableRaster newRaster = bi.getRaster();
		newRaster.setDataElements(0, 0, currentXSize, currentYSize, buffer);
		bi.setData(newRaster);

		return bi;
	}


}
