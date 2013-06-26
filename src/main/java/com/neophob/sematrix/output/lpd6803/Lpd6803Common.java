package com.neophob.sematrix.output.lpd6803;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.Adler32;

import com.neophob.sematrix.output.OutputHelper;
import com.neophob.sematrix.output.gamma.RGBAdjust;
import com.neophob.sematrix.properties.ColorFormat;

public abstract class Lpd6803Common {
	
	/** The log. */
	private static final Logger LOG = Logger.getLogger(Lpd6803Common.class.getName());

	/** number of leds horizontal<br> TODO: should be dynamic, someday. */
	public static final int NR_OF_LED_HORIZONTAL = 8;

	/** number of leds vertical<br> TODO: should be dynamic, someday. */
	public static final int NR_OF_LED_VERTICAL = NR_OF_LED_HORIZONTAL;

	/** The Constant BUFFERSIZE. */
	protected static final int BUFFERSIZE = NR_OF_LED_HORIZONTAL*NR_OF_LED_VERTICAL;

	/** The Constant START_OF_CMD. */
	protected static final byte START_OF_CMD = 0x01;
	
	/** The Constant CMD_SENDFRAME. */
	protected static final byte CMD_SENDFRAME = 0x03;
	
	/** The Constant CMD_PING. */
	protected static final byte CMD_PING = 0x04;

	/** The Constant START_OF_DATA. */
	protected static final byte START_OF_DATA = 0x10;
	
	/** The Constant END_OF_DATA. */
	protected static final byte END_OF_DATA = 0x20;

	protected static Adler32 adler = new Adler32();
	
	/** The connection error counter. */
	protected int connectionErrorCounter;
	
	/** map to store checksum of image. */
	protected Map<Byte, Long> lastDataMap = new HashMap<Byte, Long>();

	/** correction map to store adjustment data, contains offset and correction data */
	protected Map<Integer, RGBAdjust> correctionMap = new HashMap<Integer, RGBAdjust>();

	protected boolean initialized;
	
	/** The ack errors. */
	protected long ackErrors = 0;

	
	/**
	 * return connection state of lib.
	 *
	 * @return whether a lpd6803 device is connected
	 */
	public boolean connected() {
		return initialized;
	}	

	/**
	 * wrapper class to send a RGB image to the lpd6803 device.
	 * the rgb image gets converted to the lpd6803 device compatible
	 * "image format"
	 *
	 * @param ofs the image ofs
	 * @param data rgb data (int[64], each int contains one RGB pixel)
	 * @param colorFormat the color format
	 * @return nr of sended update frames
	 */
	public int sendRgbFrame(byte ofs, int[] data, ColorFormat colorFormat) {
		if (data.length!=BUFFERSIZE) {
			throw new IllegalArgumentException("data lenght must be 64 bytes!");
		}
		
		if (correctionMap.containsKey((byte)ofs)) {
			RGBAdjust correction = correctionMap.get((byte)ofs);
			return sendFrame(ofs, OutputHelper.convertBufferTo15bit(data, colorFormat, correction));			
		}

		return sendFrame(ofs, OutputHelper.convertBufferTo15bit(data, colorFormat));
	}
	


	
	/**
	 * send a frame to the active lpd6803 device.
	 *
	 * @param ofs - the offset get multiplied by 32 on the arduino!
	 * @param data byte[3*8*4]
	 * @return nr of sended frames
	 * @throws IllegalArgumentException the illegal argument exception
	 */
	public int sendFrame(byte ofs, byte data[]) throws IllegalArgumentException {		
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
		int returnValue = 0;
		
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
			
			if (sendData(cmdfull)) {
				returnValue++;
			} else {
				//in case of an error, make sure we send it the next time!
				lastDataMap.put(ofsOne, 0L);
			}
		}
		
		//send frame two
		if (didFrameChange(ofsTwo, frameTwo)) {
			cmdfull[1] = ofsTwo;
			
			flipSecondScanline(cmdfull, frameTwo);
			
			if (sendData(cmdfull)) {
				returnValue++;
			} else {
				lastDataMap.put(ofsTwo, 0L);
			}
		}/**/
		return returnValue;
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
			writeData(cmdfull);
			return waitForAck();			
		} catch (Exception e) {
			return false;
		}
	}
	
	/**
	 * Send serial data.
	 *
	 * @param cmdfull the cmdfull
	 * @return true, if successful
	 */
	protected boolean sendData(byte cmdfull[]) {
		try {
			writeData(cmdfull);
			if (waitForAck()) {
				//frame was send successful
				return true;
			}
		} catch (Exception e) {
			LOG.log(Level.WARNING, "sending serial data failed: {0}", e);
		}
		return false;
	}

	public int getConnectionErrorCounter() {
		return connectionErrorCounter;
	}

	/**
	 * 
	 * @param cmdfull
	 * @throws WriteDataException
	 */
	protected abstract void writeData(byte[] cmdfull) throws WriteDataException;
	
	/**
	 * 
	 * @return
	 */
	protected abstract boolean waitForAck();
		
	
	/**
	 * get md5 hash out of an image. used to check if the image changed
	 *
	 * @param ofs the ofs
	 * @param data the data
	 * @return true if send was successful
	 */
	protected boolean didFrameChange(byte ofs, byte data[]) {
		adler.reset();
		adler.update(data);
		long l = adler.getValue();
		
		if (!lastDataMap.containsKey(ofs)) {
			//first run
			lastDataMap.put(ofs, l);
			return true;
		}
		
		if (lastDataMap.get(ofs) == l) {
			//last frame was equal current frame, do not send it!
			//log.log(Level.INFO, "do not send frame to {0}", addr);
			return false;
		}
		//update new hash
		lastDataMap.put(ofs, l);
		return true;
	}


	/**
	 * this function feed the framebufferdata (32 pixels a 2bytes (aka 16bit)
	 * to the send array. each second scanline gets inverteds
	 *
	 * @param cmdfull the cmdfull
	 * @param frameData the frame data
	 */
	protected static void flipSecondScanline(byte cmdfull[], byte frameData[]) {
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
	 * Sleep wrapper.
	 *
	 * @param ms the ms
	 */
	protected void sleep(int ms) {
		try {
			Thread.sleep(ms);
		}
		catch(InterruptedException e) {
		}
	}


}
