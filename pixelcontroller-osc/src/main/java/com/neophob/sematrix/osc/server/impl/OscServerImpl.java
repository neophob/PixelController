package com.neophob.sematrix.osc.server.impl;

import java.io.IOException;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.neophob.sematrix.osc.model.OscMessage;
import com.neophob.sematrix.osc.server.OscMessageHandler;
import com.neophob.sematrix.osc.server.OscServerException;

import de.sciss.net.OSCListener;
import de.sciss.net.OSCMessage;
import de.sciss.net.OSCServer;

/**
 * concrete osc server implementation
 * @author michu
 *
 */
class OscServerImpl extends OscServer implements OSCListener {

	private static final Logger LOG = Logger.getLogger(OscServer.class.getName());
	
	private OSCServer oscServer;
	
	public OscServerImpl(OscMessageHandler handler, String host, int port, int bufferSize) throws OscServerException {
		super(handler, host, port, bufferSize);
		try {
			oscServer = OSCServer.newUsing( OSCServer.UDP, port );
			oscServer.addOSCListener(this);
			oscServer.setBufferSize(bufferSize);
			LOG.log(Level.INFO, "OSC Server initialized on port "+port+", buffersize: "+bufferSize);
		} catch (Exception e) {
			throw new OscServerException("Failed to start OSC Server", e);			
		}		
	}

	@Override
	public void startServer() {
		try {
			oscServer.start();
			LOG.log(Level.INFO, "OSC Server started");
		} catch (IOException e) {
			LOG.log(Level.SEVERE, "Failed to start OSC Server!", e);
		}
	}

	@Override
	public void stopServer() {
		try {
			oscServer.stop();
			LOG.log(Level.INFO, "OSC Server stopped");
		} catch (IOException e) {
			LOG.log(Level.SEVERE, "Failed to stop OSC Server!", e);
		}		
	}

	@Override
	public void messageReceived(OSCMessage m, SocketAddress addr, long time) {
		String[] args = null;
		byte[] blob = null;
		if (m.getArgCount()>0) {
			List<String> tmp = new ArrayList<String>();
			for (int i=0; i<m.getArgCount(); i++) {
				Object o = m.getArg(i);

				if (o instanceof Integer || o instanceof String || o instanceof Long) {
					tmp.add(""+o);
				} else if (o instanceof byte[]) {
					blob = (byte[])o;
				} 
			}
			args = new String[tmp.size()];
			args = tmp.toArray(args);
			
		}
		OscMessage msg = new OscMessage(m.getName(), args, blob);
		this.notifyOscClients(msg);		
	}


}
