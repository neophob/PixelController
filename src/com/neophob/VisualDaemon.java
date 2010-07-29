package com.neophob;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import processing.core.PApplet;

import com.neophob.sematrix.effect.Effect.EffectName;
import com.neophob.sematrix.generator.Generator;
import com.neophob.sematrix.generator.Generator.GeneratorName;
import com.neophob.sematrix.glue.Collector;
import com.neophob.sematrix.glue.Visual;
import com.neophob.sematrix.listener.TcpServer;
import com.neophob.sematrix.mixer.Mixer.MixerName;
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
public class VisualDaemon extends PApplet {

	private static final long serialVersionUID = -1336765543826338205L;

	public static final int FPS = 20;
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
		Collector.getInstance().init(this, FPS, NR_OF_SCREENS, 8, 8);
		srv = new TcpServer(this, 3443, "127.0.0.1", 3445);		
		
		osd = new MatrixEmulator();
		
		frameRate(Collector.getInstance().getFps());
		
		List<Integer> i2cDest = new ArrayList<Integer>();
		i2cDest.add(6); i2cDest.add(5);
		try {
			rainbowduino = new RainbowduinoDevice(i2cDest);			
		} catch (Exception e) {
			rainbowduino = null;
		}
		
		Visual v = Collector.getInstance().getVisual(0);
		v.setEffect1(Collector.getInstance().getEffect(EffectName.RND_HORIZONTAL_SHIFT));
		v.setGenerator1(Collector.getInstance().getGenerator(GeneratorName.BLINKENLIGHTS));
		v.setEffect2(Collector.getInstance().getEffect(EffectName.PASSTHRU));
		v.setGenerator2(Collector.getInstance().getGenerator(GeneratorName.BLINKENLIGHTS));
		v.setMixer(Collector.getInstance().getMixer(MixerName.ADDSAT));

		//screen nr, fx nr
		//Collector.getInstance().mapInputToScreen(0, 5);
		Collector.getInstance().mapInputToScreen(1, 4);
		
		nwh = new NewWindowHelper(true);
		/**/
		
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
			Random rand = new Random();
			int blah = rand.nextInt(50);
			
			if (blah == 22) {
				Collector.getInstance().getVisual(0).setGenerator1(rand.nextInt(7));
				Collector.getInstance().getVisual(1).setGenerator1(rand.nextInt(7));
				Collector.getInstance().getVisual(2).setGenerator1(rand.nextInt(7));
				Collector.getInstance().getVisual(3).setGenerator1(rand.nextInt(7));
				Collector.getInstance().getVisual(4).setGenerator1(rand.nextInt(7));				
			}

			if (blah == 24) {
				Collector.getInstance().getVisual(0).setGenerator2(rand.nextInt(7));
				Collector.getInstance().getVisual(1).setGenerator2(rand.nextInt(7));
				Collector.getInstance().getVisual(2).setGenerator2(rand.nextInt(7));
				Collector.getInstance().getVisual(3).setGenerator2(rand.nextInt(7));
				Collector.getInstance().getVisual(4).setGenerator2(rand.nextInt(7));
			}
			
			if (blah == 44) {
				Collector.getInstance().getVisual(0).setEffect1(rand.nextInt(5));
				Collector.getInstance().getVisual(1).setEffect1(rand.nextInt(5));
				Collector.getInstance().getVisual(2).setEffect1(rand.nextInt(5));
				Collector.getInstance().getVisual(3).setEffect1(rand.nextInt(5));
				Collector.getInstance().getVisual(4).setEffect1(rand.nextInt(5));
			}

			if (blah == 4) {
				Collector.getInstance().getVisual(0).setEffect2(rand.nextInt(5));
				Collector.getInstance().getVisual(1).setEffect2(rand.nextInt(5));
				Collector.getInstance().getVisual(2).setEffect2(rand.nextInt(5));
				Collector.getInstance().getVisual(3).setEffect2(rand.nextInt(5));
				Collector.getInstance().getVisual(4).setEffect2(rand.nextInt(5));
			}
			
			if (blah == 9) {
				Collector.getInstance().getVisual(0).setMixer(rand.nextInt(4));
				Collector.getInstance().getVisual(1).setMixer(rand.nextInt(4));
				Collector.getInstance().getVisual(2).setMixer(rand.nextInt(4));
				Collector.getInstance().getVisual(3).setMixer(rand.nextInt(4));
				Collector.getInstance().getVisual(4).setMixer(rand.nextInt(4));
			}
			
			if (blah == 11) {
				Collector.getInstance().getAllOutputMappings().get(0).setFader(Collector.getInstance().getFader(rand.nextInt(4)));					
				Collector.getInstance().getAllOutputMappings().get(1).setFader(Collector.getInstance().getFader(rand.nextInt(4)));		
			}
			
			if (blah == 19) {
				Collector.getInstance().getAllOutputMappings().get(0).getFader().startFade(rand.nextInt(5), 0);
				Collector.getInstance().getAllOutputMappings().get(1).getFader().startFade(rand.nextInt(5), 1);
			}

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
		/*
		if ((frame%400)==65) {
			Visual v = Collector.getInstance().getVisual(0);
			Fader f = new SlideLeftRight(0,4,0);
			v.setFader(f);			
		}
		if ((frame%400)==265) {
			Visual v = Collector.getInstance().getVisual(4);
			Fader f = new Crossfader(4,0,0);
			v.setFader(f);			
		}
/*		if ((frame%500)==320) {
			Visual v = Collector.getInstance().getVisual(0);
			v.setGenerator2( 0 );
			v.setEffect2( 2 );
			v.setMixer( 0 );
		}*/
	}
	
	public static void main(String args[]) {
		PApplet.main(new String[] { "com.neophob.VisualDaemon" });
	}
}
