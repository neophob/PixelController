package com.neophob.sematrix.glue;

import java.security.InvalidParameterException;
import java.util.logging.Level;
import java.util.logging.Logger;

import processing.core.PApplet;
import processing.core.PImage;

import com.neophob.sematrix.fader.Fader;

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
	public int[] getScreenBufferForDevice(Visual visual, OutputMapping map) {
		//get the visual 
		int buffer[] = visual.getBuffer();
		
		//apply output specific effect
		buffer = map.getEffect().getBuffer(buffer);
		
		//apply the fader (if needed)
		buffer = doTheFaderBaby(buffer, map);
		
		//resize to the ouput buffer return image
		return Collector.getInstance().resizeBufferForDevice(buffer, deviceXSize, deviceYSize);
	}
	

	/**
	 * strech the image for multiple outputs
	 * 
	 * @param buffer
	 * @param xOfsNr offset screen 0..n
	 * @param nrOfScreens nr of screens 1..n
	 * @return
	 */
	public int[] getScreenBufferForDevice(Visual visual, int xOfsNr, int nrOfScreens, OutputMapping map) {
		//get the visual 
		int buffer[] = visual.getBuffer();
		
		//apply output specific effect
		buffer = map.getEffect().getBuffer(buffer);
		
		//apply the fader (if needed)
		buffer = doTheFaderBaby(buffer, map);

//		return Collector.getInstance().resizeBufferForDevice(buffer, deviceXSize, deviceYSize);
		
		float f=1.0f/nrOfScreens; //0.33 - 0.33 - 1
		int xStart=(int)(xOfsNr*f*getBufferXSize()); //0 - 0.33 
		int xWidth=(int)((xOfsNr+1)*f*getBufferXSize()); //0.33 - 0.66
		
		//very UGLY and SLOW method to copy the image - im lazy!
 		PImage p = Collector.getInstance().getPapplet().createImage( getBufferXSize(), getBufferYSize(), PApplet.RGB );
		p.loadPixels();
		System.arraycopy(buffer, 0, p.pixels, 0, getBufferXSize()*getBufferYSize());
		p.updatePixels();

		//sx, sy, swidth, sheight, dx, dy, dwidth, dheight
		p.copy(xStart, 0, xWidth, getBufferYSize(), 0, 0, getBufferXSize(), getBufferYSize());

		p.loadPixels();
		int[] bfr2 = p.pixels.clone();
		p.updatePixels();
		
		return Collector.getInstance().resizeBufferForDevice(bfr2, deviceXSize, deviceYSize);
		
		//p.resize(deviceXSize, deviceYSize);
		//return p.pixels;*/
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
