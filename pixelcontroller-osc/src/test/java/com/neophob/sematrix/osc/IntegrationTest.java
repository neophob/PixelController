package com.neophob.sematrix.osc;

import com.neophob.sematrix.osc.client.OscMessageHandler;
import com.neophob.sematrix.osc.model.OscMessage;
import com.neophob.sematrix.osc.server.OscServerException;
import com.neophob.sematrix.osc.server.impl.OscServer;
import com.neophob.sematrix.osc.server.impl.OscServerFactory;


public class IntegrationTest extends OscMessageHandler {

	OscServer srv;
	
	public IntegrationTest() throws OscServerException {
		System.out.println("create server");
		srv = OscServerFactory.createServer(this, 9876, 1500);
		srv.startServer();
		System.out.println("done");
	}
	
	public void mainLoop() throws Exception {
		System.out.println("enter mainloop");
		while (true) {
//			System.out.println("packets: "+srv.getPacketCounter());
//			Thread.sleep(444);
		}
	}
	public void handleOscMessage(OscMessage msg) {
		System.out.println("Check");
		System.out.println(msg);		
	}

	public static void main(String args[]) throws Exception {
		new IntegrationTest().mainLoop();    
		System.out.println("bye");
	}


}
