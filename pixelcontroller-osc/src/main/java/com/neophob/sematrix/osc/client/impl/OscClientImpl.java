/**
 * Copyright (C) 2011-2013 Michael Vogt <michu@neophob.com>
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
package com.neophob.sematrix.osc.client.impl;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.neophob.sematrix.osc.client.PixOscClient;
import com.neophob.sematrix.osc.client.OscClientException;
import com.neophob.sematrix.osc.model.OscMessage;

import de.sciss.net.OSCClient;
import de.sciss.net.OSCMessage;
import de.sciss.net.OSCPacket;
import de.sciss.net.OSCServer;

/**
 * 
 * @author michu
 *
 */
class OscClientImpl implements PixOscClient {

	private static final Logger LOG = Logger.getLogger(OscClientImpl.class.getName());

	private OSCClient client;
	private String targetIp;
	private int targetPort;
	
	public OscClientImpl(boolean useTcp, String targetIp, int targetPort, int bufferSize) throws OscClientException {
		try {
			long t1 = System.currentTimeMillis();
			if (useTcp) {
				client = OSCClient.newUsing(OSCServer.TCP);							
			} else {
				client = OSCClient.newUsing(OSCServer.UDP);							
			}			
			this.client.setBufferSize(bufferSize);			
			this.client.setTarget( new InetSocketAddress( targetIp, targetPort ));
			this.client.start();
			
			this.targetIp = targetIp;
			this.targetPort = targetPort;
			
			LOG.log(Level.INFO, "OSC Client Factory initialized and started, buffersize: "+client.getBufferSize()
					+" in "+(System.currentTimeMillis()-t1)+"ms");
		} catch (IOException e) {
			throw new OscClientException("Failed to initialize OSC Client", e);
		}
	}	

	
	@Override
	public void sendMessage(OscMessage msg) throws OscClientException {
		if (client==null || !client.isActive()) {
			LOG.log(Level.WARNING, "Not initialized");
			return;
		}
		//TODO type check
		OSCPacket oscPacket = null;
		if (msg.getArgs()==null) {
			oscPacket = new OSCMessage(msg.getOscPattern());
		} else {
			oscPacket = new OSCMessage(msg.getOscPattern(), (Object[])msg.getArgs());	
		}
		 
		try {
			//LOG.log(Level.INFO, "Send OSC Package "+oscPacket+" to "+targetPort);
			client.send(oscPacket);
		} catch (IOException e) {
			throw new OscClientException("Failed to send OSC Message", e);
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean isConnected() {
		if (client==null) {
			return false;
		}

		return client.isConnected();
	}
	
	@Override
	public String getTargetIp() {
		return targetIp;
	}

	@Override
	public int getTargetPort() {
		return targetPort;
	}


	@Override
	public void disconnect() throws OscClientException {
		if (client==null) {
			return;
		}

		if (client.isConnected()) {
			try {
				LOG.log(Level.INFO, "OSC Client, disconnect");
				client.stop();
				client.dispose();
			} catch (Exception e) {
				LOG.log(Level.WARNING, "Ignored Exception while stopping OSC Client", e);
			}
		}		
	}

}
