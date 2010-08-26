package com.neophob;

import java.util.Date;

import processing.core.PApplet;

import com.neophob.sematrix.glue.Collector;
import com.neophob.sematrix.output.MatrixEmulator;
import com.neophob.sematrix.output.RainbowduinoDevice;
import com.neophob.sematrix.output.helper.NewWindowHelper;
import com.neophob.sematrix.properties.PropertiesHelper;

/**
 * 
 * @author michu
 *
 */
public class VisualDaemon extends PApplet {

	private static final long serialVersionUID = -1336765543826338205L;

	public static final int FPS = 25;
	//96*2*25 = 4800bytes
	
	RainbowduinoDevice rainbowduino;
	NewWindowHelper nwh;
	long lastHeartbeat;
	int error=0;
	int frame=0;
	MatrixEmulator osd;
	
	
	public void setup() {
		Collector.getInstance().init(this, FPS, 8, 8);
		frameRate(FPS);
		
		osd = new MatrixEmulator();
		
		try {
			rainbowduino = new RainbowduinoDevice(PropertiesHelper.getAllI2cAddress());			
		} catch (Exception e) {
			rainbowduino = null;
		}
		
		if (PropertiesHelper.getProperty("show.debug.window").equalsIgnoreCase("true")) {
			nwh = new NewWindowHelper(true);	
		}
	}
	
	public void draw() { 
		//update all generators

		Collector.getInstance().updateSystem();
		
		if (rainbowduino!=null) {
			long l = rainbowduino.getLatestHeartbeat();
			if (l!=lastHeartbeat) {
				error+=rainbowduino.getArduinoErrorCounter();
				System.out.println("last heartbeat: "+new Date(l).toGMTString()+
						", errorcnt: "+error+
						", buffersize: "+rainbowduino.getArduinoBufferSize());
				lastHeartbeat = l;
			}			
		}

		frame++;
	}
	
	public static void main(String args[]) {
		PApplet.main(new String[] { "com.neophob.VisualDaemon" });
	}
}
