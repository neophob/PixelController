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
package com.neophob.sematrix.core.visual.generator;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.neophob.sematrix.core.resize.Resize.ResizeName;
import com.neophob.sematrix.core.visual.MatrixData;

/**
 * display an image.
 *
 * @author mvogt
 */
public class OscListener extends Generator {

    /** The Constant RESIZE_TYP. */
    private static final ResizeName RESIZE_TYP = ResizeName.QUALITY_RESIZE;	

    /** The Constant LOG. */
    private static final Logger LOG = Logger.getLogger(OscListener.class.getName());

    private long lastUpdateTs;
    
    private boolean passthoughMode = false;

    /**
     * Instantiates a new image.
     *
     * @param controller the controller
     * @param filename the filename
     */
    public OscListener(MatrixData matrix, GeneratorName generatorName) {
        super(matrix, generatorName, RESIZE_TYP);
    }

    /**
     * TODO, resize image, synchronize update
     * 
     * @param buffer
     */
    public void updateBuffer(byte[] buffer) {
        if (buffer==null) {
            LOG.log(Level.WARNING, "buffer is null!");
            return;
        }

        if (buffer.length == this.internalBuffer.length) {
            //osc send 8bpp - regular pixelcontroller function with effects and mixer
            //please note: I cant use System.arraycopy as we convert a byte into a int 
            for (int i=0; i<internalBuffer.length; i++) {
                this.internalBuffer[i] = buffer[i];
            }
            passthoughMode = false;
            lastUpdateTs = System.currentTimeMillis();
        } else if (buffer.length == this.internalBuffer.length*3) {
            //osc send 24bpp - passthough
        	int src=0;
            for (int i=0; i<internalBuffer.length; i++) {
            	int r = buffer[src++]&255;
            	int g = buffer[src++]&255;
            	int b = buffer[src++]&255;
                this.internalBuffer[i] = (r << 16) | (g << 8) | (b);
            }        	
        	passthoughMode = true;
        	lastUpdateTs = System.currentTimeMillis();
        } else {
            LOG.log(Level.WARNING, "Invalid buffer size, expected size: {0}, effective size: {1}.", 
                    new Object[] {this.internalBuffer.length, buffer.length} );
        }
    }

    /* (non-Javadoc)
     * @see com.neophob.sematrix.core.generator.Generator#update()
     */
    @Override
    public void update() {

    }

    /* (non-Javadoc)
     * @see com.neophob.sematrix.core.generator.Generator#shuffle()
     */
    @Override
    public void shuffle() {
        //not implemented
    }

    @Override
    public boolean isInUse() {
        //if we recieved a osc packet, assume this generator is active
        return (System.currentTimeMillis()-lastUpdateTs)<2000;
    }

    @Override
	public boolean isPassThoughModeActive() {
		return passthoughMode;
	}

}
