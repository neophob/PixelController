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
package com.neophob.sematrix.osc.server;


/**
 * OSC Server interface exposed to PixelController Core
 * 
 * @author michu
 *
 */
public interface PixOscServer {

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
