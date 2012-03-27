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

import java.util.logging.Level;
import java.util.logging.Logger;

import com.neophob.sematrix.glue.Collector;
import com.neophob.sematrix.output.tpm2.Tpm2Protocol;
import com.neophob.sematrix.output.tpm2.Tpm2Serial;
import com.neophob.sematrix.properties.PropertiesHelper;

/**
 * Send data to a Tpm2 Device. this protocol is a successor of miniDMX aand
 * created by people at the ledstyles.de forum.
 * 
 * There is only ONE Matrix supported per output.
 *
 * @author michu
 */
public class Tpm2 extends OnePanelResolutionAwareOutput {

	/** The log. */
	private static final Logger LOG = Logger.getLogger(Tpm2.class.getName());
			
	private Tpm2Serial tpm2;
	
	/**
	 * init the mini dmx devices.
	 *
	 * @param controller the controller
	 */
	public Tpm2(PropertiesHelper ph, PixelControllerOutput controller) {
		super(OutputDeviceEnum.TPM2, ph, controller, 8);
		
		int baud = ph.parseMiniDmxBaudRate();
		if (baud==0) {
		    //set default
		    baud = 115200;
		}

		this.initialized = false;
		try {
			tpm2 = new Tpm2Serial(Collector.getInstance().getPapplet(), this.xResolution*this.yResolution*3, baud);
			this.initialized = true;
		} catch (NoSerialPortFoundException e) {
			LOG.log(Level.WARNING, "failed to initialize serial port!");
		}
	}
	

	/* (non-Javadoc)
	 * @see com.neophob.sematrix.output.Output#update()
	 */
	public void update() {		
		if (initialized) {					
			tpm2.sendFrame(Tpm2Protocol.doProtocol(getTransformedBuffer(), colorFormat));
			//miniDmx.sendRgbFrame(getTransformedBuffer(), colorFormat);
		}
	}


	
	/* (non-Javadoc)
	 * @see com.neophob.sematrix.output.Output#close()
	 */
	@Override
	public void close() {
		if (initialized) {
			//miniDmx.dispose();			
		}
	}

}
