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

import com.neophob.lib.rainbowduino.NoSerialPortFoundException;
import com.neophob.sematrix.glue.Collector;
import com.neophob.sematrix.output.minidmx.MiniDmxSerial;

/**
 * Send data to a miniDMX Device via serial line
 * 
 * There is only ONE Matrix supported per output
 * 
 * @author michu
 *
 */
public class MiniDmxDevice extends Output {

	private static Logger log = Logger.getLogger(MiniDmxDevice.class.getName());
	
	MiniDmxSerial miniDmx;
	
	private static final byte START_BYTE = 0x5A;
		
	private boolean initialized;
	private int xSize;
	private int ySize;
	
	/**
	 * init the mini dmx devices 
	 * @param allI2COutputs a list containing all i2c slave addresses
	 * 
	 */
	public MiniDmxDevice(PixelControllerOutput controller, int xSize, int ySize) {
		super(controller, MiniDmxDevice.class.toString());
		
		this.initialized = false;
		this.xSize = xSize;
		this.ySize = ySize;
		try {
			miniDmx = new MiniDmxSerial(Collector.getInstance().getPapplet(), this.xSize*this.ySize);			
			this.initialized = miniDmx.ping();
			log.log(Level.INFO, "ping result: "+ this.initialized);			
		} catch (NoSerialPortFoundException e) {
			log.log(Level.WARNING, "failed to initialize serial port!");
		}
	}
	

	/**
	 * 
	 */
	public void update() {
		
		if (initialized) {			
/*			for (int ofs=0; ofs<Collector.getInstance().getNrOfScreens(); ofs++) {
				//draw only on available screens!
				int[] transformedBuffer = 
					RotateBuffer.transformImage(super.getBufferForScreen(ofs), displayOptions.get(ofs));
				
				if (lpd6803.sendRgbFrame((byte)ofs, transformedBuffer, colorFormat.get(ofs))) {
					needUpdate++;
				} else {
					noUpdate++;
				}
			}			
		}*/
	}


	
	@Override
	public void close() {
		if (initialized) {
			miniDmx.dispose();			
		}
	}

}
