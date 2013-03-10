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

package com.neophob.sematrix.setup;

import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import processing.core.PApplet;

import com.neophob.sematrix.color.ColorSet;
import com.neophob.sematrix.glue.Collector;
import com.neophob.sematrix.output.AdaVision;
import com.neophob.sematrix.output.ArtnetDevice;
import com.neophob.sematrix.output.E1_31Device;
import com.neophob.sematrix.output.MiniDmxDevice;
import com.neophob.sematrix.output.NullDevice;
import com.neophob.sematrix.output.Output;
import com.neophob.sematrix.output.OutputDeviceEnum;
import com.neophob.sematrix.output.PixelInvadersDevice;
import com.neophob.sematrix.output.PixelInvadersNetDevice;
import com.neophob.sematrix.output.RainbowduinoV2Device;
import com.neophob.sematrix.output.RainbowduinoV3Device;
import com.neophob.sematrix.output.StealthDevice;
import com.neophob.sematrix.output.Tpm2;
import com.neophob.sematrix.output.Tpm2Net;
import com.neophob.sematrix.output.UdpDevice;
import com.neophob.sematrix.properties.ApplicationConfigurationHelper;

/**
 * @author mvogt
 *
 */
public class InitApplication {

    private static final Logger LOG = Logger.getLogger(InitApplication.class.getName());
    
    private static final String APPLICATION_CONFIG_FILENAME = "data/config.properties";
    private static final String PALETTE_CONFIG_FILENAME = "data/palette.properties";

    
    /**
     * load and parse configuration file
     * 
     * @param papplet
     * @return
     * @throws IllegalArgumentException
     */
    public static ApplicationConfigurationHelper loadConfiguration(PApplet papplet) throws IllegalArgumentException {
        Properties config = new Properties();
        InputStream is = null;
        try {
        	is = papplet.createInput(APPLICATION_CONFIG_FILENAME);        	
            config.load(is);            
            LOG.log(Level.INFO, "Config loaded, {0} entries", config.size());
        } catch (Exception e) {
        	String error = "Failed to open the configfile "+APPLICATION_CONFIG_FILENAME;
            LOG.log(Level.SEVERE, error, e);
            throw new IllegalArgumentException(error);
        } finally {
        	try {
        		if (is!=null) {
        			is.close();        	
        		}
        	} catch (Exception e) {
        		//ignored
        	}
        }
        
        try {
            return new ApplicationConfigurationHelper(config);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Configuration Error: ", e);
            throw new IllegalArgumentException(e);
        }
    }
    
    
    /**
     * 
     * @return
     * @throws IllegalArgumentException
     */
    public static List<ColorSet> getColorPalettes(PApplet papplet) throws IllegalArgumentException {
        //load palette
        Properties palette = new Properties();
        
        InputStream is = null;
        try {
        	is = papplet.createInput(PALETTE_CONFIG_FILENAME);
            palette.load(is);
            List<ColorSet> colorSets = ColorSet.loadAllEntries(palette);

            LOG.log(Level.INFO, "ColorSets loaded, {0} entries", colorSets.size());
            return colorSets;
        } catch (Exception e) {
            String error = "Failed to load the palette config file "+PALETTE_CONFIG_FILENAME;
            LOG.log(Level.SEVERE, error, e);
            throw new IllegalArgumentException(error, e);
        } finally {
        	try {
        		if (is!=null) {
        			is.close();        	
        		}
        	} catch (Exception e) {
        		//ignored
        	}
        }

    }
    
    
    /**
     * 
     * @param applicationConfig
     * @throws IllegalArgumentException
     */
    public static Output getOutputDevice(Collector collector, ApplicationConfigurationHelper applicationConfig) throws IllegalArgumentException {
        OutputDeviceEnum outputDeviceEnum = applicationConfig.getOutputDevice();
        Output output = null;
        try {
            switch (outputDeviceEnum) {
            case PIXELINVADERS:
                output = new PixelInvadersDevice(applicationConfig, collector.getPixelControllerOutput());
                break;
            case PIXELINVADERS_NET:
                output = new PixelInvadersNetDevice(applicationConfig, collector.getPixelControllerOutput());
                break;            	
            case STEALTH:
                output = new StealthDevice(applicationConfig, collector.getPixelControllerOutput());
                break;
            case RAINBOWDUINO_V2:
                output = new RainbowduinoV2Device(applicationConfig, collector.getPixelControllerOutput());
                break;
            case RAINBOWDUINO_V3:
                output = new RainbowduinoV3Device(applicationConfig, collector.getPixelControllerOutput());
                break;
            case ARTNET:
                output = new ArtnetDevice(applicationConfig, collector.getPixelControllerOutput());
                break;
            case E1_31:
                output = new E1_31Device(applicationConfig, collector.getPixelControllerOutput());
                break;            	
            case MINIDMX:
                output = new MiniDmxDevice(applicationConfig, collector.getPixelControllerOutput());
                break;
            case NULL:
                output = new NullDevice(applicationConfig, collector.getPixelControllerOutput());
                break;
            case ADAVISION:
                output = new AdaVision(applicationConfig, collector.getPixelControllerOutput());
                break;
            case UDP:
                output = new UdpDevice(applicationConfig, collector.getPixelControllerOutput());
                break;
            case TPM2:
                output = new Tpm2(applicationConfig, collector.getPixelControllerOutput());
                break;
            case TPM2NET:
                output = new Tpm2Net(applicationConfig, collector.getPixelControllerOutput());                
                break;
            default:
                throw new IllegalArgumentException("Unable to initialize unknown output device: " + outputDeviceEnum);
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE,"\n\nERROR: Unable to initialize output device: " + outputDeviceEnum, e);
        }
        
        return output;
    }
    
    
    
}
