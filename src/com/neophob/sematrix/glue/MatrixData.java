package com.neophob.sematrix.glue;

import java.security.InvalidParameterException;
import java.util.logging.Level;
import java.util.logging.Logger;

import processing.core.PApplet;
import processing.core.PImage;

import com.neophob.sematrix.fader.Fader;
import com.neophob.sematrix.layout.LayoutModel;
import com.neophob.sematrix.resize.Resize;
import com.neophob.sematrix.resize.Resize.ResizeName;

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
	
	private PImage tmpImage;

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
	public int[] getScreenBufferForDevice(Visual visual, OutputMapping map) {				
		int[] buffer = visual.getBuffer();
		//apply output specific effect
		buffer = map.getEffect().getBuffer(buffer);
		
		//apply the fader (if needed)
		buffer = doTheFaderBaby(buffer, map);
		
		//resize to the ouput buffer return image
		return resizeBufferForDevice(buffer, visual.getResizeOption(), deviceXSize, deviceYSize);
	}
	

	/**
	 * strech the image for multiple outputs
	 * 
	 * @param buffer
	 * @param xOfsNr offset screen 0..n
	 * @param fxOnHowMayScreens 
	 * @return
	 */
	public int[] getScreenBufferForDevice(Visual visual, LayoutModel lm, OutputMapping map) {
		int[] buffer = visual.getBuffer();
		
		//apply output specific effect
		buffer = map.getEffect().getBuffer(buffer);
		
		//apply the fader (if needed)
		buffer = doTheFaderBaby(buffer, map);

		int xStart=lm.getxStart(getBufferXSize());
		int xWidth=lm.getxWidth(getBufferXSize());
		int yStart=lm.getyStart(getBufferYSize());
		int yWidth=lm.getyWidth(getBufferYSize());
		
		//lazy creation of the pimage
		if (tmpImage==null || tmpImage.width != getBufferXSize()) {
			tmpImage = Collector.getInstance().getPapplet().createImage( getBufferXSize(), getBufferYSize(), PApplet.RGB );			
		} 
		tmpImage.loadPixels();
		System.arraycopy(buffer, 0, tmpImage.pixels, 0, getBufferXSize()*getBufferYSize());

		//TODO very UGLY and SLOW method to copy the image - im lazy!
		//copy(x, y, width, height, dx, dy, dwidth, dheight)
		tmpImage.blend(xStart, yStart, xWidth, yWidth, 0, 0, getBufferXSize(), getBufferYSize(), PImage.REPLACE);
		
		int[] bfr2 = tmpImage.pixels;
		tmpImage.updatePixels();

		return resizeBufferForDevice(bfr2, visual.getResizeOption(), deviceXSize, deviceYSize);
	}

	
	

	/**
	 * resize internal buffer to output size
	 * @param buffer
	 * @return RESIZED image
	 */
	public int[] resizeBufferForDevice(int[] buffer, ResizeName resizeName, int deviceXSize, int deviceYSize) {
		
		//processing RESIZE is buggy!
		//return ResizeImageHelper.processingResize(buffer, deviceXSize, deviceYSize, getBufferXSize(), getBufferYSize());
		
		//Area Average Filter - nice output but slow!
		//return ResizeImageHelper.areaAverageFilterResize(buffer, deviceXSize, deviceYSize, getBufferXSize(), getBufferYSize());
//	return new int[deviceXSize* deviceYSize];	

		Resize r = Collector.getInstance().getResize(resizeName);
		return r.getBuffer(buffer, deviceXSize, deviceYSize, getBufferXSize(), getBufferYSize());
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
