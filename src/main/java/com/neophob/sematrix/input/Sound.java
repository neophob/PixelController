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
package com.neophob.sematrix.input;


/**
 * The Class Sound.
 */
public final class Sound implements SeSound {

	/** The instance. */
	private static Sound instance = new Sound();

	/** The implementation. */
	private SeSound implementation=null;
	
	/**
	 * Instantiates a new sound.
	 */
	private Sound() {
		//no instance allowed
	}

	/**
	 * the setter.
	 *
	 * @param implementation the new implementation
	 */
	public synchronized void setImplementation(SeSound implementation) {
		this.implementation = implementation;
	}

	/**
	 * Gets the single instance of Sound.
	 *
	 * @return single instance of Sound
	 */
	public static Sound getInstance() {
		return instance;
	}

	/**
	 * get current volume.
	 *
	 * @return the volume
	 */
	public float getVolume() {
		return implementation.getVolume();
	}

	/* (non-Javadoc)
	 * @see com.neophob.sematrix.input.SeSound#getVolumeNormalized()
	 */
	public float getVolumeNormalized() {
		return implementation.getVolumeNormalized();
	}

	/* (non-Javadoc)
	 * @see com.neophob.sematrix.input.SeSound#isKick()
	 */
	public boolean isKick() {
		return implementation.isKick();
	}

	/* (non-Javadoc)
	 * @see com.neophob.sematrix.input.SeSound#isSnare()
	 */
	public boolean isSnare() {
		return implementation.isSnare();
	}

	/* (non-Javadoc)
	 * @see com.neophob.sematrix.input.SeSound#isHat()
	 */
	public boolean isHat() {
		return implementation.isHat();
	}
	
	/* (non-Javadoc)
	 * @see com.neophob.sematrix.input.SeSound#isPang()
	 */
	public boolean isPang() {
		return implementation.isPang();
	}
	
	/* (non-Javadoc)
	 * @see com.neophob.sematrix.input.SeSound#shutdown()
	 */
	public void shutdown() {
		implementation.shutdown();
	}

	/* (non-Javadoc)
	 * @see com.neophob.sematrix.input.SeSound#getFftAvg()
	 */
	@Override
	public int getFftAvg() {		
		return implementation.getFftAvg();
	}
	
	/* (non-Javadoc)
	 * @see com.neophob.sematrix.input.SeSound#getFftAvg(int)
	 */
	public float getFftAvg(int i) {
		return implementation.getFftAvg(i);
	}

	@Override
	public String getImplementationName() {
		return implementation.getImplementationName();
	}

}
