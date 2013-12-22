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

import com.neophob.sematrix.gui.service.impl.RemoteOscServer;


/**
 * The Class PixelController.
 *
 * @author michu
 */
public class PixelControllerP5Remote extends AbstractPixelControllerP5 {  

	public void initPixelController() {
		try {
			pixelController = new RemoteOscServer(this);			
			pixelController.start();
			LOG.log(Level.INFO, "RemoteOscServer created");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		PApplet.main(new String[] { PixelControllerP5Remote.class.getName().toString() });
	}

}
