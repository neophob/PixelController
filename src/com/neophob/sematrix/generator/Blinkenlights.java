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

import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang.StringUtils;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.lib.blinken.BlinkenLibrary;

import com.neophob.sematrix.glue.Collector;
import com.neophob.sematrix.resize.Resize.ResizeName;

/**
 * @author mvogt
 *
 */
public class Blinkenlights extends Generator implements PConstants {

	private static final String PREFIX = "blinken/";
	static Logger log = Logger.getLogger(Blinkenlights.class.getName());

	private BlinkenLibrary blinken;
	private boolean random;
	private Random rand = new Random();
	private int frames;
	private int movieFrames;
	
	private String filename="";

	public Blinkenlights(String filename) {
		super(GeneratorName.BLINKENLIGHTS, ResizeName.QUALITY_RESIZE);
		this.filename = filename;
		PApplet parent = Collector.getInstance().getPapplet();
		random=false;
		blinken = new BlinkenLibrary(parent, PREFIX+filename);
		blinkenSettings();
	}

	/**
	 * load a new file
	 * @param file
	 */
	public void loadFile(String file) {
		//only load if needed
		if (!StringUtils.equals(file, this.filename)) {
			log.log(Level.INFO, "Load blinkenlights file "+file);
			this.filename = file;
			blinken.loadFile(PREFIX+file);
			log.log(Level.INFO, "Load blinkenlights done!");
		}
		blinkenSettings();
	}
	
	private void blinkenSettings() {
		blinken.setIgnoreFileDelay(true);
		blinken.noLoop();
		blinken.stop();
		movieFrames = blinken.getNrOfFrames();
		//frames=0;
	}
	
	@Override
	public void update() {
		if (random) {
			blinken.jump(
					rand.nextInt(blinken.getNrOfFrames())
			);
		} else {
			try {
				blinken.jump(frames%movieFrames);
				frames++;				
			} catch (Exception e) {
				//a npe exception might happen if a new file is loaded!
			}
		}

		//resize image to 128x128
		int ofs, dst=0, xofs, yofs=0;		
		float xSrc,ySrc=0;
		float xDiff = internalBufferXSize/(float)blinken.width;
		float yDiff = internalBufferYSize/(float)blinken.height;
		
		try {
			for (int y=0; y<internalBufferYSize; y++) {
				if (ySrc>yDiff) {
					if (yofs<blinken.height) yofs++;				
					ySrc-=yDiff;
				}
				xofs=0;
				xSrc=0;
				for (int x=0; x<internalBufferXSize; x++) {
					if (xSrc>xDiff) {
						if (xofs<blinken.width)xofs++;
						xSrc-=xDiff;
					}				
					ofs=xofs+yofs*blinken.width;
					this.internalBuffer[dst++]=blinken.pixels[ofs];
					xSrc++;
				}
				ySrc++;
			}			
		} catch (ArrayIndexOutOfBoundsException e) {
			log.log(Level.SEVERE, "Failed to update internal buffer", e);
		}
	}
	
	public boolean isRandom() {
		return random;
	}

	public void setRandom(boolean random) {
		this.random = random;
		if (random) {
			blinken.noLoop();
			blinken.stop();
		} else {
			blinken.loop();
		}
	}
	
	

	public String getFilename() {
		return filename;
	}

	@Override
	public void close() {
		blinken.dispose();
	}
}
