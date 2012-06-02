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
package com.neophob.sematrix.output.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.neophob.sematrix.listener.MessageProcessor;
import com.neophob.sematrix.properties.ValidCommands;

import controlP5.ControlEvent;
import controlP5.ControlListener;

/**
 * GUI Listener
 * 
 * this class translate the gui stuff into a string array and send the result
 * to the PixelController MessageProcessor
 * 
 * @author michu
 *
 */
public class P5EventListener implements ControlListener {

    /** The log. */
    private static final Logger LOG = Logger.getLogger(P5EventListener.class.getName());

    private GeneratorGui callback;

    /**
     * 
     * @param callback
     */
    public P5EventListener(GeneratorGui callback) {
        this.callback = callback;
    }

    /**
     * 
     */
    public void controlEvent(ControlEvent theEvent) {
        // DropdownList is of type ControlGroup.
        // A controlEvent will be triggered from inside the ControlGroup class.
        // therefore you need to check the originator of the Event with
        // if (theEvent.isGroup())
        // to avoid an error message thrown by controlP5.        
        float value = -1f;
        int intVal;
        String name;
        
        if (theEvent.isGroup()) {
            // check if the Event was triggered from a ControlGroup
            //LOG.log(Level.INFO, theEvent.getGroup().getValue()+" from "+theEvent.getGroup());
            value = theEvent.getGroup().getValue();
        } else if (theEvent.isController()) {
            //LOG.log(Level.INFO, theEvent.getController().getValue()+" from "+theEvent.getController());
            value = theEvent.getController().getValue();
        } else if (theEvent.isTab()) {
            //events from tabs are ignored		    		   
            return;
        }
        intVal = (int)value;

        GuiElement selection = GuiElement.valueOf(theEvent.getName());

        switch (selection) {
            case EFFECT_ONE_DROPDOWN:
            case EFFECT_TWO_DROPDOWN:			
                LOG.log(Level.INFO, "EFFECT Value: "+value);
                handleEffect(value, selection);
                break;

            case GENERATOR_ONE_DROPDOWN:
            case GENERATOR_TWO_DROPDOWN:
                LOG.log(Level.INFO, selection+" Value: "+value);
                handleGenerator(value, selection);
                break;

            case MIXER_DROPDOWN:
                LOG.log(Level.INFO, selection+" Value: "+value);
                createMessage(ValidCommands.CHANGE_MIXER, value);
                break;

            case BUTTON_RANDOM_CONFIGURATION:
                LOG.log(Level.INFO, selection+" Value: "+value);
                createMessage(ValidCommands.RANDOMIZE, value);
                break;

            case BUTTON_TOGGLE_RANDOM_MODE:
                LOG.log(Level.INFO, selection+" Value: "+value);
                handleRandomMode(value);
                break;

            case BUTTON_RANDOM_PRESENT:
                LOG.log(Level.INFO, selection+" Value: "+value);
                createMessage(ValidCommands.PRESET_RANDOM, value);
                break;

            case CURRENT_VISUAL:
                LOG.log(Level.INFO, selection+" Value: "+value);
                createMessage(ValidCommands.CURRENT_VISUAL, value);
                break;

            case CURRENT_OUTPUT:
                List<Boolean> outputs = new ArrayList<Boolean>();
                for (float f: theEvent.getGroup().getArrayValue()) {
                    if (f==0 ? outputs.add(Boolean.FALSE) : outputs.add(Boolean.TRUE));
                }
                LOG.log(Level.INFO, selection+": "+value);
                createMessage(ValidCommands.CURRENT_OUTPUT, value);
                break;

            case THRESHOLD: 
                LOG.log(Level.INFO, selection+": "+intVal);
                createMessage(ValidCommands.CHANGE_THRESHOLD_VALUE, intVal);		    
                break;

            case FX_ROTOZOOMER:
                LOG.log(Level.INFO, selection+": "+intVal);
                createMessage(ValidCommands.CHANGE_ROTOZOOM, intVal);		    
                break;

            case BLINKENLIGHTS_DROPDOWN:
            	name = theEvent.getGroup().getCaptionLabel().getText();
            	LOG.log(Level.INFO, selection+" "+name);
            	createMessage(ValidCommands.BLINKEN, name);
            	break;
            	
            case IMAGE_DROPDOWN:
            	name = theEvent.getGroup().getCaptionLabel().getText();
            	LOG.log(Level.INFO, selection+" "+name);
            	createMessage(ValidCommands.IMAGE, name);
            	break;
            	
            case OUTPUT_EFFECT_DROPDOWN:
            	LOG.log(Level.INFO, selection+" "+value);
            	createMessage(ValidCommands.CHANGE_OUTPUT_EFFECT, value);
            	break;

            case OUTPUT_FADER_DROPDOWN:
            	LOG.log(Level.INFO, selection+" "+value);
            	createMessage(ValidCommands.CHANGE_OUTPUT_FADER, value);
            	break;

            case OUTPUT_SELECTED_VISUAL_DROPDOWN:
            	LOG.log(Level.INFO, selection+" "+value);
            	createMessage(ValidCommands.CHANGE_OUTPUT_VISUAL, value);
            	break;

            case OUTPUT_ALL_SELECTED_VISUAL_DROPDOWN:
            	LOG.log(Level.INFO, selection+" "+value);
            	createMessage(ValidCommands.CHANGE_ALL_OUTPUT_VISUAL, value);
            	break;
            	
            case OUTPUT_ALL_EFFECT_DROPDOWN:
            	LOG.log(Level.INFO, selection+" "+value);
            	createMessage(ValidCommands.CHANGE_ALL_OUTPUT_EFFECT, value);
            	break;
            	
            case OUTPUT_ALL_FADER_DROPDOWN:
            	LOG.log(Level.INFO, selection+" "+value);
            	createMessage(ValidCommands.CHANGE_ALL_OUTPUT_FADER, value);
            	break;
            	
            case TEXTUREDEFORM_OPTIONS:
            	LOG.log(Level.INFO, selection+" "+value);
            	createMessage(ValidCommands.TEXTDEF, value);            	
            	break;
            	
            case COLORSCROLL_OPTIONS:
            	LOG.log(Level.INFO, selection+" "+value);
            	createMessage(ValidCommands.COLOR_SCROLL_OPT, value);            	
            	break;
            	
            case TEXTFIELD:
            	name = theEvent.getStringValue();
            	LOG.log(Level.INFO, selection+" "+name);
            	createMessage(ValidCommands.TEXTWR, name);            	
            	break;
            
            case RANDOM_ELEMENT:            	
            	String param = "";            	
            	for (float ff: theEvent.getGroup().getArrayValue()) {
            		if (ff<0.5f) {
            			param += "0 ";
            		} else {
            			param += "1 ";            			
            		}
            	}
            	LOG.log(Level.INFO, selection+" "+param);            	
            	createShufflerMessage(param);
            	break;
            
            case COLOR_SET_DROPDOWN:            	
            	LOG.log(Level.INFO, selection+" "+value);
            	createMessage(ValidCommands.CURRENT_COLORSET, value);
            	break;
            	
            case PRESET_BUTTONS:
                LOG.log(Level.INFO, selection+" "+intVal);
                createMessage(ValidCommands.CHANGE_PRESENT, intVal);
                break;
                
            case LOAD_PRESET:
                LOG.log(Level.INFO, "LOAD_PRESET");
                createMessage(ValidCommands.LOAD_PRESENT, "");
                break;
            	
            case SAVE_PRESET:
                LOG.log(Level.INFO, "SAVE_PRESET");
                createMessage(ValidCommands.SAVE_PRESENT, "");
                break;
                
            default:
                LOG.log(Level.INFO, "Invalid Object: "+selection+", Value: "+value);
                break;
        }
    }


    /**
     * 
     * @param msg
     */
    private void singleSendMessageOut(String msg[]) {

        ValidCommands ret = MessageProcessor.processMsg(msg, true);
        if (ret != null) {
            switch (ret) {
                case STATUS:                    
                    callback.callbackRefreshWholeGui();
                    break;

                case STATUS_MINI:
                	callback.callbackRefreshMini();
                    break;

                default:
                    break;
            }
            try {
                Thread.sleep(100);				
            } catch (Exception e) {}
        }

    }


    /**
     * 
     * @param newValue
     * @param source
     */
    private void createMessage(ValidCommands validCommand, float newValue) {
        String msg[] = new String[2];		
        msg[0] = ""+validCommand;
        msg[1] = ""+(int)newValue;
        singleSendMessageOut(msg);
    }

    /**
     * 
     * @param validCommand
     * @param newValue
     */
    private void createMessage(ValidCommands validCommand, String newValue) {
        String msg[] = new String[2];		
        msg[0] = ""+validCommand;
        msg[1] = newValue;
        singleSendMessageOut(msg);
    }

    private void createShufflerMessage(String param) {
        String msg[] = new String[param.length()+1];		
        msg[0] = ""+ValidCommands.CHANGE_SHUFFLER_SELECT;
        String tmp[] = param.split(" ");
        System.arraycopy(tmp, 0, msg, 1, tmp.length);
        singleSendMessageOut(msg);
    }

    /**
     * toggle random mode on and off
     * @param newValue
     */
    private void handleRandomMode(float newValue) {
        String msg[] = new String[2];		
        msg[0] = ""+ValidCommands.RANDOM;		
        if (newValue==0) {
            msg[1] = "OFF";	
        } else {
            msg[1] = "ON";
        }
        singleSendMessageOut(msg);		
    }


    /**
     * 
     * @param newValue
     * @param source
     */
    private void handleEffect(float newValue, GuiElement source) {
        String msg[] = new String[2];

        if (source == GuiElement.EFFECT_ONE_DROPDOWN) {
            msg[0] = ""+ValidCommands.CHANGE_EFFECT_A;
        } else {
            msg[0] = ""+ValidCommands.CHANGE_EFFECT_B;
        }
        msg[1] = ""+(int)newValue;
        singleSendMessageOut(msg);
    }

    /**
     * 
     * @param newValue
     * @param source
     */	
    private void handleGenerator(float newValue, GuiElement source) {
        String msg[] = new String[2];

        if (source == GuiElement.GENERATOR_ONE_DROPDOWN) {
            msg[0] = ""+ValidCommands.CHANGE_GENERATOR_A;
        } else {
            msg[0] = ""+ValidCommands.CHANGE_GENERATOR_B;
        }
        msg[1] = ""+(int)newValue;
        singleSendMessageOut(msg);
    }


}
