package com.neophob.sematrix.output.emulatorhelper;

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
			LOG.log(Level.INFO, "GENERATOR Value: "+value);
			handleGenerator(value, selection);
			break;

		case MIXER_DROPDOWN:
			LOG.log(Level.INFO, "MIXER Value: "+value);
			handleMixer(value);
			break;
			
			
		default:
			LOG.log(Level.INFO, "Invalid Object: "+selection);
			break;
		}
	}
	

	/**
	 * 
	 * @param newValue
	 * @param source
	 */
	private void handleMixer(float newValue) {
		String msg[] = new String[2];		
		msg[0] = ""+ValidCommands.CHANGE_MIXER;
		msg[1] = ""+(int)newValue;
		MessageProcessor.processMsg(msg, true);
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
		MessageProcessor.processMsg(msg, true);
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
		MessageProcessor.processMsg(msg, true);
	}
}
