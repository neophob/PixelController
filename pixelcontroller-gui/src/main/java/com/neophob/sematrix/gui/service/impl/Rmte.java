package com.neophob.sematrix.gui.service.impl;

import com.neophob.sematrix.core.properties.ValidCommands;
import com.neophob.sematrix.osc.client.OscClientException;
import com.neophob.sematrix.osc.client.PixOscClient;
import com.neophob.sematrix.osc.client.impl.OscClientFactory;
import com.neophob.sematrix.osc.model.OscMessage;
import com.neophob.sematrix.osc.server.OscMessageHandler;
import com.neophob.sematrix.osc.server.PixOscServer;
import com.neophob.sematrix.osc.server.impl.OscServerFactory;

public class Rmte extends OscMessageHandler implements Runnable{
	private static final String TARGET_HOST = "pixelcontroller.local";
	private static final int REMOTE_OSC_SERVER_PORT = 9876;
	private static final int LOCAL_OSC_SERVER_PORT = 9875;

	//size of recieving buffer, should fit a whole image buffer
	private static final int BUFFER_SIZE = 50000;

	private PixOscServer oscServer;
	private PixOscClient oscClient;

	public Rmte() throws Exception {
		System.out.println("init srv");
		oscServer = OscServerFactory.createServerUdp(this, LOCAL_OSC_SERVER_PORT, BUFFER_SIZE);
		System.out.println("init client");
		oscClient = OscClientFactory.createClientUdp(TARGET_HOST, REMOTE_OSC_SERVER_PORT, BUFFER_SIZE);
		System.out.println("init done");
		oscServer.startServer();
		System.out.println("started");
		sendOscMessage(ValidCommands.GET_VERSION);
		System.out.println("sent");
Thread.sleep(1000);
	}

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		new Rmte();
	}

	@Override
	public void handleOscMessage(OscMessage msg) {
		System.out.println(System.currentTimeMillis()+" IN: "+msg);
	}

	private void sendOscMessage(ValidCommands cmd) {
		sendOscMessage(cmd.toString());
	}
	OscMessage msg;

	private void sendOscMessage(String s) {
		
		msg = new OscMessage(s);
		Thread startThread = new Thread(this);
		startThread.setName("xxxxx");
		startThread.setDaemon(true);
		startThread.start();
		
	}
	
	@Override
	public void run() {
		try {
System.out.println(System.currentTimeMillis()+" OUT...");			
			oscClient.sendMessage(msg);
			System.out.println("OUT DONW");			
		} catch (OscClientException e) {
			e.printStackTrace();
		}
		
		msg = new OscMessage(ValidCommands.GET_CONFIGURATION+"");
		try {
			System.out.println(System.currentTimeMillis()+" two outs...");
			oscClient.sendMessage(msg);
			oscClient.sendMessage(msg);
		} catch (OscClientException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}


}
