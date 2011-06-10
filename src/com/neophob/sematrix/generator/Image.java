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

import java.security.InvalidParameterException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang.StringUtils;

import processing.core.PImage;

import com.neophob.sematrix.glue.Collector;
import com.neophob.sematrix.resize.Resize.ResizeName;

/**
 * @author mvogt
 *
 */
public class Image extends Generator {

	public static final String PREFIX = "pics/";
	private static Logger log = Logger.getLogger(Image.class.getName());
	
	private PImage pimage;
	
	private String filename;
	
	/**
	 * 
	 * @param filename
	 */
	public Image(String filename) {
		super(GeneratorName.IMAGE, ResizeName.PIXEL_RESIZE);
/*		PApplet parent = Collector.getInstance().getPapplet();
		pimage = parent.loadImage(filename);
		log.log(Level.INFO, "resize to img "+filename+" "+internalBufferXSize+", "+internalBufferYSize);
		pimage.resize(internalBufferXSize, internalBufferYSize);*/
		this.loadFile(filename);
	}
	
	/**
	 * load a new file
	 * @param filename
	 */
	public void loadFile(String filename) {
		//only load if needed
		if (StringUtils.equals(filename, this.filename)) {
			return;
		}
		
		this.filename = filename;
		try {
			PImage tmp = Collector.getInstance().getPapplet().loadImage(Image.PREFIX+filename);
			if (tmp==null || tmp.height<2) {
				throw new InvalidParameterException("invalid data");
			}
			pimage = tmp;
			log.log(Level.INFO, "resize to img "+filename+" "+internalBufferXSize+", "+internalBufferYSize);
			//TODO still buggy!
			pimage.resize(internalBufferXSize, internalBufferYSize);
		} catch (Exception e) {
			log.log(Level.WARNING,
					"Failed to load image {0}!", new Object[] { filename });
		}
	}

	
	@Override
	public void update() {
		pimage.loadPixels();
		System.arraycopy(pimage.pixels, 0, this.internalBuffer, 0, internalBufferXSize*internalBufferYSize);
		pimage.updatePixels();
	}

	
	public String getFilename() {
		return filename;
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
	}
}
