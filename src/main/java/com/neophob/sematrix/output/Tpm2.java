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

import java.util.logging.Level;
import java.util.logging.Logger;

import com.neophob.sematrix.glue.Collector;
import com.neophob.sematrix.output.tpm2.Tpm2NetProtocol;
import com.neophob.sematrix.output.tpm2.Tpm2Serial;
import com.neophob.sematrix.properties.ApplicationConfigurationHelper;

/**
 * Send data to a Tpm2 Device. this protocol is a successor of miniDMX and
 * created by people at the ledstyles.de forum.
 * 
 * There is only ONE Matrix supported per output.
 * 
 * @author michu
 */
public class Tpm2 extends OnePanelResolutionAwareOutput {

	/** The log. */
	private static final Logger LOG = Logger.getLogger(Tpm2.class.getName());
			
	private static final String VERSION = "1.1";

	private Tpm2Serial tpm2;
	
	/**
	 * init the mini dmx devices.
	 *
	 * @param controller the controller
	 */
	public Tpm2(ApplicationConfigurationHelper ph, PixelControllerOutput controller) {
		super(OutputDeviceEnum.TPM2, ph, controller, 8);
		
		int baud = ph.parseTpm2BaudRate();
		if (baud==0) {
		    //set default
		    baud = 115200;
		}
		
		//HINT: on windows you need to (for example) use COM1, com1 will not work! (case sensitive)
		String serialPort = OutputHelper.getSerialPortName(ph.getTpm2Device().toUpperCase());
		this.initialized = false;
		this.supportConnectionState = true;
		try {
			tpm2 = new Tpm2Serial(Collector.getInstance().getPapplet(), serialPort, baud);
			this.initialized = true;
			
			LOG.log(Level.INFO, "Initialized TPM2 serial device v{0}, target port: {1}, Resolution: {2}/{3}",  
					new Object[] { VERSION, serialPort, this.matrixData.getDeviceXSize(), this.matrixData.getDeviceYSize()}
			);
			
		} catch (NoSerialPortFoundException e) {
			LOG.log(Level.WARNING, "failed to initialize serial port!", e);
		}
	}
	

	/* (non-Javadoc)
	 * @see com.neophob.sematrix.output.Output#update()
	 */
	public void update() {		
		if (initialized) {					
			
			byte[] rgbBuffer = OutputHelper.convertBufferTo24bit(getTransformedBuffer(), colorFormat);
			if (rgbBuffer.length < 511) {
				//small frame, fit in one packed
				tpm2.sendFrame(Tpm2NetProtocol.createImagePayload(0,1,rgbBuffer));				
			} else {
				//need to splitup buffers				
				int bytesToSend = rgbBuffer.length;
				int currentUniverse = 0;
				int totalUniverse = (int)((bytesToSend/510f))+1;
				while (currentUniverse < totalUniverse) { 
					int l = bytesToSend - 510*currentUniverse;
					if (l>510) l=510;
					byte[] tmp = new byte[l];
					//System.out.println(l+" bytes, "+currentUniverse+"/"+totalUniverse );
					
					System.arraycopy(rgbBuffer, 510*currentUniverse, tmp, 0, l);
					tpm2.sendFrame(Tpm2NetProtocol.createImagePayload(currentUniverse, totalUniverse, tmp));
					
					//debug out
					while (tpm2.getPort().available() > 0) {
						LOG.log(Level.INFO, "<<< ["+tpm2.getPort().readString()+"]");
					} 			
					
					currentUniverse++;
				}
			}
		}
	}


	
	/* (non-Javadoc)
	 * @see com.neophob.sematrix.output.Output#close()
	 */
	@Override
	public void close() {
		if (initialized) {
			tpm2.dispose();
		}
	}
	
    @Override
    public boolean isConnected() {
        return this.initialized;
    }	

    @Override
    public String getConnectionStatus(){        
        if (initialized) {
            return "Connected on port "+tpm2.getConnectedPort();            
        }
        return "Not connected!";
    }
    
    @Override
    public boolean isSupportConnectionState() {
        return true;
    }

}
