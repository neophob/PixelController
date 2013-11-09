package com.neophob.sematrix.osc.server.impl;

import java.net.SocketException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

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

	private static final Logger LOG = Logger.getLogger(OscServer.class.getName());
	
	private OSCPortIn receiver;
	
	public OscServerImpl(OscMessageHandler handler, String host, int port, int bufferSize) throws OscServerException {
		super(handler, host, port, bufferSize);
		try {
			receiver = new OSCPortIn(port, bufferSize);
			receiver.addAllListener(this);
			LOG.log(Level.INFO, "OSC Server initialized on port "+port+", buffersize: "+bufferSize);
		} catch (SocketException e) {
			throw new OscServerException("Failed to start OSC Server", e);			
		}		
	}

	@Override
	public void startServer() {
		receiver.startListening();
		LOG.log(Level.INFO, "OSC Server started");
	}

	@Override
	public void stopServer() {
		receiver.stopListening();
		LOG.log(Level.INFO, "OSC Server stopped");
	}

	@Override
	public void acceptMessage(Date time, OSCMessage message) {
		//ignore time
		LOG.log(Level.INFO, "MESSAGE: "+message);
		
		List<String> args = new ArrayList<String>();
		byte[] blob = null;
new NullPointerException().printStackTrace();
		if (message.getArguments() != null) {
			for (Object o: message.getArguments()) {
				if (o==null) continue;
				LOG.log(Level.INFO, "\n  OSC PARAM: "+o+", type: "+o.getClass());
				if (o.getClass()==Integer.class || o.getClass()==String.class) {
					args.add(""+o);
//				} else if () {
//					args.add(""+o);
				} 
			}			
		}
		
		OscMessage msg = new OscMessage(message.getAddress()/*, String[] args, byte[] blob*/);
		this.notifyOscClients(msg);
	}

}
