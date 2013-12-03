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

import com.neophob.sematrix.mdns.server.IServer;

/**
 * this abstract osc server register the caller (which must extend the abstract class OscMessageHandler)
 * in the observer and will be notified if the osc server recieve a message
 * 
 * @author michu
 *
 */
public abstract class MDnsServer implements IServer {

	private final int port;
	private final boolean useTcp;
	private String registerName;
		
	/**
	 * 
	 * @param handler the caller, used for callback
	 * @param host
	 * @param port
	 * @param bufferSize
	 */
	public MDnsServer(int port, boolean useTcp, String registerName) {
		this.port = port;
		this.useTcp = useTcp;
		this.registerName = registerName;		
	}
		
	@Override
	public int getListeningPort() {
		return port;
	}
	
	@Override
	public String getRegisterName() {
		return registerName;
	}

	@Override
	public boolean isUsingTcp() {
		return useTcp;
	}

}
