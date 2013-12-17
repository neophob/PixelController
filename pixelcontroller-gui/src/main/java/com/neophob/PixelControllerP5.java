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
package com.neophob;

import java.util.logging.Level;

import processing.core.PApplet;

import com.neophob.sematrix.gui.service.impl.LocalServer;


/**
 * The Class PixelController.
 *
 * @author michu
 */
public class PixelControllerP5 extends AbstractPixelControllerP5 {  

	public void initPixelController() {
		pixelController = new LocalServer(this);
		pixelController.start();
		LOG.log(Level.INFO, "LocalServer created");
	}

	/* (non-Javadoc)
	 * @see processing.core.PApplet#draw()
	 */
	public void draw() {
	    if (!pixelController.isInitialized()) {	    	
	        return;
	    } else if (!postInitDone) {
	    	postSetupInitialisation();
	    	return;
	    }
	    		
		// update matrixEmulator instance
		long startTime = System.currentTimeMillis();

		this.matrixEmulator.update();
		pixelController.updateNeededTimeForMatrixEmulator(System.currentTimeMillis() - startTime);
	}

	public static void main(String args[]) {
		PApplet.main(new String[] { PixelControllerP5.class.getName().toString() });
	}

}
