package com.neophob.sematrix.mdns.client;

public interface PixMDnsClient {

	void start() throws MDnsClientException;

	boolean mdnsServerFound();
	
	int getPort();
	
	String getServerName();
	
	String getFirstIp();
}
