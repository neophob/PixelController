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

package com.neophob.sematrix.output.emulatorhelper;

import java.util.logging.Level;
import java.util.logging.Logger;

import processing.core.PApplet;
import processing.core.PImage;

import com.neophob.sematrix.glue.Collector;
import com.neophob.sematrix.glue.Visual;


public class InternalBuffer extends PApplet {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2344499301021L;

	private static final int BFR_X = 128;
	private static final int BFR_Y = 128;
	
	static Logger log = Logger.getLogger(InternalBuffer.class.getName());

	private boolean displayHoriz;
	private int x,y;
	private int[] buffer;
	private PImage pImage=null;

	/**
	 * 
	 * @param displayHoriz
	 * @param x
	 * @param y
	 */
	public InternalBuffer(boolean displayHoriz, int x, int y) {
		this.displayHoriz = displayHoriz;
		this.x = x;
		this.y = y;
	}
	
	/**
	 * 
	 */
    public void setup() {
    	log.log(Level.INFO, "create frame with size "+x+"/"+y);
        size(x,y);
        frameRate(Collector.getInstance().getFps());
        background(0,0,0);
    }

    /**
     * draw the whole internal buffer on screen
     */
	public void draw() {
		int x=0, y=0;
		for (Visual v: Collector.getInstance().getAllVisuals()) {
			//get image
			buffer = Collector.getInstance().getMatrix().resizeBufferForDevice(v.getBuffer(), v.getResizeOption(), BFR_X, BFR_Y);
			
			if (pImage==null) {
				//create an image out of the buffer
		 		pImage = Collector.getInstance().getPapplet().createImage( BFR_X, BFR_Y, PApplet.RGB );				
			}
			pImage.loadPixels();
			System.arraycopy(buffer, 0, pImage.pixels, 0, BFR_X*BFR_Y);
			pImage.updatePixels();

			//display it
			image(pImage,x,y);
			if (displayHoriz) {
				x += pImage.width;
			} else {
				y += pImage.height;
			}			
		}
	}


}
