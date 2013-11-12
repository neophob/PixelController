package com.neophob.sematrix.osc;

import org.junit.Assert;
import org.junit.Test;

import com.neophob.sematrix.osc.client.impl.OscClientFactory;
import com.neophob.sematrix.osc.model.OscMessage;
import com.neophob.sematrix.osc.server.OscMessageHandler;
import com.neophob.sematrix.osc.server.impl.OscServer;
import com.neophob.sematrix.osc.server.impl.OscServerFactory;

public class ClientServerTest extends OscMessageHandler {

	OscMessage lastMsg;
	
	@Test
	public void ClientServerMsg() throws Exception {
		final String msgName = "/PILLEPALLE";
		
		OscServer srv = OscServerFactory.createServer(this, 8888, 4096);
		srv.startServer();
		OscMessage msg = new OscMessage(msgName, "PARAM");
		OscClientFactory.sendOscMessage("127.0.0.1", 8888, msg);
		Thread.sleep(100);
		Assert.assertEquals(msgName, lastMsg.getOscPattern());
		
		//TODO test parameter
		srv.stopServer();
	}

	@Override
	public void handleOscMessage(OscMessage msg) {
		System.out.println(msg);
		lastMsg = msg;
	}

}
