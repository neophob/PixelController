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

import java.util.logging.Level;
import java.util.logging.Logger;



/**
 * Output Helper
 *
 * @author michu
 */
public class OutputHelper {

	/** The log. */
	private static final Logger LOG = Logger.getLogger(OutputHelper.class.getName());

	private OutputHelper() {
		//no instance allowed
	}
	
	/**
	 * this function feed the framebufferdata (32 pixels a 2bytes (aka 16bit)
	 * to the send array. each second scanline gets inverteds
	 *
	 * @param cmdfull the cmdfull
	 * @param frameData the frame data
	 */
	public static int[] flipSecondScanline(int buffer[], int xResolution, int yResolution) {
		int bufferTwo[] = buffer.clone();
		
		for (int y=0; y<yResolution; y++) {
			if (y%2==1) {
				int ofs = y*xResolution;
				for (int x=0; x<xResolution; x++) {
					bufferTwo[ofs+x] = buffer[xResolution+ofs-x-1];
				}
			}
		}		
		return bufferTwo;
	}
	
	/**
	 * do manual mapping, this is used to support a more exotic device configuration
	 * 
	 * @param buffer
	 * @param xResolution
	 * @param yResolution
	 * @return
	 */
	public static int[] manualMapping(int src[], int mapping[], int xResolution, int yResolution) {
		int bufferTwo[] = src.clone();
		int lenght = src.length;
		int ofs=0;
		for (int i: mapping) {
			if (i+1>lenght) {
				LOG.log(Level.SEVERE, "Your manual mapping is wrong,the first index is 0! Invalid entry index: {0}", i);
			} else {
				bufferTwo[ofs++] = src[i]; 				
			}
		}
		return bufferTwo;
	}
}
