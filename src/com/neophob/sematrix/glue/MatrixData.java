package com.neophob.sematrix.glue;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.security.InvalidParameterException;
import java.util.logging.Level;
import java.util.logging.Logger;

import processing.core.PApplet;
import processing.core.PImage;

import com.neophob.sematrix.fader.Fader;
import com.neophob.sematrix.layout.LayoutModel;

/**
 * matrix display buffer class
 * 
 * the internal buffer is much larger than the actual device. the buffer for the matrix is recalculated
 * each frame. reason: better display quality 
 * 
 * @author mvogt
 *
 */
public class MatrixData {

	private static Logger log = Logger.getLogger(MatrixData.class.getName());

	/** the internal buffer is 8 times larger than the output buffer */
	private static final int INTERNAL_BUFFER_SIZE = 8;
	

	//output buffer
	private int deviceXSize;
	private int deviceYSize;
	private int deviceSize;

	/**
	 * init matrix data
	 * 
	 * @param nrOfScreens
	 * @param screenXSize
	 * @param screenYSize
	 * @param strechScreenOnAll
	 */
	public MatrixData(int deviceXSize, int deviceYSize) {
		if (deviceXSize < 0 || deviceYSize < 0) {
			throw new InvalidParameterException("screenXSize and screenYsize must be > 0!");
		}
		this.deviceXSize = deviceXSize;
		this.deviceYSize = deviceYSize;
		this.deviceSize = deviceXSize*deviceYSize;

		log.log(Level.INFO,
				"screenSize: {0} ({1} * {2}), "
				, new Object[] { deviceSize, deviceXSize, deviceYSize });
		
		Collector.getInstance().setMatrix(this);
	}
	
	/**
	 * fade the buffer
	 * @param buffer
	 * @param map
	 * @return
	 */
	private int[] doTheFaderBaby(int[] buffer, OutputMapping map) {
		Fader fader = map.getFader();
		if (fader.isStarted()) {
			if (fader.isDone()) {
				//fading is finished
				fader.cleanUp();
			}
			buffer=fader.getBuffer(buffer);
		}
		return buffer;
	}
	
	/**
	 * input: 64*64*nrOfScreens buffer
	 * output: 8*8 buffer (resized from 64*64)
	 * 
	 * ImageUtils.java, Copyright (c) JForum Team
	 * 
	 * @param screenNr select physical screen/matrix 
	 * @return
	 */
	public int[] getScreenBufferForDevice(int buffer[], OutputMapping map) {
		//apply output specific effect
		buffer = map.getEffect().getBuffer(buffer);
		
		//apply the fader (if needed)
		buffer = doTheFaderBaby(buffer, map);
		
		//resize to the ouput buffer return image
		return resizeBufferForDevice(buffer, deviceXSize, deviceYSize);
	}
	

	/**
	 * strech the image for multiple outputs
	 * 
	 * @param buffer
	 * @param xOfsNr offset screen 0..n
	 * @param fxOnHowMayScreens 
	 * @return
	 */
	public int[] getScreenBufferForDevice(int buffer[], LayoutModel lm, OutputMapping map) {
		//apply output specific effect
		buffer = map.getEffect().getBuffer(buffer);
		
		//apply the fader (if needed)
		buffer = doTheFaderBaby(buffer, map);

		int xStart=lm.getxStart(getBufferXSize());
		int xWidth=lm.getxWidth(getBufferXSize());
		int yStart=lm.getyStart(getBufferYSize());
		int yWidth=lm.getyWidth(getBufferYSize());
		
		//very UGLY and SLOW method to copy the image - im lazy!
 		PImage p = Collector.getInstance().getPapplet().createImage( getBufferXSize(), getBufferYSize(), PApplet.RGB );
		p.loadPixels();
		System.arraycopy(buffer, 0, p.pixels, 0, getBufferXSize()*getBufferYSize());

		//copy(x, y, width, height, dx, dy, dwidth, dheight)
		p.copy(xStart, yStart, xWidth, yWidth, 0, 0, getBufferXSize(), getBufferYSize());
		
		int[] bfr2 = p.pixels;
		p.updatePixels();

		return resizeBufferForDevice(bfr2, deviceXSize, deviceYSize);
	}


	/**
	 * workarround until processing resize works 
	 * 
	 * @param image
	 * @param width
	 * @param height
	 * @return
	 */
	private static BufferedImage resize2(BufferedImage image, int width, int height) {
		int type = image.getType() == 0? BufferedImage.TYPE_INT_RGB : image.getType();
		BufferedImage resizedImage = new BufferedImage(width, height, type);
		Graphics2D g = resizedImage.createGraphics();
		g.setComposite(AlphaComposite.Src);

		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);

		g.setRenderingHint(RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_SPEED);

		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_OFF);

		g.drawImage(image, 0, 0, width, height, null);
		g.dispose();
		return resizedImage;
	}

	/**
	 * TODO maybe move
	 * convert buffer to output size
	 * @param buffer
	 * @return RESIZED image
	 */
	public int[] resizeBufferForDevice(int[] buffer, int deviceXSize, int deviceYSize) {
		
		//Processing resize is buggy!
/*		int[] ret = new int[deviceXSize*deviceYSize];
 		PImage pImage = Collector.getInstance().getPapplet().createImage
 			( getBufferXSize(), getBufferYSize(), PApplet.RGB );
		pImage.loadPixels();
		System.arraycopy(buffer, 0, pImage.pixels, 0, buffer.length);
		pImage.updatePixels();

		pImage.resize(deviceXSize, deviceYSize);
		
		pImage.loadPixels();
		ret = pImage.pixels;
		pImage.updatePixels();
		
		return ret;*/
		
		BufferedImage bi = new BufferedImage(getBufferXSize(), getBufferYSize(), BufferedImage.TYPE_INT_RGB);
		bi.setRGB(0, 0, getBufferXSize(), getBufferYSize(), buffer, 0, getBufferXSize());
		BufferedImage resizedImage = resize2(bi, deviceXSize, deviceYSize);

		DataBufferInt dbi = (DataBufferInt)resizedImage.getRaster().getDataBuffer();
		return dbi.getData();
	}


	/** 
	 * ========[ getter/setter ]====================================================================== 
	 */
	
	/**
	 * return effective device pixel size
	 * @return
	 */
	public int getDeviceXSize() {
		return deviceXSize;
	}

	/**
	 * return effective device pixel size
	 * @return
	 */
	public int getDeviceYSize() {
		return deviceYSize;
	}

	/**
	 * return effective BUFFER size
	 * @return
	 */
	public int getBufferXSize() {
		return deviceXSize*INTERNAL_BUFFER_SIZE;
	}

	/**
	 * return effective BUFFER size
	 * @return
	 */
	public int getBufferYSize() {
		return deviceYSize*INTERNAL_BUFFER_SIZE;
	}

	public int getDeviceSize() {
		return deviceSize;
	}


}
