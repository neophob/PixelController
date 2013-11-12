package com.neophob.sematrix.osc.server;

import com.neophob.sematrix.osc.model.OscMessage;
import com.neophob.sematrix.osc.server.impl.OscServer;


public class TestOscServer extends OscServer {

	public TestOscServer(OscMessageHandler handler) {
		super(handler, "127.0.0.1", 3333, 50000);		
	}
	
	@Override
	public void startServer() {
		this.notifyOscClients(new OscMessage("/HELLO"));
		this.notifyOscClients(new OscMessage("/HELLO", new byte[] {0,1,2}));
		this.notifyOscClients(new OscMessage("/HELLO", new String[] {"WORLD"}, new byte[] {0,1,2}));
		this.notifyOscClients(new OscMessage("/HELLO", "WORLD"));
	}

	@Override
	public void stopServer() {
		// TODO Auto-generated method stub
		
	}
	
}
