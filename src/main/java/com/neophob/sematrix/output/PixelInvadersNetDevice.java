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
import com.neophob.sematrix.output.lpd6803.Lpd6803Net;
import com.neophob.sematrix.properties.ApplicationConfigurationHelper;
import com.neophob.sematrix.properties.ColorFormat;
import com.neophob.sematrix.properties.DeviceConfig;

/**
 * Send data to the PixelInvaders Device.
 * A Pixelinvaders Panel is always 8x8 but supports multiple panels
 *
 * @author michu
 */
public class PixelInvadersNetDevice extends Output {

	/** The log. */
	private static final Logger LOG = Logger.getLogger(PixelInvadersNetDevice.class.getName());
		
	/** The display options, does the buffer needs to be flipped? rotated? */
	private List<DeviceConfig> displayOptions;
	
	/** The output color format. */
	private List<ColorFormat> colorFormat;
	
	/** define how the panels are arranged */
	private List<Integer> panelOrder;
	
	/** The lpd6803. */
	private Lpd6803Net lpd6803 = null;

	private boolean initialized;
	
	long sendedFrames = 0;
	
	/**
	 * init the lpd6803 devices.
	 *
	 * @param controller the controller
	 * @param displayOptions the display options
	 * @param colorFormat the color format
	 */
	public PixelInvadersNetDevice(ApplicationConfigurationHelper ph, PixelControllerOutput controller) {
		super(OutputDeviceEnum.PIXELINVADERS_NET, ph, controller, 5);
		
		this.displayOptions = ph.getLpdDevice();
		this.colorFormat = ph.getColorFormat();
		this.panelOrder = ph.getPanelOrder();
		this.initialized = false;	
		
		String ip = ph.getPixelinvadersNetIp();
		int port = ph.getPixelinvadersNetPort();
		try {
			lpd6803 = new Lpd6803Net( Collector.getInstance().getPapplet(), ip, port, ph.getPixelInvadersCorrectionMap() );
			this.initialized = lpd6803.connected();
			LOG.log(Level.INFO, "\nPING result: "+ this.initialized+"\n\n");			
		} catch (Exception e) {
			LOG.log(Level.WARNING, "failed to conect to pixelcontroller network device!", e);
		}
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
							Lpd6803Net.NR_OF_LED_HORIZONTAL, Lpd6803Net.NR_OF_LED_VERTICAL);
				
				int sf = lpd6803.sendRgbFrame((byte)panelNr, transformedBuffer, colorFormat.get(panelNr));
				sendedFrames += sf;
			}			
		}
	}

	@Override
	public String getConnectionStatus(){
	    if (initialized) {
	        return "Connected to "+lpd6803.getDestIp()+":"+lpd6803.getDestPort();	        
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
		return lpd6803.connected();
	}


	@Override
	public long getErrorCounter() {
		if (initialized) {			
			return lpd6803.getConnectionErrorCounter();
		}
	    return 0;
	}
}
