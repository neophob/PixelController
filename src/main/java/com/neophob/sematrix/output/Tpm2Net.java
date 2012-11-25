/**
 * Copyright (C) 2011 Michael Vogt <michu@neophob.com>
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

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.neophob.sematrix.glue.Collector;
import com.neophob.sematrix.output.lpd6803.Lpd6803;
import com.neophob.sematrix.properties.ApplicationConfigurationHelper;
import com.neophob.sematrix.properties.ColorFormat;
import com.neophob.sematrix.properties.DeviceConfig;

/**
 * 
 * Send data to a TPM2Net Device.
 * 
 * TPM2 use UDP as transport layer, Port 65506
 * 
 * see http://www.ledstyles.de/ftopic18969.html for more details
 * 
 * 
 * @author michu
 *
 */
public class Tpm2Net extends Output {
	
	/** The log. */
	private static final Logger LOG = Logger.getLogger(Tpm2Net.class.getName());

	private static final int TPM2_NET_HEADER_SIZE = 5;
	private static final int TPM2_NET_PORT = 65506;
	
	private DatagramSocket outputSocket;
	private InetAddress targetAddr;
	
	/** The initialized. */
	protected boolean initialized;

	/** The display options, does the buffer needs to be flipped? rotated? */
	private List<DeviceConfig> displayOptions;
	
	/** The output color format. */
	private List<ColorFormat> colorFormat;
	
	/** define how the panels are arranged */
	private List<Integer> panelOrder;

	/**
	 * 
	 * @param ph
	 * @param controller
	 */
	public Tpm2Net(ApplicationConfigurationHelper ph, PixelControllerOutput controller) {
		super(OutputDeviceEnum.TPM2NET, ph, controller, 8);
		
		this.displayOptions = ph.getLpdDevice();
		this.colorFormat = ph.getColorFormat();
		this.panelOrder = ph.getPanelOrder();
		String targetAddrStr = ph.getTpm2NetIpAddress();
		this.initialized = false;		

		try {
			this.targetAddr = InetAddress.getByName(targetAddrStr);			
			this.outputSocket = new DatagramSocket();
			
			this.initialized = true;
		} catch (Exception e) {
			LOG.log(Level.SEVERE, "Failed to resolve target address: {0}", e);
		}


	}


	
	/**
	 * 
	 * @param targetAddr
	 * @param universeId
	 * @param frameSize
	 * @param data
	 */
	private void sendTpm2NetPacketOut(int universeId, int frameSize, byte[] data) {
        byte[] outputBuffer = new byte[frameSize + TPM2_NET_HEADER_SIZE];
		outputBuffer[0] = (byte)0x9c;
		outputBuffer[1] = (byte)0xda;
		outputBuffer[2] = ((byte)(frameSize >> 8 & 0xFF));
		outputBuffer[3] = ((byte)(frameSize & 0xFF));
		outputBuffer[4] = ((byte)universeId);

		System.arraycopy(data, 0, outputBuffer, TPM2_NET_HEADER_SIZE, frameSize);
		outputBuffer[TPM2_NET_HEADER_SIZE + frameSize] = (byte)0x36;

		DatagramPacket tpm2UdpPacket = new DatagramPacket(outputBuffer, frameSize + TPM2_NET_HEADER_SIZE + 1, 
				targetAddr, TPM2_NET_PORT);
		
		try {
			this.outputSocket.send(tpm2UdpPacket);
		} catch (Exception e) {
			LOG.log(Level.SEVERE, "Failed to send network data: {0}", e);
		}
	}

	@Override
	public void update() {

		if (initialized) {			
			for (int ofs=0; ofs<Collector.getInstance().getNrOfScreens(); ofs++) {
				//get the effective panel buffer
				int panelNr = this.panelOrder.get(ofs);

				int[] transformedBuffer = 
						RotateBuffer.transformImage(super.getBufferForScreen(ofs), displayOptions.get(panelNr),
								Lpd6803.NR_OF_LED_HORIZONTAL, Lpd6803.NR_OF_LED_VERTICAL);
				
				byte[] rgbBuffer = OutputHelper.convertBufferTo24bit(transformedBuffer, colorFormat.get(panelNr));
				
				//TODO optimize packt sender
				sendTpm2NetPacketOut(0, 3*rgbBuffer.length, rgbBuffer);
			}
		}
	}

	@Override
	public void close() {		
		if (this.initialized) {
			LOG.log(Level.INFO, "Close network socket");
			try {
				this.outputSocket.close();				
			} catch (Exception e) {
				LOG.log(Level.SEVERE, "Failed to close network socket: {0}", e);
			}
		} else {
			LOG.log(Level.INFO, "Network socket not initialized, nothing to do.");
		}
			
	}
}