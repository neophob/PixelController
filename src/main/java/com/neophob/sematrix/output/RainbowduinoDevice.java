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

import com.neophob.lib.rainbowduino.NoSerialPortFoundException;
import com.neophob.lib.rainbowduino.Rainbowduino;
import com.neophob.sematrix.glue.Collector;

/**
 * Send data to Rainbowduino
 * 
 * @author michu
 *
 */
public class RainbowduinoDevice extends ArduinoOutput {

	private static Logger log = Logger.getLogger(RainbowduinoDevice.class.getName());
	
	private List<Integer> allI2cAddress;
	private Rainbowduino rainbowduino = null;

	/**
	 * init the rainbowduino devices 
	 * @param allI2COutputs a list containing all i2c slave addresses
	 */
	public RainbowduinoDevice(PixelControllerOutput controller, List<Integer> allI2cAddress) {
		super(controller, RainbowduinoDevice.class.toString());
		this.allI2cAddress = allI2cAddress;
		
		this.initialized = false;		
		try {
			rainbowduino = new Rainbowduino( Collector.getInstance().getPapplet(), allI2cAddress);			
			this.initialized = rainbowduino.ping();
			log.log(Level.INFO, "ping result: "+ this.initialized);			
		} catch (NoSerialPortFoundException e) {
			log.log(Level.WARNING, "failed to initialize serial port!");
		}
		
	}

	public long getLatestHeartbeat() {
		if (initialized) {
			return rainbowduino.getArduinoHeartbeat();			
		}
		return -1;
	}

	public int getArduinoBufferSize() {
		if (initialized) {
			return rainbowduino.getArduinoBufferSize();			
		}
		return -1;
	}

	public int getArduinoErrorCounter() {
		if (initialized) {
			return rainbowduino.getArduinoErrorCounter();			
		}
		return -1;
	}

	public void update() {
		if (initialized) {
			int size=allI2cAddress.size();
			for (int screen=0; screen<Collector.getInstance().getNrOfScreens(); screen++) {
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
				log.log(Level.INFO, "sended frames: {0}% {1}/{2}, ack Errors: {3} last Error: {4}, arduino buffer size: {5}", 
						new Object[] {result, needUpdate, noUpdate, rainbowduino.getAckErrors(), 
							rainbowduino.getArduinoErrorCounter(), rainbowduino.getArduinoBufferSize()});				
			}
			
		}
	}

	/**
	 * 
	 */
	public void printAvailableI2cAdr() {
		if (initialized) {
			List<Integer> list = rainbowduino.scanI2cBus();
			String foundDevices="";
			for (int i: list) {
				foundDevices+=i+" ";
			}
			log.log(Level.INFO, "Found i2c devices: <{0}>", foundDevices);
		} else {
			log.log(Level.INFO, "I2C scan aborted - not connected to arduino!");
		}
	}
	
	
	@Override
	public void close() {
		if (initialized) {
			rainbowduino.dispose();			
		}
	}

}
