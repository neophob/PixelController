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
package com.neophob.sematrix.output;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.neophob.sematrix.output.e131.E1_31DataPacket;
import com.neophob.sematrix.properties.ApplicationConfigurationHelper;

/**
 * The Class ArtnetDevice.
 *
 * @author michu
 * 
 * TODO:
 *  -implement multicast
 *  -implement multipanel support
 */
public class E1_31Device extends AbstractDmxDevice {

	private static final Logger LOG = Logger.getLogger(E1_31Device.class.getName());

	private E1_31DataPacket dataPacket = new E1_31DataPacket();
	private DatagramPacket packet;
	private DatagramSocket dsocket;

	private int errorCounter=0;


	/*        if (ipaddr.StartsWith(wxT("239.255.")) || ipaddr == wxT("MULTICAST"))
    {
        // multicast - universe number must be in lower 2 bytes
        wxString ipaddrWithUniv = wxString::Format(wxT("%d.%d.%d.%d"),239,255,(int)UnivHi,(int)UnivLo);
        remoteAddr.Hostname (ipaddrWithUniv);
    }
}*/



	/**
	 * 
	 * @param controller
	 */
	public E1_31Device(ApplicationConfigurationHelper ph, PixelControllerOutput controller) {
		super(OutputDeviceEnum.E1_31, ph, controller, 8);

        //Get dmx specific config
		this.pixelsPerUniverse = ph.getE131PixelsPerUniverse();
	    try {
			this.targetAdress = InetAddress.getByName(ph.getE131Ip());
		} catch (UnknownHostException e) {
			//TODO report
			LOG.log(Level.SEVERE, "Failed to find target address!", e);
		}
	    this.firstUniverseId = ph.getE131StartUniverseId();
	    calculateNrOfUniverse();
	    
		try {
			packet = new DatagramPacket(new byte[0], 0, targetAdress, E1_31DataPacket.E131_PORT);
			dsocket = new DatagramSocket();
			
			this.initialized = true;
			LOG.log(Level.INFO, "E1.31Device device initialized");

		} catch (Exception e) {
			LOG.log(Level.WARNING, "failed to initialize E1.31Device device", e);
		}
	}
	
	


	@Override
	public void close()	{
	    if (initialized) {
			dsocket.close();   
	    }	    
	}


	@Override
	protected void sendBufferToReceiver(int universeId, byte[] buffer) {
		if (this.initialized) {			
			byte[] data = dataPacket.assembleNewE131Packet(this.sequenceID++, universeId, buffer);
			packet.setData(data);
			packet.setLength(data.length);
			try {
				dsocket.send(packet);
			} catch (IOException e) {
			    errorCounter++;
				LOG.log(Level.WARNING, "failed to send E1.31 data.", e);				
			}			
		}
	}

	@Override
	public long getErrorCounter() {
	    return errorCounter;
	}

}

