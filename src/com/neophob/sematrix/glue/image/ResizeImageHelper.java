package com.neophob.sematrix.glue.image;

import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.VolatileImage;
import java.awt.image.WritableRaster;

import processing.core.PApplet;
import processing.core.PImage;

import com.neophob.sematrix.glue.Collector;

public class ResizeImageHelper {

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
		int[] ret = new int[deviceXSize*deviceYSize];

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
	 * Resize Image using Scalr lib
	 * @param buffer
	 * @param deviceXSize
	 * @param deviceYSize
	 * @param currentXSize
	 * @param currentYSize
	 * @return
	 */
	public static int[] multiStepBilinearResize(int[] buffer, int deviceXSize, int deviceYSize, int currentXSize, int currentYSize) {
		BufferedImage bi = new BufferedImage(currentXSize, currentYSize, BufferedImage.TYPE_INT_RGB);
		//bi.setRGB(0, 0, currentXSize, currentYSize, buffer, 0, currentXSize);
		WritableRaster newRaster = bi.getRaster();
		newRaster.setDataElements(0, 0, currentXSize, currentYSize, buffer);
		bi.setData(newRaster);


		if (deviceXSize > currentXSize) {
			//upscale - used for debug view
			bi = Scalr.resize(bi, Scalr.Method.SPEED, deviceXSize, deviceYSize);
		} else {
			//downscale - used to send to device
			bi = Scalr.resize(bi, Scalr.Method.QUALITY, deviceXSize, deviceYSize);	
		}		              
		return getPixelsFromImage(bi, deviceXSize, deviceYSize);
	}



	public static int[] createVolatileImageFromBuffer(int[] buffer, int deviceXSize, int deviceYSize, int currentXSize, int currentYSize) {
		BufferedImage bi = new BufferedImage(currentXSize, currentYSize, BufferedImage.TYPE_INT_RGB);
		bi.setRGB(0, 0, currentXSize, currentYSize, buffer, 0, currentXSize);	
		
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsConfiguration gc = ge.getDefaultScreenDevice().getDefaultConfiguration();

		VolatileImage vi = gc.createCompatibleVolatileImage(currentXSize, currentYSize, Transparency.OPAQUE);

		int valid = vi.validate(gc);
		if (valid != VolatileImage.IMAGE_INCOMPATIBLE) {
			//			return vi.getSnapshot().getRGB(0, 0, deviceXSize, deviceYSize, null, 0, deviceXSize);
			//do error
			return null;
		}

		Graphics2D g = null;
		try {
			g = vi.createGraphics();
			g.drawImage(bi, null, 0, 0);

		} finally {	
			// It's always best to dispose of your Graphics objects.
			g.dispose();
		}
		int[] ret = getPixelsFromImage(vi.getSnapshot(), deviceXSize, deviceYSize);		
		vi.flush();
		
		return ret;
	}


/*	public static int[] testResize(int[] buffer, int deviceXSize, int deviceYSize, int currentXSize, int currentYSize) {

	}
*/
}
