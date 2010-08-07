package com.neophob.sematrix.listener;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.neophob.sematrix.effect.Tint;
import com.neophob.sematrix.effect.Effect.EffectName;
import com.neophob.sematrix.generator.Blinkenlights;
import com.neophob.sematrix.generator.Image;
import com.neophob.sematrix.generator.Generator.GeneratorName;
import com.neophob.sematrix.glue.Collector;
import com.neophob.sematrix.listener.TcpServer.ValidCommands;

public class MessageProcessor {

	private static Logger log = Logger.getLogger(MessageProcessor.class.getName());

	private MessageProcessor() {
		//no instance
	}

	/**
	 * 
	 * @param msg
	 */
	public static synchronized ValidCommands processMsg(String[] msg) {
		if (msg==null || msg.length<1) {
			return null;
		}

		try {			
			ValidCommands cmd = ValidCommands.valueOf(msg[0]);

			switch (cmd) {
			case STATUS:
				return ValidCommands.STATUS;

			case CHANGE_GENERATOR_A:
				try {
					int a = Integer.parseInt(msg[1]);
					int b = Integer.parseInt(msg[2]);
					int c = Integer.parseInt(msg[3]);
					int d = Integer.parseInt(msg[4]);
					int e = Integer.parseInt(msg[5]);
					Collector.getInstance().getVisual(0).setGenerator1(a);
					Collector.getInstance().getVisual(1).setGenerator1(b);
					Collector.getInstance().getVisual(2).setGenerator1(c);
					Collector.getInstance().getVisual(3).setGenerator1(d);
					Collector.getInstance().getVisual(4).setGenerator1(e);
				} catch (Exception e) {e.printStackTrace();}
				break;
			case CHANGE_GENERATOR_B:
				try {
					int a = Integer.parseInt(msg[1]);
					int b = Integer.parseInt(msg[2]);
					int c = Integer.parseInt(msg[3]);
					int d = Integer.parseInt(msg[4]);
					int e = Integer.parseInt(msg[5]);
					Collector.getInstance().getVisual(0).setGenerator2(a);
					Collector.getInstance().getVisual(1).setGenerator2(b);
					Collector.getInstance().getVisual(2).setGenerator2(c);
					Collector.getInstance().getVisual(3).setGenerator2(d);
					Collector.getInstance().getVisual(4).setGenerator2(e);
				} catch (Exception e) {e.printStackTrace();}
				break;

			case CHANGE_EFFECT_A:
				try {
					int a = Integer.parseInt(msg[1]);
					int b = Integer.parseInt(msg[2]);
					int c = Integer.parseInt(msg[3]);
					int d = Integer.parseInt(msg[4]);
					int e = Integer.parseInt(msg[5]);
					Collector.getInstance().getVisual(0).setEffect1(a);
					Collector.getInstance().getVisual(1).setEffect1(b);
					Collector.getInstance().getVisual(2).setEffect1(c);
					Collector.getInstance().getVisual(3).setEffect1(d);
					Collector.getInstance().getVisual(4).setEffect1(e);
				} catch (Exception e) {e.printStackTrace();}
				break;

			case CHANGE_EFFECT_B:
				try {					
					int a = Integer.parseInt(msg[1]);
					int b = Integer.parseInt(msg[2]);
					int c = Integer.parseInt(msg[3]);
					int d = Integer.parseInt(msg[4]);
					int e = Integer.parseInt(msg[5]);
					Collector.getInstance().getVisual(0).setEffect2(a);
					Collector.getInstance().getVisual(1).setEffect2(b);
					Collector.getInstance().getVisual(2).setEffect2(c);
					Collector.getInstance().getVisual(3).setEffect2(d);
					Collector.getInstance().getVisual(4).setEffect2(e);
				} catch (Exception e) {e.printStackTrace();}
				break;

			case CHANGE_MIXER:
				try {					
					int a = Integer.parseInt(msg[1]);
					int b = Integer.parseInt(msg[2]);
					int c = Integer.parseInt(msg[3]);
					int d = Integer.parseInt(msg[4]);
					int e = Integer.parseInt(msg[5]);
					Collector.getInstance().getVisual(0).setMixer(a);
					Collector.getInstance().getVisual(1).setMixer(b);
					Collector.getInstance().getVisual(2).setMixer(c);
					Collector.getInstance().getVisual(3).setMixer(d);
					Collector.getInstance().getVisual(4).setMixer(e);
				} catch (Exception e) {e.printStackTrace();}
				break;

			case CHANGE_OUTPUT:
				try {					
					int newFxA = Integer.parseInt(msg[1]);
					int newFxB = Integer.parseInt(msg[2]);
					int oldFxA = Collector.getInstance().getFxInputForScreen(0);
					int oldFxB = Collector.getInstance().getFxInputForScreen(1);
					if(oldFxA!=newFxA) {
						log.log(Level.INFO,	"Change Output 0, old fx: {0}, new fx {1}", new Object[] {oldFxA, newFxA});
						//Collector.getInstance().mapInputToScreen(0, newFxA);						
						Collector.getInstance().getAllOutputMappings().get(0).getFader().startFade(newFxA, 0);
					}
					if(oldFxB!=newFxB) {
						log.log(Level.INFO,	"Change Output 1, old fx: {0}, new fx {1}", new Object[] {oldFxB, newFxB});
						//Collector.getInstance().mapInputToScreen(1, newFxB);
						Collector.getInstance().getAllOutputMappings().get(1).getFader().startFade(newFxB, 1);
					}
				} catch (Exception e) {e.printStackTrace();}
				break;

			case CHANGE_OUTPUT_EFFECT:
				try {					
					int newFxA = Integer.parseInt(msg[1]);
					int newFxB = Integer.parseInt(msg[2]);
					Collector.getInstance().getAllOutputMappings().get(0).setEffect(Collector.getInstance().getEffect(newFxA));
					Collector.getInstance().getAllOutputMappings().get(1).setEffect(Collector.getInstance().getEffect(newFxB));
				} catch (Exception e) {e.printStackTrace();}
				break;

			case CHANGE_FADER:
				try {					
					int a = Integer.parseInt(msg[1]);
					int b = Integer.parseInt(msg[2]);
					Collector.getInstance().getAllOutputMappings().get(0).setFader(Collector.getInstance().getFader(a));					
					Collector.getInstance().getAllOutputMappings().get(1).setFader(Collector.getInstance().getFader(b));					
				} catch (Exception e) {e.printStackTrace();}
				break;

			case CHANGE_TINT:
				try {					
					int r = Integer.parseInt(msg[1]);
					int g = Integer.parseInt(msg[2]);
					int b = Integer.parseInt(msg[3]);
					if (r>255) r=255;
					if (g>255) g=255;
					if (b>255) b=255;
					Tint t = (Tint)Collector.getInstance().getEffect(EffectName.TINT);
					t.setColor(r, g, b);
					Collector.getInstance().setRGB(r, g, b);
				} catch (Exception e) {e.printStackTrace();}
				break;

			case SAVE_PRESENT:
				int idxs = Collector.getInstance().getSelectedPresent();
				List<String> present = Collector.getInstance().getCurrentStatus();
				Collector.getInstance().getPresent().get(idxs).setPresent(present);
				//TODO SAVE PRESENT
				break;

			case LOAD_PRESENT:
				int idxl = Collector.getInstance().getSelectedPresent();
				present = Collector.getInstance().getPresent().get(idxl).getPresent();
				if (present!=null) { 
					Collector.getInstance().setCurrentStatus(present);
				}
				return ValidCommands.STATUS;

			case CHANGE_PRESENT:
				try {
					int a = Integer.parseInt(msg[1]);
					Collector.getInstance().setSelectedPresent(a);
				} catch (Exception e) {e.printStackTrace();}
				break;
				
			case BLINKEN:
				try {
					String fileToLoad = msg[1];
					Collector.getInstance().setFileBlinken(fileToLoad);
					Blinkenlights blink = (Blinkenlights)Collector.getInstance().getGenerator(GeneratorName.BLINKENLIGHTS);
					blink.loadFile(fileToLoad);
				} catch (Exception e) {e.printStackTrace();}
				break;

			case IMAGE:
				try {
					String fileToLoad = msg[1];
					Collector.getInstance().setFileImage(fileToLoad);
					Image img = (Image)Collector.getInstance().getGenerator(GeneratorName.IMAGE);
					img.loadFile(fileToLoad);
				} catch (Exception e) {e.printStackTrace();}
				break;

			case RANDOM:
				try {
					String onOrOff = msg[1];
					if (onOrOff.equalsIgnoreCase("ON")) {
						Collector.getInstance().setRandomMode(true);
					}
					if (onOrOff.equalsIgnoreCase("OFF")) {
						Collector.getInstance().setRandomMode(false);
						return ValidCommands.STATUS;
					}
				} catch (Exception e) {e.printStackTrace();}
				break;

			default:
				System.out.println("valid: "+cmd);
				for (int i=1; i<msg.length; i++) System.out.println(msg[i]);
				break;
			}
		} catch (IllegalArgumentException e) {
			log.log(Level.INFO,	"Illegal argument: <{0}>", new Object[] { msg[0] });			
		}		

		return null;
	}
}
