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
package com.neophob.sematrix.osc.client.impl;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.neophob.sematrix.osc.client.OscClientException;
import com.neophob.sematrix.osc.model.OscMessage;

/**
 * OSC Client Factory, send OSC Message
 * 
 * @author michu
 *
 */
public abstract class OscClientFactory {

	private static final Logger LOG = Logger.getLogger(OscClientFactory.class.getName());

	private static final boolean USE_TCP = false;
	
	private static OscClientImpl client = null; 

	/**
	 * 
	 * @param targetIp
	 * @param targetPort
	 * @param msg
	 * @throws OscClientException
	 */
	public static void sendOscMessage(String targetIp, int targetPort, OscMessage msg) throws OscClientException {		
		if (client == null) {
			client = new OscClientImpl(USE_TCP);
		}
		
		client.sendMessage(targetIp, targetPort, msg);
	}

	public static void disconnectOscClient() {
		if (client !=null && client.isConnected()) {
			try {
				client.disconnect();
			} catch (OscClientException e) {
				LOG.log(Level.WARNING, "Failed to disconnect OSC Client", e);
			}
		}
	}
}
