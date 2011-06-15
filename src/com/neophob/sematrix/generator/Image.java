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

import org.apache.commons.lang.StringUtils;

import processing.core.PImage;

import com.neophob.sematrix.glue.Collector;
import com.neophob.sematrix.resize.PixelControllerResize;
import com.neophob.sematrix.resize.Resize.ResizeName;

/**
 * display an image
 * 
 * @author mvogt
 *
 */
public class Image extends Generator {

	public static final String PREFIX = "pics/";
	
	private static final ResizeName RESIZE_TYP = ResizeName.PIXEL_RESIZE;	
	private static Logger LOG = Logger.getLogger(Image.class.getName());
	
	private String filename="http://neophob.com";
	
	/**
	 * 
	 * @param filename
	 */
	public Image(PixelControllerGenerator controller, String filename) {
		super(controller, GeneratorName.IMAGE, RESIZE_TYP);
		this.loadFile(filename);
	}
	
	/**
	 * load a new file
	 * @param filename
	 */
	public void loadFile(String filename) {
		//only load if needed
		if (StringUtils.equals(filename, this.filename)) {
			LOG.log(Level.INFO, "new filename does not differ from old: "+Image.PREFIX+filename);
			return;
		}
		
		this.filename = filename;
		try {
			PImage tmp = Collector.getInstance().getPapplet().loadImage(Image.PREFIX+filename);
			if (tmp==null || tmp.height<2) {
				LOG.log(Level.WARNING, "could not load "+Image.PREFIX+filename+" "+tmp);
				return;
			}
			LOG.log(Level.INFO, "resize to img "+filename+" "+internalBufferXSize+", "+internalBufferYSize);

			PixelControllerResize res = Collector.getInstance().getPixelControllerResize();
			
			tmp.loadPixels();
			this.internalBuffer = res.resizeImage(RESIZE_TYP, tmp.pixels, 
					tmp.width, tmp.height, internalBufferXSize, internalBufferYSize);
			tmp.updatePixels();
			
			//pimage.resize(internalBufferXSize, internalBufferYSize);
		} catch (Exception e) {
			e.printStackTrace();
			LOG.log(Level.WARNING,
					"Failed to load image {0}: {1}", new Object[] { Image.PREFIX+filename,e });
		}
	}

	
	@Override
	public void update() {
		//just relax here...
	}

	
	public String getFilename() {
		return filename;
	}

	@Override
	public void close() {
		// nothing todo
	}
}
