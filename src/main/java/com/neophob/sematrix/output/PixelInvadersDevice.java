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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.neophob.sematrix.glue.Collector;
import com.neophob.sematrix.output.pixelinvaders.Lpd6803Common;
import com.neophob.sematrix.properties.ApplicationConfigurationHelper;
import com.neophob.sematrix.properties.ColorFormat;
import com.neophob.sematrix.properties.DeviceConfig;

/**
 * Send data to the PixelInvaders Device.
 * A PixelInvaders Panel is always 8x8 but supports multiple panels
 *
 * @author michu
 */
public abstract class PixelInvadersDevice extends Output {

	private static final Logger LOG = Logger.getLogger(PixelInvadersDevice.class.getName());
	
	/** The display options, does the buffer needs to be flipped? rotated? */
	protected List<DeviceConfig> displayOptions;

	/** The output color format. */
	protected List<ColorFormat> colorFormat;

	/** define how the panels are arranged */
	protected List<Integer> panelOrder;

	//primitive arrays can not added to a map, and autoboxing do not work
	protected Map<Integer, Object> transformedBuffer = new HashMap<Integer, Object>(); 

	protected boolean initialized = false;

	protected long sentFrames = 0;
	protected long sentBytes = 0;
	protected long ignoredFrames = 0;
	protected long errorFrames = 0;
	
	private Lpd6803Common lpd6803;
	
	/**
	 * 
	 * @param outputDeviceEnum
	 * @param ph
	 * @param controller
	 * @param bpp
	 */
	public PixelInvadersDevice(OutputDeviceEnum outputDeviceEnum, ApplicationConfigurationHelper ph,
			PixelControllerOutput controller, int bpp) {
		super(outputDeviceEnum, ph, controller, bpp);
		
		this.displayOptions = ph.getLpdDevice();
		this.colorFormat = ph.getColorFormat();
		this.panelOrder = ph.getPanelOrder();
		
		this.initialized = false;
		this.lpd6803 = null;
	}

	
	
	public void setLpd6803(Lpd6803Common lpd6803) {
		this.lpd6803 = lpd6803;
	}



	/**
	 * 
	 * @param lpd6803
	 * @param o
	 */
	public void sendPayload() {
		this.transformedBuffer.clear();

		int totalFrames = 0;
		//step 1, check how many data packages need to send
		for (int ofs=0; ofs<Collector.getInstance().getNrOfScreens(); ofs++) {
			//draw only on available screens!

			//get the effective panel buffer
			int panelNr = this.panelOrder.get(ofs);

			int[] bfr = 
					RotateBuffer.transformImage(super.getBufferForScreen(ofs), displayOptions.get(panelNr),
							lpd6803.getNrOfLedHorizontal(), lpd6803.getNrOfLedVertical());

			bfr = OutputHelper.flipSecondScanline(bfr, lpd6803.getNrOfLedHorizontal(), lpd6803.getNrOfLedVertical());

			if (lpd6803.didFrameChange((byte)ofs, bfr)) {
				this.transformedBuffer.put(panelNr, bfr);
				totalFrames++;
			} else {
				ignoredFrames++;
			}
		}

		//step 2, send data out
		for (Map.Entry<Integer, Object> entry: this.transformedBuffer.entrySet()) {
			int panelNr = entry.getKey();
			int[] data = (int[])entry.getValue();
			int sendedBytes = lpd6803.sendRgbFrame((byte)panelNr, data, colorFormat.get(panelNr), totalFrames);
			if (sendedBytes>0) {
				sentBytes += sendedBytes;
				sentFrames++;
			} else {
				errorFrames++;
			}								
		}
		
		if ((sentFrames+ignoredFrames)%2000==0) {
			float f = sentFrames+ignoredFrames;
			float result = (100.0f/f)*sentFrames;
			LOG.log(Level.INFO, "sent frames: {0}% ({1}/{2}, total {3}kb), errors: {4}", 
					new Object[] {result, sentFrames, ignoredFrames, (sentBytes/1024), (errorFrames+lpd6803.getConnectionErrorCounter())});				
		}		
	}
	
	@Override
	public long getErrorCounter() {
		if (this.lpd6803==null) {
			return errorFrames;
		}
	    return errorFrames+lpd6803.getConnectionErrorCounter();
	}
}
