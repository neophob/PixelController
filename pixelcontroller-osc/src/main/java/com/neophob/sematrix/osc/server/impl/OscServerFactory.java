package com.neophob.sematrix.osc.server.impl;

import com.neophob.sematrix.osc.client.OscMessageHandler;
import com.neophob.sematrix.osc.server.OscServerException;
import com.neophob.sematrix.osc.server.impl.OscServer;
import com.neophob.sematrix.osc.server.impl.OscServerImpl;

public abstract class OscServerFactory {

	public static OscServer createServer(OscMessageHandler handler, int port, int bufferSize) throws OscServerException {
		return new OscServerImpl(handler, "", port, bufferSize);				
	}

}
