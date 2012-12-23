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

package com.neophob.sematrix.output.gamma;

/**
 * create gamma tab for color correction
 * code ripped from https://github.com/scottjgibson/lightingPi/blob/master/lightingPi.py
 * 
 * @author michu
 *
 */
public abstract class Gammatab {

	//create gammatab for lpd6803 based led strips
	public static int[] lpd8806GammaTab() {
		int[] ret = new int[256];
		
		for (int i=0; i<256; i++) {
			ret[i] = 0x80|(int)(Math.pow ((float)(i)/255.0f, 2.5f)*127.0f+0.5f);
		}
		
		return ret;
	}

	//create gammatab for lpd6803 based led strips
	public static int[] lpd6803GammaTab() {
		int[] ret = new int[256];
		
		for (int i=0; i<256; i++) {
			ret[i] = (int)(Math.pow ((float)(i)/255.0f, 2.0f)*255.0f+0.5f);
		}
		
		return ret;
	}

	//create gammatab for ws2801 based led strips
	public static int[] ws2801GammaTab() {
		int[] ret = new int[256];
		
		for (int i=0; i<256; i++) {
			ret[i] = (int)(Math.pow ((float)(i)/255.0f, 2.5f)*255.0f+0.5f);
		}
		
		return ret;
	}

}
