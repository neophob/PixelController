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

import com.neophob.sematrix.effect.Effect;
import com.neophob.sematrix.effect.Effect.EffectName;
import com.neophob.sematrix.effect.RotoZoom;
import com.neophob.sematrix.fader.PixelControllerFader;
import com.neophob.sematrix.generator.Generator;
import com.neophob.sematrix.glue.Collector;
import com.neophob.sematrix.glue.OutputMapping;
import com.neophob.sematrix.glue.Shuffler;
import com.neophob.sematrix.mixer.Mixer;
import com.neophob.sematrix.properties.ValidCommands;

/**
 * The Class MessageProcessor.
 */
public final class MessageProcessor {


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
					int nr = col.getCurrentVisual();
					tmp=Integer.parseInt(msg[1]);
					Generator g = col.getPixelControllerGenerator().getGenerator(tmp);
					//silly check of generator exists
					g.getId();
					col.getVisual(nr).setGenerator1(g);
				} catch (Exception e) {
					LOG.log(Level.WARNING, IGNORE_COMMAND, e);
				}
				break;

			case CHANGE_GENERATOR_B:
				try {
					//the new method - used by the gui
					int nr = col.getCurrentVisual();
					tmp=Integer.parseInt(msg[1]);
					Generator g = col.getPixelControllerGenerator().getGenerator(tmp);
					g.getId();
					col.getVisual(nr).setGenerator2(g);
				} catch (Exception e) {
					LOG.log(Level.WARNING,	IGNORE_COMMAND, e);
				}
				break;

			case CHANGE_EFFECT_A:
				try {
					int nr = col.getCurrentVisual();
					tmp=Integer.parseInt(msg[1]);
					Effect e = col.getPixelControllerEffect().getEffect(tmp);
					e.getId();
					col.getVisual(nr).setEffect1(e);						
				} catch (Exception e) {
					LOG.log(Level.WARNING,	IGNORE_COMMAND, e);
				}
				break;

			case CHANGE_EFFECT_B:
				try {
					int nr = col.getCurrentVisual();
					tmp=Integer.parseInt(msg[1]);
					Effect e = col.getPixelControllerEffect().getEffect(tmp);
					e.getId();
					col.getVisual(nr).setEffect2(e);						
				} catch (Exception e) {
					LOG.log(Level.WARNING, IGNORE_COMMAND, e);
				}
				break;

			case CHANGE_MIXER:
				try {
					//the new method - used by the gui
					int nr = col.getCurrentVisual();
					tmp=Integer.parseInt(msg[1]);
					Mixer m = col.getPixelControllerMixer().getMixer(tmp);
					m.getId();
					col.getVisual(nr).setMixer(m);
				} catch (Exception e) {
					LOG.log(Level.WARNING, IGNORE_COMMAND, e);
				}
				break;

			case CHANGE_OUTPUT_VISUAL:
				try {
					int nr = col.getCurrentOutput();				
					int newFx = Integer.parseInt(msg[1]);
					int oldFx = col.getFxInputForScreen(nr);
					LOG.log(Level.INFO,	"old fx: {0}, new fx {1}", new Object[] {oldFx, newFx});
					if (oldFx!=newFx) {
						LOG.log(Level.INFO,	"Change Output 0, old fx: {0}, new fx {1}", new Object[] {oldFx, newFx});
						if (startFader) {
							//start fader to change screen
							col.getOutputMappings(nr).getFader().startFade(newFx, nr);								
						} else {
							//do not fade if we load setting from present
							col.mapInputToScreen(nr, newFx);
						}
					}
				} catch (Exception e) {
					LOG.log(Level.WARNING,	IGNORE_COMMAND, e);
				}
				break;

			case CHANGE_ALL_OUTPUT_VISUAL:
				try {
					int size = col.getAllOutputMappings().size();

					for (int i=0; i<size; i++) {
						int newFx = Integer.parseInt(msg[1]);
						int oldFx = col.getFxInputForScreen(i);
						if (oldFx!=newFx) {
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
				
			case CHANGE_OUTPUT_FADER:
				try {
					int nr = col.getCurrentOutput();
					tmp=Integer.parseInt(msg[1]);
					//do not start a new fader while the old one is still running
					if (!col.getOutputMappings(nr).getFader().isStarted()) {
						col.getOutputMappings(nr).setFader(PixelControllerFader.getFader(tmp));							
					}
				} catch (Exception e) {
					LOG.log(Level.WARNING,	IGNORE_COMMAND, e);
				}
				break;

			case CHANGE_ALL_OUTPUT_FADER:
				try {
					tmp=Integer.parseInt(msg[1]);
					for (OutputMapping om: col.getAllOutputMappings()) {
						//do not start a new fader while the old one is still running
						if (!om.getFader().isStarted()) {
							om.setFader(PixelControllerFader.getFader(tmp));							
						}						
					}
				} catch (Exception e) {
					LOG.log(Level.WARNING,	IGNORE_COMMAND, e);
				}
				break;

			case CHANGE_SHUFFLER_SELECT:
				try {					
					int size = col.getPixelControllerShufflerSelect().getShufflerSelect().size();
					if (size>msgLength) {
						size=msgLength;
					}
					boolean b;
					for (int i=0; i<size; i++) {
						b = false;
						if (msg[i+1].equals("1")) {
							b = true;
						}
						col.getPixelControllerShufflerSelect().setShufflerSelect(i, b);
					}					
				} catch (Exception e) {
					LOG.log(Level.WARNING, IGNORE_COMMAND, e);
				}
				break;

			case CHANGE_ROTOZOOM:
				try {					
					int val = Integer.parseInt(msg[1]);
					RotoZoom r = (RotoZoom)col.getPixelControllerEffect().getEffect(EffectName.ROTOZOOM);
					r.setAngle(val);					
				} catch (Exception e) {
					LOG.log(Level.WARNING, IGNORE_COMMAND, e);
				}
				break;

			case SAVE_PRESENT:
				try {
					int idxs = col.getSelectedPresent();
					List<String> present = col.getCurrentStatus();
					col.getPresent().get(idxs).setPresent(present);
					col.getPh().savePresents();					
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
					if (a>255) {
						a=255;
					}
					if (a<0) {
						a=0;
					}
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

			case COLOR_SCROLL_OPT:
				try {
					int dir = Integer.parseInt(msg[1]);
					col.getPixelControllerGenerator().setColorScrollingDirection(dir);
				} catch (Exception e) {
					LOG.log(Level.WARNING,	IGNORE_COMMAND, e);
				}
				break;

			case COLOR_FADE_LENGTH:
				try {
					int length = Integer.parseInt(msg[1]);
					col.getPixelControllerGenerator().setColorFadeTime(length);
				} catch (Exception e) {
					LOG.log(Level.WARNING,	IGNORE_COMMAND, e);
				}
				break;

			case COLOR_SCROLL_LENGTH:
				try {
					int length = Integer.parseInt(msg[1]);
					col.getPixelControllerGenerator().setColorScrollingFadeLength(length);
				} catch (Exception e) {
					LOG.log(Level.WARNING,	IGNORE_COMMAND, e);
				}
				break;

            case TEXTDEF:
				try {
					int lut = Integer.parseInt(msg[1]);
					col.getPixelControllerEffect().setTextureDeformationLut(lut);
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

			case RANDOMIZE:	//one shot randomizer
				try {
					Shuffler.manualShuffleStuff();
					return ValidCommands.STATUS;
				} catch (Exception e) {
					LOG.log(Level.WARNING, IGNORE_COMMAND, e);
				}
				break;

			case PRESET_RANDOM:	//one shot randomizer, use a pre-stored present
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

			case CURRENT_OUTPUT:
				//change the selected output, need to update
				//some of the gui elements 
				try {
					int a = Integer.parseInt(msg[1]);
					Collector.getInstance().setCurrentOutput(a);
					return ValidCommands.STATUS_MINI;
				} catch (Exception e) {
					LOG.log(Level.WARNING, IGNORE_COMMAND, e);
				}
				break;

			//create a screenshot of all current buffers
			case SCREENSHOT:
				Collector.getInstance().saveScreenshot();
				LOG.log(Level.INFO, "Saved some screenshots");
				break;
				
			//change current colorset
			case CURRENT_COLORSET:
				int a = Integer.parseInt(msg[1]);
				Collector.getInstance().setCurrentColorSet(a);
				return ValidCommands.STATUS_MINI;
				
			//pause output, needed to create screenshots or take an image of the output
			case FREEZE:
				Collector.getInstance().togglePauseMode();
				break;
				
			//unkown message
			default:
				StringBuffer sb = new StringBuffer();
				for (int i=0; i<msg.length;i++) {
					sb.append(msg[i]);
					sb.append("; ");
				}
				LOG.log(Level.INFO,	"Ignored command <{0}>", sb);
				break;
			}
		} catch (IllegalArgumentException e) {
			LOG.log(Level.INFO,	"Illegal argument <{0}>: {1}", new Object[] { msg[0], e });			
		}		

		return null;
	}
}
