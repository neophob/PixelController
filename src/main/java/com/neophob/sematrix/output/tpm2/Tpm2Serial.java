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

package com.neophob.sematrix.output.tpm2;

import java.util.logging.Level;
import java.util.logging.Logger;

import processing.core.PApplet;
import processing.serial.Serial;

import com.neophob.sematrix.output.NoSerialPortFoundException;
import com.neophob.sematrix.output.OutputHelper;
import com.neophob.sematrix.output.misc.MD5;
import com.neophob.sematrix.properties.ColorFormat;

/**
 * library to communicate with an TPM2 serial device<br>
 * created for ledstyles.de 
 * <br><br>
 * 
 * @author Michael Vogt / neophob.com
 *
 */
public class Tpm2Serial {
	
    /** The log. */
    private static final Logger LOG = Logger.getLogger(Tpm2Serial.class.getName());

    /** internal lib version. */
	public static final String VERSION = "1.0";
	
	/** The app. */
	private PApplet app;

	/** The baud. */
	private int baud;
	
	/** The port. */
	private Serial port;

	/** map to store checksum of image. */
	private String lastDataMap;
		
	/**
	 * Create a new instance to communicate with the tpm2 device.
	 *
	 * @param app the app
	 * @param targetBuffersize the target buffersize
	 * @throws NoSerialPortFoundException the no serial port found exception
	 */
	public Tpm2Serial(PApplet app, int targetBuffersize, int baud) throws NoSerialPortFoundException {
		this(app, null, targetBuffersize, baud);
	}

	/**
	 * Create a new instance to communicate with the rainbowduino.
	 *
	 * @param app the _app
	 * @param targetBuffersize the target buffersize
	 * @param portName the port name
	 * @throws NoSerialPortFoundException the no serial port found exception
	 */
	public Tpm2Serial(PApplet app, int targetBuffersize, String portName, int baud) throws NoSerialPortFoundException {
		this(app, portName, targetBuffersize, baud);
	}


	/**
	 * Create a new instance to communicate with the rainbowduino.
	 *
	 * @param app the app
	 * @param portName the port name
	 * @param targetBuffersize the target buffersize
	 * @throws NoSerialPortFoundException the no serial port found exception
	 */
	public Tpm2Serial(PApplet app, String portName, int targetBuffersize, int baud) throws IllegalArgumentException, NoSerialPortFoundException {
		
		LOG.log(Level.INFO,	"Initialize Tpm2Serial lib v{0}", VERSION);
		
		this.app = app;
		this.app.registerDispose(this);
		this.baud = baud;
		
		lastDataMap = "";
		
		String serialPortName="";	
		
		if (portName!=null && !portName.trim().isEmpty()) {
			//open specific port
			LOG.log(Level.INFO,	"open port: {0}", portName);
			serialPortName = portName;
			openPort(portName);
		} else {
			//try to find the port
			String[] ports = Serial.list();
			for (int i=0; port==null && i<ports.length; i++) {
				LOG.log(Level.INFO,	"open port: {0}", ports[i]);
				try {
					serialPortName = ports[i];
					openPort(ports[i]);
				//catch all, there are multiple exception to catch (NoSerialPortFoundException, PortInUseException...)
				} catch (Exception e) {
					// search next port...
				}
			}
		}
				
		if (port==null) {
			throw new NoSerialPortFoundException("Error: no serial port found!");
		}
		
		LOG.log(Level.INFO,	"found serial port: "+serialPortName);
	}


	/**
	 * clean up library.
	 */
	public void dispose() {
		if (connected()) {
			port.stop();
		}
	}



	/**
	 * return the version of the library.
	 *
	 * @return String version number
	 */
	public String version() {
		return VERSION;
	}

	/**
	 * return connection state of lib.
	 *
	 * @return wheter rainbowudino is connected
	 */
	public boolean connected() {
		return (port != null);
	}	

	

	/**
	 * Open serial port with given name. Send ping to check if port is working.
	 * If not port is closed and set back to null
	 *
	 * @param portName the port name
	 * @throws NoSerialPortFoundException the no serial port found exception
	 */
	private void openPort(String portName) throws NoSerialPortFoundException {
		try {
			port = new Serial(app, portName, this.baud);
		} catch (Exception e) {	
			LOG.log(Level.WARNING, "Failed to open port {0}: {1}", new Object[] {portName, e});
			if (port != null) {
				port.stop();        					
			}
			port = null;
			throw new NoSerialPortFoundException("Failed to open port "+portName+": "+e);
		}	
	}

	
	/**
	 * wrapper class to send a RGB image to the miniDmx device.
	 * the rgb image gets converted to the miniDmx compatible
	 * "image format"
	 *
	 * @param data rgb data (int[64], each int contains one RGB pixel)
	 * @param colorFormat the color format
	 * @return true if send was successful
	 */
	public boolean sendRgbFrame(int[] data, ColorFormat colorFormat) {
		return sendFrame(OutputHelper.convertBufferTo24bit(data, colorFormat));
	}


	
	/**
	 * get md5 hash out of an image. used to check if the image changed
	 *
	 * @param data the data
	 * @return true if send was successful
	 */
	private boolean didFrameChange(byte data[]) {
		String s = MD5.asHex(data);
		
		if (lastDataMap.isEmpty()) {
			//first run
			lastDataMap=s;
			return true;
		}
		
		if (lastDataMap.equals(s)) {
			//last frame was equal current frame, do not send it!
			return false;
		}
		//update new hash
		lastDataMap=s;
		return true;
	}
	
	/**
	 * @param data byte[3*8*4]
	 * @return true if send was successful
	 * @throws IllegalArgumentException the illegal argument exception
	 */
	public boolean sendFrame(byte data[]) throws IllegalArgumentException {
		if (didFrameChange(data)) {
			writeSerialData(data);
		}

		return false;
	}
	

	/**
	 * send the data to the serial port.
	 *
	 * @param cmdfull the cmdfull
	 * 
	 */
	private synchronized void writeSerialData(byte[] cmdfull) {
		if (port==null) {
			LOG.log(Level.INFO, "port not ready!");
		}
		
		try {
			port.output.write(cmdfull);
			//port.output.flush();
			//DO NOT flush the buffer... hmm not sure about this, processing flush also
			//and i discovered strange "hangs"...
		} catch (Exception e) {
			LOG.log(Level.INFO, "Error sending serial data!", e);			
		}		
	}


}
