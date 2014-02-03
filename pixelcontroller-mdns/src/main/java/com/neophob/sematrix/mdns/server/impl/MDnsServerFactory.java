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
package com.neophob.sematrix.mdns.server.impl;

import com.neophob.sematrix.mdns.server.MDnsServerException;
import com.neophob.sematrix.mdns.server.PixMDnsServer;

/**
 * mDNS / Bonjour Server Factory class
 * 
 * @author michu
 *
 */
public final class MDnsServerFactory {
	
	private static final boolean USE_TCP = false;
	
	private MDnsServerFactory() {
		//no instance
	}
	
	public static PixMDnsServer createServer(int port, String registerName) throws MDnsServerException {
		return new MDnsServerImpl(port, USE_TCP, registerName);				
	}

}
