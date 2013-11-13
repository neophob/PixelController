package com.neophob.sematrix.osc.server.impl;

import com.neophob.sematrix.osc.server.OscMessageHandler;
import com.neophob.sematrix.osc.server.OscServerException;

/**
 * OSC Server Factory class
 * 
 * @author michu
 *
 */
public abstract class OscServerFactory {

	private static final boolean USE_TCP = false;
	
	public static OscServer createServer(OscMessageHandler handler, int port, int bufferSize) throws OscServerException {
		return new OscServerImpl(USE_TCP, handler, "", port, bufferSize);				
	}

}
