package com.neophob.sematrix.core.rmi;

import java.io.Serializable;
import java.net.SocketAddress;
import java.util.Observer;

import com.neophob.sematrix.core.properties.Command;
import com.neophob.sematrix.osc.client.OscClientException;
import com.neophob.sematrix.osc.server.OscServerException;

public interface RmiApi {
	
	void startServer(Observer handler, int port, int bufferSize) throws OscServerException;
	
	void startClient(String targetIp, int targetPort, int bufferSize) throws OscClientException;
	
	void shutdown();
	
	/**
	 * send data to server
	 * @param socket, optional target address, if null last connection will be reused
	 * @param cmd the command, what to execute
	 * @param data optional parameter to send an object
	 * @throws OscClientException
	 */
	void sendPayload(SocketAddress socket, Command cmd, Serializable data) throws OscClientException;
	
	<T> T reassembleObject(byte[] data, Class<T> type);
}
