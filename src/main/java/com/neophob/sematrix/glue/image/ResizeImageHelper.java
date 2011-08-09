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

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.AreaAveragingScaleFilter;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageObserver;
import java.awt.image.PixelGrabber;
import java.awt.image.ReplicateScaleFilter;
import java.awt.image.WritableRaster;
import java.util.logging.Level;
import java.util.logging.Logger;

import processing.core.PApplet;
import processing.core.PImage;

import com.neophob.sematrix.glue.Collector;
import com.neophob.sematrix.output.RainbowduinoDevice;

/**
 * 
 * @author michu
 *
 */
public final class ResizeImageHelper {

	private static Logger log = Logger.getLogger(RainbowduinoDevice.class.getName());
	
	private ResizeImageHelper() {
		//no instance allowed, util class
	}


	/**
	 * DO not use processing resize - its BUGGY!
	 * 
	 * @param buffer
	 * @param deviceXSize
	 * @param deviceYSize
	 * @param currentXSize
	 * @param currentYSize
	 * @return
	 */
	public static int[] processingResize(int[] buffer, int deviceXSize, int deviceYSize, int currentXSize, int currentYSize) {
		int[] ret;// = new int[deviceXSize*deviceYSize];

		PImage pImage = Collector.getInstance().getPapplet().createImage( currentXSize, currentYSize, PApplet.RGB );
		pImage.loadPixels();
		System.arraycopy(buffer, 0, pImage.pixels, 0, buffer.length);
		pImage.updatePixels();

		pImage.resize(deviceXSize, deviceYSize);
		pImage.loadPixels();
		ret = pImage.pixels;
		pImage.updatePixels();

		return ret;
	}


	/**
	 * internal use - get buffer from image
	 * @param scaledImage
	 * @param deviceXSize
	 * @param deviceYSize
	 * @return
	 */
	private static int[] getPixelsFromImage(BufferedImage scaledImage, int deviceXSize, int deviceYSize) {
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

	/**
	 * Resize Image using Scalr lib
	 * @param buffer
	 * @param deviceXSize
	 * @param deviceYSize
	 * @param currentXSize
	 * @param currentYSize
	 * @return
	 */
	public static int[] multiStepBilinearResize(int[] buffer, int deviceXSize, int deviceYSize, int currentXSize, int currentYSize) {
		BufferedImage bi = createImage(buffer, currentXSize, currentYSize);

/*		if (deviceXSize > currentXSize) {
			//upscale - used for debug view
//			bi = Scalr.resize(bi, Scalr.Method.SPEED, deviceXSize, deviceYSize, false, false);
		} else {
			//downscale - used to send to device
//			bi = Scalr.resize(bi, Scalr.Method.QUALITY, deviceXSize, deviceYSize, false, false);	
		}	*/	              
//		bi = Scalr.resize(bi, Scalr.Method.QUALITY, deviceXSize, deviceYSize);
//		bi = Scalr.resize(bi, Scalr.Method.SPEED, deviceXSize, deviceYSize);		
//		bi = Scalr.resize(bi, Scalr.Method.BALANCED, deviceXSize, deviceYSize);		

		int[] ret = getPixelsFromImage(bi, deviceXSize, deviceYSize);
		
		//destroy image
		bi.flush();
		
		return ret;
	}



	/**
	 * 
	 * @param buffer
	 * @param deviceXSize
	 * @param deviceYSize
	 * @param currentXSize
	 * @param currentYSize
	 * @return
	 */
	public static int[] oldResize(int[] buffer, int deviceXSize, int deviceYSize, int currentXSize, int currentYSize) {
		BufferedImage bi = createImage(buffer, currentXSize, currentYSize);

		Image scaledImage;
		if (deviceXSize>=currentXSize) {			
			//enlarge image with an replicate scale filter
			scaledImage = Toolkit.getDefaultToolkit().createImage (new FilteredImageSource (bi.getSource(),
					new ReplicateScaleFilter(deviceXSize, deviceYSize)));		
		} else {
			//shrink image with an area average filter
			scaledImage = Toolkit.getDefaultToolkit().createImage (new FilteredImageSource (bi.getSource(),
					new AreaAveragingScaleFilter(deviceXSize, deviceYSize)));		
		}

		//get pixels out
		return grabPixels(scaledImage, deviceXSize, deviceYSize);
	}

	/**
	 * 
	 * @param scaledImage
	 * @param deviceXSize
	 * @param deviceYSize
	 * @return
	 */
	private static int[] grabPixels(Image scaledImage, int deviceXSize, int deviceYSize) {		
		int[] pixels = new int[deviceXSize*deviceYSize];
		PixelGrabber pg = new PixelGrabber(scaledImage, 0, 0, deviceXSize, deviceYSize, pixels, 0, deviceXSize);
		try {
			pg.grabPixels();
		} catch (InterruptedException e) {
			log.log(Level.WARNING, "interrupted waiting for pixels!");
		}
		if ((pg.getStatus() & ImageObserver.ABORT) != 0) {
			log.log(Level.WARNING, "image fetch aborted or errored");
		}
		return pixels;
	}


	/**
	 * 
	 * @param buffer
	 * @param deviceXSize
	 * @param deviceYSize
	 * @param currentXSize
	 * @param currentYSize
	 * @return
	 */
	public static int[] resizeBicubic(int[] buffer, int deviceXSize, int deviceYSize, int currentXSize, int currentYSize) {
		int height = currentYSize;
		int width = currentXSize;

		int newHeight = deviceYSize;
		int newWidth = deviceXSize;
		
		int[] dest = new int[newHeight*newWidth];

		double xFactor = (double)width / newWidth;
		double yFactor = (double)height / newHeight;

		// coordinates of source points and cooefficiens
		double ox, oy, dx, dy, k1, k2;
		int ox1, oy1, ox2, oy2;
		// destination pixel values
		double r, g, b;
		// width and height decreased by 1
		int ymax = height - 1;
		int xmax = width - 1;

		// RGB
		for (int y = 0; y < newHeight; y++) {
			// Y coordinates
			oy = (double)y * yFactor - 0.5f;
			oy1 = (int)oy;
			dy = oy - (double)oy1;

			for (int x = 0; x < newWidth; x++) {
				// X coordinates
				ox = (double)x * xFactor - 0.5f;
				ox1 = (int)ox;
				dx = ox - (double)ox1;

				// initial pixel value
				r = g = b = 0;

				for (int n = -1; n < 3; n++)
				{
					// get Y cooefficient
					k1 = BiCubicKernel(dy - (double)n);

					oy2 = oy1 + n;
					if (oy2 < 0)
						oy2 = 0;
					if (oy2 > ymax)
						oy2 = ymax;

					for (int m = -1; m < 3; m++)
					{
						// get X cooefficient
						k2 = k1 * BiCubicKernel((double)m - dx);

						ox2 = ox1 + m;
						if (ox2 < 0)
							ox2 = 0;
						if (ox2 > xmax)
							ox2 = xmax;

						// get pixel of original image
						// p = src + oy2 * srcStride + ox2 * 3;
						int srcPixel=buffer[ox2+(width*oy2)];
						r += k2 * ((srcPixel>>16) & 0xff);
						g += k2 * ((srcPixel >>8) & 0xff);
						b += k2 * (srcPixel & 0xff);
					}
				}
				                     
				dest[x+(newWidth*y)]=(int)(((short)r<<16) | ((short)g<<8) |(short) b);
			}

		}
		return dest;
	}

	/**
	 * 
	 * @param x
	 * @return
	 */
	private static double BiCubicKernel(double x) {
		if (x > 2.0d) {
			return 0.0d;
		}
			
		double a, b, c, d;
		double xm1 = x - 1.0d;
		double xp1 = x + 1.0d;
		double xp2 = x + 2.0d;

		a = (xp2 <= 0.0d) ? 0.0 : xp2 * xp2 * xp2;
		b = (xp1 <= 0.0d) ? 0.0 : xp1 * xp1 * xp1;
		c = (x <= 0.0d) ? 0.0 : x * x * x;
		d = (xm1 <= 0.0d) ? 0.0 : xm1 * xm1 * xm1;

		return (0.16666666666666666667d * (a - (4.0d * b) + (6.0d * c) - (4.0d * d)));
	}
}
