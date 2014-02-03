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
package com.neophob.sematrix.osc.server;

import com.neophob.sematrix.osc.model.OscMessage;
import com.neophob.sematrix.osc.server.impl.AbstractOscServer;


public class TestOscServer extends AbstractOscServer {

	public TestOscServer(OscMessageHandler handler) {
		super(handler, "127.0.0.1", 3333, 50000);		
	}
	
	@Override
	public void startServer() {
		this.notifyOscClients(new OscMessage("/HELLO"));
		this.notifyOscClients(new OscMessage("/HELLO", new byte[] {0,1,2}));
		this.notifyOscClients(new OscMessage("/HELLO", new String[] {"WORLD"}, new byte[] {0,1,2}));
		this.notifyOscClients(new OscMessage("/HELLO", "WORLD"));
	}

	@Override
	public void stopServer() {
		// TODO Auto-generated method stub
		
	}
	
}
