/**
 * Copyright (C) 2011 Michael Vogt <michu@neophob.com>
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
import com.neophob.sematrix.resize.Resize.ResizeName;

/**
 * matrix display buffer class
 * 
 * the internal buffer is much larger than the actual device. the buffer for the matrix is recalculated
 * each frame. reason: better display quality 
 * 
 * TODO:
 *  -need a color concept, some generators provide colors, some not, some change colors, some not...
 * 
 * @author mvogt
 *
 */
public abstract class Generator {

	public enum GeneratorName {
		PASSTHRU(0),
		BLINKENLIGHTS(1),
		IMAGE(2),
		PLASMA(3),
		SIMPLECOLORS(4),
		FIRE(5),
		METABALLS(6),
		PIXELIMAGE(7),
		TEXTURE_DEFORMATION(8),
		TEXTWRITER(9),
		IMAGE_ZOOMER(10),
		CELL(11),
		PLASMA_ADVANCED(12),
		FFT(13),
		GEOMETRICS(14);
		
		private int id;
		
		GeneratorName(int id) {
			this.id = id;
		}
		
		public int getId() {
			return id;
		}
	}
	
	private static Logger log = Logger.getLogger(Generator.class.getName());

	private GeneratorName name;
	
	private ResizeName resizeOption;
	
	//internal, larger buffer
	public int[] internalBuffer;
	protected int internalBufferXSize;
	protected int internalBufferYSize;
	
	/**
	 * 
	 * @param name
	 */
	public Generator(PixelControllerGenerator controller, GeneratorName name, ResizeName resizeOption) {
		this.name = name;
		this.resizeOption = resizeOption;
		MatrixData matrix = Collector.getInstance().getMatrix();
		this.internalBufferXSize = matrix.getBufferXSize();
		this.internalBufferYSize = matrix.getBufferYSize();
		this.internalBuffer = new int[internalBufferXSize*internalBufferYSize];

		log.log(Level.INFO,
				"SeMAtrixParent: internalBufferSize: {0} name: {1} "
				, new Object[] { internalBuffer.length, name });
		
		//add to list
		controller.addInput(this);
	}

	public abstract void update();
	
	public abstract void close();

	public int getInternalBufferXSize() {
		return internalBufferXSize;
	}

	public int getInternalBufferYSize() {
		return internalBufferYSize;
	}

	public int getInternalBufferSize() {
		return internalBuffer.length;
	}

	public GeneratorName getName() {
		return name;
	}
	
	public ResizeName getResizeOption() {
		return resizeOption;
	}

	public int[] getBuffer() {
		return internalBuffer;
	}
	
	/**
	 * used for debug output
	 * @return
	 */
	public PImage getBufferAsImage() {
		PImage pImage = Collector.getInstance().getPapplet().createImage
							( internalBufferXSize, internalBufferYSize, PApplet.RGB );
		pImage.loadPixels();
		System.arraycopy(internalBuffer, 0, pImage.pixels, 0, internalBuffer.length);
		pImage.updatePixels();
		return pImage;
	}

	public int getId() {
		return this.name.getId();
	}
}
