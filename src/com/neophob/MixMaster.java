package com.neophob;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import processing.core.PApplet;

import com.neophob.sematrix.effect.Effect;
import com.neophob.sematrix.effect.PassThru;
import com.neophob.sematrix.generator.Blinkenlights;
import com.neophob.sematrix.generator.Generator;
import com.neophob.sematrix.generator.Plasma;
import com.neophob.sematrix.generator.SimpleColors;
import com.neophob.sematrix.glue.Collector;
import com.neophob.sematrix.glue.MatrixData;
import com.neophob.sematrix.input.Sound;
import com.neophob.sematrix.listener.TcpServer;
import com.neophob.sematrix.mixer.Mixer;
import com.neophob.sematrix.mixer.Multiply;
import com.neophob.sematrix.output.MatrixEmulator;
import com.neophob.sematrix.output.Output;
import com.neophob.sematrix.output.RainbowduinoDevice;
import com.neophob.sematrix.output.helper.NewWindowHelper;

/**
 * 
 * @author michu
 *
 * TODO:
 *  -mixer
 *  -"fullscreen"
 *  -layouts
 */
public class MixMaster extends PApplet {
	//50 fps, and the arduino gets shaky!
	public static final int FPS = 50;
	//96*2*25 = 4800bytes
	public static final int NR_OF_SCREENS = 2;
	
	RainbowduinoDevice rainbowduino;
	NewWindowHelper nwh;
	long lastHeartbeat;
	int error=0;
	int frame=0;
	MatrixEmulator osd;
	TcpServer srv;
	
	public void setup() {
		Collector.getInstance().init(this, NR_OF_SCREENS);
		srv = new TcpServer(this, 3443, "127.0.0.1", 3445);
		
		Sound.getInstance();
		new MatrixData(8, 8);
		osd = new MatrixEmulator();
		
		frameRate(FPS);
		background(33,33,33);
		size(900,400);
		List<Integer> i2cDest = new ArrayList<Integer>();
		i2cDest.add(6); i2cDest.add(5);
		try {
			rainbowduino = new RainbowduinoDevice(i2cDest);			
		} catch (Exception e) {
			rainbowduino = null;
		}

		Plasma plasma = new Plasma();
		Effect plasmaFx = new PassThru();
		Mixer plamaMix = new Multiply(plasmaFx);
		//mix.addEffect(volumeDisplayFx);
		
		SimpleColors sc = new SimpleColors();
		Effect fxSc = new PassThru();
		Mixer scMix = new Multiply(fxSc);
		
		Blinkenlights blink = new Blinkenlights("torus.bml");
		Effect fxBlink = new PassThru();
		Mixer blinkMix = new Multiply(fxBlink);
		
		Collector.getInstance().mapInputToScreen(0, 0);
		Collector.getInstance().mapInputToScreen(1, 0);
		
		nwh = new NewWindowHelper(true);
		/**/
		
	}
	
	public void draw() { 
		for (Generator m: Collector.getInstance().getAllGenerators()) {
			m.update();
		}
		for (Output o: Collector.getInstance().getAllOutputs()) {
			o.update();
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
		
/*		if ((frame%500)==160) {
			Collector.getInstance().mapInputToScreen(1, 1);
		}
		if ((frame%500)==320) {
			Collector.getInstance().mapInputToScreen(1, 2);
			Collector.getInstance().mapInputToScreen(0, 2);
		}*/
	}
	
	public static void main(String args[]) {
		PApplet.main(new String[] { /*"--present", */"com.neophob.MixMaster" });
	}
}
