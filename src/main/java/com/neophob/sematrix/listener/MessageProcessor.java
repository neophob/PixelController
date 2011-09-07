/**
 * Copyright (C) 2011 Michael Vogt <michu@neophob.com>
 *
 * This file is part of PixelController.
 *
 * PixelController is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PixelController is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with PixelController.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.neophob.sematrix.listener;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.neophob.sematrix.effect.Effect.EffectName;
import com.neophob.sematrix.effect.RotoZoom;
import com.neophob.sematrix.fader.PixelControllerFader;
import com.neophob.sematrix.glue.Collector;
import com.neophob.sematrix.glue.Shuffler;
import com.neophob.sematrix.properties.PropertiesHelper;

/**
 * The Class MessageProcessor.
 */
public final class MessageProcessor {

	/**
	 * The Enum ValidCommands.
	 */
	public enum ValidCommands {
		//refresh whole gui
		/** The STATUS. */
		STATUS,
		//just refresh the gui
		/** The STATU s_ mini. */
		STATUS_MINI,
		
		/** The CHANG e_ generato r_ a. */
		CHANGE_GENERATOR_A,
		
		/** The CHANG e_ generato r_ b. */
		CHANGE_GENERATOR_B,
		
		/** The CHANG e_ effec t_ a. */
		CHANGE_EFFECT_A,
		
		/** The CHANG e_ effec t_ b. */
		CHANGE_EFFECT_B,
		
		/** The CHANG e_ mixer. */
		CHANGE_MIXER,
		
		/** The CHANG e_ output. */
		CHANGE_OUTPUT,
		
		/** The CHANG e_ outpu t_ effect. */
		CHANGE_OUTPUT_EFFECT,
		
		/** The CHANG e_ fader. */
		CHANGE_FADER,
		
		/** The CHANG e_ tint. */
		CHANGE_TINT,
		
		/** The CHANG e_ present. */
		CHANGE_PRESENT,
		
		/** The CHANG e_ shuffle r_ select. */
		CHANGE_SHUFFLER_SELECT,
		
		/** The CHANG e_ threshol d_ value. */
		CHANGE_THRESHOLD_VALUE,
		
		/** The CHANG e_ rotozoom. */
		CHANGE_ROTOZOOM,
		
		/** The SAV e_ present. */
		SAVE_PRESENT,
		
		/** The LOA d_ present. */
		LOAD_PRESENT,
		
		/** The BLINKEN. */
		BLINKEN,
		
		/** The IMAGE. */
		IMAGE,
		
		/** The IMAG e_ zoomer. */
		IMAGE_ZOOMER,
		
		/** The TEXTDEF. */
		TEXTDEF,
		
		/** The TEXTDE f_ file. */
		TEXTDEF_FILE,
		
		/** The TEXTWR. */
		TEXTWR,
		//used for enable/disable random mode
		/** The RANDOM. */
		RANDOM,
		//used as a one shot randomizer
		/** The RANDOMIZE. */
		RANDOMIZE,
		//select a saved entrys
		/** The PRESE t_ random. */
		PRESET_RANDOM,
		
		/** The CURREN t_ visual. */
		CURRENT_VISUAL
	}

	/** The log. */
	private static final Logger LOG = Logger.getLogger(MessageProcessor.class.getName());
	
	/** The Constant IGNORE_COMMAND. */
	private static final String IGNORE_COMMAND = "Ignored command";

	/**
	 * Instantiates a new message processor.
	 */
	private MessageProcessor() {
		//no instance
	}

	/**
	 * process message from gui.
	 *
	 * @param msg the msg
	 * @param startFader the start fader
	 * @return STATUS if we need to send updates back to the gui (loaded preferences)
	 */
	public static synchronized ValidCommands processMsg(String[] msg, boolean startFader) {
		if (msg==null || msg.length<1) {
			return null;
		}

		int msgLength = msg.length-1;
		int tmp;		
		try {
			ValidCommands cmd = ValidCommands.valueOf(msg[0]);
			Collector col = Collector.getInstance();
			switch (cmd) {
			case STATUS:
				return ValidCommands.STATUS;

			case CHANGE_GENERATOR_A:
				try {
					if (msg.length==2) {
						//the new method - used by the gui
						int nr = col.getCurrentVisual();
						tmp=Integer.parseInt(msg[1]);
						col.getVisual(nr).setGenerator1(tmp);
					} else {
						int size = col.getAllVisuals().size();
						if (size>msgLength) size=msgLength;
						for (int i=0; i<size; i++) {
							tmp=Integer.parseInt(msg[i+1]);
							col.getVisual(i).setGenerator1(tmp);
						}						
					}

				} catch (Exception e) {
					LOG.log(Level.WARNING, IGNORE_COMMAND, e);
				}
				break;
				
			case CHANGE_GENERATOR_B:
				try {
					if (msg.length==2) {
						//the new method - used by the gui
						int nr = col.getCurrentVisual();
						tmp=Integer.parseInt(msg[1]);
						col.getVisual(nr).setGenerator2(tmp);
					} else {
						int size = col.getAllVisuals().size();
						if (size>msgLength) size=msgLength;
						for (int i=0; i<size; i++) {
							tmp=Integer.parseInt(msg[i+1]);
							col.getVisual(i).setGenerator2(tmp);
						}						
					}

				} catch (Exception e) {
					LOG.log(Level.WARNING,	IGNORE_COMMAND, e);
				}
				break;

			case CHANGE_EFFECT_A:
				try {
					if (msg.length==2) {
						//the new method - used by the gui
						int nr = col.getCurrentVisual();
						tmp=Integer.parseInt(msg[1]);
				
						col.getVisual(nr).setEffect1(tmp);						
					} else {
						//the "old" method - used by the saved presents
						int size = col.getAllVisuals().size();
						if (size>msgLength) size=msgLength;
						for (int i=0; i<size; i++) {
							tmp=Integer.parseInt(msg[i+1]);
							col.getVisual(i).setEffect1(tmp);
						}						
					}
				} catch (Exception e) {
					LOG.log(Level.WARNING,	IGNORE_COMMAND, e);
				}
				break;

			case CHANGE_EFFECT_B:
				try {
					if (msg.length==2) {
						//the new method - used by the gui
						int nr = col.getCurrentVisual();
						tmp=Integer.parseInt(msg[1]);
						col.getVisual(nr).setEffect2(tmp);						
					} else {
						int size = col.getAllVisuals().size();
						if (size>msgLength) size=msgLength;
						for (int i=0; i<size; i++) {
							tmp=Integer.parseInt(msg[i+1]);
							col.getVisual(i).setEffect2(tmp);
						}						
					}
 
				} catch (Exception e) {
					LOG.log(Level.WARNING, IGNORE_COMMAND, e);
				}
				break;

			case CHANGE_MIXER:
				try {
					if (msg.length==2) {
						//the new method - used by the gui
						int nr = col.getCurrentVisual();
						tmp=Integer.parseInt(msg[1]);
						col.getVisual(nr).setMixer(tmp);
					} else {
						int size = col.getAllVisuals().size();
						if (size>msgLength) size=msgLength;
						for (int i=0; i<size; i++) {
							tmp=Integer.parseInt(msg[i+1]);
							col.getVisual(i).setMixer(tmp);
						}						
					}

				} catch (Exception e) {
					LOG.log(Level.WARNING, IGNORE_COMMAND, e);
				}
				break;

			case CHANGE_OUTPUT:
				try {
					int size = col.getAllOutputMappings().size();
					if (size>msgLength) size=msgLength;
					for (int i=0; i<size; i++) {
						int newFx = Integer.parseInt(msg[i+1]);
						int oldFx = col.getFxInputForScreen(i);
						if(oldFx!=newFx) {
							LOG.log(Level.INFO,	"Change Output 0, old fx: {0}, new fx {1}", new Object[] {oldFx, newFx});
							if (startFader) {
								//start fader to change screen
								col.getOutputMappings(i).getFader().startFade(newFx, i);								
							} else {
								//do not fade if we load setting from present
								col.mapInputToScreen(i, newFx);
							}
						}
					}
				} catch (Exception e) {
					LOG.log(Level.WARNING,	IGNORE_COMMAND, e);
				}
				break;

			case CHANGE_OUTPUT_EFFECT:
				try {
					int size = col.getAllOutputMappings().size();
					if (size>msgLength) size=msgLength;
					for (int i=0; i<size; i++) {
						tmp=Integer.parseInt(msg[i+1]);
						col.getOutputMappings(i).setEffect(col.getPixelControllerEffect().getEffect(tmp));
					}
				} catch (Exception e) {
					LOG.log(Level.WARNING,	IGNORE_COMMAND, e);
				}
				break;

			case CHANGE_FADER:
				try {
					int size = col.getAllOutputMappings().size();
					if (size>msgLength) size=msgLength;
					for (int i=0; i<size; i++) {
						tmp=Integer.parseInt(msg[i+1]);
						//do not start a new fader while the old one is still running
						if (!col.getOutputMappings(i).getFader().isStarted()) {
							col.getOutputMappings(i).setFader(PixelControllerFader.getFader(tmp));							
						}
					}
				} catch (Exception e) {
					LOG.log(Level.WARNING,	IGNORE_COMMAND, e);
				}
				break;
				
			case CHANGE_SHUFFLER_SELECT:
				try {					
					int size = col.getPixelControllerShufflerSelect().getShufflerSelect().size();
					if (size>msgLength) size=msgLength;
					boolean b;
					for (int i=0; i<size; i++) {
						b = false;
						if (msg[i+1].equals("1")) b = true;
						col.getPixelControllerShufflerSelect().setShufflerSelect(i, b);
					}					
				} catch (Exception e) {
					LOG.log(Level.WARNING, IGNORE_COMMAND, e);
				}
				break;
				
			case CHANGE_ROTOZOOM:
				try {					
					int val = Integer.parseInt(msg[1]);		
					//col.setRotoZoomAngle(val);			
					//log.log(Level.WARNING,	"rotozoom value: "+val);
					RotoZoom r = (RotoZoom)col.getPixelControllerEffect().getEffect(EffectName.ROTOZOOM);
					r.setAngle(val);					
				} catch (Exception e) {
					LOG.log(Level.WARNING, IGNORE_COMMAND, e);
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
					if (r<0) r=0;
					if (g<0) g=0;
					if (b<0) b=0;
					col.getPixelControllerEffect().setRGB(r, g, b);
				} catch (Exception e) {
					LOG.log(Level.WARNING,	IGNORE_COMMAND, e);
				}
				break;

			case SAVE_PRESENT:
				try {
					int idxs = col.getSelectedPresent();
					List<String> present = col.getCurrentStatus();
					col.getPresent().get(idxs).setPresent(present);
					PropertiesHelper.getInstance().savePresents();
				} catch (Exception e) {
					LOG.log(Level.WARNING,	IGNORE_COMMAND, e);
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
					LOG.log(Level.WARNING,	IGNORE_COMMAND, e);
				}
				break;
				
			case CHANGE_PRESENT:
				try {
					int a = Integer.parseInt(msg[1]);
					col.setSelectedPresent(a);
				} catch (Exception e) {
					LOG.log(Level.WARNING,	IGNORE_COMMAND, e);
				}
				break;
				
			case CHANGE_THRESHOLD_VALUE:
				try {
					int a = Integer.parseInt(msg[1]);
					if (a>255) a=255;
					if (a<0) a=0;
					col.getPixelControllerEffect().setThresholdValue(a);
				} catch (Exception e) {
					LOG.log(Level.WARNING,	IGNORE_COMMAND, e);
				}
				break;
				
			case BLINKEN:
				try {
					String fileToLoad = msg[1];
					col.getPixelControllerGenerator().setFileBlinken(fileToLoad);
				} catch (Exception e) {
					LOG.log(Level.WARNING,	IGNORE_COMMAND, e);
				}
				break;

			case IMAGE:
				try {
					String fileToLoad = msg[1];
					col.getPixelControllerGenerator().setFileImageSimple(fileToLoad);
				} catch (Exception e) {
					LOG.log(Level.WARNING,	IGNORE_COMMAND, e);
				}
				break;

			case IMAGE_ZOOMER:
				try {
					String fileToLoad = msg[1];
					col.getPixelControllerGenerator().setFileImageZoomer(fileToLoad);
				} catch (Exception e) {
					LOG.log(Level.WARNING,	IGNORE_COMMAND, e);
				}
				break;
				
			case TEXTDEF:
				try {
					int lut = Integer.parseInt(msg[1]);
					col.getPixelControllerGenerator().setTextureDeformationLut(lut);
				} catch (Exception e) {
					LOG.log(Level.WARNING,	IGNORE_COMMAND, e);
				}
				break;
			
			case TEXTDEF_FILE:
				try {
					String fileToLoad = msg[1];
					col.getPixelControllerGenerator().setFileTextureDeformation(fileToLoad);
				} catch (Exception e) {
					LOG.log(Level.WARNING,	IGNORE_COMMAND, e);
				}
				break;
			
			case TEXTWR:
				try {
					String message = msg[1];
					col.getPixelControllerGenerator().setText(message);
				} catch (Exception e) {
					LOG.log(Level.WARNING, IGNORE_COMMAND, e);
				}
				break;
				
			case RANDOM:	//enable or disable random mode
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
					LOG.log(Level.WARNING, IGNORE_COMMAND, e);
				}
				break;

			case RANDOMIZE:	//one shot randomizer for current visual
				try {
					Shuffler.manualShuffleStuff();
					return ValidCommands.STATUS;
				} catch (Exception e) {
					LOG.log(Level.WARNING, IGNORE_COMMAND, e);
				}
				break;
				
			case PRESET_RANDOM:	//one shot randomizer for current visual, use a pre-stored present
				try {
					Shuffler.presentShuffler();
					return ValidCommands.STATUS;					
				} catch (Exception e) {
					LOG.log(Level.WARNING, IGNORE_COMMAND, e);
				}
				break;

			case CURRENT_VISUAL:
				//change the selected visual, need to update
				//some of the gui elements 
				try {
					int a = Integer.parseInt(msg[1]);
					Collector.getInstance().setCurrentVisual(a);
					return ValidCommands.STATUS_MINI;
				} catch (Exception e) {
					LOG.log(Level.WARNING, IGNORE_COMMAND, e);
				}
				break;
				
			default:
				StringBuffer stringBuffer = new StringBuffer();
				for (int i=0; i<msg.length;i++) {
					stringBuffer.append(msg[i]);
					stringBuffer.append("; ");
				}
				LOG.log(Level.INFO,	"Ignored command <{0}>", stringBuffer.toString());
				break;
			}
		} catch (IllegalArgumentException e) {
			LOG.log(Level.INFO,	"Illegal argument <{0}>: {1}", new Object[] { msg[0], e });
			e.printStackTrace();
		}		

		return null;
	}
}
