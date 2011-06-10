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
import java.awt.image.DataBufferInt;
import java.awt.image.WritableRaster;

import com.neophob.sematrix.glue.Collector;

/**
 * resize a larger buffer for a smaller buffer
 * 
 * @author michu
 *
 */
public abstract class Resize {

	public enum ResizeName {
		PIXEL_RESIZE(0),
		QUALITY_RESIZE(1);
		
		private int id;
		
		ResizeName(int id) {
			this.id = id;
		}
		
		public int getId() {
			return id;
		}
	}
	
	private ResizeName resizeName;
	
	public Resize(ResizeName resizeName) {
		this.resizeName = resizeName;
		Collector.getInstance().addResize(this);
	}
	
	public abstract int[] getBuffer(int[] buffer, int deviceXSize, int deviceYSize, int currentXSize, int currentYSize);
	
	public int getId() {
		return this.resizeName.getId();
	}
	
	
	/**
	 * internal use - get buffer from image
	 * @param scaledImage
	 * @param deviceXSize
	 * @param deviceYSize
	 * @return
	 */
	protected static int[] getPixelsFromImage(BufferedImage scaledImage, int deviceXSize, int deviceYSize) {
		//painfull slow!
		//return scaledImage.getRGB(0, 0, deviceXSize, deviceYSize, null, 0, deviceXSize);
		DataBufferInt buf = (DataBufferInt) scaledImage.getRaster().getDataBuffer();
		return buf.getData();
	}

	/**
	 * 
	 * @param buffer
	 * @param currentXSize
	 * @param currentYSize
	 * @return
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
