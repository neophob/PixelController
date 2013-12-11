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
package com.neophob.sematrix.core.visual;

import java.security.InvalidParameterException;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    private final int deviceXSize;

    /** The device y size. */
    private final int deviceYSize;

    /** The device size. */
    private final int deviceSize;

    /** internal buffer size */
    private int bufferWidth;
    private int bufferHeight;

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
        int internalBufferSizeMultiplier = 8;
        bufferWidth = deviceXSize*internalBufferSizeMultiplier;
        bufferHeight = deviceYSize*internalBufferSizeMultiplier;
        
        while (getRgbBufferSize()>60*1024 && internalBufferSizeMultiplier>1) {
        	internalBufferSizeMultiplier/=2;
            bufferWidth = deviceXSize*internalBufferSizeMultiplier;
            bufferHeight = deviceYSize*internalBufferSizeMultiplier;
        }

        LOG.log(Level.INFO, "screenSize: {0} ({1} * {2}), multiplication factor: {3} ({4} * {5})", 
        		new Object[] { deviceSize, deviceXSize, deviceYSize, internalBufferSizeMultiplier, bufferWidth, bufferHeight});        
    }

    /**
     * 
     * @return
     */
    private int getRgbBufferSize() {
    	return getBufferXSize()*getBufferYSize()*3;
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

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String
				.format("MatrixData [deviceXSize=%s, deviceYSize=%s, deviceSize=%s, bufferWidth=%s, bufferHeight=%s]",
						deviceXSize, deviceYSize, deviceSize, bufferWidth,
						bufferHeight);
	}


}
