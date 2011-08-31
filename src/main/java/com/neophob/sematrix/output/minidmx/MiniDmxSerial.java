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
 * TODO: does not work yet
 * 
 * @author Michael Vogt / neophob.com
 *
 */
public class MiniDmxSerial {
		
	private static Logger log = Logger.getLogger(MiniDmxSerial.class.getName());
	
	/** 
	 * internal lib version
	 */
	public static final String VERSION = "1.0";

	//connection errors to arduino, TODO: use it!
	private int connectionErrorCounter;

	private int targetBuffersize;
	
	private PApplet app;

	private int baud = 230400;
	private Serial port;

	/**
	 * map to store checksum of image
	 */
	private String lastDataMap;
	
	
	/**
	 * Create a new instance to communicate with the rainbowduino.
	 * 
	 * @param _app
	 * @param rainbowduinoAddr
	 * @throws NoSerialPortFoundException
	 */
	public MiniDmxSerial(PApplet app, int targetBuffersize) throws NoSerialPortFoundException {
		this(app, null, targetBuffersize);
	}

	/**
	 * Create a new instance to communicate with the rainbowduino.
	 * 
	 * @param _app
	 * @param rainbowduinoAddr
	 * @param portName
	 * @throws NoSerialPortFoundException
	 */
	public MiniDmxSerial(PApplet _app, int targetBuffersize, String portName) throws NoSerialPortFoundException {
		this(_app, portName, targetBuffersize);
	}


	/**
	 * Create a new instance to communicate with the rainbowduino.
	 * 
	 * @param _app
	 * @param portName
	 * @param targetBuffersize
	 * @param rainbowduinoAddr
	 * @throws NoSerialPortFoundException
	 */
	public MiniDmxSerial(PApplet _app, String portName, int targetBuffersize) throws NoSerialPortFoundException {
		
		log.log(Level.INFO,	"Initialize miniDMX lib v{0}", VERSION);
		
		this.app = _app;
		app.registerDispose(this);
		
		lastDataMap = "";
		
		String serialPortName="";
		this.targetBuffersize = targetBuffersize;
		
		if (portName!=null && !portName.trim().isEmpty()) {
			//open specific port
			log.log(Level.INFO,	"open port: {0}", portName);
			serialPortName = portName;
			openPort(portName);
		} else {
			//try to find the port
			String[] ports = Serial.list();
			for (int i=0; port==null && i<ports.length; i++) {
				log.log(Level.INFO,	"open port: {0}", ports[i]);
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
		
		log.log(Level.INFO,	"found serial port: "+serialPortName);
	}


	/**
	 * clean up library
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
	 * return connection state of lib 
	 * 
	 * @return wheter rainbowudino is connected
	 */
	public boolean connected() {
		return (port != null);
	}	

	

	/**
 	 * 
 	 * Open serial port with given name. Send ping to check if port is working.
	 * If not port is closed and set back to null
	 * 
	 * @param portName
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
			log.log(Level.WARNING, "No response from port {0}", portName);
			if (port != null) {
				port.stop();        					
			}
			port = null;
			throw new NoSerialPortFoundException("No response from port "+portName);
		} catch (Exception e) {	
			log.log(Level.WARNING, "Failed to open port {0}: {1}", new Object[] {portName, e});
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
		//TODO
		return false;
	}
	
	
	/**
	 * wrapper class to send a RGB image to the miniDmx device.
	 * the rgb image gets converted to the miniDmx compatible
	 * "image format"
	 * 
	 * @param ofs the image ofs
	 * @param data rgb data (int[64], each int contains one RGB pixel)
	 * @return true if send was successful
	 */
	public boolean sendRgbFrame(int[] data, ColorFormat colorFormat) {
		return sendFrame(convertBufferTo15bit(data, colorFormat));
	}


	
	/**
	 * get md5 hash out of an image. used to check if the image changed
	 * @param addr
	 * @param data
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
	 * @param ofs - the offset get multiplied by 32 on the arduino!
	 * @param data byte[3*8*4]
	 * @return true if send was successful
	 */
	public boolean sendFrame(byte data[]) throws IllegalArgumentException {		
		//TODO
		return false;
	}
	

	/**
	 * 
	 * @param cmdfull
	 * @return
	 */
	private boolean sendSerialData(byte cmdfull[]) {
		try {
			writeSerialData(cmdfull);
			if (waitForAck()) {
				//frame was send successful
				return true;
			}
		} catch (Exception e) {
			log.log(Level.WARNING, "sending serial data failed: {0}", e);
		}
		return false;
	}
	
	/**
	 * send the data to the serial port
	 * @param cmdfull
	 */
	private synchronized void writeSerialData(byte[] cmdfull) throws SerialPortException {
		//TODO handle the 128 byte buffer limit!
		if (port==null) {
			throw new SerialPortException("port is not ready!");
		}
		
		//log.log(Level.INFO, "Serial Wire Size: {0}", cmdfull.length);

		try {
			port.output.write(cmdfull);
			//port.output.flush();
			//DO NOT flush the buffer... hmm not sure about this, processing flush also
			//and i discovered strange "hangs"...
		} catch (Exception e) {
			log.log(Level.INFO, "Error sending serial data!", e);
			connectionErrorCounter++;
			throw new SerialPortException("cannot send serial data, errorNr: "+connectionErrorCounter+", Error: "+e);
		}		
	}
	
	/**
	 * read data from serial port, wait for ACK
	 * @return true if ack received, false if not
	 */
	private synchronized boolean waitForAck() {		
		//TODO
		return false;		
	}




	/**
	 * Sleep wrapper
	 * @param ms
	 */
	private void sleep(int ms) {
		try {
			Thread.sleep(ms);
		}
		catch(InterruptedException e) {
		}
	}
	
	/**
	 * 
	 * @param data
	 * @return
	 * @throws IllegalArgumentException
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
	 * 
	 * @param r
	 * @param g
	 * @param b
	 * @return
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



}
