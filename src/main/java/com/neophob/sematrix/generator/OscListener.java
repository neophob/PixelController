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
package com.neophob.sematrix.generator;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.neophob.sematrix.resize.Resize.ResizeName;

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
			
	private byte[] buffer;
	
	/**
	 * Instantiates a new image.
	 *
	 * @param controller the controller
	 * @param filename the filename
	 */
	public OscListener(PixelControllerGenerator controller, GeneratorName generatorName) {
		super(controller, generatorName, RESIZE_TYP);
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
			this.buffer = buffer;			
			int ofs=0;
			for (int i=0; i<internalBuffer.length; i++) {
				this.internalBuffer[i] = this.buffer[ofs++]; 
			}
		} else {
			LOG.log(Level.WARNING, "Invalid buffer size, expected size: {0}, effective size: {1}.", 
					new Object[] {this.internalBuffer.length, buffer.length} );
		}
	}
	
	/* (non-Javadoc)
	 * @see com.neophob.sematrix.generator.Generator#update()
	 */
	@Override
	public void update() {

	}

	/* (non-Javadoc)
	 * @see com.neophob.sematrix.generator.Generator#shuffle()
	 */
	@Override
	public void shuffle() {
		//not implemented
	}
	

}
