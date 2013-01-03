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

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.neophob.sematrix.glue.Collector;
import com.neophob.sematrix.output.neorainbowduino.Rainbowduino;
import com.neophob.sematrix.properties.ApplicationConfigurationHelper;

/**
 * Send data to Rainbowduino.
 *
 * @author michu
 */
public class RainbowduinoV2Device extends ArduinoOutput {

	/** The log. */
	private static final Logger LOG = Logger.getLogger(RainbowduinoV2Device.class.getName());
	
	/** The all i2c address. */
	private List<Integer> allI2cAddress;
	
	/** The rainbowduino. */
	private Rainbowduino rainbowduino = null;

	/**
	 * init the rainbowduino devices.
	 *
	 * @param controller the controller
	 * @param allI2cAddress the all i2c address
	 */
	public RainbowduinoV2Device(ApplicationConfigurationHelper ph, PixelControllerOutput controller) {
		super(OutputDeviceEnum.RAINBOWDUINO_V2, ph, controller, 4);
		
		this.allI2cAddress = ph.getI2cAddr();		
		this.initialized = false;		
		try {
			rainbowduino = new Rainbowduino( Collector.getInstance().getPapplet(), allI2cAddress);			
			this.initialized = rainbowduino.ping();
			LOG.log(Level.INFO, "ping result: "+ this.initialized);			
		} catch (NoSerialPortFoundException e) {
			LOG.log(Level.WARNING, "failed to initialize serial port!", e);
		}
		
	}

	/* (non-Javadoc)
	 * @see com.neophob.sematrix.output.ArduinoOutput#getLatestHeartbeat()
	 */
	public long getLatestHeartbeat() {
		if (initialized) {
			return rainbowduino.getArduinoHeartbeat();			
		}
		return -1;
	}

	/* (non-Javadoc)
	 * @see com.neophob.sematrix.output.ArduinoOutput#getArduinoBufferSize()
	 */
	public int getArduinoBufferSize() {
		if (initialized) {
			return rainbowduino.getArduinoBufferSize();			
		}
		return -1;
	}

	/* (non-Javadoc)
	 * @see com.neophob.sematrix.output.ArduinoOutput#getArduinoErrorCounter()
	 */
	public long getArduinoErrorCounter() {
		if (initialized) {
			return rainbowduino.getAckErrors();			
		}
		return -1;
	}

	/* (non-Javadoc)
	 * @see com.neophob.sematrix.output.Output#update()
	 */
	public void update() {
		if (initialized) {
			int size=allI2cAddress.size();
			int totalScreens = Collector.getInstance().getNrOfScreens();
			for (int screen=0; screen<totalScreens; screen++) {
				//draw only on available screens!
				if (screen<size) {
					int i2cAddr = allI2cAddress.get(screen);
					if (!rainbowduino.sendRgbFrame((byte)i2cAddr, super.getBufferForScreen(screen))) {
						noUpdate++;
					} else {
						needUpdate++;
					}
						
				}
			}
			
			if ((noUpdate+needUpdate)%100==0) {
				float f = noUpdate+needUpdate;
				float result = (100.0f/f)*needUpdate;
				LOG.log(Level.INFO, "sended frames: {0}% {1}/{2}, ack Errors: {3} last Error: {4}, arduino buffer size: {5}", 
						new Object[] {result, needUpdate, noUpdate, rainbowduino.getAckErrors(), 
							rainbowduino.getArduinoErrorCounter(), rainbowduino.getArduinoBufferSize()});				
			}
			
		}
	}

	/**
	 * Prints the available i2c adr.
	 */
	public void printAvailableI2cAdr() {
		if (initialized) {
			List<Integer> list = rainbowduino.scanI2cBus();
			StringBuffer foundDevices = new StringBuffer();
			for (int i: list) {
			    foundDevices.append(i);
			    foundDevices.append(" ");
			}
			LOG.log(Level.INFO, "Found i2c devices: <{0}>", foundDevices);
		} else {
			LOG.log(Level.INFO, "I2C scan aborted - not connected to arduino!");
		}
	}
	
	
	/* (non-Javadoc)
	 * @see com.neophob.sematrix.output.Output#close()
	 */
	@Override
	public void close() {
		if (initialized) {
			rainbowduino.dispose();			
		}
	}

}
