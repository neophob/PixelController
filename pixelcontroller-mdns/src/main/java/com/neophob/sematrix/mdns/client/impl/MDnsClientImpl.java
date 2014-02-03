/**
 * Copyright (C) 2011-2014 Michael Vogt <michu@neophob.com>
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
package com.neophob.sematrix.mdns.client.impl;

import java.net.InetAddress;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;

import com.neophob.sematrix.mdns.client.MDnsClientException;
import com.neophob.sematrix.mdns.client.PixMDnsClient;

class MDnsClientImpl implements PixMDnsClient {

	private JmDNS mdnsQuery;
	private ServiceInfo[] services;
	private String type;
	private int timeout;
	
	public MDnsClientImpl(String type, int timeout) {
		this.type = type;
		this.timeout = timeout;
	}
	
	/**
	 * start pixelcontroller bonjour server
	 */
	public void start() throws MDnsClientException {
		try {
			mdnsQuery = JmDNS.create();
		} catch (Exception e) {
			throw new MDnsClientException(e);
		}
		
		services = mdnsQuery.list(type, timeout);
	}
	
	/**
	 * was the pixelcontroller server found via mdns?
	 */
	public boolean mdnsServerFound() {
		return services!=null && services.length>0;
	}
	
	/**
	 * return -1 if port is not found or the port of the pixelcontroller osc server
	 */
	public int getPort() {
		if (!mdnsServerFound()) {
			return -1;
		}
		return services[0].getPort();		
	}
	
	/**
	 * get service name
	 */
	public String getServerName() {
		if (!mdnsServerFound()) {
			return "";
		}
		return services[0].getServer();
	}
	
	public String getFirstIp() {
		if (!mdnsServerFound()) {
			return "";
		}
		
		InetAddress[] addr = services[0].getInetAddresses();
		if (addr.length<1) {
			return "";
		}
		return addr[0].getHostAddress();
	}

}
