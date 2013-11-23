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
package com.neophob.sematrix.core.output;

import java.net.BindException;
import java.net.InetAddress;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;

import artnet4j.ArtNet;
import artnet4j.ArtNetNode;
import artnet4j.ArtNetServer;
import artnet4j.events.ArtNetDiscoveryListener;
import artnet4j.packets.ArtDmxPacket;

import com.neophob.sematrix.core.properties.ApplicationConfigurationHelper;

/**
 * The Class ArtnetDevice.
 *
 * @author michu
 * @author Rainer Ostendorf <mail@linlab.de>
 * 
 * TODO:
 *  -automatic updating of node configurations ?
 */
public class ArtnetDevice extends AbstractDmxDevice implements ArtNetDiscoveryListener {

	private static final Logger LOG = Logger.getLogger(ArtnetDevice.class.getName());

	private ArtNet artnet;
	
	/**
	 * 
	 * @param controller
	 */
	public ArtnetDevice(ApplicationConfigurationHelper ph, int nrOfScreens) {
		super(OutputDeviceEnum.ARTNET, ph, 8, nrOfScreens);
		
		this.displayOptions = ph.getArtNetDevice();
		
        //Get dmx specific config
		this.pixelsPerUniverse = ph.getArtNetPixelsPerUniverse();
	    try {
			this.targetAdress = InetAddress.getByName(ph.getArtNetIp());
		    this.firstUniverseId = ph.getArtNetStartUniverseId();
		    calculateNrOfUniverse();
		    
			this.artnet = new ArtNet();				
		    String broadcastAddr = ph.getArtNetBroadcastAddr();	    
		    if (StringUtils.isBlank(broadcastAddr)) {
		        broadcastAddr = ArtNetServer.DEFAULT_BROADCAST_IP;
		    }
		    
			LOG.log(Level.INFO, "Initialize ArtNet device IP: {0}, broadcast IP: {1}, Port: {2}",  
					new Object[] { this.targetAdress.toString(), broadcastAddr, ArtNetServer.DEFAULT_PORT}
			);

		    this.artnet.init();
		    this.artnet.setBroadCastAddress(broadcastAddr);
		    this.artnet.start();
		    this.artnet.getNodeDiscovery().addListener(this);
		    this.artnet.startNodeDiscovery();
		    		    
		    this.initialized = true;
			LOG.log(Level.INFO, "ArtNet device initialized, use "+this.displayOptions.size()+" panels");
			
		} catch (BindException e) {
			LOG.log(Level.WARNING, "\nFailed to initialize ArtNet device:", e);
			LOG.log(Level.WARNING, "Make sure no ArtNet Tools like DMX-Workshop are running!\n\n");
		} catch (Exception e) {
			LOG.log(Level.WARNING, "Failed to initialize ArtNet device:", e);
		}
	}
		

	/**
	 * send buffer to a dmx universe
	 * a DMX universe can address up to 512 channels - this means up to
	 * 170 RGB LED (510 Channels)
	 * 
	 * Just for myself:
	 * ArtNet packets are made up of the Ethernet data (source and destination IP addresses), followed by
	 * the ArtNet Subnet (0 to 15) and the ArtNet universe (0 to 15), and finally the DMX data for that
	 * universe).
	 *
	 * @param artnetReceiver
	 * @param frameBuf
	 */
	protected void sendBufferToReceiver(int universeId, byte[] buffer) {
		ArtDmxPacket dmx = new ArtDmxPacket();
		
		//parameter: int subnetID, int universeID
		//TODO: make subnet Id configurable?
		dmx.setUniverse(0, universeId);
		dmx.setSequenceID(sequenceID % 255);
		
		//byte[] dmxData, int numChannels
		dmx.setDMX(buffer, buffer.length);
		this.artnet.unicastPacket(dmx, this.targetAdress);		
		this.sequenceID++;
	}

	@Override
	public void close()	{
	    if (initialized) {
	        this.artnet.stop();   
	    }	    
	}



    /* (non-Javadoc)
     * @see artnet4j.events.ArtNetDiscoveryListener#discoveredNewNode(artnet4j.ArtNetNode)
     */
    @Override
    public void discoveredNewNode(ArtNetNode arg0) {
        LOG.log(Level.INFO, "ArtNet discovery: new node found: "+arg0);        
    }


    /* (non-Javadoc)
     * @see artnet4j.events.ArtNetDiscoveryListener#discoveredNodeDisconnected(artnet4j.ArtNetNode)
     */
    @Override
    public void discoveredNodeDisconnected(ArtNetNode arg0) {
        LOG.log(Level.INFO, "ArtNet discovery: discovered node disconnected: "+arg0);
    }


    /* (non-Javadoc)
     * @see artnet4j.events.ArtNetDiscoveryListener#discoveryCompleted(java.util.List)
     */
    @Override
    public void discoveryCompleted(List<ArtNetNode> arg0) {
        int nr=0;
        if (arg0!=null) {
            nr = arg0.size();
        }
        LOG.log(Level.INFO, "ArtNet discovery complete, found "+nr+" devices");
    }


    /* (non-Javadoc)
     * @see artnet4j.events.ArtNetDiscoveryListener#discoveryFailed(java.lang.Throwable)
     */
    @Override
    public void discoveryFailed(Throwable arg0) {
        LOG.log(Level.INFO, "ArtNet discovery failed!", arg0);
    }

}

