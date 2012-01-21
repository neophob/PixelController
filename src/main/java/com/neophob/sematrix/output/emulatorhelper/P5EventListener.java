package com.neophob.sematrix.output.emulatorhelper;

import java.util.logging.Level;
import java.util.logging.Logger;

import controlP5.ControlEvent;
import controlP5.ControlListener;

public class P5EventListener implements ControlListener {

	/** The log. */
	private static final Logger LOG = Logger.getLogger(P5EventListener.class.getName());

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

		GuiElemts selection = GuiElemts.valueOf(theEvent.getName());

		switch (selection) {
		case EFFECT_ONE_DROPDOWN:
			LOG.log(Level.INFO, "EFFECT1 OFS: "+value);
			break;

		case GENERATOR_ONE_DROPDOWN:
			LOG.log(Level.INFO, "GENERATOR1 OFS: "+value);
			break;

		default:
			break;
		}

		//		LOG.log(Level.INFO, "NAME "+theEvent.getName());
		//		LOG.log(Level.INFO, "VALUE "+theEvent.getValue());
		//		//		  LOG.log(Level.INFO, "STRING_VALUE "+theEvent.getStringValue());
		//
		//		LOG.log(Level.INFO, "TYPE "+theEvent.stringValue());
		//		//		  LOG.log(Level.INFO, "TYPE "+theEvent.getType());
	}
}
