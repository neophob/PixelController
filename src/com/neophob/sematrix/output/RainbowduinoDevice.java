package com.neophob.sematrix.output;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.neophob.lib.rainbowduino.Rainbowduino;
import com.neophob.sematrix.glue.Collector;

public class RainbowduinoDevice extends Output {

	private static Logger log = Logger.getLogger(RainbowduinoDevice.class.getName());
	
	private List<Integer> allI2cAddress;
	private Rainbowduino rainbowduino = null;
	private boolean ping;
	
	/**
	 * init the rainbowduino devices 
	 * @param allI2COutputs a list containing all i2c slave addresses
	 */
	public RainbowduinoDevice(List<Integer> allI2cAddress) {
		super(RainbowduinoDevice.class.toString());
		this.allI2cAddress = allI2cAddress;
		
		rainbowduino = new Rainbowduino( Collector.getInstance().getPapplet() );
		rainbowduino.initPort();
		
		ping = rainbowduino.ping((byte)0);;
		log.log(Level.INFO, "ping result: "+ ping);
	}
	

	public long getLatestHeartbeat() {
		return rainbowduino.getArduinoHeartbeat();
	}

	public int getArduinoBufferSize() {
		return rainbowduino.getArduinoBufferSize();
	}

	public int getArduinoErrorCounter() {
		return rainbowduino.getArduinoErrorCounter();
	}

	public void update() {
		if (ping) {
			int size=allI2cAddress.size();
			for (int screen=0; screen<Collector.getInstance().getNrOfScreens(); screen++) {
				//draw only on available screens!
				if (screen<size) {
					int i2cAddr = allI2cAddress.get(screen);
					rainbowduino.sendRgbFrame((byte)i2cAddr, super.getBufferForScreen(screen), false);					
				}

			}
		}
	}

	@Override
	public void close() {
		rainbowduino.dispose();
	}

}
