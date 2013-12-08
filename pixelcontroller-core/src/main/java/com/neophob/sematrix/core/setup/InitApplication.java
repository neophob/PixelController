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

package com.neophob.sematrix.core.setup;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.neophob.sematrix.core.glue.Collector;
import com.neophob.sematrix.core.glue.FileUtils;
import com.neophob.sematrix.core.glue.Shuffler;
import com.neophob.sematrix.core.output.ArtnetDevice;
import com.neophob.sematrix.core.output.E1_31Device;
import com.neophob.sematrix.core.output.MiniDmxDevice;
import com.neophob.sematrix.core.output.NullDevice;
import com.neophob.sematrix.core.output.Output;
import com.neophob.sematrix.core.output.OutputDeviceEnum;
import com.neophob.sematrix.core.output.PixelInvadersNetDevice;
import com.neophob.sematrix.core.output.PixelInvadersSerialDevice;
import com.neophob.sematrix.core.output.RainbowduinoV2Device;
import com.neophob.sematrix.core.output.RainbowduinoV3Device;
import com.neophob.sematrix.core.output.StealthDevice;
import com.neophob.sematrix.core.output.Tpm2;
import com.neophob.sematrix.core.output.Tpm2Net;
import com.neophob.sematrix.core.output.UdpDevice;
import com.neophob.sematrix.core.preset.PresetServiceImpl;
import com.neophob.sematrix.core.properties.ApplicationConfigurationHelper;

/**
 * @author mvogt
 *
 */
public abstract class InitApplication {

	private static final Logger LOG = Logger.getLogger(InitApplication.class.getName());

	private static final String APPLICATION_CONFIG_FILENAME = "config.properties";


	/**
	 * load and parse configuration file
	 * 
	 * @param papplet
	 * @return
	 * @throws IllegalArgumentException
	 */
	public static ApplicationConfigurationHelper loadConfiguration(FileUtils fileUtils) throws IllegalArgumentException {
		Properties config = new Properties();
		InputStream is = null;
		String fileToLoad = fileUtils.getDataDir()+File.separator+APPLICATION_CONFIG_FILENAME;
		try {
			is = new FileInputStream(fileToLoad);
			config.load(is);            
			LOG.log(Level.INFO, "Config loaded, {0} entries", config.size());
		} catch (Exception e) {
			String error = "Failed to open the configfile "+fileToLoad;
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
	 * @param applicationConfig
	 * @throws IllegalArgumentException
	 */
	public static Output getOutputDevice(Collector collector, ApplicationConfigurationHelper applicationConfig) throws IllegalArgumentException {
		OutputDeviceEnum outputDeviceEnum = applicationConfig.getOutputDevice();
		Output output = null;
		try {
			switch (outputDeviceEnum) {
			case PIXELINVADERS:
				output = new PixelInvadersSerialDevice(applicationConfig, collector.getNrOfScreens());
				break;
			case PIXELINVADERS_NET:
				output = new PixelInvadersNetDevice(applicationConfig, collector.getNrOfScreens());
				break;            	
			case STEALTH:
				output = new StealthDevice(applicationConfig, collector.getNrOfScreens());
				break;
			case RAINBOWDUINO_V2:
				output = new RainbowduinoV2Device(applicationConfig);
				break;
			case RAINBOWDUINO_V3:
				output = new RainbowduinoV3Device(applicationConfig);
				break;
			case ARTNET:
				output = new ArtnetDevice(applicationConfig, collector.getNrOfScreens());
				break;
			case E1_31:
				output = new E1_31Device(applicationConfig, collector.getNrOfScreens());
				break;            	
			case MINIDMX:
				output = new MiniDmxDevice(applicationConfig);
				break;
			case NULL:
				output = new NullDevice(applicationConfig);
				break;
			case UDP:
				output = new UdpDevice(applicationConfig);
				break;
			case TPM2:
				output = new Tpm2(applicationConfig);
				break;
			case TPM2NET:
				output = new Tpm2Net(applicationConfig);                
				break;
			default:
				throw new IllegalArgumentException("Unable to initialize unknown output device: " + outputDeviceEnum);
			}

			collector.getPixelControllerOutput().addOutput(output);

		} catch (Exception e) {
			LOG.log(Level.SEVERE,"\n\nERROR: Unable to initialize output device: " + outputDeviceEnum, e);
		}

		return output;
	}

	/**
	 * 
	 * @param collector
	 * @param applicationConfig
	 */
	public static void setupInitialConfig(Collector collector, ApplicationConfigurationHelper applicationConfig) {
		//start in random mode?
		if (applicationConfig.startRandommode()) {
			LOG.log(Level.INFO, "Random Mode enabled");
			Shuffler.manualShuffleStuff();
			collector.setRandomMode(true);
		}

		//load saves presets
		int presetNr = applicationConfig.loadPresetOnStart();
		if (presetNr < 0 || presetNr >= PresetServiceImpl.NR_OF_PRESET_SLOTS) {
			presetNr=0;
		}
		LOG.log(Level.INFO,"Load preset "+presetNr);
		//TODO fixme
		List<String> preset = collector.getPresets().get(presetNr).getPresent();
		collector.setSelectedPreset(presetNr);
		if (preset!=null) { 
			collector.setCurrentStatus(preset);
		} else {
			LOG.log(Level.WARNING,"Invalid preset load on start value ignored!");
		}
	}


}
