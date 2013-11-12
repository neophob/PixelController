package com.neophob.sematrix.osc.server.impl;

import java.util.Observable;

import com.neophob.sematrix.osc.model.OscMessage;
import com.neophob.sematrix.osc.server.IServer;
import com.neophob.sematrix.osc.server.OscMessageHandler;

/**
 * this abstract osc server register the caller (which must extend the abstract class OscMessageHandler)
 * in the observer and will be notified if the osc server recieve a message
 * 
 * @author michu
 *
 */
public abstract class OscServer extends Observable implements IServer {

	private final String host;
	private final int port;
	private final int bufferSize;
	private int cntPackages;
	private long recievedBytes;
		
	/**
	 * 
	 * @param handler the caller, used for callback
	 * @param host
	 * @param port
	 * @param bufferSize
	 */
	public OscServer(OscMessageHandler handler, String host, int port, int bufferSize) {
		this.host = host;
		this.port = port;
		this.bufferSize = bufferSize;
		
		//register the caller as observer
		addObserver(handler);
	}

	/**
	 * if the server recieved a message, this method must be called to inform 
	 * all clients
	 * 
	 * @param msg
	 */
	protected synchronized void notifyOscClients(final OscMessage msg) {
		setChanged();
        notifyObservers(msg);
        cntPackages++;
        recievedBytes += msg.getMessageSize();
	}
		
	@Override
	public int getListeningPort() {
		return port;
	}

	@Override
	public String getListeningHost() {
		return host;
	}
	
	@Override
	public int getBufferSize() {
		return bufferSize;
	}

	@Override
	public int getPacketCounter() {
		return cntPackages;
	}

	@Override
	public long getBytesRecieved() {
		return recievedBytes;
	}

}
