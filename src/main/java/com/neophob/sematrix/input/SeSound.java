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

package com.neophob.sematrix.input;

/**
 * The Interface SeSound.
 *
 * @author michu
 */
public interface SeSound {

	/**
	 * Gets the volume.
	 *
	 * @return the volume
	 */
	float getVolume();
	
	/**
	 * Gets the volume normalized.
	 *
	 * @return the volume normalized
	 */
	float getVolumeNormalized();
	
	/**
	 * Checks if is kick.
	 *
	 * @return true, if is kick
	 */
	boolean isKick();
	
	/**
	 * Checks if is snare.
	 *
	 * @return true, if is snare
	 */
	boolean isSnare();
	
	/**
	 * Checks if is hat.
	 *
	 * @return true, if is hat
	 */
	boolean isHat();
	
	/**
	 * Checks if is pang.
	 *
	 * @return true, if is pang
	 */
	boolean isPang();

	/**
	 * Shutdown.
	 */
	void shutdown();
	
	/**
	 * Gets the fft avg.
	 *
	 * @return the fft avg
	 */
	int getFftAvg();
	
	/**
	 * Gets the fft avg.
	 *
	 * @param i the i
	 * @return the fft avg
	 */
	float getFftAvg(int i);
}
