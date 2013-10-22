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
/*
A nice wrapper class to control the Rainbowduino 

(c) copyright 2009 by rngtng - Tobias Bielohlawek
(c) copyright 2010/2011 by Michael Vogt/neophob.com 
http://code.google.com/p/rainbowduino-firmware/wiki/FirmwareFunctionsReference

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General
Public License along with this library; if not, write to the
Free Software Foundation, Inc., 59 Temple Place, Suite 330,
Boston, MA  02111-1307  USA
 */

package com.neophob.sematrix.output.pixelinvaders;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import netP5.Bytes;
import processing.core.PApplet;
import processing.serial.Serial;

import com.neophob.sematrix.output.NoSerialPortFoundException;
import com.neophob.sematrix.output.SerialPortException;
import com.neophob.sematrix.output.gamma.RGBAdjust;

/**
 * library to communicate with an LPD6803 stripes via serial port<br>
 * <br><br>
 * part of the neorainbowduino library.
 *
 * @author Michael Vogt / neophob.com
 */
public class Lpd6803 extends Lpd6803Common {
		
	/** The log. */
	private static final Logger LOG = Logger.getLogger(Lpd6803.class.getName());
	
	/** internal lib version. */
	public static final String VERSION = "2.0";

	//how many attemps are made to get the data
	private static final int TIMEOUT_LOOP = 50;
	
	//wait TIMEOUT_SLEEP ms, until next loop
	private static final int TIMEOUT_SLEEP = 4;

	/** The app. */
	private PApplet app;

	/** The baud. */
	private int baud = 115200;
	
	/** The port. */
	private Serial port;
	
	/** The arduino heartbeat. */
	private long arduinoHeartbeat;
	
	/** The arduino buffer size. */
	private int arduinoBufferSize;
	
	//logical errors reported by arduino, TODO: rename to lastErrorCode
	/** The arduino last error. */
	private int arduinoLastError;
	
	//connection errors to arduino, TODO: use it!
	/** The connection error counter. */
	private int connectionErrorCounter;
	
	private String serialPortName;
	
	
	/**
	 * Create a new instance to communicate with the lpd6803 device.
	 *
	 * @param app the app
	 * @throws NoSerialPortFoundException the no serial port found exception
	 */
	public Lpd6803(PApplet app, List<String> portBlacklist, Map<Integer, RGBAdjust> correctionMap, int panelSize) throws NoSerialPortFoundException {
		this(app, null, 0, portBlacklist, correctionMap, panelSize);
	}


	/**
	 * Create a new instance to communicate with the lpd6803 device.
	 *
	 * @param _app the _app
	 * @param portName the port name
	 * @param baud the baud
	 * @throws NoSerialPortFoundException the no serial port found exception
	 */
	public Lpd6803(PApplet app, String portName, int baud, List<String> portBlacklist, Map<Integer, RGBAdjust> correctionMap, int panelSize) throws NoSerialPortFoundException {
		super(panelSize, panelSize);
		LOG.log(Level.INFO,	"Initialize LPD6803 lib v{0}", VERSION);
		
		this.app = app;
		app.registerDispose(this);		
		this.correctionMap = correctionMap;
		
		serialPortName="";
		if(baud > 0) {
			this.baud = baud;
		}

		if (portName!=null && !portName.trim().isEmpty()) {
			//open specific port
			LOG.log(Level.INFO,	"open port: {0}", portName);
			serialPortName = portName;
			openPort(portName);
			initialized = true;
		} else {
			//try to find the port
			String[] ports = Serial.list();
						
			for (int i=0; port==null && i<ports.length; i++) {
		         //check blacklist
	            if (portBlacklist!=null && portBlacklist.contains(ports[i])) {
	                LOG.log(Level.INFO, "ignore blacklist port: {0}", ports[i]);
	                continue;
	            }

				LOG.log(Level.INFO,	"open port: {0}", ports[i]);
				try {
					serialPortName = ports[i];
					openPort(ports[i]);
					initialized = true;
				//catch all, there are multiple exception to catch (NoSerialPortFoundException, PortInUseException...)
				} catch (Exception e) {
				    serialPortName="";
					// search next port...
				}
			}
		}
				
		if (port==null) {		    
			throw new NoSerialPortFoundException("\nError: no serial port found!\n");
		}

		LOG.log(Level.INFO,	"found serial port: "+serialPortName);
	}


	/**
	 * clean up library.
	 */
	public void dispose() {
		if (connected()) {
			LOG.log(Level.INFO,	"Serial connection closed");
			port.stop();
			port = null;
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
	 * Open serial port with given name. Send ping to check if port is working.
	 * If not port is closed and set back to null
	 *
	 * @param portName the port name
	 * @throws NoSerialPortFoundException the no serial port found exception
	 */
	private void openPort(String portName) throws NoSerialPortFoundException {
		if (portName == null) {
			return;
		}
		
		try {
			port = new Serial(app, portName, this.baud);
			sleep(1500); //give it time to initialize
			if (ping()) {
				return;
			}
			
			LOG.log(Level.WARNING, "No response from port {0}", portName);
			if (port != null) {
				port.stop();
				LOG.log(Level.WARNING, Bytes.getAsString(getReplyFromController()));
			}
			port = null;
			throw new NoSerialPortFoundException("No response from port "+portName);
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
	 * get last error code from arduino
	 * if the errorcode is between 100..109 - serial connection issue (pc-arduino issue)
	 * if the errorcode is < 100 it's a i2c lib error code (arduino-rainbowduino error)
	 *    check http://arduino.cc/en/Reference/WireEndTransmission for more information
	 *   
	 * @return last error code from arduino
	 */
	public int getArduinoErrorCounter() {
		return arduinoLastError;
	}

	/**
	 * return the serial buffer size of the arduino
	 * 
	 * the buffer is by default 128 bytes - if the buffer is most of the
	 * time almost full (>110 bytes) you probabely send too much serial data.
	 *
	 * @return arduino filled serial buffer size
	 */
	public int getArduinoBufferSize() {
		return arduinoBufferSize;
	}

	/**
	 * per default arduino update this library each 3s with statistic information
	 * this value save the timestamp of the last message.
	 * 
	 * @return timestamp when the last heartbeat receieved. should be updated each 3s.
	 */
	public long getArduinoHeartbeat() {
		return arduinoHeartbeat;
	}
	
	
	/**
	 * how may times the serial response was missing / invalid.
	 *
	 * @return the ack errors
	 */
	public long getAckErrors() {
		return ackErrors;
	}

	/**
	 * send the data to the serial port.
	 *
	 * @param cmdfull the cmdfull
	 * @throws SerialPortException the serial port exception
	 */
	protected synchronized void writeData(byte[] cmdfull) throws WriteDataException {
		if (port==null) {
			throw new WriteDataException("serial port is not ready!");
		}
		
		try {
			port.output.write(cmdfull);			
			port.output.flush();
		} catch (Exception e) {
			LOG.log(Level.INFO, "Error sending serial data!", e);
			connectionErrorCounter++;
			throw new WriteDataException("cannot send serial data, errorNr: "+connectionErrorCounter+", Error: "+e);
		}		
	}
	
	
	/**
	 * read data from serial port, wait for ACK.
	 *
	 * @return true if ack received, false if not
	 */
	protected synchronized boolean waitForAck() {		
		long start = System.currentTimeMillis();
		int timeout=TIMEOUT_LOOP; //wait up to 50ms
		//log.log(Level.INFO, "wait for ack");
		while (timeout > 0 && port.available() < 2) {
			sleep(TIMEOUT_SLEEP); //in ms
			timeout--;
		}
		if (timeout == 0 && port.available() < 2) {
			LOG.log(Level.INFO, "#### No serial reply, duration: {0}ms ###", System.currentTimeMillis()-start);
			ackErrors++;
			return false;
		}
		
		byte[] msg = port.readBytes();
		String reply = Bytes.getAsString(msg);
		LOG.log(Level.INFO, "got ACK: {0}", reply);

		if (reply.contains("AK")) {
			return true;
		}
		return false;		
	}


	/**
     * @return the serialPortName
     */
    public String getSerialPortName() {
        return serialPortName;
    }

	@Override
	protected byte[] getReplyFromController() {
		return port.readBytes();
	}

}
