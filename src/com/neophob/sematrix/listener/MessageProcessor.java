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
import com.neophob.sematrix.properties.PropertiesHelper;

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

		int tmp;
		
		try {			
			ValidCommands cmd = ValidCommands.valueOf(msg[0]);
			Collector col = Collector.getInstance();
			switch (cmd) {
			case STATUS:
				return ValidCommands.STATUS;

			case CHANGE_GENERATOR_A:
				try {
					int size = col.getAllVisuals().size();
					for (int i=0; i<size; i++) {
						tmp=Integer.parseInt(msg[i+1]);
						col.getVisual(i).setGenerator1(tmp);
					}
/*					int a = Integer.parseInt(msg[1]);
					int b = Integer.parseInt(msg[2]);
					int c = Integer.parseInt(msg[3]);
					int d = Integer.parseInt(msg[4]);
					int e = Integer.parseInt(msg[5]);
					col.getVisual(0).setGenerator1(a);
					col.getVisual(1).setGenerator1(b);
					col.getVisual(2).setGenerator1(c);
					col.getVisual(3).setGenerator1(d);
					col.getVisual(4).setGenerator1(e);*/
				} catch (Exception e) {
					log.log(Level.WARNING,	"Ignored command", e);
				}
				break;
			case CHANGE_GENERATOR_B:
				try {
					int size = col.getAllVisuals().size();
					for (int i=0; i<size; i++) {
						tmp=Integer.parseInt(msg[i+1]);
						col.getVisual(i).setGenerator2(tmp);
					}
				} catch (Exception e) {
					log.log(Level.WARNING,	"Ignored command", e);
				}
				break;

			case CHANGE_EFFECT_A:
				try {
					int size = col.getAllVisuals().size();
					for (int i=0; i<size; i++) {
						tmp=Integer.parseInt(msg[i+1]);
						col.getVisual(i).setEffect1(tmp);
					}
				} catch (Exception e) {
					log.log(Level.WARNING,	"Ignored command", e);
				}
				break;

			case CHANGE_EFFECT_B:
				try {					
					int size = col.getAllVisuals().size();
					for (int i=0; i<size; i++) {
						tmp=Integer.parseInt(msg[i+1]);
						col.getVisual(i).setEffect2(tmp);
					}
				} catch (Exception e) {
					log.log(Level.WARNING,	"Ignored command", e);
				}
				break;

			case CHANGE_MIXER:
				try {
					int size = col.getAllVisuals().size();
					for (int i=0; i<size; i++) {
						tmp=Integer.parseInt(msg[i+1]);
						col.getVisual(i).setMixer(tmp);
					}
				} catch (Exception e) {
					log.log(Level.WARNING,	"Ignored command", e);
				}
				break;

			case CHANGE_OUTPUT:
				try {
					int size = col.getAllOutputMappings().size();
					for (int i=0; i<size; i++) {
						int newFx = Integer.parseInt(msg[i+1]);
						int oldFx = col.getFxInputForScreen(i);
						if(oldFx!=newFx) {
							log.log(Level.INFO,	"Change Output 0, old fx: {0}, new fx {1}", new Object[] {oldFx, newFx});
							//col.mapInputToScreen(0, newFxA);						
							col.getOutputMappings(i).getFader().startFade(newFx, i);
						}
					}
				} catch (Exception e) {
					log.log(Level.WARNING,	"Ignored command", e);
				}
				break;

			case CHANGE_OUTPUT_EFFECT:
				try {
					int size = col.getAllOutputMappings().size();
					for (int i=0; i<size; i++) {
						tmp=Integer.parseInt(msg[i+1]);
						col.getOutputMappings(i).setEffect(col.getEffect(tmp));
					}
				} catch (Exception e) {
					log.log(Level.WARNING,	"Ignored command", e);
				}
				break;

			case CHANGE_FADER:
				try {
					int size = col.getAllOutputMappings().size();
					for (int i=0; i<size; i++) {
						tmp=Integer.parseInt(msg[i+1]);
						col.getOutputMappings(i).setFader(col.getFader(tmp));
					}
				} catch (Exception e) {
					log.log(Level.WARNING,	"Ignored command", e);
				}
				break;
				
			case CHANGE_SHUFFLER_SELECT:
				try {					
					int size = col.getShufflerSelect().size();
					boolean b;
					for (int i=0; i<size; i++) {
						b = false;
						if (msg[i+1].equals("1")) b = true;
						col.setShufflerSelect(i, b);
					}					
				} catch (Exception e) {
					log.log(Level.WARNING,	"Ignored command", e);
				}
				break;
				
			case CHANGE_TINT:
				try {					
					int r = Integer.parseInt(msg[1]);
					int g = Integer.parseInt(msg[2]);
					int b = Integer.parseInt(msg[3]);
					if (r>255) r=255;
					if (g>255) g=255;
					if (b>255) b=255;
					Tint t = (Tint)col.getEffect(EffectName.TINT);
					t.setColor(r, g, b);
					col.setRGB(r, g, b);
				} catch (Exception e) {
					log.log(Level.WARNING,	"Ignored command", e);
				}
				break;

			case SAVE_PRESENT:
				try {
					int idxs = col.getSelectedPresent();
					List<String> present = col.getCurrentStatus();
					col.getPresent().get(idxs).setPresent(present);
					PropertiesHelper.savePresents();
				} catch (Exception e) {
					log.log(Level.WARNING,	"Ignored command", e);
				}
				break;

			case LOAD_PRESENT:
				try {
					int idxl = col.getSelectedPresent();
					List<String> present = col.getPresent().get(idxl).getPresent();
					if (present!=null) { 
						col.setCurrentStatus(present);
					}
					return ValidCommands.STATUS;					
				} catch (Exception e) {
					log.log(Level.WARNING,	"Ignored command", e);
				}
				break;
			case CHANGE_PRESENT:
				try {
					int a = Integer.parseInt(msg[1]);
					col.setSelectedPresent(a);
				} catch (Exception e) {
					log.log(Level.WARNING,	"Ignored command", e);
				}
				break;
				
			case BLINKEN:
				try {
					String fileToLoad = msg[1];
					col.setFileBlinken(fileToLoad);
					Blinkenlights blink = (Blinkenlights)col.getGenerator(GeneratorName.BLINKENLIGHTS);
					blink.loadFile(fileToLoad);
				} catch (Exception e) {
					log.log(Level.WARNING,	"Ignored command", e);
				}
				break;

			case IMAGE:
				try {
					String fileToLoad = msg[1];
					col.setFileImage(fileToLoad);
					Image img = (Image)col.getGenerator(GeneratorName.IMAGE);
					img.loadFile(fileToLoad);
				} catch (Exception e) {
					log.log(Level.WARNING,	"Ignored command", e);
				}
				break;

			case RANDOM:
				try {
					String onOrOff = msg[1];
					if (onOrOff.equalsIgnoreCase("ON")) {
						col.setRandomMode(true);
					}
					if (onOrOff.equalsIgnoreCase("OFF")) {
						col.setRandomMode(false);
						return ValidCommands.STATUS;
					}
				} catch (Exception e) {
					log.log(Level.WARNING,	"Ignored command", e);
				}
				break;

			default:
				String s="";
				for (int i=0; i<msg.length;i++) s+=msg[i]+"; ";
				log.log(Level.INFO,	"Ignored command <{0}>", s);
				break;
			}
		} catch (IllegalArgumentException e) {
			log.log(Level.INFO,	"Illegal argument: <{0}>", new Object[] { msg[0] });			
		}		

		return null;
	}
}
