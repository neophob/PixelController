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

package com.neophob.sematrix.output.minidmx;

import java.util.logging.Level;
import java.util.logging.Logger;

import processing.core.PApplet;
import processing.serial.Serial;

import com.neophob.lib.rainbowduino.NoSerialPortFoundException;
import com.neophob.lib.rainbowduino.RainbowduinoHelper;
import com.neophob.lib.rainbowduino.SerialPortException;
import com.neophob.sematrix.properties.ColorFormat;

/**
 * library to communicate with an miniDmx device<br>
 * created for ledstyles.de 
 * <br><br>
 * 
 * TODO: guess it does not work yet, as its untested
 * TODO: padding
 * 
 * @author Michael Vogt / neophob.com
 *
 */
public class MiniDmxSerial {
		
	/** The log. */
	private static final Logger LOG = Logger.getLogger(MiniDmxSerial.class.getName());
	
	/** internal lib version. */
	public static final String VERSION = "1.0";

	/** The Constant START_OF_BLOCK. */
	private static final byte START_OF_BLOCK = (byte)0x5a;
	
	/** The Constant END_OF_BLOCK. */
	private static final byte END_OF_BLOCK   = (byte)0xa5;

	/** The Constant REPLY_SUCCESS. */
	private static final byte REPLY_SUCCESS  = (byte)0xc1;
	
	/** The Constant REPLY_ERROR. */
	private static final byte REPLY_ERROR    = (byte)0xc0;
	
	/** The Constant SEND_96_BYTES. */
	private static final byte SEND_96_BYTES    = (byte)0xa0; //32 pixel, for example 8x4 pixel
	
	/** The Constant SEND_256_BYTES. */
	private static final byte SEND_256_BYTES   = (byte)0xa1; //85 pixel, for example 8x8 pixel and padding
	
	/** The Constant SEND_512_BYTES. */
	private static final byte SEND_512_BYTES   = (byte)0xa2; //170 pixel, for example 16x8 pixel and padding
	
	/** The Constant SEND_768_BYTES. */
	private static final byte SEND_768_BYTES   = (byte)0xb0; //256 pixel, for example 16x16 pixel
	
	/** The Constant SEND_1536_BYTES. */
	private static final byte SEND_1536_BYTES  = (byte)0xb1; //512 pixel, for example 32x16 pixel
	
	/** The Constant SEND_3072_BYTES. */
	private static final byte SEND_3072_BYTES  = (byte)0xb2; //1024 pixel, for example 32x32 pixel
	
	//connection errors to arduino, TODO: use it!
	/** The connection error counter. */
	private int connectionErrorCounter;
	
	/** The ack errors. */
	private long ackErrors = 0;

	/** The target buffersize. */
	private int targetBuffersize;
	
	/** The app. */
	private PApplet app;

	/** The baud. */
	private int baud = 230400;
	
	/** The port. */
	private Serial port;

	/** map to store checksum of image. */
	private String lastDataMap;
	
	
	/**
	 * Create a new instance to communicate with the rainbowduino.
	 *
	 * @param app the app
	 * @param targetBuffersize the target buffersize
	 * @throws NoSerialPortFoundException the no serial port found exception
	 */
	public MiniDmxSerial(PApplet app, int targetBuffersize) throws NoSerialPortFoundException {
		this(app, null, targetBuffersize);
	}

	/**
	 * Create a new instance to communicate with the rainbowduino.
	 *
	 * @param _app the _app
	 * @param targetBuffersize the target buffersize
	 * @param portName the port name
	 * @throws NoSerialPortFoundException the no serial port found exception
	 */
	public MiniDmxSerial(PApplet _app, int targetBuffersize, String portName) throws NoSerialPortFoundException {
		this(_app, portName, targetBuffersize);
	}


	/**
	 * Create a new instance to communicate with the rainbowduino.
	 *
	 * @param _app the _app
	 * @param portName the port name
	 * @param targetBuffersize the target buffersize
	 * @throws NoSerialPortFoundException the no serial port found exception
	 */
	public MiniDmxSerial(PApplet _app, String portName, int targetBuffersize) throws NoSerialPortFoundException {
		
		LOG.log(Level.INFO,	"Initialize miniDMX lib v{0}", VERSION);
		
		this.app = _app;
		app.registerDispose(this);
		
		lastDataMap = "";
		
		String serialPortName="";	
		if (targetBuffersize == 96 || targetBuffersize == 256 || targetBuffersize == 512 || targetBuffersize == 768 || targetBuffersize == 3072) {
			this.targetBuffersize = targetBuffersize;
		} else {
			LOG.log(Level.SEVERE, "Invalid buffer size selected: {0}"+targetBuffersize);
			throw new NoSerialPortFoundException("Invalid buffer size selected: "+targetBuffersize);
		}
		
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
	 * send a serial ping command to the arduino board.
	 * 
	 * @return wheter ping was successfull (arduino reachable) or not
	 */
	public boolean ping() {		
		byte data[] = new byte[512];
		
		//just send a frame
		return sendFrame(data);
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
		return sendFrame(convertBufferTo24bit(data, colorFormat));
	}


	
	/**
	 * get md5 hash out of an image. used to check if the image changed
	 *
	 * @param data the data
	 * @return true if send was successful
	 */
	private boolean didFrameChange(byte data[]) {
		String s = RainbowduinoHelper.getMD5(data);
		
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
	 * send a frame to the miniDMX device.
	 * 
	 * 0x5A - start of block
	 * 0xA0 - DMX-Out using 96 channels
	 * 96 Bytes payload
	 * 0xA5 - end of block
	 * 
	 * instead of a0h (96b):
	 * -a1h: 256b
	 * -a2h: 512b
	 * -
	 *
	 * @param data byte[3*8*4]
	 * @return true if send was successful
	 * @throws IllegalArgumentException the illegal argument exception
	 */
	public boolean sendFrame(byte data[]) throws IllegalArgumentException {		
		if (data.length!=targetBuffersize) {
			throw new IllegalArgumentException("data lenght must be "+targetBuffersize+" bytes!");
		}
		byte cmdfull[] = new byte[targetBuffersize+3];
		
		cmdfull[0] = START_OF_BLOCK;
		switch (targetBuffersize) {
		case 96:
			cmdfull[1] = SEND_96_BYTES;
			break;
		case 256:
			cmdfull[1] = SEND_256_BYTES;
			break;
		case 512:
			cmdfull[1] = SEND_512_BYTES;
			break;
		case 768:
			cmdfull[1] = SEND_768_BYTES;
			break;
		case 1536:
			cmdfull[1] = SEND_1536_BYTES;
			break;			
		case 3072:
			cmdfull[1] = SEND_3072_BYTES;
			break;
		default:
			LOG.log(Level.WARNING, "Invalid targetBuffersize: {0}", targetBuffersize);
			return false;
		}
		System.arraycopy(data, 0, cmdfull, 2, targetBuffersize);
		cmdfull[targetBuffersize+2] = END_OF_BLOCK;

		//send frame only if needed
		if (didFrameChange(data)) {
			if (sendSerialData(cmdfull)) {
				return true;
			} else {
				//in case of an error, make sure we send it the next time!
				lastDataMap="";
			}
		}

		return false;
	}
	

	/**
	 * Send serial data.
	 *
	 * @param cmdfull the cmdfull
	 * @return true, if successful
	 */
	private boolean sendSerialData(byte cmdfull[]) {
		try {
			writeSerialData(cmdfull);
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
	 * send the data to the serial port.
	 *
	 * @param cmdfull the cmdfull
	 * @throws SerialPortException the serial port exception
	 */
	private synchronized void writeSerialData(byte[] cmdfull) throws SerialPortException {
		//TODO handle the 128 byte buffer limit!
		if (port==null) {
			throw new SerialPortException("port is not ready!");
		}
		
		try {
			port.output.write(cmdfull);
			//port.output.flush();
			//DO NOT flush the buffer... hmm not sure about this, processing flush also
			//and i discovered strange "hangs"...
		} catch (Exception e) {
			LOG.log(Level.INFO, "Error sending serial data!", e);
			connectionErrorCounter++;
			throw new SerialPortException("cannot send serial data, errorNr: "+connectionErrorCounter+", Error: "+e);
		}		
	}
	
	/**
	 * read data from serial port, wait for ACK, miniDMX should send
	 * 
	 * 0x5A - start of block
	 * 0xC1 - success
	 * 0xA5 - end of block
	 * 
	 * 0x5A - start of block
	 * 0xC0 - ERROR
	 * 0xA5 - end of block
	 * 
	 * after 100ms.
	 *
	 * @return true if ack received, false if not
	 */
	private synchronized boolean waitForAck() {		

		long start = System.currentTimeMillis();
		int timeout=8; //wait up to 24ms
		while (timeout > 0 && port.available() < 2) {
			sleep(4); //in ms
			timeout--;
		}

		if (timeout == 0 && port.available() < 2) {
			LOG.log(Level.INFO, "#### No serial reply, duration: {0}ms ###", System.currentTimeMillis()-start);
			ackErrors++;
			return false;
		}
		
		//we need at least 3 bytes for a correct reply
		byte[] msg = port.readBytes();
		if (msg.length<3) {
			LOG.log(Level.INFO, "#### less than 3 bytes of data receieved: {0}ms ###", System.currentTimeMillis()-start);
			ackErrors++;
			return false;
		}
		
		int ofs=0;
		for (byte b:msg) {
			if (b==START_OF_BLOCK && msg.length-ofs>2 && msg[ofs+2]==END_OF_BLOCK) {
				byte ack = msg[ofs+1];
				if (ack==REPLY_SUCCESS) {
					return true;
				}
				if (ack==REPLY_ERROR) {
					LOG.log(Level.INFO, "#### Invalid reply: {0}ms ###", System.currentTimeMillis()-start);
					return true;
				}
				LOG.log(Level.INFO, "#### Unknown reply: {0} ###", ack);
				
			}
			ofs++;
		}
		
		return false;		
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
	
	
	/**
	 * Convert buffer to24bit.
	 *
	 * @param data the data
	 * @param colorFormat the color format
	 * @return the byte[]
	 * @throws IllegalArgumentException the illegal argument exception
	 */
	public byte[] convertBufferTo24bit(int[] data, ColorFormat colorFormat) throws IllegalArgumentException {
		if (data.length!=targetBuffersize) {
			throw new IllegalArgumentException("data lenght must be "+targetBuffersize+" bytes!");
		}

		int[] r = new int[targetBuffersize];
		int[] g = new int[targetBuffersize];
		int[] b = new int[targetBuffersize];
		int tmp;
		int ofs=0;

		//step#1: split up r/g/b 
		for (int n=0; n<targetBuffersize; n++) {
			//one int contains the rgb color
			tmp = data[ofs];

			switch (colorFormat) {
			case RGB:
				r[ofs] = (int) ((tmp>>16) & 255);
				g[ofs] = (int) ((tmp>>8)  & 255);
				b[ofs] = (int) ( tmp      & 255);		
				
				break;
			case RBG:
				r[ofs] = (int) ((tmp>>16) & 255);
				b[ofs] = (int) ((tmp>>8)  & 255);
				g[ofs] = (int) ( tmp      & 255);		
				
				break;
			}
			ofs++;
		}

		ofs=0;
		byte[] buffer = new byte[targetBuffersize*3];
		for (int i=0; i<targetBuffersize; i++) {
			buffer[ofs++] = (byte)r[i];
			buffer[ofs++] = (byte)g[i];
			buffer[ofs++] = (byte)b[i];
		}
		
		return buffer;
	}

	
	/**
	 * Convert buffer to15bit.
	 *
	 * @param data the data
	 * @param colorFormat the color format
	 * @return the byte[]
	 * @throws IllegalArgumentException the illegal argument exception
	 */
	public byte[] convertBufferTo15bit(int[] data, ColorFormat colorFormat) throws IllegalArgumentException {
		if (data.length!=targetBuffersize) {
			throw new IllegalArgumentException("data lenght must be "+targetBuffersize+" bytes!");
		}

		int[] r = new int[targetBuffersize];
		int[] g = new int[targetBuffersize];
		int[] b = new int[targetBuffersize];
		int tmp;
		int ofs=0;

		//step#1: split up r/g/b 
		for (int n=0; n<targetBuffersize; n++) {
			//one int contains the rgb color
			tmp = data[ofs];

			switch (colorFormat) {
			case RGB:
				r[ofs] = (int) ((tmp>>16) & 255);
				g[ofs] = (int) ((tmp>>8)  & 255);
				b[ofs] = (int) ( tmp      & 255);		
				
				break;
			case RBG:
				r[ofs] = (int) ((tmp>>16) & 255);
				b[ofs] = (int) ((tmp>>8)  & 255);
				g[ofs] = (int) ( tmp      & 255);		
				
				break;
			}
			ofs++;
		}

		return convertTo15Bit(r,g,b);
	}

	/**
	 * Convert to15 bit.
	 *
	 * @param r the r
	 * @param g the g
	 * @param b the b
	 * @return the byte[]
	 */
	private static byte[] convertTo15Bit(int[] r, int[] g, int[] b) {
		int dst=0;
		byte[] converted = new byte[128];
		//convert to 24bpp to 15(16)bpp 
		//output format: RRRRRGGG GGGBBBBB (64x)
		for (int i=0; i<64;i++) {
			byte b1 = (byte)(r[i]>>3);
			byte b2 = (byte)(g[i]>>3);
			byte b3 = (byte)(b[i]>>3);

			converted[dst++] = (byte)((b1<<2) | (b2>>3));
			converted[dst++] = (byte)(((b2&7)<<5) | b3);
		}
		
		return converted;
	}

	/**
	 * Gets the connection error counter.
	 *
	 * @return the connection error counter
	 */
	public int getConnectionErrorCounter() {
		return connectionErrorCounter;
	}

	/**
	 * Gets the ack errors.
	 *
	 * @return the ack errors
	 */
	public long getAckErrors() {
		return ackErrors;
	}


}
