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
package com.neophob.sematrix.mdns.server.impl;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;

import com.neophob.sematrix.mdns.server.MDnsServerException;

/**
 * concrete osc server implementation
 * @author michu
 *
 */
class MDnsServerImpl extends MDnsServer implements Runnable {

	private static final Logger LOG = Logger.getLogger(MDnsServerImpl.class.getName());
	
	public final static String REMOTE_TYPE_TCP = "_pixelcontroller._tcp.local.";
	public final static String REMOTE_TYPE_UDP = "_pixelcontroller._udp.local.";
	
	private JmDNS jmdns;
	
	private boolean started = false;
	
	/**
	 * 
	 * 
	 * @param useTcp
	 * @param handler
	 * @param host
	 * @param port
	 * @param bufferSize
	 * @throws OscServerException
	 */
	public MDnsServerImpl(int port, boolean useTcp, String registerName) throws MDnsServerException {
		super(port, useTcp, registerName);
		try {
			jmdns = JmDNS.create(registerName);
			LOG.log(Level.INFO, "mDNS Server initialized on port "+port+", registerName: "+registerName);
		} catch (Exception e) {
			throw new MDnsServerException("Failed to start mDNS Server", e);			
		}		
	}

	@Override
	public void startServer() {
		try {
            String type = super.isUsingTcp() ? REMOTE_TYPE_TCP : REMOTE_TYPE_UDP;
            LOG.log(Level.INFO, "mDNS Server: Requesting pairing for " + type);
            ServiceInfo pairservice = ServiceInfo.create(type, /*toHex(name)*/"PXLCNT", super.getListeningPort(), 0, 0, "PixelController OSC Server");
         
            jmdns.registerService(pairservice);
            
			LOG.log(Level.INFO, "mDNS Server started and registered as "+jmdns.getName());
			started = true;
		} catch (IOException e) {
			LOG.log(Level.SEVERE, "Failed to start mDNS Server!", e);
		}
	}
	
	@Override
	public void startServerAsync() {
		Thread startThread = new Thread(this);
		startThread.setName("Bonjour async started thread");
		startThread.setDaemon(true);
		startThread.start();

	}


	@Override
	public void stopServer() {
		if (!started) {
			LOG.log(Level.INFO, "mDNS Server was not started yet..");
			return;
		}
		
		try {
			jmdns.unregisterAllServices();
			jmdns.close();
			LOG.log(Level.INFO, "mDNS Server stopped");
		} catch (Exception e) {
			LOG.log(Level.SEVERE, "Failed to stop mDNS Server!", e);
		}		
	}

	/**
	 * start mdns server async
	 */
	@Override
	public void run() {
		LOG.log(Level.INFO, "[ASYNC] Start mDNS Server thread");
		this.startServer();		
		LOG.log(Level.INFO, "[ASYNC] finished mDNS Server thread");
	}




}
