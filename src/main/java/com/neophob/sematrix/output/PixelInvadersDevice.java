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

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.neophob.sematrix.glue.Collector;
import com.neophob.sematrix.output.pixelinvaders.Lpd6803;
import com.neophob.sematrix.properties.ApplicationConfigurationHelper;
import com.neophob.sematrix.properties.ColorFormat;
import com.neophob.sematrix.properties.DeviceConfig;

/**
 * Send data to the PixelInvaders Device.
 * A Pixelinvaders Panel is always 8x8 but supports multiple panels
 *
 * @author michu
 */
public class PixelInvadersDevice extends ArduinoOutput {

	/** The log. */
	private static final Logger LOG = Logger.getLogger(PixelInvadersDevice.class.getName());
		
	/** The display options, does the buffer needs to be flipped? rotated? */
	private List<DeviceConfig> displayOptions;
	
	/** The output color format. */
	private List<ColorFormat> colorFormat;
	
	/** define how the panels are arranged */
	private List<Integer> panelOrder;
	
	/** The lpd6803. */
	private Lpd6803 lpd6803 = null;

	/**
	 * init the lpd6803 devices.
	 *
	 * @param controller the controller
	 * @param displayOptions the display options
	 * @param colorFormat the color format
	 */
	public PixelInvadersDevice(ApplicationConfigurationHelper ph, PixelControllerOutput controller) {
		super(OutputDeviceEnum.PIXELINVADERS, ph, controller, 5);
		
		this.displayOptions = ph.getLpdDevice();
		this.colorFormat = ph.getColorFormat();
		this.panelOrder = ph.getPanelOrder();
		this.initialized = false;	
		
		try {
			lpd6803 = new Lpd6803( Collector.getInstance().getPapplet(), ph.getPixelInvadersBlacklist(), ph.getPixelInvadersCorrectionMap(), panelOrder.size() );			
			this.initialized = lpd6803.ping();
			LOG.log(Level.INFO, "\nPING result: "+ this.initialized+"\n\n");			
		} catch (NoSerialPortFoundException e) {
			LOG.log(Level.WARNING, "failed to initialize serial port!");
		} catch (Throwable e) {
			//catch really ALL excetions here!
			LOG.log(Level.SEVERE, "\n\n\n\nSERIOUS ERROR, check your RXTX installation!", e);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e1) {}
		}
	}
	
	/* (non-Javadoc)
	 * @see com.neophob.sematrix.output.ArduinoOutput#getLatestHeartbeat()
	 */
	public long getLatestHeartbeat() {
		if (initialized) {
			return lpd6803.getArduinoHeartbeat();			
		}
		return -1;
	}

	/* (non-Javadoc)
	 * @see com.neophob.sematrix.output.ArduinoOutput#getArduinoBufferSize()
	 */
	public int getArduinoBufferSize() {
		if (initialized) {
			return lpd6803.getArduinoBufferSize();			
		}
		return -1;
	}

	/* (non-Javadoc)
	 * @see com.neophob.sematrix.output.ArduinoOutput#getArduinoErrorCounter()
	 */
	public long getArduinoErrorCounter() {
		if (initialized) {
			return lpd6803.getAckErrors();			
		}
		return -1;
	}

	/* (non-Javadoc)
	 * @see com.neophob.sematrix.output.Output#update()
	 */
	public void update() {
		
		if (initialized) {			
			for (int ofs=0; ofs<Collector.getInstance().getNrOfScreens(); ofs++) {
				//draw only on available screens!
				
				//get the effective panel buffer
				int panelNr = this.panelOrder.get(ofs);
				
				int[] transformedBuffer = 
					RotateBuffer.transformImage(super.getBufferForScreen(ofs), displayOptions.get(panelNr),
							Lpd6803.NR_OF_LED_HORIZONTAL, Lpd6803.NR_OF_LED_VERTICAL);
				
				transformedBuffer= OutputHelper.flipSecondScanline(transformedBuffer, Lpd6803.NR_OF_LED_HORIZONTAL, Lpd6803.NR_OF_LED_VERTICAL);
				
				int sendedFrames = lpd6803.sendRgbFrame((byte)panelNr, transformedBuffer, colorFormat.get(panelNr));
				if (sendedFrames>0) {
					needUpdate+=sendedFrames;
				} else {
					noUpdate++;
				}
			}
			
			if ((noUpdate+needUpdate)%1000==0) {
				float f = noUpdate+needUpdate;
				float result = (100.0f/f)*needUpdate;
				LOG.log(Level.INFO, "sended frames: {0}% {1}/{2}, ack Errors: {3} last Error: {4}, arduino buffer size: {5}", 
						new Object[] {result, needUpdate, noUpdate, lpd6803.getAckErrors(), 
						lpd6803.getArduinoErrorCounter(), lpd6803.getArduinoBufferSize()});				
			}
			
		}
	}

	@Override
	public String getConnectionStatus(){
	    if (initialized) {
	        return "Connected on port "+lpd6803.getSerialPortName();	        
	    }
	    return "Not connected!";
	}
	
	/* (non-Javadoc)
	 * @see com.neophob.sematrix.output.Output#close()
	 */
	@Override
	public void close() {
		if (initialized) {
			lpd6803.dispose();			
		}
	}

	@Override
    public boolean isSupportConnectionState() {
        return true;
    }
	
	@Override
	public boolean isConnected() {
		if (initialized) {
			return lpd6803.connected();
		}
		return false;
	}

	@Override
	public long getErrorCounter() {
		if (initialized) {			
			return lpd6803.getConnectionErrorCounter();
		}
	    return 0;
	}

}
