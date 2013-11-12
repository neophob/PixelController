package com.neophob.sematrix.osc;

import static org.junit.Assert.*;

import org.junit.Test;

import com.neophob.sematrix.osc.model.OscMessage;
import com.neophob.sematrix.osc.server.OscMessageHandler;
import com.neophob.sematrix.osc.server.TestOscServer;
import com.neophob.sematrix.osc.server.impl.OscServer;

public class ObserverTest extends OscMessageHandler {

	private OscMessage msg = null;
	
	@Test
	public void TestObserver() {
		OscServer srv = new TestOscServer(this);				
		srv.startServer();
		//initial / removed
		assertEquals("HELLO", msg.getPattern());
		assertEquals("WORLD", msg.getArgs()[0]);
		assertEquals(4, srv.getPacketCounter());
		assertTrue(srv.getBytesRecieved() > 20);
	}
	
	public void handleOscMessage(OscMessage msg) {
		this.msg = msg;
		System.out.println(msg);		
	}

}
