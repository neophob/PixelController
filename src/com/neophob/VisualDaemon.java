package com.neophob;

import java.util.Date;

import processing.core.PApplet;

import com.neophob.sematrix.glue.Collector;
import com.neophob.sematrix.output.Lpd6803Device;
import com.neophob.sematrix.output.MatrixEmulator;
import com.neophob.sematrix.output.helper.NewWindowHelper;
import com.neophob.sematrix.properties.PropertiesHelper;

/**
 * 
 * @author michu
 * 
 * TODO:
 * make image resize option (speed/quality) user selectable
 * make zoom option, usefull for one screen
 *
 */
public class VisualDaemon extends PApplet {

	private static final long serialVersionUID = -1336765543826338205L;
	
	private static final int DEVICE_SIZE = 8;

	public static final int FPS = 20;
	//96*2*25 = 4800bytes

	//Output rainbowduino;
	Lpd6803Device lpd6803;
	NewWindowHelper nwh;
	long lastHeartbeat;
	int error=0;
	int frameCounter=0;
	MatrixEmulator osd;

	public void setup() {
		//		ImageIcon titlebaricon = new ImageIcon(loadBytes("logo.jpg"));
		//		super.frame.setIconImage(titlebaricon.getImage()); 
		//		super.frame.setTitle("This is in the titlebar!");

		Collector.getInstance().init(this, FPS, DEVICE_SIZE, DEVICE_SIZE);
		frameRate(FPS);
		noSmooth();
		
		osd = new MatrixEmulator();

/*		try {
			rainbowduino = new RainbowduinoDevice(PropertiesHelper.getAllI2cAddress());
		} catch (Exception e) {
			rainbowduino = null;
		}*/
		try {
			lpd6803 = new Lpd6803Device(PropertiesHelper.getInstance().getLpdDevice());
		} catch (Exception e) {
			lpd6803 = null;
		}
		
		
		if (PropertiesHelper.getInstance().getProperty("show.debug.window").equalsIgnoreCase("true")) {
			nwh = new NewWindowHelper(true);	
		}
	}

	public void draw() { 
		//update all generators
 
		Collector.getInstance().updateSystem();

		if (lpd6803!=null && lpd6803.getArduinoErrorCounter()>0) {
			error=lpd6803.getArduinoErrorCounter();
			System.out.println("error at: "+new Date(lpd6803.getLatestHeartbeat()).toGMTString()+
					", errorcnt: "+error+
					", buffersize: "+lpd6803.getArduinoBufferSize());
		}

		frameCounter++;
	}

	public static void main(String args[]) {
		PApplet.main(new String[] { "com.neophob.VisualDaemon" });
	}
}
