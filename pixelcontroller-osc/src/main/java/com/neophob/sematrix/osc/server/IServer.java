package com.neophob.sematrix.osc.server;


/**
 * OSC Server interface exposed to PixelController Core
 * 
 * @author michu
 *
 */
public interface IServer {

	/**
	 * start the OSC server
	 */
	void startServer();

	/**
	 * start the OSC server
	 */
	void stopServer();

	/**
	 * @return listening port of the osc server
	 */
	int getListeningPort();

	/**
	 * @return listening host(ip or hostname) of the osc server
	 */
	String getListeningHost();

	/**
	 * 
	 * @return buffersize of the osc server, aka maximal packet size
	 */
	int getBufferSize();
	
	/**
	 * 
	 * @return how many OSC packets the server recieved
	 */
	int getPacketCounter();
	
	/**
	 * 
	 * @return how many bytes the server recieved
	 */
	long getBytesRecieved();	
}
