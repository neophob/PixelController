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


public class Sound implements SeSound {

	private static Sound instance = new Sound();

	private SeSound implementation=null;
	
	private Sound() {
		
	}

	/**
	 * the setter
	 * @param implementation
	 */
	public synchronized void setImplementation(SeSound implementation) {
		this.implementation = implementation;
	}

	public static Sound getInstance() {
		return instance;
	}

	/**
	 * get current volume
	 * @return
	 */
	public float getVolume() {
		return implementation.getVolume();
	}

	public float getVolumeNormalized() {
		return implementation.getVolumeNormalized();
	}

	public boolean isKick() {
		return implementation.isKick();
	}

	public boolean isSnare() {
		return implementation.isSnare();
	}

	public boolean isHat() {
		return implementation.isHat();
	}
	
	public boolean isPang() {
		return implementation.isPang();
	}
	
	public void shutdown() {
		implementation.shutdown();
	}

	@Override
	public int getFftAvg() {		
		return implementation.getFftAvg();
	}
	
	public float getFftAvg(int i) {
		return implementation.getFftAvg(i);
	}

}
