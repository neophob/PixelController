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

import com.neophob.sematrix.glue.Collector;
import com.neophob.sematrix.properties.DeviceConfig;

/**
 * this class will transform a buffer
 * 
 * @author michu
 *
 */
public final class RotateBuffer {

	//TODO baaad!
	private static int deviceXSize = 8;

	private static Logger LOG = Logger.getLogger(RotateBuffer.class.getName());

	private RotateBuffer() {
		//no instance
	}

	/**
	 * 
	 * @param buffer
	 * @return
	 */
	private static int[] rotate90(int[] buffer) {
		int[] ret = new int[deviceXSize*deviceXSize];
		int ofs=0;
		for (int x=0; x<deviceXSize; x++) {			
			for (int y=0; y<deviceXSize; y++) {
				ret[deviceXSize*y+deviceXSize-1-x] = buffer[ofs++];
			}
		}
		return ret;
	}

	/**
	 * 
	 * @param buffer
	 * @return
	 */
	private static int[] flipY(int[] buffer) {
		int[] ret = new int[deviceXSize*deviceXSize];
		int ofs=0;
		for (int x=0; x<deviceXSize; x++) {			
			for (int y=0; y<deviceXSize; y++) {
				//flipX
				//ret[deviceXSize-1-y+x*deviceXSize] = buffer[ofs++];
				ret[(deviceXSize-1-x)*deviceXSize+y] = buffer[ofs++];
			}
		}
		return ret;
	}

	private static int[] rotate180(int[] buffer) {
		int[] ret = new int[deviceXSize*deviceXSize];
		int ofs=0;
		for (int x=0; x<deviceXSize; x++) {			
			for (int y=0; y<deviceXSize; y++) {
				ret[deviceXSize-1-y + (deviceXSize-1-x)*deviceXSize] = buffer[ofs++];
			}
		}
		return ret;
	}


	/**
	 * 
	 * @param buffer
	 * @return
	 */
	private static int[] rotate270(int[] buffer) {
		int[] ret = new int[deviceXSize*deviceXSize];
		int ofs=0;
		for (int x=0; x<deviceXSize; x++) {			
			for (int y=0; y<deviceXSize; y++) {
				ret[x+deviceXSize*(deviceXSize-1-y)] = buffer[ofs++];
				//flip at 0.0
				//ret[x+deviceXSize*y] = buffer[ofs++];
			}
		}
		return ret;
	}

	/**
	 * TODO add x/y options
	 * 
	 * @param buffer
	 * @param deviceConfig
	 * @return
	 */
	public static int[] transformImage(int[] buffer, DeviceConfig deviceConfig) {

		if (deviceXSize==0) {
			deviceXSize = Collector.getInstance().getMatrix().getDeviceXSize();
		}

		switch (deviceConfig) {
		case NO_ROTATE:
			return buffer;

		case ROTATE_90:
			return rotate90(buffer);			

		case ROTATE_90_FLIPPEDY:
			return flipY(
					rotate90(buffer)
			);

		case ROTATE_180:
			return rotate180(buffer);

		case ROTATE_180_FLIPPEDY:
			return flipY( 
					rotate180(buffer) 
			);

		case ROTATE_270:
			return rotate270(buffer);

		case ROTATE_270_FLIPPEDY:
			return flipY(
					rotate270(buffer)
			);

		default:
			LOG.log(Level.SEVERE, "invalid device config: {0}", deviceConfig);			
			break;
		}
		return null;
	}
}
