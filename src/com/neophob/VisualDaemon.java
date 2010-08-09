package com.neophob;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import processing.core.PApplet;

import com.neophob.sematrix.generator.Blinkenlights;
import com.neophob.sematrix.generator.Generator;
import com.neophob.sematrix.generator.Generator.GeneratorName;
import com.neophob.sematrix.glue.Collector;
import com.neophob.sematrix.glue.Shuffler;
import com.neophob.sematrix.output.MatrixEmulator;
import com.neophob.sematrix.output.Output;
import com.neophob.sematrix.output.RainbowduinoDevice;
import com.neophob.sematrix.output.helper.NewWindowHelper;

/**
 * 
 * @author michu
 *
 */
public class VisualDaemon extends PApplet {

	private static final long serialVersionUID = -1336765543826338205L;

	public static final int FPS = 30;
	//96*2*25 = 4800bytes
	public static final int NR_OF_SCREENS = 4;
	
	RainbowduinoDevice rainbowduino;
	NewWindowHelper nwh;
	long lastHeartbeat;
	int error=0;
	int frame=0;
	MatrixEmulator osd;
	
	
	public void setup() {
		Collector.getInstance().init(this, FPS, NR_OF_SCREENS, 8, 8);
		osd = new MatrixEmulator();
		
		frameRate(Collector.getInstance().getFps());
		
		List<Integer> i2cDest = new ArrayList<Integer>();
		i2cDest.add(6); i2cDest.add(5); 
		try {
			rainbowduino = new RainbowduinoDevice(i2cDest);			
		} catch (Exception e) {
			rainbowduino = null;
		}
		
		//screen nr, fx nr
		//Collector.getInstance().mapInputToScreen(0, 5);
		Blinkenlights blink = (Blinkenlights)Collector.getInstance().getGenerator(GeneratorName.BLINKENLIGHTS);
		for (int i=880; i<100; i++) {
			blink.loadFile("bb-rauten2.bml");
			System.out.println(i);
		}
		//nwh = new NewWindowHelper(true);
	}
	
	public void draw() { 
		//update all generators
		for (Generator m: Collector.getInstance().getAllGenerators()) {
			m.update();
		}
		for (Output o: Collector.getInstance().getAllOutputs()) {
			o.update();
		}
		
		if (Collector.getInstance().isRandomMode()) {
			Shuffler.shuffleStuff();
		}
		
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
