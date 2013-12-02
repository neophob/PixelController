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
package com.neophob.sematrix.osc;

import org.junit.Assert;
import org.junit.Test;

import com.neophob.sematrix.osc.client.impl.OscClientFactory;
import com.neophob.sematrix.osc.model.OscMessage;
import com.neophob.sematrix.osc.server.OscMessageHandler;
import com.neophob.sematrix.osc.server.impl.OscServer;
import com.neophob.sematrix.osc.server.impl.OscServerFactory;

public class ClientServerTest extends OscMessageHandler {

	OscMessage lastMsgHandler;
	
	@Test
	public void ClientServerMsg() throws Exception {
		final String msgName = "/PILLEPALLE";
		final String msgNameTwo = "/PILLEPALLE-TWO";
		final String msgName3 = "/PILLEPALLE-3";
		final String param = "param234";
		
		OscServer srv1 = OscServerFactory.createServer(this, 8888, 4096);
		srv1.startServer();

		OscMessage msg = new OscMessage(msgName); 
		OscClientFactory.sendOscMessage("127.0.0.1", 8888, msg);
		Thread.sleep(100);
		Assert.assertEquals(msgName, lastMsgHandler.getOscPattern());
		
		msg = new OscMessage(msgNameTwo, param);
		OscClientFactory.sendOscMessage("127.0.0.1", 8888, msg);
		Thread.sleep(100);
		Assert.assertEquals(msgNameTwo, lastMsgHandler.getOscPattern());
		Assert.assertEquals(param, lastMsgHandler.getArgs()[0]);

		OscClientFactory.disconnectOscClient();
		Thread.sleep(100);

		srv1.stopServer();

		//recreate a new server on a new port, test osc client
		srv1 = OscServerFactory.createServer(this, 8889, 4096);
		srv1.startServer();
		
		msg = new OscMessage(msgName3, param);
		OscClientFactory.sendOscMessage("127.0.0.1", 8889, msg);
		Thread.sleep(100);
		Assert.assertEquals(msgName3, lastMsgHandler.getOscPattern());
		Assert.assertEquals(param, lastMsgHandler.getArgs()[0]);

		srv1.stopServer();
	}

	@Override
	public void handleOscMessage(OscMessage msg) {
		System.out.println(msg);
		lastMsgHandler = msg;
	}

}
