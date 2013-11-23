package com.neophob.sematrix.osc.client.impl;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.neophob.sematrix.osc.client.OscClientException;
import com.neophob.sematrix.osc.model.OscMessage;

/**
 * OSC Client Factory, send OSC Message
 * 
 * @author michu
 *
 */
public abstract class OscClientFactory {

	private static final Logger LOG = Logger.getLogger(OscClientFactory.class.getName());

	private static final boolean USE_TCP = false;
	
	private static OscClientImpl client = null; 

	/**
	 * 
	 * @param targetIp
	 * @param targetPort
	 * @param msg
	 * @throws OscClientException
	 */
	public static void sendOscMessage(String targetIp, int targetPort, OscMessage msg) throws OscClientException {		
		if (client == null) {
			client = new OscClientImpl(USE_TCP);
		}
		
		client.sendMessage(targetIp, targetPort, msg);
	}

	public static void disconnectOscClient() {
		if (client !=null && client.isConnected()) {
			try {
				client.disconnect();
			} catch (OscClientException e) {
				LOG.log(Level.WARNING, "Failed to disconnect OSC Client", e);
			}
		}
	}
}
