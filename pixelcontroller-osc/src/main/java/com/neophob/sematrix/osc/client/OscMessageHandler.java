package com.neophob.sematrix.osc.client;

import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.neophob.sematrix.osc.model.OscMessage;

/**
 * observer pattern implementation for client callback
 * 
 * @author michu
 *
 */
public abstract class OscMessageHandler implements Observer {

	private static final Logger LOG = Logger.getLogger(OscMessageHandler.class.getName());
	
	public abstract void handleOscMessage(OscMessage msg);
	
	@Override
	public void update(Observable o, Object arg) {
		if (arg instanceof OscMessage) {
			OscMessage msg = (OscMessage) arg;
			handleOscMessage(msg);
        } else {
        	LOG.log(Level.WARNING, "Ignored notification of unknown type: "+arg);
        }
	}

}
