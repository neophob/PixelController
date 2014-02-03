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
package com.neophob.sematrix.osc.server.impl;

import java.util.Observable;
import java.util.Observer;

import com.neophob.sematrix.osc.model.OscMessage;
import com.neophob.sematrix.osc.server.PixOscServer;

/**
 * this abstract osc server register the caller (which must extend the abstract class OscMessageHandler)
 * in the observer and will be notified if the osc server recieve a message
 * 
 * @author michu
 *
 */
public abstract class AbstractOscServer extends Observable implements PixOscServer {

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
	public AbstractOscServer(Observer handler, String host, int port, int bufferSize) {
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
