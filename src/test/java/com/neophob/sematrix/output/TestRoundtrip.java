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

package com.neophob.sematrix.output;

import processing.core.PApplet;

import com.neophob.sematrix.output.lpd6803.Lpd6803;

/**
 * simply test class, only used to test the lib<br>
 * <br>
 * part of the neorainbowduino library
 * 
 * @author Michael Vogt / neophob.com
 *
 */
public class TestRoundtrip extends PApplet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5014930687650644180L;
	
	
	Lpd6803 r;
	int [] frame1;
	byte [] frame2;
	 
	/** 
	 * ss
	 */
	public void setup() {		 
		frameRate(500);
		
		try {
			r = new Lpd6803(this, 9600, null);
			long l1 = System.currentTimeMillis();
			r.ping();
			long l2= System.currentTimeMillis()-l1;
			System.out.println("need "+l2+"ms to send ping");
		} catch (Exception e) {
			e.printStackTrace(); 
		}
		frame1 = new int[64];
		frame2 = new byte[128];
		
	}
	
	public void draw() {
		long l1 = System.currentTimeMillis();
		//r.sendRgbFrame((byte)1, frame1);
		r.sendFrame((byte)0, frame2);
		//r.ping();
		long l2= System.currentTimeMillis()-l1;
		System.out.println("need "+l2+"ms to send data");
	}
	
	public static void main(String args[]) {
		PApplet.main(new String[] { "com.neophob.sematrix.output.TestRoundtrip" });
	}

}
