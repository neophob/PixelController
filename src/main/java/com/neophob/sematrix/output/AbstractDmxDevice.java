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

import java.net.InetAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.neophob.sematrix.properties.ApplicationConfigurationHelper;

/**
 * 
 * @author michu
 *
 */
public abstract class AbstractDmxDevice extends OnePanelResolutionAwareOutput {

	private static final Logger LOG = Logger.getLogger(AbstractDmxDevice.class.getName());

	//dmx specific settings
	protected int sequenceID;
	protected int pixelsPerUniverse;
	protected int nrOfUniverse;
	protected int firstUniverseId;
	protected InetAddress targetAdress;

	/** The initialized. */
	protected boolean initialized;

	
	/**
	 * 
	 * @param outputDeviceEnum
	 * @param ph
	 * @param controller
	 * @param bpp
	 */
	public AbstractDmxDevice(OutputDeviceEnum outputDeviceEnum, ApplicationConfigurationHelper ph, PixelControllerOutput controller, int bpp) {
		super(outputDeviceEnum, ph, controller, bpp);

		this.initialized = false;	        
	}
	
	/**
	 * concrete classes need to implement this
	 * 
	 * @param universeId
	 * @param buffer
	 */
	protected abstract void sendBufferToReceiver(int universeId, byte[] buffer);
	
	/**
	 * 
	 */
	protected void calculateNrOfUniverse() {
	    //check how many universe we need
	    this.nrOfUniverse = 1;
	    int bufferSize=xResolution*yResolution;
	    if (bufferSize > pixelsPerUniverse) {
	    	while (bufferSize > pixelsPerUniverse) {
	    		this.nrOfUniverse++;
	    		bufferSize -= pixelsPerUniverse;
	    	}
	    }

        LOG.log(Level.INFO, "\tPixels per universe: "+pixelsPerUniverse);
        LOG.log(Level.INFO, "\tFirst universe ID: "+firstUniverseId);
        LOG.log(Level.INFO, "\t# of universe: "+nrOfUniverse);
        LOG.log(Level.INFO, "\tTarget address: "+targetAdress);		
	}
	
	
    /* (non-Javadoc)
     * @see com.neophob.sematrix.output.Output#update()
     */
	@Override
	public void update() {
		if (this.initialized) {
			if (this.nrOfUniverse == 1) {
				sendBufferToReceiver(this.firstUniverseId, OutputHelper.convertBufferTo24bit(getTransformedBuffer(), colorFormat) );
			} else {
				int[] fullBuffer = getTransformedBuffer();				
				int remainingInt = fullBuffer.length;
				int ofs=0;
				for (int i=0; i<this.nrOfUniverse; i++) {
					int tmp=pixelsPerUniverse;
					if (remainingInt<pixelsPerUniverse) {
						tmp = remainingInt;
					}
					int[] buffer = new int[tmp];
					System.arraycopy(fullBuffer, ofs, buffer, 0, tmp);
					remainingInt-=tmp;
					ofs+=tmp;
					sendBufferToReceiver(this.firstUniverseId+i, OutputHelper.convertBufferTo24bit(buffer, colorFormat) );					
				}
			}			
		}
	}
	
	@Override
    public String getConnectionStatus(){
        if (initialized) {
            return "Target IP: "+targetAdress+", Nr. of universe: "+nrOfUniverse;            
        }
        return "Not connected!";
    }
	
	@Override
    public boolean isSupportConnectionState() {
        return true;
    }
	
    @Override
    public boolean isConnected() {
        return initialized;
    }

}
