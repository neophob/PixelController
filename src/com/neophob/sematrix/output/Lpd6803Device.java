package com.neophob.sematrix.output;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.neophob.lib.rainbowduino.Lpd6803;
import com.neophob.lib.rainbowduino.NoSerialPortFoundException;
import com.neophob.sematrix.glue.Collector;

public class Lpd6803Device extends Output {

	private static Logger log = Logger.getLogger(Lpd6803Device.class.getName());
		
	private Lpd6803 lpd6803 = null;
	private boolean initialized;
	
	long needUpdate, noUpdate;

	/**
	 * init the rainbowduino devices 
	 * @param allI2COutputs a list containing all i2c slave addresses
	 */
	public Lpd6803Device() {
		super(Lpd6803Device.class.toString());
		
		this.initialized = false;		
		try {
			lpd6803 = new Lpd6803( Collector.getInstance().getPapplet() );			
			this.initialized = lpd6803.ping();
			log.log(Level.INFO, "ping result: "+ this.initialized);			
		} catch (NoSerialPortFoundException e) {
			log.log(Level.WARNING, "failed to initialize serial port!");
		}
	}
	

	public long getLatestHeartbeat() {
		if (initialized) {
			return lpd6803.getArduinoHeartbeat();			
		}
		return -1;
	}

	public int getArduinoBufferSize() {
		if (initialized) {
			return lpd6803.getArduinoBufferSize();			
		}
		return -1;
	}

	public int getArduinoErrorCounter() {
		if (initialized) {
			return lpd6803.getArduinoErrorCounter();			
		}
		return -1;
	}

	/**
	 * 
	 */
	public void update() {
		
		if (initialized) {			
			for (int screen=0; screen<Collector.getInstance().getNrOfScreens(); screen++) {
				//draw only on available screens!
				if (!lpd6803.sendRgbFrame((byte)screen, super.getBufferForScreen(screen))) {
					noUpdate++;
				} else {
					needUpdate++;
				}
			}
			
			if ((noUpdate+needUpdate)%100==0) {
				float f = noUpdate+needUpdate;
				float result = (100.0f/f)*needUpdate;
				log.log(Level.INFO, "sended frames: {0}% {1}/{2}, ack Errors: {3} last Error: {4}, arduino buffer size: {5}", 
						new Object[] {result, needUpdate, noUpdate, lpd6803.getAckErrors(), 
						lpd6803.getArduinoErrorCounter(), lpd6803.getArduinoBufferSize()});				
			}
			
		}
	}


	
	@Override
	public void close() {
		if (initialized) {
			lpd6803.dispose();			
		}
	}

}
