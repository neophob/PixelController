package com.neophob.sematrix.mdns.client.impl;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;

import com.neophob.sematrix.mdns.client.MDnsClientException;
import com.neophob.sematrix.mdns.client.PixMDnsClient;

class MDnsClient implements PixMDnsClient {

	private JmDNS mdnsQuery;
	private ServiceInfo[] services;
	private String type;
	private int timeout;
	
	public MDnsClient(String type, int timeout) {
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
}
