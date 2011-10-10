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

import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.neophob.sematrix.resize.Resize;
import com.neophob.sematrix.resize.Resize.ResizeName;
import com.neophob.sematrix.resize.util.ScalrOld;


/**
 *
 * @author mvogt
 */
public class ScreenCapture extends Generator {

	/** The log. */
	private static final Logger LOG = Logger.getLogger(ScreenCapture.class.getName());

	private Robot robot;
	private Rectangle rectangle;
	private int frames;
	
	/**
	 * Instantiates a new plasma2.
	 *
	 * @param controller the controller
	 */
	public ScreenCapture(PixelControllerGenerator controller, int xOffset, int yOffset) {
		super(controller, GeneratorName.SCREEN_CAPTURE, ResizeName.QUALITY_RESIZE);
		
		rectangle = new Rectangle(xOffset, yOffset, internalBufferXSize*2, internalBufferYSize*2);
		
		try {
			robot = new Robot();
			LOG.log(Level.INFO, "ScreenCapture initialized, offset "+rectangle.x+"/"+rectangle.y
					+", size: "+rectangle.width+"/"+rectangle.height);
			
		} catch (Exception e) {
			LOG.log(Level.SEVERE, "Failed to initialize ScreenCapture: ", e);
		}

	}

	/* (non-Javadoc)
	 * @see com.neophob.sematrix.generator.Generator#update()
	 */
	@Override
	public void update() {
		frames++;
		
		//capture each 2nd frame
		if (frames%2==1 && robot != null) {
			BufferedImage screencapture = robot.createScreenCapture(rectangle);
			screencapture = ScalrOld.resize(screencapture, ScalrOld.Method.QUALITY, internalBufferXSize, internalBufferYSize);

			this.internalBuffer = Resize.getPixelsFromImage(screencapture, internalBufferXSize, internalBufferYSize);
		}		
	}

}
