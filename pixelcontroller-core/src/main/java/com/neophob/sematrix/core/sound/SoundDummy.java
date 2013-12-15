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
package com.neophob.sematrix.core.sound;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The Class SoundDummy.
 */
public class SoundDummy implements ISound {
	
	private transient static final Logger LOG = Logger.getLogger(SoundDummy.class.getName());

	/**
	 * Instantiates a new sound dummy.
	 */
	public SoundDummy() {
		LOG.log(Level.INFO,	"Sound thread started, dummy implementation...");
	}
	
	/**
	 * get current volume.
	 *
	 * @return the volume
	 */
	public float getVolume() {
		return 0.5f;
	}

	/* (non-Javadoc)
	 * @see com.neophob.sematrix.core.input.SeSound#getVolumeNormalized()
	 */
	public float getVolumeNormalized() {
		return getVolume();
	}

	/* (non-Javadoc)
	 * @see com.neophob.sematrix.core.input.SeSound#isKick()
	 */
	public boolean isKick() {
		return false;
	}

	/* (non-Javadoc)
	 * @see com.neophob.sematrix.core.input.SeSound#isSnare()
	 */
	public boolean isSnare() {
		return false;
	}

	/* (non-Javadoc)
	 * @see com.neophob.sematrix.core.input.SeSound#isHat()
	 */
	public boolean isHat() {
		return false;
	}
	
	/* (non-Javadoc)
	 * @see com.neophob.sematrix.core.input.SeSound#isPang()
	 */
	public boolean isPang() {
		return false;
	}
	
	/* (non-Javadoc)
	 * @see com.neophob.sematrix.core.input.SeSound#getFftAvg()
	 */
	public int getFftAvg() {		
		return 1;
	}
	
	/* (non-Javadoc)
	 * @see com.neophob.sematrix.core.input.SeSound#getFftAvg(int)
	 */
	public float getFftAvg(int i) {
		return 1.0f;
	}

	/* (non-Javadoc)
	 * @see com.neophob.sematrix.core.input.SeSound#shutdown()
	 */
	public void shutdown() {
	}

	@Override
	public String getImplementationName() {		
		return "Dummy Sound";
	}
	
}
