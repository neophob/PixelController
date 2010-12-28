package com.neophob.sematrix.glue;

import java.security.InvalidParameterException;
import java.util.logging.Level;
import java.util.logging.Logger;

import processing.core.PApplet;
import processing.core.PImage;

import com.neophob.sematrix.fader.Fader;
import com.neophob.sematrix.glue.image.ResizeImageHelper;
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
			buffer=fader.getBuffer(buffer);
			//do not cleanup fader here, the box layout gets messed up!
			//the fader is cleaned up in the update system method
/*			if (fader.isDone()) {
				//fading is finished
				fader.cleanUp();
			}*/
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
		
		//TODO
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
	 * resize internal buffer to output size
	 * @param buffer
	 * @return RESIZED image
	 */
	public int[] resizeBufferForDevice(int[] buffer, int deviceXSize, int deviceYSize) {
		
		//processing RESIZE is buggy!
		//return ResizeImageHelper.processingResize(buffer, deviceXSize, deviceYSize, getBufferXSize(), getBufferYSize());
		
		//Area Average Filter - nice output but slow!
		//return ResizeImageHelper.areaAverageFilterResize(buffer, deviceXSize, deviceYSize, getBufferXSize(), getBufferYSize());
		
		return ResizeImageHelper.multiStepBilinearResize(buffer, deviceXSize, deviceYSize, getBufferXSize(), getBufferYSize());
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
