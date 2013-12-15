package com.neophob.sematrix.core.osc;

import com.neophob.sematrix.core.api.PixelController;
import com.neophob.sematrix.core.properties.ValidCommands;
import com.neophob.sematrix.osc.client.OscClientException;
import com.neophob.sematrix.osc.client.PixOscClient;
import com.neophob.sematrix.osc.model.OscMessage;

public class OscReplyManager {

	private PixOscClient oscClient;
	private PixelController pixelController;
	
	public OscReplyManager(PixelController pixelController, PixOscClient oscClient, OscMessage oscIn) throws OscClientException {
		this.pixelController = pixelController;
		this.oscClient = oscClient;
	}

	public void sendReply(String[] msg) throws OscClientException {
				
		ValidCommands cmd = ValidCommands.valueOf(msg[0]);
		OscMessage reply;
System.out.println("send reply");		
		switch (cmd) {
		case GET_CONFIGURATION:				
			String s = pixelController.getConfig()+""; //TODO serialize
			reply = new OscMessage(cmd.toString(), s);
			oscClient.sendMessage(reply);
			break;
			
		case GET_VERSION:
			reply = new OscMessage(cmd.toString(), pixelController.getVersion());
			oscClient.sendMessage(reply);
			break;
		}
		
	}
}
