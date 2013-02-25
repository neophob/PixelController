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

package com.neophob.sematrix.output.lpd6803;

import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import processing.core.PApplet;
import processing.net.Client;

import com.neophob.sematrix.output.NoSerialPortFoundException;
import com.neophob.sematrix.output.SerialPortException;

/**
 * http://blog.mafr.de/2010/03/14/tcp-for-low-latency-applications/
 * 
 * library to communicate with an LPD6803 stripes via tcp port<br>
 * <br><br>
 *
 * @author Michael Vogt / neophob.com
 */
public class Lpd6803Net extends Lpd6803Common{
		
	/** The log. */
	private static final Logger LOG = Logger.getLogger(Lpd6803Net.class.getName());
	
	/** internal lib version. */
	public static final String VERSION = "1.0";
	
	Client clientConnection;
	
	private String destIp;	
	private int destPort;
	

	/**
	 * Create a new instance to communicate with the lpd6803 device.
	 *
	 * @param _app the _app
	 * @param portName the port name
	 * @param baud the baud
	 * @throws NoSerialPortFoundException the no serial port found exception
	 */
	public Lpd6803Net(PApplet pa, String destIp, int destPort) throws IOException {
		LOG.log(Level.INFO,	"Initialize LPD6803 net lib v{0}", VERSION);
		
		this.destIp = destIp;
		this.destPort = destPort;
		
		this.lastDataMap = new HashMap<Byte, String>();
		
		//output connection
		LOG.log(Level.INFO, "Connect to target "+destIp+":"+destPort);
		this.clientConnection = new Client(pa, destIp, destPort); 
		
		this.initialized = this.ping();
		
		LOG.log(Level.INFO,	"initialized: "+this.initialized);
	}


	/**
	 * clean up library.
	 */
	public void dispose() {
		if (connected()) {
			LOG.log(Level.INFO,	"Close network connection");
						
			try {
				clientConnection.stop();
			} catch (Exception e) {
				LOG.log(Level.WARNING, "Failed to close socket", e);
			}			
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
		try {
			clientConnection.write(cmdfull);
		} catch (Exception e) {
			LOG.log(Level.INFO, "Error sending network data!", e);
			connectionErrorCounter++;
			throw new WriteDataException("cannot send serial data, errorNr: "+connectionErrorCounter+", Error: "+e);
		}		
	}
	
	
	/**
	 * read data from network, wait for ACK.
	 *
	 * @return true if ack received, false if not
	 */
	protected synchronized boolean waitForAck() {
		Client client = clientConnection;
		if (client !=null) {
			//TODO maybe finetune here
			sleep(8);
			byte[] msg = client.readBytes();
			
			if (msg==null) {
				ackErrors++;
				return false;
			}
			for (int i=0; i<msg.length-1; i++) {
				if (msg[i]== 'A' && msg[i+1]== 'K') {
					try {
						int lastError = msg[i+3];
						if (lastError!=0) {
							LOG.log(Level.INFO, "Last Errorcode: {0}", lastError);
							ackErrors++;
							return false;
						}
						return true;
					} catch (Exception e) {
						// we failed to update statistics...
					}

					ackErrors++;
					return false;					
				}
			}			
		}
		
		ackErrors++;
		return false;		
	}


	/**
     * @return the destination ip
     */
    public String getDestIp() {
        return destIp;
    }

    /**
     * 
     * @return the destination port
     */
    public int getDestPort() {
		return destPort;
	}


}
