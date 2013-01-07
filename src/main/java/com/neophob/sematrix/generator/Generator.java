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

import processing.core.PApplet;
import processing.core.PImage;

import com.neophob.sematrix.glue.Collector;
import com.neophob.sematrix.glue.MatrixData;
import com.neophob.sematrix.glue.RandomizeState;
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
public abstract class Generator implements RandomizeState {

	/**
	 * The Enum GeneratorName.
	 */
	public enum GeneratorName {
		
		/** The PASSTHRU. */
		PASSTHRU(0),
		
		/** The BLINKENLIGHTS. */
		BLINKENLIGHTS(1),
		
		/** The IMAGE. */
		IMAGE(2),
		
		/** The PLASMA. */
		PLASMA(3),
		
		/** The COLOR_SCROLL. */
		COLOR_SCROLL(4),
		
		/** The FIRE. */
		FIRE(5),
		
		/** The METABALLS. */
		METABALLS(6),
		
		/** The PIXELIMAGE. */
		PIXELIMAGE(7),
		
		COLOR_FADE(8),
		
		/** The TEXTWRITER. */
		TEXTWRITER(9),
		
		/** The IMAGE zoomer. */
		DROPS(10),
		
		/** The CELL. */
		CELL(11),
		
		/** The PLASMA advanced. */
		PLASMA_ADVANCED(12),
		
		/** The FFT. */
		FFT(13),
		
		/** The SCREEN_CAPTURE. */
		SCREEN_CAPTURE(14), 
        
		;
		
		/** The id. */
		private int id;
		
		/**
		 * Instantiates a new generator name.
		 *
		 * @param id the id
		 */
		GeneratorName(int id) {
			this.id = id;
		}
		
		/**
		 * Gets the id.
		 *
		 * @return the id
		 */
		public int getId() {
			return id;
		}
	}
	
	/** The log. */
	private static final Logger LOG = Logger.getLogger(Generator.class.getName());

	/** The name. */
	private GeneratorName name;
	
	/** The resize option. */
	private ResizeName resizeOption;
	
	//internal, larger buffer
	/** The internal buffer. */
	public int[] internalBuffer;
	
	/** The internal buffer x size. */
	protected int internalBufferXSize;
	
	/** The internal buffer y size. */
	protected int internalBufferYSize;
	
	/** is the generator selected and thus active? */
	protected boolean active;
		
	/**
	 * Instantiates a new generator.
	 *
	 * @param controller the controller
	 * @param name the name
	 * @param resizeOption the resize option
	 */
	public Generator(PixelControllerGenerator controller, GeneratorName name, ResizeName resizeOption) {
		this.name = name;
		this.resizeOption = resizeOption;
		MatrixData matrix = Collector.getInstance().getMatrix();
		this.internalBufferXSize = matrix.getBufferXSize();
		this.internalBufferYSize = matrix.getBufferYSize();
		this.internalBuffer = new int[internalBufferXSize*internalBufferYSize];

		LOG.log(Level.INFO,
				"Generator: internalBufferSize: {0} name: {1} "
				, new Object[] { internalBuffer.length, name });
		
		//add to list
		controller.addInput(this);
		this.active = false;
	}

	/**
	 * update the generator.
	 */
	public abstract void update();
	
	/**
	 * deinit generator.
	 */
	public void close() {
		//nothing todo
	}

	/**
	 * Gets the internal buffer x size.
	 *
	 * @return the internal buffer x size
	 */
	public int getInternalBufferXSize() {
		return internalBufferXSize;
	}

	/**
	 * Gets the internal buffer y size.
	 *
	 * @return the internal buffer y size
	 */
	public int getInternalBufferYSize() {
		return internalBufferYSize;
	}

	/**
	 * Gets the internal buffer size.
	 *
	 * @return the internal buffer size
	 */
	public int getInternalBufferSize() {
		return internalBuffer.length;
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public GeneratorName getName() {
		return name;
	}
	
	/**
	 * Gets the resize option.
	 *
	 * @return the resize option
	 */
	public ResizeName getResizeOption() {
		return resizeOption;
	}

	/**
	 * Gets the buffer.
	 *
	 * @return the buffer
	 */
	public int[] getBuffer() {
		return internalBuffer;
	}
	
	/**
	 * used for debug output.
	 *
	 * @return the buffer as image
	 */
	public PImage getBufferAsImage() {
		PImage pImage = Collector.getInstance().getPapplet().createImage
							( internalBufferXSize, internalBufferYSize, PApplet.RGB );
		pImage.loadPixels();
		System.arraycopy(internalBuffer, 0, pImage.pixels, 0, internalBuffer.length);
		pImage.updatePixels();
		return pImage;
	}
	
	
	/* (non-Javadoc)
	 * @see com.neophob.sematrix.glue.RandomizeState#shuffle()
	 */
	public void shuffle() {
		//default shuffle method - do nothing
	}

	/**
	 * this method get called if the generator gets activated
	 */
	protected void nowActive() {
		
	}

	/**
	 * this method get called if the generator gets inactive
	 */
	protected void nowInactive() {
		
	}

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public int getId() {
		return this.name.getId();
	}

	/**
	 * is generator selected?
	 * @return
	 */
	public boolean isActive() {
		return active;
	}

	/**
	 * update state
	 * @param active
	 */
	public void setActive(boolean active) {
		if (!active && this.active) {
			nowInactive();
		} else if(active && !this.active) {
			nowActive();
		}
		this.active = active;
	}
		
}
