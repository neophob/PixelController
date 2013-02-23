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
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import processing.core.PApplet;
import processing.net.Client;

import com.neophob.sematrix.output.NoSerialPortFoundException;
import com.neophob.sematrix.output.OutputHelper;
import com.neophob.sematrix.output.SerialPortException;
import com.neophob.sematrix.output.misc.MD5;
import com.neophob.sematrix.properties.ColorFormat;

/**
 * http://blog.mafr.de/2010/03/14/tcp-for-low-latency-applications/
 * 
 * library to communicate with an LPD6803 stripes via tcp port<br>
 * <br><br>
 *
 * @author Michael Vogt / neophob.com
 */
public class Lpd6803Net {
		
	/** The log. */
	private static final Logger LOG = Logger.getLogger(Lpd6803Net.class.getName());

	/** number of leds horizontal<br> TODO: should be dynamic, someday. */
	public static final int NR_OF_LED_HORIZONTAL = 8;

	/** number of leds vertical<br> TODO: should be dynamic, someday. */
	public static final int NR_OF_LED_VERTICAL = NR_OF_LED_HORIZONTAL;

	/** The Constant BUFFERSIZE. */
	private static final int BUFFERSIZE = NR_OF_LED_HORIZONTAL*NR_OF_LED_VERTICAL;
	
	/** internal lib version. */
	public static final String VERSION = "1.0";

	/** The Constant START_OF_CMD. */
	private static final byte START_OF_CMD = 0x01;
	
	/** The Constant CMD_SENDFRAME. */
	private static final byte CMD_SENDFRAME = 0x03;
	
	/** The Constant CMD_PING. */
	private static final byte CMD_PING = 0x04;

	/** The Constant START_OF_DATA. */
	private static final byte START_OF_DATA = 0x10;
	
	/** The Constant END_OF_DATA. */
	private static final byte END_OF_DATA = 0x20;

	/** The connection error counter. */
	private int connectionErrorCounter;
	
	/** map to store checksum of image. */
	private Map<Byte, String> lastDataMap;
	
	Client clientConnection;
	
	private String destIp;	
	private int destPort;
	
	private boolean initialized;
	
	/** The ack errors. */
	private long ackErrors = 0;


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
	 * return connection state of lib.
	 *
	 * @return whether a lpd6803 device is connected
	 */
	public boolean connected() {
		return initialized;
	}	



	/**
	 * send a serial ping command to the arduino board.
	 * 
	 * @return wheter ping was successfull (arduino reachable) or not
	 */
	public boolean ping() {		
		/*
		 *  0   <startbyte>
		 *  1   <i2c_addr>/<offset>
		 *  2   <num_bytes_to_send>
		 *  3   command type, was <num_bytes_to_receive>
		 *  4   data marker
		 *  5   ... data
		 *  n   end of data
		 */
		
		byte cmdfull[] = new byte[7];
		cmdfull[0] = START_OF_CMD;
		cmdfull[1] = 0; //unused here!
		cmdfull[2] = 0x01;
		cmdfull[3] = CMD_PING;
		cmdfull[4] = START_OF_DATA;
		cmdfull[5] = 0x02;
		cmdfull[6] = END_OF_DATA;

		try {
			writeNetworkData(cmdfull);
			return waitForAck();			
		} catch (Exception e) {
			return false;
		}
	}

	
	/**
	 * wrapper class to send a RGB image to the lpd6803 device.
	 * the rgb image gets converted to the lpd6803 device compatible
	 * "image format"
	 *
	 * @param ofs the image ofs
	 * @param data rgb data (int[64], each int contains one RGB pixel)
	 * @param colorFormat the color format
	 * @return true if send was successful
	 */
	public boolean sendRgbFrame(byte ofs, int[] data, ColorFormat colorFormat) {
		if (data.length!=BUFFERSIZE) {
			throw new IllegalArgumentException("data lenght must be 64 bytes!");
		}
		return sendFrame(ofs, OutputHelper.convertBufferTo15bit(data, colorFormat));
	}


	
	/**
	 * get md5 hash out of an image. used to check if the image changed
	 *
	 * @param ofs the ofs
	 * @param data the data
	 * @return true if send was successful
	 */
	private boolean didFrameChange(byte ofs, byte data[]) {
		String s = MD5.asHex(data);
		
		if (!lastDataMap.containsKey(ofs)) {
			//first run
			lastDataMap.put(ofs, s);
			return true;
		}
		
		if (lastDataMap.get(ofs).equals(s)) {
			//last frame was equal current frame, do not send it!
			//log.log(Level.INFO, "do not send frame to {0}", addr);
			return false;
		}
		//update new hash
		lastDataMap.put(ofs, s);
		return true;
	}
	
	/**
	 * send a frame to the active lpd6803 device.
	 *
	 * @param ofs - the offset get multiplied by 32 on the arduino!
	 * @param data byte[3*8*4]
	 * @return true if send was successful
	 * @throws IllegalArgumentException the illegal argument exception
	 */
	public boolean sendFrame(byte ofs, byte data[]) throws IllegalArgumentException {		
		if (data.length!=128) {
			throw new IllegalArgumentException("data lenght must be 128 bytes!");
		}
		
/*		//TODO stop if connection counter > n
 		if (connectionErrorCounter>) {
			return false;
		}*/

		byte ofsOne = (byte)(ofs*2);
		byte ofsTwo = (byte)(ofsOne+1);
		byte frameOne[] = new byte[BUFFERSIZE];
		byte frameTwo[] = new byte[BUFFERSIZE];
		boolean returnValue = false;
		
		System.arraycopy(data, 0, frameOne, 0, BUFFERSIZE);
		System.arraycopy(data, BUFFERSIZE, frameTwo, 0, BUFFERSIZE);
		
		byte sendlen = BUFFERSIZE;
		byte cmdfull[] = new byte[sendlen+7];
		
		cmdfull[0] = START_OF_CMD;
		//cmdfull[1] = ofs;
		cmdfull[2] = (byte)sendlen;
		cmdfull[3] = CMD_SENDFRAME;
		cmdfull[4] = START_OF_DATA;		
//		for (int i=0; i<sendlen; i++) {
//			cmdfull[5+i] = data[i];
//		}
		cmdfull[sendlen+5] = END_OF_DATA;

		//send frame one
		if (didFrameChange(ofsOne, frameOne)) {
			cmdfull[1] = ofsOne;
			
			//this is needed due the hardware-wirings 
			flipSecondScanline(cmdfull, frameOne);
			
			if (sendNetworkData(cmdfull)) {
				returnValue=true;
			} else {
				//in case of an error, make sure we send it the next time!
				lastDataMap.put(ofsOne, "");
			}
		}
		
		//send frame two
		if (didFrameChange(ofsTwo, frameTwo)) {
			cmdfull[1] = ofsTwo;
			
			flipSecondScanline(cmdfull, frameTwo);
			
			if (sendNetworkData(cmdfull)) {
				returnValue=true;
			} else {
				lastDataMap.put(ofsTwo, "");
			}
		}/**/
		return returnValue;
	}
	
	/**
	 * this function feed the framebufferdata (32 pixels a 2bytes (aka 16bit)
	 * to the send array. each second scanline gets inverteds
	 *
	 * @param cmdfull the cmdfull
	 * @param frameData the frame data
	 */
	private static void flipSecondScanline(byte cmdfull[], byte frameData[]) {
		int toggler=14;
		for (int i=0; i<16; i++) {
			cmdfull[   5+i] = frameData[i];
			cmdfull[32+5+i] = frameData[i+32];
			
			cmdfull[16+5+i] = frameData[16+toggler];				
			cmdfull[48+5+i] = frameData[48+toggler];
			
			if (i%2==0) {
				toggler++;
			} else {
				toggler-=3;
			}
		}
	}

	/**
	 * Send serial data.
	 *
	 * @param cmdfull the cmdfull
	 * @return true, if successful
	 */
	private boolean sendNetworkData(byte cmdfull[]) {
		try {
			writeNetworkData(cmdfull);
			if (waitForAck()) {
				//frame was send successful
				return true;
			}
		} catch (Exception e) {
			LOG.log(Level.WARNING, "sending serial data failed: {0}", e);
		}
		return false;
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
	private synchronized void writeNetworkData(byte[] cmdfull) throws SerialPortException {
		try {
			clientConnection.write(cmdfull);
		} catch (Exception e) {
			LOG.log(Level.INFO, "Error sending network data!", e);
			connectionErrorCounter++;
			throw new SerialPortException("cannot send serial data, errorNr: "+connectionErrorCounter+", Error: "+e);
		}		
	}
	
	
	/**
	 * read data from serial port, wait for ACK.
	 *
	 * @return true if ack received, false if not
	 */
	private synchronized boolean waitForAck() {
		//Client client = listeningSrv.available();
		Client client = clientConnection;
//		System.out.println("wait for ack, "+client);
		if (client !=null) {
			sleep(8);
			byte[] msg = client.readBytes();
			
			if (msg==null) {
				ackErrors++;
//				System.out.println("no reply");
				return false;
			}
//			System.out.println("got "+msg.length+" bytes");
			for (int i=0; i<msg.length-1; i++) {
				if (msg[i]== 'A' && msg[i+1]== 'K') {
					try {
						//System.out.println("GOOD");
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
     * @return the serialPortName
     */
    public String getDestIp() {
        return destIp;
    }

    public int getDestPort() {
		return destPort;
	}


	/**
	 * Sleep wrapper.
	 *
	 * @param ms the ms
	 */
	private void sleep(int ms) {
		try {
			Thread.sleep(ms);
		}
		catch(InterruptedException e) {
		}
	}
	




}
