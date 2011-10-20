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

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.neophob.sematrix.properties.PropertiesHelper;

/**
 * The Class ArduinoOutput.
 *
 * @author michu
 */
public abstract class ArduinoOutput extends Output {
	
	/** The log. */
	private static final Logger LOG = Logger.getLogger(ArduinoOutput.class.getName());

	/** The initialized. */
	protected boolean initialized;
	
	/** The need update. */
	protected long needUpdate;
	
	/** The no update. */
	protected long noUpdate;
	
	/**
	 * Instantiates a new arduino output.
	 *
	 * @param outputDeviceEnum the outputDeviceEnum
	 * @param ph the ph
	 * @param controller the controller
	 */
	public ArduinoOutput(OutputDeviceEnum outputDeviceEnum, PropertiesHelper ph, PixelControllerOutput controller, int bpp) {
		super(outputDeviceEnum, ph, controller, bpp);
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
	
	
	/* (non-Javadoc)
	 * @see com.neophob.sematrix.output.Output#logStatistics()
	 */
	@SuppressWarnings("deprecation")
	public void logStatistics() {
		if (this.getArduinoErrorCounter() > 0) {
			int error = this.getArduinoErrorCounter();
			LOG.log(Level.SEVERE,"error at: {0}, errorcnt: {1}, buffersize: {2}",
					new Object[] {
						new Date(this.getLatestHeartbeat()).toGMTString(),
						error, this.getArduinoBufferSize()
					}
			);
		}		
	}

}
