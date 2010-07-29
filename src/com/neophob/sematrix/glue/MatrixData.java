package com.neophob.sematrix.glue;

import java.security.InvalidParameterException;
import java.util.logging.Level;
import java.util.logging.Logger;

import processing.core.PImage;

import com.neophob.sematrix.fader.Fader;
import com.neophob.sematrix.generator.Generator;

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

	static Logger log = Logger.getLogger(MatrixData.class.getName());

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
		int buffer[] = visual.getBuffer();
		buffer = doTheFaderBaby(buffer, map);
		PImage p = Collector.getInstance().getImageFromBuffer(buffer, deviceXSize, deviceYSize);
		return p.pixels;
	}
	

	/**
	 * strech the image for multiple outputs
	 * 
	 * @param buffer
	 * @param xOfsNr offset screen 0..n
	 * @param total nr of screens 1..n
	 * @return
	 */
	public int[] getScreenBufferForDevice(Visual visual, int xOfsNr, int total, OutputMapping map) {
		//get internal buffer as image
		int buffer[] = visual.getBuffer();
		buffer = doTheFaderBaby(buffer, map);

		PImage p = Collector.getInstance().getImageFromBuffer(buffer, deviceXSize, deviceYSize);
		
		float f=1.0f/total; //0.33 - 0.33 - 1
		int xStart=(int)(xOfsNr*f*p.width); //0 - 0.33 
		int xWidth=(int)((xOfsNr+1)*f*p.width); //0.33 - 0.66
		
		     //sx, sy, swidth, sheight, dx, dy, dwidth, dheight
		p.copy(xStart, 0, xWidth, p.height, 0, 0, p.width, p.height);
		p.resize(deviceXSize, deviceYSize);
		return p.pixels;
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
		return deviceXSize*Generator.INTERNAL_BUFFER_SIZE;
	}

	/**
	 * return effective BUFFER size
	 * @return
	 */
	public int getBufferYSize() {
		return deviceYSize*Generator.INTERNAL_BUFFER_SIZE;
	}

	public int getDeviceSize() {
		return deviceSize;
	}


}
