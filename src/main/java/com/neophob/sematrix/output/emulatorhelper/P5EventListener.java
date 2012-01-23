package com.neophob.sematrix.output.emulatorhelper;

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

	private GraphicalFrontEnd callback;
	
	/**
	 * 
	 * @param callback
	 */
	public P5EventListener(GraphicalFrontEnd callback) {
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
		if (theEvent.isGroup()) {
			// check if the Event was triggered from a ControlGroup
			//LOG.log(Level.INFO, theEvent.getGroup().getValue()+" from "+theEvent.getGroup());
			value = theEvent.getGroup().getValue();
		} 
		else if (theEvent.isController()) {
			//LOG.log(Level.INFO, theEvent.getController().getValue()+" from "+theEvent.getController());
			value = theEvent.getController().getValue();
		}

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
			LOG.log(Level.INFO, selection+"");
			createMessage(ValidCommands.CURRENT_OUTPUT, value);
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
				callback.callbackRefreshMini();
				break;

			case STATUS_MINI:
				callback.callbackRefreshWholeGui();
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
