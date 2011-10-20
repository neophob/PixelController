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

/**
 * The Enum OutputDeviceEnum.
 */
public enum OutputDeviceEnum {
	
	/** The PIXELINVADERS (LPD6803 Based) PANELS. */
	PIXELINVADERS(true),
	
	/** The RAINBOWDUINO. */
	RAINBOWDUINO(true),
	
	/** The ARTNET. */
	ARTNET(true),
	
	/** The MINIDMX. */
	MINIDMX(true),
	
	/** The NULL Output. */
	NULL(false);
	
	private boolean physical;
	
	private OutputDeviceEnum(boolean physical) {
		this.physical = physical;
	}
	
	public boolean isPhysical() {
		return this.physical;
	}
}
