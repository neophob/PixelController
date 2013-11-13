package com.neophob.sematrix.osc;

import org.junit.Assert;
import org.junit.Test;

import com.neophob.sematrix.osc.client.impl.OscClientFactory;
import com.neophob.sematrix.osc.model.OscMessage;
import com.neophob.sematrix.osc.server.OscMessageHandler;
import com.neophob.sematrix.osc.server.impl.OscServer;
import com.neophob.sematrix.osc.server.impl.OscServerFactory;

public class ClientServerTest extends OscMessageHandler {

	OscMessage lastMsgHandler;
	
	@Test
	public void ClientServerMsg() throws Exception {
		final String msgName = "/PILLEPALLE";
		final String msgNameTwo = "/PILLEPALLE-TWO";
		final String msgName3 = "/PILLEPALLE-3";
		final String param = "param234";
		
		OscServer srv1 = OscServerFactory.createServer(this, 8888, 4096);
		srv1.startServer();

		OscMessage msg = new OscMessage(msgName); 
		OscClientFactory.sendOscMessage("127.0.0.1", 8888, msg);
		Thread.sleep(100);
		Assert.assertEquals(msgName, lastMsgHandler.getOscPattern());
		
		msg = new OscMessage(msgNameTwo, param);
		OscClientFactory.sendOscMessage("127.0.0.1", 8888, msg);
		Thread.sleep(100);
		Assert.assertEquals(msgNameTwo, lastMsgHandler.getOscPattern());
		Assert.assertEquals(param, lastMsgHandler.getArgs()[0]);

		OscClientFactory.disconnectOscClient();
		Thread.sleep(100);

		srv1.stopServer();

		//recreate a new server on a new port, test osc client
		srv1 = OscServerFactory.createServer(this, 8889, 4096);
		srv1.startServer();
		
		msg = new OscMessage(msgName3, param);
		OscClientFactory.sendOscMessage("127.0.0.1", 8889, msg);
		Thread.sleep(100);
		Assert.assertEquals(msgName3, lastMsgHandler.getOscPattern());
		Assert.assertEquals(param, lastMsgHandler.getArgs()[0]);

		srv1.stopServer();
	}

	@Override
	public void handleOscMessage(OscMessage msg) {
		System.out.println(msg);
		lastMsgHandler = msg;
	}

}
