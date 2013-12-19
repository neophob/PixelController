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
package com.neophob.sematrix.osc.server.impl;

import java.util.Observer;

import com.neophob.sematrix.osc.server.OscServerException;
import com.neophob.sematrix.osc.server.PixOscServer;

/**
 * OSC Server Factory class
 * 
 * @author michu
 *
 */
public final class OscServerFactory {

	private OscServerFactory() {
		//no instance
	}
	
	public static PixOscServer createServerTcp(Observer handler, int port, int bufferSize) throws OscServerException {
		return new OscServerImpl(true, handler, "", port, bufferSize);				
	}

	public static PixOscServer createServerUdp(Observer handler, int port, int bufferSize) throws OscServerException {
		return new OscServerImpl(false, handler, "", port, bufferSize);				
	}

}
