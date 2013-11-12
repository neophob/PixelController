package com.neophob.sematrix.osc.client.impl;

import java.io.IOException;
import java.net.InetSocketAddress;

import com.neophob.sematrix.osc.client.IClient;
import com.neophob.sematrix.osc.client.OscClientException;
import com.neophob.sematrix.osc.model.OscMessage;

import de.sciss.net.OSCClient;
import de.sciss.net.OSCMessage;
import de.sciss.net.OSCPacket;
import de.sciss.net.OSCServer;

class OscClientImpl implements IClient {

	private OSCClient client;
	private String targetIp;
	private int targetPort;
	
	public OscClientImpl() throws OscClientException {
		try {
			client = OSCClient.newUsing(OSCServer.UDP);
			targetPort = 0;
			targetIp = "";
		} catch (IOException e) {
			throw new OscClientException("Failed to initialize OSC Client", e);
		}
	}
	
	/**
	 * 
	 * @param targetIp
	 * @param targetPort
	 * @return
	 */
	private boolean sendMessageToNewHost(String targetIp, int targetPort) {
		return client.isConnected() && this.targetPort == targetPort 
				&& this.targetIp.equals(targetIp);
	}

	@Override
	public void sendMessage(String targetIp, int targetPort, OscMessage msg) throws OscClientException {
		if (sendMessageToNewHost(targetIp, targetPort)) {
			this.disconnect();
		}
		
		if (!client.isConnected()) {			
			try {
				client.setTarget( new InetSocketAddress( targetIp, targetPort ));
				client.start();
				
				this.targetPort = targetPort;
				this.targetIp = targetIp;
			} catch (IOException e) {
				throw new OscClientException("Failed to start OSC client", e);
			}
		}
		
		//TODO make sure msg.getArgs is not null
		//TODO type check
		OSCPacket oscPacket = new OSCMessage(msg.getOscPattern(), (Object[])msg.getArgs()); 
		try {
			client.send(oscPacket);
		} catch (IOException e) {
			throw new OscClientException("Failed to send OSC Message", e);
		}

	}
	

	@Override
	public void disconnect() throws OscClientException {
		if (!client.isConnected()) {
			try {
				client.stop();
				targetPort = 0;
				targetIp = "";
			} catch (IOException e) {
				throw new OscClientException("Failed to stop OSC client", e);
			}
		}
		
	}

}
