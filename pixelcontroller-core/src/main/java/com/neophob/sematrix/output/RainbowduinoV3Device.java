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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.neophob.sematrix.properties.ApplicationConfigurationHelper;

import de.programmerspain.rv3sf.api.GammaTable;
import de.programmerspain.rv3sf.api.RainbowduinoV3;

/**
 * An adapter implementation against the 'rainbowduino-v3-streaming-firmware'
 * available at https://code.google.com/p/rainbowduino-v3-streaming-firmware/
 * 
 * @author Markus Lang (m@rkus-lang.de) | http://programmers-pain.de/ | https://code.google.com/p/rainbowduino-v3-streaming-firmware/
 *
 */
public class RainbowduinoV3Device extends Output {
    
    private static final Logger LOG = Logger.getLogger(RainbowduinoV3Device.class.getName());
    
	private RainbowduinoV3[] rainbowduinoV3Devices;
	private boolean initialized =  false;

	public RainbowduinoV3Device(ApplicationConfigurationHelper ph) {
		super(OutputDeviceEnum.RAINBOWDUINO_V3, ph, 8);
		
		// initialize internal variables
		try {
	        List<String> tmp = ph.getRainbowduinoV3SerialDevices();
	        List<String> devices = new ArrayList<String>();
	        for (String s: tmp) {
	            //convert os dependent serial port names
	            devices.add(OutputHelper.getSerialPortName(s));
	        }

	        //this counter is used to track serial port initialization errors
	        int errorCounter=0;
	        
	        this.rainbowduinoV3Devices = new RainbowduinoV3[devices.size()];
	        GammaTable gammaTable = new GammaTable();
	        // construct RainbowduinoV3 instances
	        for (int i = 0; i < devices.size(); i++) {
	        	LOG.log(Level.INFO, "Try to open serial port "+devices.get(i));
	            this.rainbowduinoV3Devices[i] = new RainbowduinoV3(devices.get(i), gammaTable);
	            if (!this.rainbowduinoV3Devices[i].isInitialized()) {
	            	errorCounter++;
	            }
	        }
	        
	        if (errorCounter==0) {
		        initialized = true;	        	
		        LOG.log(Level.INFO, "Rainbowduino output initialized sucessfully");
	        } else {
	        	LOG.log(Level.WARNING, "Failed to initialize Rainbowduino output! # of serial ports in error state: "+errorCounter);
	        }
		} catch (Exception e) {
			//for example gnu.io.NoSuchPortException
			LOG.log(Level.SEVERE, "Failed open serial port, check your config file", e);
		} catch (Throwable t) {
		    LOG.log(Level.SEVERE, "\n\n\n\nSERIOUS ERROR, check your RXTX installation!", t);
		}
	}

	@Override
	public void update() {
	    if (!initialized) {
	        return;
	    }
	    
		for (int i = 0; i < this.rainbowduinoV3Devices.length; i++) {
			this.rainbowduinoV3Devices[i].sendFrame(super.getBufferForScreen(i));
		}
	}

	@Override
	public void close() {
        if (!initialized) {
            return;
        }
        
		for (RainbowduinoV3 rainbowduinoV3 : this.rainbowduinoV3Devices) {
			rainbowduinoV3.close();
		}
	}
	
    @Override
    public boolean isSupportConnectionState() {
        return true;
    }
    
    @Override
    public boolean isConnected() {
        if (initialized) {
            for (int i = 0; i < this.rainbowduinoV3Devices.length; i++) {
                //of at least one device is not initialized, report it
                if (!this.rainbowduinoV3Devices[i].isInitialized()) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
    
    @Override
    public long getErrorCounter() {
        if (initialized) {          
            long cnt = 0;
            for (int i = 0; i < this.rainbowduinoV3Devices.length; i++) {
                cnt += this.rainbowduinoV3Devices[i].getErrorCounter();
            }
            return cnt;
        }
        return 0;
    }

	
}
