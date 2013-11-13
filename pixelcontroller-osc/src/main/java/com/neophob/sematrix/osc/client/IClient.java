package com.neophob.sematrix.osc.client;

import com.neophob.sematrix.osc.model.OscMessage;

public interface IClient {

	void sendMessage(String targetIp, int targetPort, OscMessage msg) throws OscClientException;
	
	void disconnect() throws OscClientException;
	
	boolean isConnected();
}
