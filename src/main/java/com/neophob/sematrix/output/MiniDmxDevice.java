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

import java.util.logging.Level;
import java.util.logging.Logger;

import com.neophob.sematrix.glue.Collector;
import com.neophob.sematrix.output.minidmx.MiniDmxSerial;
import com.neophob.sematrix.properties.ApplicationConfigurationHelper;

/**
 * Send data to a miniDMX Device via serial line
 * 
 * There is only ONE Matrix supported per output.
 *
 * @author michu
 */
public class MiniDmxDevice extends OnePanelResolutionAwareOutput {

	/** The log. */
	private static final Logger LOG = Logger.getLogger(MiniDmxDevice.class.getName());
	
	/** The mini dmx. */
	private MiniDmxSerial miniDmx;
		
	/**
	 * init the mini dmx devices.
	 *
	 * @param controller the controller
	 */
	public MiniDmxDevice(ApplicationConfigurationHelper ph, PixelControllerOutput controller) {
		super(OutputDeviceEnum.MINIDMX, ph, controller, 8);
		
		int baud = ph.parseMiniDmxBaudRate();
		if (baud==0) {
		    //set default
		    baud = 115200;
		}
		this.supportConnectionState = true;
		this.initialized = false;
		try {
			miniDmx = new MiniDmxSerial(Collector.getInstance().getPapplet(), this.xResolution*this.yResolution*3, baud);			
			this.initialized = miniDmx.ping();			
			LOG.log(Level.INFO, "ping result: "+ this.initialized);			
		} catch (NoSerialPortFoundException e) {
			LOG.log(Level.WARNING, "failed to initialize serial port!");
		}
	}
	

	/* (non-Javadoc)
	 * @see com.neophob.sematrix.output.Output#update()
	 */
	public void update() {		
		if (initialized) {							
			miniDmx.sendRgbFrame(getTransformedBuffer(), colorFormat);
		}
	}


	
	/* (non-Javadoc)
	 * @see com.neophob.sematrix.output.Output#close()
	 */
	@Override
	public void close() {
		if (initialized) {
			miniDmx.dispose();			
		}
	}
	
	@Override
	public boolean isConnected() {
	    return this.initialized;
	}

}
