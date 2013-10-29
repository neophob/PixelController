/**
 * Copyright (C) 2011-2013 Michael Vogt <michu@neophob.com>
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
package com.neophob.sematrix.glue;

import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import processing.core.PApplet;
import processing.core.PImage;

import com.neophob.sematrix.fader.IFader;
import com.neophob.sematrix.layout.LayoutModel;
import com.neophob.sematrix.output.Output;
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

    /** The log. */
    private static final Logger LOG = Logger.getLogger(MatrixData.class.getName());

    /** The device x size. */
    private int deviceXSize;

    /** The device y size. */
    private int deviceYSize;

    /** The device size. */
    private int deviceSize;

    /** This map is used to store temporary images */
    private Map<Output, PImage> pImagesMap;

    /** internal buffer size */
    int bufferWidth;
    int bufferHeight;

    /**
     * init matrix data.
     * Use Case 1: 2 PixelInvader panels, each panel have a 8x8 resolution
     * Use Case 2: 1 TPM2Net panel, size 24*16
     *
     * @param deviceXSize the device x size
     * @param deviceYSize the device y size
     */
    public MatrixData(int deviceXSize, int deviceYSize) {
        if (deviceXSize < 1 || deviceYSize < 1) {
            throw new InvalidParameterException("screenXSize and screenYsize must be > 0!");
        }
        this.deviceXSize = deviceXSize;
        this.deviceYSize = deviceYSize;
        this.deviceSize = deviceXSize*deviceYSize;

        //select buffer size depending on the output device
/*        if (deviceXSize < 16) {
            bufferWidth = 64;
        } else {
            bufferWidth = 128;
        }

        if (deviceYSize < 16) {
            bufferHeight = 64;
        } else {
            bufferHeight = 128;
        }*/
        int internalBufferSizeMultiplier = 8;
        bufferWidth = deviceXSize*internalBufferSizeMultiplier;
        bufferHeight = deviceYSize*internalBufferSizeMultiplier;
        
        while (getRgbBufferSize()>60*1024 && internalBufferSizeMultiplier>1) {
        	internalBufferSizeMultiplier/=2;
            bufferWidth = deviceXSize*internalBufferSizeMultiplier;
            bufferHeight = deviceYSize*internalBufferSizeMultiplier;
        }

        this.pImagesMap = new HashMap<Output, PImage>();

        LOG.log(Level.INFO, "screenSize: {0} ({1} * {2}), multiplication factor: {3} ({4} * {5})", 
        		new Object[] { deviceSize, deviceXSize, deviceYSize, internalBufferSizeMultiplier, bufferWidth, bufferHeight});

        Collector.getInstance().setMatrix(this);
    }

    private int getRgbBufferSize() {
    	return getBufferXSize()*getBufferYSize()*3;
    }
    
    /**
     * fade the buffer.
     *
     * @param buffer the buffer
     * @param map the map
     * @return the int[]
     */
    private int[] doTheFaderBaby(int[] buffer, OutputMapping map) {
        IFader fader = map.getFader();
        if (fader.isStarted()) {
            buffer=fader.getBuffer(buffer, Collector.getInstance().getVisual(fader.getNewVisual()).getBuffer());
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
     * @param visual the visual
     * @param map the map
     * @return the screen buffer for device
     */
    public int[] getScreenBufferForDevice(Visual visual, OutputMapping map) {
        int[] buffer = visual.getBuffer();
        //apply output specific effect
        //buffer = map.getBuffer();
        //buffer = map.getFader().getBuffer(buffer);

        //apply the fader (if needed)
        buffer = doTheFaderBaby(buffer, map);

        //resize to the ouput buffer return image
        return resizeBufferForDevice(buffer, visual.getResizeOption(), deviceXSize, deviceYSize);
    }


    /**
     * strech the image for multiple outputs.
     *
     * @param visual the visual
     * @param lm the lm
     * @param map the map
     * @param output the output
     * @return the screen buffer for device
     */
    public int[] getScreenBufferForDevice(Visual visual, LayoutModel lm, OutputMapping map, Output output) {
        int[] buffer = visual.getBuffer();

        //apply output specific effect
        //buffer = map.getBuffer();
        //buffer = map.getFader().getBuffer(buffer);

        //apply the fader (if needed)
        buffer = doTheFaderBaby(buffer, map);

        int xStart=lm.getxStart(bufferWidth);
        int xWidth=lm.getxWidth(bufferWidth);
        int yStart=lm.getyStart(bufferHeight);
        int yWidth=lm.getyWidth(bufferHeight);

        // initialize PImage instance in a lazy way and store a dedicated
        // instance per output in an internal map to avoid constructing an
        // PImage instance with every method call.
        PImage tmpImage = this.pImagesMap.get(output);
        if (tmpImage==null || tmpImage.width != bufferWidth) {
            tmpImage = Collector.getInstance().getPapplet().createImage(bufferWidth, bufferHeight, PApplet.RGB);
            this.pImagesMap.put(output, tmpImage);
        } 

        tmpImage.loadPixels();
        System.arraycopy(buffer, 0, tmpImage.pixels, 0, bufferWidth*bufferHeight);

        //TODO very UGLY and SLOW method to copy the image - im lazy!
        //copy(x, y, width, height, dx, dy, dwidth, dheight)
        tmpImage.blend(xStart, yStart, xWidth, yWidth, 0, 0, bufferWidth, bufferHeight, PImage.REPLACE);
        int[] bfr2 = tmpImage.pixels;
        tmpImage.updatePixels();

        return resizeBufferForDevice(bfr2, visual.getResizeOption(), deviceXSize, deviceYSize);
    }

    /**
     * resize internal buffer to output size.
     *
     * @param buffer the buffer
     * @param resizeName the resize name
     * @param deviceXSize the device x size
     * @param deviceYSize the device y size
     * @return RESIZED image
     */
    public int[] resizeBufferForDevice(int[] buffer, ResizeName resizeName, int deviceXSize, int deviceYSize) {		
        //processing RESIZE is buggy!
        //return ResizeImageHelper.processingResize(buffer, deviceXSize, deviceYSize, getBufferXSize(), getBufferYSize());

        //Area Average Filter - nice output but slow!
        //return ResizeImageHelper.areaAverageFilterResize(buffer, deviceXSize, deviceYSize, getBufferXSize(), getBufferYSize());
        //return new int[deviceXSize* deviceYSize];	

        Resize r = Collector.getInstance().getPixelControllerResize().getResize(resizeName);
        return r.getBuffer(buffer, deviceXSize, deviceYSize, getBufferXSize(), getBufferYSize());
    }


    /**
     * ========[ getter/setter ]======================================================================.
     *
     * @return the device x size
     */

    /**
     * return effective device pixel size
     * @return
     */
    public int getDeviceXSize() {
        return deviceXSize;
    }

    /**
     * return effective device pixel size.
     *
     * @return the device y size
     */
    public int getDeviceYSize() {
        return deviceYSize;
    }

    /**
     * return effective BUFFER size.
     *
     * @return the buffer x size
     */
    public int getBufferXSize() {
        return bufferWidth;
    }

    /**
     * return effective BUFFER size.
     *
     * @return the buffer y size
     */
    public int getBufferYSize() {
        return bufferHeight;
    }

    /**
     * Gets the device size.
     *
     * @return the device size
     */
    public int getDeviceSize() {
        return deviceSize;
    }


}
