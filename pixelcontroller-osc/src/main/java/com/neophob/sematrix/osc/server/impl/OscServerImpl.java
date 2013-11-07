package com.neophob.sematrix.osc.server.impl;

import java.net.SocketException;
import java.util.Date;

import com.illposed.osc.OSCListener;
import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPortIn;
import com.neophob.sematrix.osc.client.OscMessageHandler;
import com.neophob.sematrix.osc.model.OscMessage;
import com.neophob.sematrix.osc.server.OscServerException;

/**
 * concrete osc implementation
 * @author michu
 *
 */
class OscServerImpl extends OscServer implements OSCListener {

	private OSCPortIn receiver;
	
	public OscServerImpl(OscMessageHandler handler, String host, int port, int bufferSize) throws OscServerException {
		super(handler, host, port, bufferSize);
		try {
			receiver = new OSCPortIn(port, bufferSize);
			receiver.addAllListener(this);
		} catch (SocketException e) {
			throw new OscServerException("Failed to start OSC Server", e);			
		}		
	}

	@Override
	public void startServer() {
		receiver.startListening();
	}

	@Override
	public void acceptMessage(Date time, OSCMessage message) {
		//ignore time
System.out.println("MESSAGE!!!"+message);		
		OscMessage msg = new OscMessage(message.getAddress());
		//TODO parse blob and arguments
		this.notifyOscClients(msg);
	}

}
