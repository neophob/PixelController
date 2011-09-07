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

import com.neophob.sematrix.properties.PropertiesHelper;

/**
 * The Class ArduinoOutput.
 *
 * @author michu
 */
public abstract class ArduinoOutput extends Output {
	
	/** The initialized. */
	protected boolean initialized;
	
	/** The need update. */
	protected long needUpdate;
	
	/** The no update. */
	protected long noUpdate;
	
	/**
	 * Instantiates a new arduino output.
	 *
	 * @param controller the controller
	 * @param name the name
	 */
	public ArduinoOutput(PropertiesHelper ph, PixelControllerOutput controller, String name) {
		super(ph, controller, name);
	}
	
	/**
	 * Gets the arduino error counter.
	 *
	 * @return the arduino error counter
	 */
	public abstract int getArduinoErrorCounter();

	/**
	 * Gets the arduino buffer size.
	 *
	 * @return the arduino buffer size
	 */
	public abstract int getArduinoBufferSize();
	
	/**
	 * Gets the latest heartbeat.
	 *
	 * @return the latest heartbeat
	 */
	public abstract long getLatestHeartbeat();
}
