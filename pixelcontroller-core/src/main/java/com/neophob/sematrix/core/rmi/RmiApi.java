package com.neophob.sematrix.core.rmi;

import java.io.Serializable;
import java.net.SocketAddress;
import java.util.Observer;

import com.neophob.sematrix.core.properties.Command;
import com.neophob.sematrix.osc.client.OscClientException;
import com.neophob.sematrix.osc.server.OscServerException;

/**
 * simple api to abstract the transport of data/objects between a server and a client
 * 
 * @author michu
 *
 */
public interface RmiApi {
	
	/**
	 * starts a RMI server
	 * @param handler notification if a client send data
	 * @param port
	 * @param bufferSize
	 * @throws OscServerException
	 */
	void startServer(Observer handler, int port, int bufferSize) throws OscServerException;
	
	/**
	 * starts a RMI client that connect to an RMI server
	 * @param targetIp
	 * @param targetPort
	 * @param bufferSize
	 * @throws OscClientException
	 */
	void startClient(String targetIp, int targetPort, int bufferSize) throws OscClientException;
	
	/**
	 * shutdown
	 */
	void shutdown();
	
	/**
	 * send data to server
	 * @param socket, optional target address, if null last connection will be reused
	 * @param cmd the command, what to execute
	 * @param data optional parameter to send an object
	 * @throws OscClientException
	 */
	void sendPayload(SocketAddress socket, Command cmd, Serializable data) throws OscClientException;
	
	/**
	 * recreate an object from binary data
	 *  
	 * @param data
	 * @param type
	 * @return
	 */
	<T> T reassembleObject(byte[] data, Class<T> type);
}
