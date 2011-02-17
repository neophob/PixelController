package com.neophob.sematrix.listener;

import java.util.logging.Level;
import java.util.logging.Logger;

import netP5.NetAddress;

import org.apache.commons.lang.StringUtils;

import oscP5.OscArgument;
import oscP5.OscBundle;
import oscP5.OscMessage;
import oscP5.OscP5;
import processing.core.PApplet;

import com.neophob.sematrix.glue.Collector;
import com.neophob.sematrix.listener.MessageProcessor.ValidCommands;

public class OscServer {

	public static final String OSC_PREFIX = "/neo/";
	private static Logger log = Logger.getLogger(OscServer.class.getName());

	private OscP5 oscP5;
	private NetAddress myRemoteLocation;

	/**
	 * 
	 * @param papplet
	 * @param listeningPort
	 * @param sendingPort
	 * @param sendingAddr
	 */
	public OscServer(PApplet papplet, int listeningPort, int sendingPort, String sendingAddr) {
		/* start oscP5, listening for incoming messages at port 12000 */
		oscP5 = new OscP5(this, listeningPort);
		log.log(Level.INFO,	"OSC Server started at port {0}", new Object[] { listeningPort });

		/* myRemoteLocation is a NetAddress. a NetAddress takes 2 parameters,
		 * an ip address and a port number. myRemoteLocation is used as parameter in
		 * oscP5.send() when sending osc packets to another computer, device, 
		 * application. usage see below. for testing purposes the listening port
		 * and the port of the remote location address are the same, hence you will
		 * send messages back to this sketch.
		 */
		myRemoteLocation = new NetAddress(sendingAddr, sendingPort);
		log.log(Level.INFO,	"OSC Client send to {0}:{1}", new Object[] { sendingPort, sendingAddr});
	}

	private static String getParamHack(OscArgument arg) {
		try {
			return arg.stringValue();
		} catch (Exception e) {}

		try {
			return ""+arg.intValue();
		} catch (Exception e) {}

		try {
			return ""+arg.floatValue();
		} catch (Exception e) {}

		return "";
	}

	void oscEvent(OscMessage oscMessage) {
		/* check if theOscMessage has the address pattern we are looking for. */

		try {
			String s = oscMessage.addrPattern();			
			System.out.println("MSG!"+s);
			
			if (StringUtils.startsWithIgnoreCase(s, "/neo/")) {
				int paramCount = oscMessage.arguments().length;

				String[] msg = new String[1+paramCount];
				msg[0] = StringUtils.substring(s, 5);
				for (int x=0; x<paramCount; x++) {
					msg[x+1] = getParamHack(oscMessage.get(x));
				}
				
				ValidCommands response = MessageProcessor.processMsg(msg, true);
				if (response!=null && response == ValidCommands.STATUS) {
					sendStatusToGui();
				}


			} 

		} catch (Exception e) {
			e.printStackTrace();
		}		
	}

	/**
	 * 
	 */
	private void sendStatusToGui() {
		OscBundle myBundle = new OscBundle();
		
		for (String s:Collector.getInstance().getCurrentStatus()) {
			
			String[] msgArray = s.split(" ");		
			OscMessage myMessage = new OscMessage(OSC_PREFIX+msgArray[0]);
			for (int n=1; n<msgArray.length; n++) {
				try {
					int param = Integer.parseInt(msgArray[n]);
					myMessage.add(param);				
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
			myBundle.add(myMessage);
		}
		oscP5.send(myBundle, myRemoteLocation);
	}

/*	private void sendOscMsg(String msg) {
		String[] msgArray = msg.split(" ");		
		OscMessage myMessage = new OscMessage(OSC_PREFIX+msgArray[0]);
		for (int n=1; n<msgArray.length; n++) {
			try {
				int param = Integer.parseInt(msgArray[n]);
				myMessage.add(param);				
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		System.out.println("MSG:"+msg);
		oscP5.send(myMessage, myRemoteLocation);
		
	}*/
}
