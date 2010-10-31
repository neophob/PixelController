package com.neophob.sematrix.output;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.neophob.lib.rainbowduino.NoSerialPortFoundException;
import com.neophob.lib.rainbowduino.Rainbowduino;
import com.neophob.sematrix.glue.Collector;

public class RainbowduinoDevice extends Output {

	private static Logger log = Logger.getLogger(RainbowduinoDevice.class.getName());
	
	private List<Integer> allI2cAddress;
	private Rainbowduino rainbowduino = null;
	private boolean initialized;
	
	/**
	 * init the rainbowduino devices 
	 * @param allI2COutputs a list containing all i2c slave addresses
	 */
	public RainbowduinoDevice(List<Integer> allI2cAddress) {
		super(RainbowduinoDevice.class.toString());
		this.allI2cAddress = allI2cAddress;
		
		this.initialized = false;		
		try {
			rainbowduino = new Rainbowduino( Collector.getInstance().getPapplet(), allI2cAddress);
			//rainbowduino.initPort("/dev/null", allI2cAddress);
			this.initialized = rainbowduino.ping();
			log.log(Level.INFO, "ping result: "+ this.initialized);			
		} catch (NoSerialPortFoundException e) {
			log.log(Level.WARNING, "failed to initialize serial port!");
		}
		
	}
	

	public long getLatestHeartbeat() {
		if (initialized) {
			return rainbowduino.getArduinoHeartbeat();			
		}
		return -1;
	}

	public int getArduinoBufferSize() {
		if (initialized) {
			return rainbowduino.getArduinoBufferSize();			
		}
		return -1;
	}

	public int getArduinoErrorCounter() {
		if (initialized) {
			return rainbowduino.getArduinoErrorCounter();			
		}
		return -1;
	}

	public void update() {
		if (initialized) {
			int size=allI2cAddress.size();
			for (int screen=0; screen<Collector.getInstance().getNrOfScreens(); screen++) {
				//draw only on available screens!
				if (screen<size) {
					int i2cAddr = allI2cAddress.get(screen);
					rainbowduino.sendRgbFrame((byte)i2cAddr, super.getBufferForScreen(screen));					
				}

			}
		}
	}

	@Override
	public void close() {
		if (initialized) {
			rainbowduino.dispose();			
		}
	}

}
