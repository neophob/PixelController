/**
 * Copyright (C) 2011 Michael Vogt <michu@neophob.com>
 * Copyright (C) 2011 Rainer Ostendorf <mail@linlab.de>
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

package com.neophob.sematrix.output;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

import artnet4j.ArtNet;
import artnet4j.ArtNetException;
import artnet4j.packets.ArtDmxPacket;

import com.neophob.sematrix.glue.Collector;

public class ArtnetOutput extends Output {

	private static Logger log = Logger.getLogger(ArtnetOutput.class.getName());

	private int sequenceID = 0;

	private ArtNet artnet;

	public ArtnetOutput(PixelControllerOutput controller) {
		super(controller, ArtnetOutput.class.toString());
		artnet = new ArtNet();
		artnet.init();
		try {
			artnet.start();
		} catch (Exception e) {
			log.log(Level.WARNING, "failed to initialize ArtNet port!");
		}
	}

	@Override
	public void update() {
		sendBufferToArtnetReceiver(0, super.getBufferForScreen(0) );		
	}

	private void sendBufferToArtnetReceiver(int artnetReceiver, int[] frameBuf) {
		ArtDmxPacket dmx = new ArtDmxPacket();
		dmx.setUniverse(0, 0);
		dmx.setSequenceID(sequenceID % 255);
		byte[] buffer = new byte[frameBuf.length *3 ];
		for (int i = 0; i < frameBuf.length; i++) {
			buffer[i*3]     = (byte) ((frameBuf[i]>>16) & 0xff);
			buffer[(i*3)+1] = (byte) ((frameBuf[i]>>8) & 0xff);
			buffer[(i*3)+2] = (byte) ( frameBuf[i] & 0xff);
		}
		dmx.setDMX(buffer, buffer.length);
		artnet.unicastPacket(dmx, "192.168.2.151");
		sequenceID++;
	}

	@Override
	public void close()	{}
}

