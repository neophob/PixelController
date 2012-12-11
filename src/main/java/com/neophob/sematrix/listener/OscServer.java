/**
 * Copyright (C) 2011 Michael Vogt <michu@neophob.com>
 *
 * This file is part of PixelController.
 *
 * PixelController is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PixelController is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with PixelController.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.neophob.sematrix.listener;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;

import oscP5.OscEventListener;
import oscP5.OscMessage;
import oscP5.OscP5;
import oscP5.OscStatus;
import processing.core.PApplet;

import com.neophob.sematrix.glue.Collector;
import com.neophob.sematrix.properties.ValidCommands;

/**
 * OSC Interface 
 * 
 * @author michu
 *
 */
public class OscServer implements OscEventListener {

	/** The log. */
	private static final Logger LOG = Logger.getLogger(OscServer.class.getName());

	private int listeningPort;

	private OscP5 oscP5;

	/**
	 * 
	 * @param listeningPort
	 */
	public OscServer(PApplet papplet, int listeningPort) {
		this.listeningPort = listeningPort;
		LOG.log(Level.INFO,	"Start OSC Server at port {0}", new Object[] { listeningPort });
		this.oscP5 = new OscP5(papplet, this.listeningPort);
		this.oscP5.addListener(this);
		
		//log only error and warnings
		OscP5.setLogStatus(netP5.Logger.ALL, netP5.Logger.OFF);
		OscP5.setLogStatus(netP5.Logger.ERROR, netP5.Logger.ON);
		OscP5.setLogStatus(netP5.Logger.WARNING, netP5.Logger.ON);
	}

	/**
	 * 
	 * @param theOscMessage
	 */
	public void oscEvent(OscMessage theOscMessage) {
		//sanity check
		if (StringUtils.isBlank(theOscMessage.addrPattern())) {
			LOG.log(Level.INFO,	"Ignore empty OSC message...");
			return;
		}
		
		LOG.log(Level.INFO,	"Received an osc message. with address pattern {0} typetag {1}.", 
				new Object[] { theOscMessage.addrPattern(), theOscMessage.typetag() });		

		//address pattern -> internal message mapping
		String pattern = theOscMessage.addrPattern().trim().toUpperCase();
		try {
			ValidCommands command = ValidCommands.valueOf(pattern);
			String[] msg = new String[1+command.getNrOfParams()];
			msg[0] = pattern;
			for (int i=0; i<command.getNrOfParams(); i++) {

				//parse osc message
				if (theOscMessage.checkTypetag("s")) {
					msg[i+1] = theOscMessage.get(i).stringValue();
					LOG.log(Level.INFO,	"PARAM {0}", msg[i+1]); 
				} else
					if (theOscMessage.checkTypetag("i")) {
						msg[i+1] = ""+theOscMessage.get(i).intValue();	
					} else
						if (theOscMessage.checkTypetag("f")) {
							msg[i+1] = ""+theOscMessage.get(i).floatValue();	
						}				
			}
			MessageProcessor.processMsg(msg, true);
			
	        //refresh gui
			Collector.getInstance().setTriggerGuiRefresh(true);			    
			
		} catch (Exception e) {
			LOG.log(Level.WARNING, "Failed to parse OSC Message", e);
			return;
		}		
	}

	@Override
	public void oscStatus(OscStatus arg0) {
		// TODO Auto-generated method stub
		
	}
	
	
}
