package com.neophob.sematrix.core.rmi;

import java.io.Serializable;
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
	
	public enum Protocol {
		TCP, UDP
	}
	/**
	 * starts a RMI server
	 * @param handler notification if a client send data
	 * @param port
	 * @param bufferSize
	 * @throws OscServerException
	 */
	void startServer(Protocol protocol, Observer handler, int port) throws OscServerException;
	
	/**
	 * starts a RMI client that connect to an RMI server
	 * @param targetIp
	 * @param targetPort
	 * @param bufferSize
	 * @throws OscClientException
	 */
	void startClient(Protocol protocol, String targetIp, int targetPort) throws OscClientException;
	String getClientTargetIp();
	int getClientTargetPort();
	
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
	void sendPayload(Command cmd, Serializable data) throws OscClientException;
	
	/**
	 * recreate an object from binary data
	 *  
	 * @param data
	 * @param type
	 * @return
	 */
	<T> T reassembleObject(byte[] data, Class<T> type);
}
