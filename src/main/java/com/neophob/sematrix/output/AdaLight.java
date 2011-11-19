package com.neophob.sematrix.output;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import processing.serial.Serial;

import com.neophob.sematrix.glue.Collector;
import com.neophob.sematrix.properties.PropertiesHelper;

public class AdaLight extends OnePanelResolutionAwareOutput {

	/** The log. */
	private static final Logger LOG = Logger.getLogger(AdaLight.class.getName());

	private static final int BPS = 115200;
	private static final int HEADERSIZE = 6;

	private static final String VERSION = "0.1";

	private int panelsize;
	
	private byte[] buffer;
	private Serial port;
	
	public AdaLight(PropertiesHelper ph, PixelControllerOutput controller) {
		super(OutputDeviceEnum.ADALIGHT, ph, controller, 8);

		LOG.log(Level.INFO,	"Initialize AdaLight lib v{0}", VERSION);

		LOG.log(Level.INFO,  "AdaLight X resolution: {0}, Y resolution: {1}", new Object[] {
		        this.xResolution, this.yResolution});

		this.panelsize = this.xResolution*this.yResolution;
		//TODO should use autodetection someday
		port = new Serial(Collector.getInstance().getPapplet(), Serial.list()[0], BPS);
				
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
