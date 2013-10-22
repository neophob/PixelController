package com.neophob.sematrix.output.pixelinvaders;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.Adler32;

import netP5.Bytes;

import com.neophob.sematrix.output.OutputHelper;
import com.neophob.sematrix.output.gamma.RGBAdjust;
import com.neophob.sematrix.output.tpm2.Tpm2NetProtocol;
import com.neophob.sematrix.properties.ColorFormat;

public abstract class Lpd6803Common {
	
	/** The log. */
	private static final Logger LOG = Logger.getLogger(Lpd6803Common.class.getName());

	protected int nrOfLedHorizontal;

	protected int nrOfLedVertical;

	/** The Constant BUFFERSIZE. */
	protected int bufferSize;

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
	
	public Lpd6803Common(int xRes, int yRes) {
		this.nrOfLedHorizontal = xRes;
		this.nrOfLedVertical = yRes;
		this.bufferSize = xRes*yRes;
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
	 * wrapper class to send a RGB image to the lpd6803 device.
	 * the rgb image gets converted to the lpd6803 device compatible
	 * "image format"
	 *
	 * @param ofs the image ofs
	 * @param data rgb data (int[64], each int contains one RGB pixel)
	 * @param colorFormat the color format
	 * @param totalPanels total panels
	 * @return nr of sended bytes
	 */
	public int sendRgbFrame(byte ofs, int[] data, ColorFormat colorFormat, int totalPanels) {
		if (data.length!=bufferSize) {
			throw new IllegalArgumentException("data lenght must be "+bufferSize+" bytes, was "+data.length);
		}
		
		int ofsAsInt = ofs;
		if (correctionMap.containsKey(ofsAsInt)) {
			RGBAdjust correction = correctionMap.get(ofsAsInt);
			return sendFrame(ofs, OutputHelper.convertBufferTo15bit(data, colorFormat, correction), totalPanels);			
		}

		return sendFrame(ofs, OutputHelper.convertBufferTo15bit(data, colorFormat), totalPanels);
	}
	


	
	/**
	 * send a frame to the active lpd6803 device.
	 *
	 * @param ofs - the offset get multiplied by 32 on the Arduino!
	 * @param data byte[3*8*4]
	 * @return nr of sended bytes or -1 if an error occurred
	 * @throws IllegalArgumentException the illegal argument exception
	 */
	public int sendFrame(byte ofs, byte data[], int totalPanels) throws IllegalArgumentException {		
		byte[] imagePayload = Tpm2NetProtocol.createImagePayload(ofs, totalPanels, data);

		if (sendData(imagePayload)) {
			return imagePayload.length;
		}
		//in case of an error, make sure we send it the next time!
		lastDataMap.put(ofs, 0L);
		return -1;
	}
	
	/**
	 * send a serial ping command to the arduino board.
	 * 
	 * @return wheter ping was successfull (arduino reachable) or not
	 */
	public boolean ping() {
		byte[] pingPayload = Tpm2NetProtocol.createCmdPayload(new byte[] {(byte)1});

		try {
			writeData(pingPayload);
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
	public boolean sendData(byte cmdfull[]) {
		try {
			writeData(cmdfull);

			//just write out debug output from the microcontroller
			byte[] replyFromController = getReplyFromController();
			if (replyFromController!=null && replyFromController.length > 0) {
				String reply = Bytes.getAsString(replyFromController);
				if (reply.contains("ERR:")) {
					connectionErrorCounter++;
				}
				LOG.log(Level.INFO, "<<< ["+reply+"]");
			}  			
			return true;
		} catch (Exception e) {
			LOG.log(Level.WARNING, "sending serial data failed: {0}", e);
		}
		return false;
	}

	
	/**
	 * 
	 * @return
	 */
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
	 * get all data which are sent back from the controller
	 * 
	 * @return
	 */
	protected abstract byte[] getReplyFromController();
	
	/**
	 * get md5 hash out of an image. used to check if the image changed
	 *
	 * @param ofs the ofs
	 * @param data the data
	 * @return true if send was successful
	 */
	private boolean didFrameChange(byte ofs, byte data[]) {
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
	 * 
	 * @param ofs
	 * @param data
	 * @return
	 */
	public boolean didFrameChange(byte ofs, int data[]) {
		ByteBuffer b = ByteBuffer.allocate(data.length*4);
		for (int i: data) {
			b.putInt(i);			
		}

		return didFrameChange(ofs, b.array());
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

	
	/**
	 * is the serial port initialized
	 * 
	 * @return
	 */
	public boolean isInitialized() {
		return initialized;
	}

	/**
	 * 
	 * @return
	 */
	public int getNrOfLedHorizontal() {
		return nrOfLedHorizontal;
	}

	/**
	 * 
	 * @return
	 */
	public int getNrOfLedVertical() {
		return nrOfLedVertical;
	}


}
