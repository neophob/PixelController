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

import java.util.logging.Level;
import java.util.logging.Logger;

import com.neophob.sematrix.glue.Collector;
import com.neophob.sematrix.output.minidmx.MiniDmxSerial;
import com.neophob.sematrix.properties.ColorFormat;
import com.neophob.sematrix.properties.PropertiesHelper;

/**
 * Send data to a miniDMX Device via serial line
 * 
 * There is only ONE Matrix supported per output.
 *
 * @author michu
 */
public class MiniDmxDevice extends Output {

	/** The log. */
	private static final Logger LOG = Logger.getLogger(MiniDmxDevice.class.getName());
	
	/** The mini dmx. */
	private MiniDmxSerial miniDmx;
	
	/** The initialized. */
	private boolean initialized;
	
	/** The x size. */
	private int xSize;
	
	/** The y size. */
	private int ySize;
	
	/** The output color format. */
	private ColorFormat colorFormat;
	
	/**
	 * init the mini dmx devices.
	 *
	 * @param controller the controller
	 */
	public MiniDmxDevice(PropertiesHelper ph, PixelControllerOutput controller) {
		super(ph, controller, MiniDmxDevice.class.toString(), 8);
		
		this.initialized = false;
		this.xSize = ph.parseMiniDmxDevicesX();
		this.ySize = ph.parseMiniDmxDevicesY();
		int baud = ph.parseMiniDmxBaudRate();
		if (baud==0) {
		    //set default
		    baud = 115200;
		}
		
		this.colorFormat = ColorFormat.RBG;
		if (ph.getColorFormat().size()>0) {
			this.colorFormat = ph.getColorFormat().get(0);
		}
		
		try {
			miniDmx = new MiniDmxSerial(Collector.getInstance().getPapplet(), this.xSize*this.ySize*3, baud);			
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
			miniDmx.sendRgbFrame(super.getBufferForScreen(0), colorFormat);
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

}
