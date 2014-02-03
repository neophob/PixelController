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
package com.neophob.sematrix.osc;

import static org.junit.Assert.*;

import org.junit.Test;

import com.neophob.sematrix.osc.model.OscMessage;
import com.neophob.sematrix.osc.server.OscMessageHandler;
import com.neophob.sematrix.osc.server.TestOscServer;
import com.neophob.sematrix.osc.server.impl.AbstractOscServer;

public class ObserverTest extends OscMessageHandler {

	private OscMessage msg = null;
	
	@Test
	public void TestObserver() {
		AbstractOscServer srv = new TestOscServer(this);				
		srv.startServer();
		//initial / removed
		assertEquals("HELLO", msg.getPattern());
		assertEquals("WORLD", msg.getArgs()[0]);
		assertEquals(4, srv.getPacketCounter());
		assertTrue(srv.getBytesRecieved() > 20);
	}
	
	public void handleOscMessage(OscMessage msg) {
		this.msg = msg;
		System.out.println(msg);		
	}

}
