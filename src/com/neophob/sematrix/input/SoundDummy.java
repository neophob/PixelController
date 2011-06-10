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

import java.util.Random;

public class SoundDummy implements SeSound {

	private Random random;
	
	public SoundDummy() {
		random = new Random();
	}
	
	/**
	 * get current volume
	 * @return
	 */
	public float getVolume() {
		return random.nextFloat();
	}

	public float getVolumeNormalized() {
		return getVolume();
	}

	public boolean isKick() {
		return random.nextBoolean();
	}

	public boolean isSnare() {
		return random.nextBoolean();
	}

	public boolean isHat() {
		return random.nextBoolean();
	}
	
	public boolean isPang() {
		return random.nextBoolean();
	}
	
	public int getFftAvg() {		
		return 1;
	}
	
	public float getFftAvg(int i) {
		return 1.0f;
	}

	public void shutdown() {
	}
	
}
