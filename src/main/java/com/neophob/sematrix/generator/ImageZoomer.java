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

import processing.core.PApplet;
import processing.core.PImage;

import com.neophob.sematrix.glue.Collector;
import com.neophob.sematrix.resize.Resize.ResizeName;

/**
 * 
 * @author michu
 * TODO voluminize
 * 
 */
public class ImageZoomer extends Generator {

	private static Logger log = Logger.getLogger(ImageZoomer.class.getName());

	public static final String PREFIX = "pics/";
	public static final int MOVE_DURATION_IN_S = 4;

	private PImage origImg, clipedImg;  // Declare variable "a" of type PImage 
	
	private float zoom = 1;
	private float distZoom, beginZoom;

	private float beginX = 0.0f;  // Initial x-coordinate
	private float beginY = 0.0f;  // Initial y-coordinate
	private float distX;          // X-axis distance to move
	private float distY;          // Y-axis distance to move
	private float exponent = 64/2;   // Determines the curve
	private float x = 0.0f;        // Current x-coordinate
	private float y = 0.0f;        // Current y-coordinate
	private float step = 0.01f;    // Size of each step along the path
	private float pct = 1.0f;      // Percentage traveled (0.0 to 1.0)

	private String filename;
	
	public ImageZoomer(PixelControllerGenerator controller, String filename) {
		super(controller, GeneratorName.IMAGE_ZOOMER, ResizeName.QUALITY_RESIZE);				
		clipedImg = Collector.getInstance().getPapplet().createImage(internalBufferXSize, internalBufferYSize, PApplet.RGB);
		this.loadImage(filename);
		log.log(Level.INFO, "IMAGE SIZE: "+origImg.width+" "+internalBufferXSize+", "+internalBufferYSize);
		
		step = (1.0f/MOVE_DURATION_IN_S)/Collector.getInstance().getFps();
	}

	/**
	 * load a new file
	 * @param filename
	 */
	public void loadImage(String filename) {
		//only load if needed
		if (StringUtils.equals(filename, this.filename)) {
			return;
		}

		this.filename = filename;
		try {
			origImg = Collector.getInstance().getPapplet().loadImage(PREFIX+filename);
			if (origImg==null || origImg.height<2) {
				throw new InvalidParameterException("invalid data");
			}
			this.updateTarget();
		} catch (Exception e) {
			log.log(Level.WARNING,
					"Failed to load image {0}!", new Object[] { filename });
		}	
	}

	@Override
	public void update() {
		if (origImg==null) {
			log.log(Level.WARNING, "image is null!");
			return;
		}
		
		doTheMove();
		
		//get piece of large image		
		origImg.loadPixels();
		clipedImg.copy(origImg, (int)x, (int)y, internalBufferXSize, internalBufferYSize, 0, 0, internalBufferXSize, internalBufferYSize);
		origImg.updatePixels();
		
		//save it to internal buffer
		clipedImg.loadPixels();
		System.arraycopy(clipedImg.pixels, 0, this.internalBuffer, 0, internalBufferXSize*internalBufferYSize);		
		clipedImg.updatePixels();
		
	}

	/**
	 * 
	 */
	private void updateTarget() {
		pct = 0.0f;
		if (x>origImg.width-internalBufferXSize) {
			beginX = 0;
		} else {
			beginX = x;			
		}
		if (y>origImg.height-internalBufferYSize) {
			beginY = 0;
		} else {
			beginY = y;
		}

		float endX = (float)Math.random()*(origImg.width-internalBufferXSize);
		float endY = (float)Math.random()*(origImg.height-internalBufferYSize);
		distX = endX - beginX;
		distY = endY - beginY;

		beginZoom = zoom;
		float endZoom = 1.0f+(float)Math.random()*1.5f;
		distZoom = endZoom-beginZoom;
	}

	/**
	 * 
	 */
	private void doTheMove() {
		pct += step;
		if (pct > 1.0) {
			updateTarget();
		}

		x = beginX + (pct * distX);
		y = beginY + ((float)Math.pow(pct, exponent) * distY);
		zoom = beginZoom + (pct * distZoom);
	}

	
	public String getFilename() {
		return filename;
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
	}



}
