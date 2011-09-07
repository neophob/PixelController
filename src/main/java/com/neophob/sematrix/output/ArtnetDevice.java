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

import java.util.logging.Level;
import java.util.logging.Logger;

import com.neophob.sematrix.properties.PropertiesHelper;

import artnet4j.ArtNet;
import artnet4j.packets.ArtDmxPacket;

/**
 * The Class ArtnetDevice.
 *
 * @author michu
 * @author Rainer Ostendorf <mail@linlab.de>
 * 
 * TODO:
 * -support for multiple devices
 * -support for buffer rotation
 * -more options in the config file
 */
public class ArtnetDevice extends Output {

	private static final Logger LOG = Logger.getLogger(ArtnetDevice.class.getName());

	private int sequenceID = 0;
	private ArtNet artnet;
	private boolean initialized;
	
	private String ip;

	/**
	 * 
	 * @param controller
	 */
	public ArtnetDevice(PropertiesHelper ph, PixelControllerOutput controller) {
		super(ph, controller, ArtnetDevice.class.toString());

		initialized = false;
		artnet = new ArtNet();
		artnet.init();
		ip = ph.getArtNetIp();
		try {
			artnet.start();
			initialized = true;
			LOG.log(Level.INFO, "ArtNet device initialized");
		} catch (Exception e) {
			LOG.log(Level.WARNING, "failed to initialize ArtNet port!");
		}
	}

	@Override
	public void update() {
		if (initialized) {
			sendBufferToArtnetReceiver(0, super.getBufferForScreen(0));
		}
	}


	/**
	 * 
	 * @param artnetReceiver
	 * @param frameBuf
	 */
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
		artnet.unicastPacket(dmx, ip);
		sequenceID++;
	}

	@Override
	public void close()	{}
}

