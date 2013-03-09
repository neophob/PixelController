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
package com.neophob.sematrix.output;

/**
 * The Enum OutputDeviceEnum.
 */
public enum OutputDeviceEnum {
	
	/** The PIXELINVADERS (LPD6803 Based) PANELS. */
	PIXELINVADERS(PixelInvadersDevice.class, true),
	
	/** The Element STEALTH LED PANELS. */
	STEALTH(StealthDevice.class, true),

	/** The RAINBOWDUINO_V2. */
	RAINBOWDUINO_V2(RainbowduinoV2Device.class, true),
	
	/** The RAINBOWDUINO_V3. */
	RAINBOWDUINO_V3(RainbowduinoV3Device.class, true),

	/** The ARTNET. */
	ARTNET(ArtnetDevice.class, true),

	/** The E1.31 output. */
	E1_31(E1_31Device.class, true),

	/** The MINIDMX. */
	MINIDMX(MiniDmxDevice.class, true),

	/** The ADAVISION. */
	ADAVISION(AdaVision.class, true),

	/** The TPM2. */
	TPM2(Tpm2.class, true),

	/** The TPM2Net. */
	TPM2NET(Tpm2Net.class, true),

	/** The UDP. */
	UDP(UdpDevice.class, true),
	
	PIXELINVADERS_NET(PixelInvadersNetDevice.class, true),

	/** The NULL Output. */
	NULL(NullDevice.class, true);
	
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
