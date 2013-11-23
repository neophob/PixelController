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

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.neophob.sematrix.core.properties.ApplicationConfigurationHelper;

/**
 * Send frames out via UDP
 *
 * @author michu
 * 
 */
public class UdpDevice extends OnePanelResolutionAwareOutput {

	private static final Logger LOG = Logger.getLogger(UdpDevice.class.getName());

	private DatagramPacket packet;
	private DatagramSocket dsocket;
	
	private String targetHost;
	private int targetPort;
	private int errorCounter=0;

	/**
	 * 
	 * @param controller
	 */
	public UdpDevice(ApplicationConfigurationHelper ph) {
		super(OutputDeviceEnum.UDP, ph, 8);

		targetHost = ph.getUdpIp();
		targetPort = ph.getUdpPort();
		
		try {
			InetAddress address = InetAddress.getByName(targetHost);
			packet = new DatagramPacket(new byte[0], 0, address, targetPort);
			dsocket = new DatagramSocket();
			
			this.initialized = true;
			LOG.log(Level.INFO, "UDP device initialized, send data to {0}:{1}", 
					new String[] {this.targetHost, ""+this.targetPort});

		} catch (Exception e) {
			LOG.log(Level.WARNING, "failed to initialize UDP device", e);
		}
	}


	/* (non-Javadoc)
	 * @see com.neophob.sematrix.core.output.Output#update()
	 */
	@Override
	public void update() {
		if (this.initialized) {			
			byte[] buffer = OutputHelper.convertBufferTo24bit(getTransformedBuffer(), colorFormat);
			packet.setData(buffer);
			packet.setLength(buffer.length);
			try {
				dsocket.send(packet);
			} catch (IOException e) {
			    errorCounter++;
				LOG.log(Level.WARNING, "failed to send UDP data.", e);				
			}
		}
	}

    @Override
    public boolean isSupportConnectionState() {
        return true;
    }

    @Override
    public boolean isConnected() {
        return initialized;
    }

    @Override
    public String getConnectionStatus(){
        if (initialized) {
            return "Target IP "+targetHost+":"+targetPort;            
        }
        return "Not connected!";
    }
    
	@Override
	public void close()	{
		if (initialized) {
			dsocket.close();   
		}	    
	}
	

	@Override
	public long getErrorCounter() {
	    return errorCounter;
	}

}

