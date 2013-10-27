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

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.Adler32;

import com.neophob.sematrix.glue.Collector;
import com.neophob.sematrix.output.tpm2.Tpm2NetProtocol;
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
 * Protocol:
 * Blockstart-Byte:        0x9C
 * 
 * Block-Art:              0xDA = Datenframe (DAta) *oder*
 *                         0xC0 = Befehl (Command) *oder*
 *                         0xAA = Angeforderte Antwort (vom Datenempfänger an den Sender)
 * 
 * Framegrösse in 16 Bit:  High-Byte zuerst, dann
 *                         Low-Byte
 * 
 * Paketnummer:            0-255
 * 
 * Anzahl Pakete:          1-255
 *
 * Nutzdaten:              1 - 65.535 Bytes Daten oder Befehle mit Parametern
 * 
 * Blockende-Byte:         0x36
 * 
 * 
 * 
 * @author michu
 *
 */
public class Tpm2Net extends Output {

	/** The log. */
	private static final Logger LOG = Logger.getLogger(Tpm2Net.class.getName());

	private static Adler32 adler = new Adler32();

	private DatagramSocket outputSocket;

	/** The initialized. */
	protected boolean initialized;

	/** The display options, does the buffer needs to be flipped? rotated? */
	private List<DeviceConfig> displayOptions;

	/** The output color format. */
	private List<ColorFormat> colorFormat;

	/** define how the panels are arranged */
	private List<Integer> panelOrder;

	private DatagramPacket tpm2UdpPacket;

	private String targetAddrStr;

	private long errorCounter = 0;

	/** map to store checksum of image. */
	private Map<Integer, Long> lastDataMap;

    /** flip each 2nd scanline? */
	private boolean snakeCabeling;
    
    /** Manual mapping */
	private int[] mapping;

	/**
	 * 
	 * @param ph
	 * @param controller
	 */
	public Tpm2Net(ApplicationConfigurationHelper ph, PixelControllerOutput controller) {
		super(OutputDeviceEnum.TPM2NET, ph, controller, 8);

		this.displayOptions = ph.getTpm2NetDevice();		
		this.colorFormat = ph.getColorFormat();
		this.panelOrder = ph.getPanelOrder();
		this.snakeCabeling = ph.isOutputSnakeCabeling();
		this.mapping = ph.getOutputMappingValues();
		 
		targetAddrStr = ph.getTpm2NetIpAddress();
		this.initialized = false;		
		this.lastDataMap = new HashMap<Integer, Long>();

		try {
			InetAddress targetAddr = InetAddress.getByName(targetAddrStr);
			this.outputSocket = new DatagramSocket();
			this.tpm2UdpPacket = new DatagramPacket(new byte[0], 0, targetAddr, Tpm2NetProtocol.TPM2_NET_PORT);

			this.initialized = true;
			LOG.log(Level.INFO, "Initialized TPM2NET device, target IP: {0}:{1}, Resolution: {2}/{3}, snakeCabeling: {4}",  
					new Object[] { targetAddr, Tpm2NetProtocol.TPM2_NET_PORT, 
					this.matrixData.getDeviceXSize(), this.matrixData.getDeviceYSize(), this.snakeCabeling}
					);

		} catch (Exception e) {
			LOG.log(Level.SEVERE, "Failed to resolve target address "+targetAddrStr+":"+Tpm2NetProtocol.TPM2_NET_PORT+" {0}", e);
		}
	}

	/**
	 * 
	 * @param ofs
	 * @param data
	 * @return
	 */
	private boolean didFrameChange(int ofs, byte data[]) {
		adler.reset();
		adler.update(data);
		long l = adler.getValue();

		if (!lastDataMap.containsKey(ofs)) {
			//first run
			lastDataMap.put(ofs, l);
			return true;
		}

		if (lastDataMap.get(ofs) == l) {
			//last frame was equal current frame, do not send it!
			//log.log(Level.INFO, "do not send frame to {0}", addr);
			return false;
		}
		//update new hash
		lastDataMap.put(ofs, l);
		return true;
	}


	/**
	 * send out a tpm2net paket
	 *  
	 * @param packetNumber: a tpm2net frame can consists out of multiple udp packets
	 * @param frameSize
	 * @param data
	 */
	private void sendTpm2NetPacketOut(int packetNumber, int totalPackets, byte[] data) {		
		tpm2UdpPacket.setData(Tpm2NetProtocol.createImagePayload(packetNumber, totalPackets, data));

		try {
			this.outputSocket.send(tpm2UdpPacket);
		} catch (Exception e) {
			errorCounter++;
			LOG.log(Level.SEVERE, "Failed to send network data: {0}", e);
		}
	}

	@Override
	/**
	 * update panels
	 */
	public void update() {

		if (initialized) {
			int nrOfScreens = Collector.getInstance().getNrOfScreens();
			for (int ofs=0; ofs<nrOfScreens; ofs++) {
				//get the effective panel buffer
				int panelNr = this.panelOrder.get(ofs);

				int[] transformedBuffer = 
						RotateBuffer.transformImage(super.getBufferForScreen(ofs), displayOptions.get(panelNr),
								this.matrixData.getDeviceXSize(), this.matrixData.getDeviceYSize());
				
				if (this.snakeCabeling) {
		            //flip each 2nd scanline
		            transformedBuffer= OutputHelper.flipSecondScanline(transformedBuffer, this.matrixData.getDeviceXSize(), this.matrixData.getDeviceYSize());
		        } else if (this.mapping.length>0) {
		        	//do manual mapping
		        	transformedBuffer = OutputHelper.manualMapping(transformedBuffer, mapping, this.matrixData.getDeviceXSize(), this.matrixData.getDeviceYSize());
		        }
				
				byte[] rgbBuffer = OutputHelper.convertBufferTo24bit(transformedBuffer, colorFormat.get(panelNr));

				//send small UDP packages, this is not optimal but the client needs less memory
				//TODO maybe add option to send one or multiple packets				

				if (didFrameChange(ofs, rgbBuffer)) {
					sendTpm2NetPacketOut(ofs, nrOfScreens, rgbBuffer);
				}
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
			return "Target IP "+targetAddrStr+":"+Tpm2NetProtocol.TPM2_NET_PORT;            
		}
		return "Not connected!";
	}

	@Override
	public long getErrorCounter() {        
		return errorCounter;
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