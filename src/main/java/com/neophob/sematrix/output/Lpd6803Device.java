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
import com.neophob.sematrix.glue.Collector;
import com.neophob.sematrix.output.lpd6803.Lpd6803;
import com.neophob.sematrix.properties.ColorFormat;
import com.neophob.sematrix.properties.DeviceConfig;

/**
 * Send data to Lpd6803 Device
 * 
 * @author michu
 *
 */
public class Lpd6803Device extends Output {

	private static Logger log = Logger.getLogger(Lpd6803Device.class.getName());
		
	//does the buffer needs to be flipped? rotated?
	private List<DeviceConfig> displayOptions;
	//output format
	private List<ColorFormat> colorFormat;
	
	private Lpd6803 lpd6803 = null;
	private boolean initialized;
	
	private long needUpdate, noUpdate;

	/**
	 * init the lpd6803 devices
	 * @param controller
	 * @param displayOptions
	 * @param colorFormat
	 */
	public Lpd6803Device(PixelControllerOutput controller, List<DeviceConfig> displayOptions,
			List<ColorFormat> colorFormat) {
		super(controller, Lpd6803Device.class.toString());
		
		this.displayOptions = displayOptions;
		this.colorFormat = colorFormat;
		this.initialized = false;		
		try {
			lpd6803 = new Lpd6803( Collector.getInstance().getPapplet() );			
			this.initialized = lpd6803.ping();
			log.log(Level.INFO, "ping result: "+ this.initialized);			
		} catch (NoSerialPortFoundException e) {
			log.log(Level.WARNING, "failed to initialize serial port!");
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public long getLatestHeartbeat() {
		if (initialized) {
			return lpd6803.getArduinoHeartbeat();			
		}
		return -1;
	}

	/**
	 * 
	 * @return
	 */
	public int getArduinoBufferSize() {
		if (initialized) {
			return lpd6803.getArduinoBufferSize();			
		}
		return -1;
	}

	/**
	 * 
	 * @return
	 */
	public int getArduinoErrorCounter() {
		if (initialized) {
			return lpd6803.getArduinoErrorCounter();			
		}
		return -1;
	}

	/**
	 * 
	 */
	public void update() {
		
		if (initialized) {			
			for (int ofs=0; ofs<Collector.getInstance().getNrOfScreens(); ofs++) {
				//draw only on available screens!
				int[] transformedBuffer = 
					RotateBuffer.transformImage(super.getBufferForScreen(ofs), displayOptions.get(ofs),
							Lpd6803.NR_OF_LED_HORIZONTAL, Lpd6803.NR_OF_LED_VERTICAL);
				
				if (lpd6803.sendRgbFrame((byte)ofs, transformedBuffer, colorFormat.get(ofs))) {
					needUpdate++;
				} else {
					noUpdate++;
				}
			}
			
			if ((noUpdate+needUpdate)%100==0) {
				float f = noUpdate+needUpdate;
				float result = (100.0f/f)*needUpdate;
				log.log(Level.INFO, "sended frames: {0}% {1}/{2}, ack Errors: {3} last Error: {4}, arduino buffer size: {5}", 
						new Object[] {result, needUpdate, noUpdate, lpd6803.getAckErrors(), 
						lpd6803.getArduinoErrorCounter(), lpd6803.getArduinoBufferSize()});				
			}
			
		}
	}


	
	@Override
	public void close() {
		if (initialized) {
			lpd6803.dispose();			
		}
	}

}
