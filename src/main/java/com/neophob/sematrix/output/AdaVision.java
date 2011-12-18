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

import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import processing.serial.Serial;

import com.neophob.sematrix.glue.Collector;
import com.neophob.sematrix.properties.PropertiesHelper;

/**
 * Output device for adavision
 * 
 * @author michu
 *
 */
public class AdaVision extends OnePanelResolutionAwareOutput {

	/** The log. */
	private static final Logger LOG = Logger.getLogger(AdaVision.class.getName());

	private static final int BPS = 115200;
	private static final int HEADERSIZE = 6;

	private static final String VERSION = "0.3";

	private int panelsize;
	
	private byte[] buffer;
	private Serial port;
	
	/**
	 * 
	 * @param ph
	 * @param controller
	 */
	public AdaVision(PropertiesHelper ph, PixelControllerOutput controller) {
		super(OutputDeviceEnum.ADAVISION, ph, controller, 8);

		LOG.log(Level.INFO,	"Initialize AdaVision lib v{0}", VERSION);

		//TODO should use autodetection someday
		String serialPort = ph.getAdavisionSerialPort();
		if (serialPort==null) {
			serialPort = Serial.list()[0];
		}

		this.panelsize = this.xResolution*this.yResolution;
		int bps = ph.getAdavisionSerialPortSpeed();
		if (bps<1) {
			//use default value
			bps = BPS;
		}
		
		LOG.log(Level.INFO,  "AdaVision X resolution: {0}, Y resolution: {1}, using {2} at {3} bps", 
				new Object[] { this.xResolution, this.yResolution, serialPort, bps} );

 		port = new Serial(Collector.getInstance().getPapplet(), serialPort, bps);
				
		// A special header / magic word is expected by the corresponding LED
		// streaming code running on the Arduino.  This only needs to be initialized
		// once (not in draw() loop) because the number of LEDs remains constant:
		buffer = new byte[HEADERSIZE + panelsize * 3];
		buffer[0] = 'A';                                // Magic word
		buffer[1] = 'd';
		buffer[2] = 'a';
		buffer[3] = (byte)((panelsize - 1) >> 8);      // LED count high byte
		buffer[4] = (byte)((panelsize - 1) & 0xff);    // LED count low byte
		buffer[5] = (byte)(buffer[3] ^ buffer[4] ^ 0x55); // Checksum
		
		initialized = true;
	}

	@Override
	public void update() {
		if (initialized) {							
			writeSerialData(OutputHelper.convertBufferTo24bit(getTransformedBuffer(), colorFormat));			
		}		
	}

	/**
	 * 
	 * @param buffer
	 */
	private synchronized void writeSerialData(byte[] rawBuffer) {
		try {
			//copy raw data into buffer
			System.arraycopy(rawBuffer, 0, buffer, HEADERSIZE, rawBuffer.length);
			port.output.write(buffer);
			//port.output.flush();
			//DO NOT flush the buffer... hmm not sure about this, processing flush also
			//and i discovered strange "hangs"...
		} catch (Exception e) {
			LOG.log(Level.INFO, "Error sending serial data!", e);
		}		
	}
	
	
	@Override
	public void close() {
		if (initialized) {
		    // Fill buffer (after header) with 0's, and issue to Arduino...
		    Arrays.fill(buffer, HEADERSIZE, buffer.length, (byte)0);
		    port.write(buffer);
		    
			port.dispose();
		}
	}
}
