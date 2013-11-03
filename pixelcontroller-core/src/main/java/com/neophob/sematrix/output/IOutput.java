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

import com.neophob.sematrix.output.gamma.GammaType;

/**
 * Output device interface
 * 
 * @author michu
 *
 */
public interface IOutput {

	/**
	 * 
	 * @return Output type
	 */
	OutputDeviceEnum getType();

	/**
	 * connection oriented device?
	 * @return
	 */
	boolean isSupportConnectionState();

	/**
	 * 
	 * @return connection state
	 */
	boolean isConnected();

	/**
	 * if device supports a connection status, overwrite me.
	 * examples: connected to /dev/aaa or IP Adress: 1.2.3.4
	 */	
	String getConnectionStatus();

	/**
	 * configured gamma type
	 * @return
	 */
	GammaType getGammaType();

	/**
	 * 
	 * @return color resolution
	 */
	int getBpp();

	/**
	 * @return how many errors occured (if supported)
	 */
	long getErrorCounter();
}
