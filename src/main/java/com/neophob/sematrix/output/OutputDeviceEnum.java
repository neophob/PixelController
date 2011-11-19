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
	PIXELINVADERS(Lpd6803Device.class, true),
	
	/** The RAINBOWDUINO. */
	RAINBOWDUINO(RainbowduinoDevice.class, true),
	
	/** The ARTNET. */
	ARTNET(ArtnetDevice.class, true),
	
	/** The MINIDMX. */
	MINIDMX(MiniDmxDevice.class, true),

	/** The ADALIGHT. */
	ADALIGHT(AdaLight.class, true),

	/** The NULL Output. */
	NULL(NullDevice.class, false);
	
	/** The implementing class. */
	private Class<? extends Output> implementingClass;
	
	/** The physical. */
	private boolean physical;
	
	/**
	 * Instantiates a new output device enum.
	 *
	 * @param implementingClass the implementing class
	 * @param physical the physical
	 */
	private OutputDeviceEnum(Class<? extends Output> implementingClass, boolean physical) {
		this.implementingClass = implementingClass;
		this.physical = physical;
	}
	
	/**
	 * Checks if is physical.
	 *
	 * @return true, if is physical
	 */
	public boolean isPhysical() {
		return this.physical;
	}
	
	/**
	 * Gets the implementing class.
	 *
	 * @return the implementing class
	 */
	public Class<? extends Output> getImplementingClass() {
		return this.implementingClass;
	}
	
	/**
	 * Gets the readable name.
	 *
	 * @return the readable name
	 */
	public String getReadableName() {
		return this.name() + " (" + this.implementingClass.getSimpleName() + ")";
	}
}
