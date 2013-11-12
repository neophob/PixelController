package com.neophob.sematrix.osc.client.impl;

import com.neophob.sematrix.osc.client.OscClientException;
import com.neophob.sematrix.osc.model.OscMessage;

public abstract class OscClientFactory {

	private static OscClientImpl client = null; 
	
	public static void sendOscMessage(String targetIp, int targetPort, OscMessage msg) throws OscClientException {
		
		if (client == null) {
			client = new OscClientImpl();
		}
		
		client.sendMessage(targetIp, targetPort, msg);
	}

}
