package com.neophob.sematrix.osc.server.impl;

import com.neophob.sematrix.osc.server.OscMessageHandler;
import com.neophob.sematrix.osc.server.OscServerException;

public abstract class OscServerFactory {

	public static OscServer createServer(OscMessageHandler handler, int port, int bufferSize) throws OscServerException {
		return new OscServerImpl(handler, "", port, bufferSize);				
	}

}
