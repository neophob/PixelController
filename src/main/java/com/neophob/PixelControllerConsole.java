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
package com.neophob;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;

import com.neophob.sematrix.Framerate;
import com.neophob.sematrix.glue.Collector;
import com.neophob.sematrix.glue.FileUtils;
import com.neophob.sematrix.glue.Shuffler;
import com.neophob.sematrix.output.ArduinoOutput;
import com.neophob.sematrix.output.Output;
import com.neophob.sematrix.properties.ApplicationConfigurationHelper;
import com.neophob.sematrix.setup.InitApplication;


/**
 * The Class PixelController.
 *
 * @author michu
 */
public class PixelControllerConsole {  

	/** The log. */
	private static final Logger LOG = Logger.getLogger(PixelControllerConsole.class.getName());

	/** The Constant FPS. */
	public static final int FPS = 25;

	private Collector collector;

	/** The output. */
	private Output output;

	private ApplicationConfigurationHelper applicationConfig;
	private FileUtils fileUtils;
	private Framerate framerate;
	
	/**
	 * 
	 */
	public PixelControllerConsole() {
		LOG.log(Level.INFO, "\n\nPixelController "+getVersion()+" - http://www.pixelinvaders.ch\n\n");	        
		fileUtils = new FileUtils();
		applicationConfig = InitApplication.loadConfiguration(fileUtils);

		LOG.log(Level.INFO, "Create Collector");
		this.collector = Collector.getInstance();

		LOG.log(Level.INFO, "Initialize System");
		this.collector.init(null, fileUtils, applicationConfig);     
		framerate = new Framerate(applicationConfig.parseFps());

		LOG.log(Level.INFO, "Initialize TCP/OSC Server");
//		this.collector.initDaemons(applicationConfig);     

		LOG.log(Level.INFO, "Initialize Output device");
		this.output = InitApplication.getOutputDevice(this.collector, applicationConfig);
		if (this.output==null) {
			throw new IllegalArgumentException("No output device found!");
		}
		this.collector.setOutput(output);

		LOG.log(Level.INFO, "Apply Settings");
		if (applicationConfig.startRandommode()) {
			LOG.log(Level.INFO, "Random Mode enabled");
			Shuffler.manualShuffleStuff();
			this.collector.setRandomMode(true);
		}

		//load saves presets
		int presetNr = applicationConfig.loadPresetOnStart(Collector.NR_OF_PRESET_SLOTS);
		if (presetNr >= 0) {
			presetNr--;
			LOG.log(Level.INFO,"Load preset "+presetNr);
			List<String> present = this.collector.getPresets().get(presetNr).getPresent();
			this.collector.setSelectedPreset(presetNr);
			if (present!=null) { 
				this.collector.setCurrentStatus(present);
			} else {
				LOG.log(Level.WARNING,"Invalid preset load on start value ignored!");
			}
		} 

		LOG.log(Level.INFO, "--- PixelController Setup END ---");
		LOG.log(Level.INFO, "---------------------------------");
		LOG.log(Level.INFO, "");

	}


	public void mainLoop() {		
		LOG.info("enter main loop...");
        long cnt = 0;
        
		while (true) {
    		if (Collector.getInstance().isInPauseMode()) {
    			//no update here, we're in pause mode
    			return;
    		}

    		if (this.output != null && this.output.getClass().isAssignableFrom(ArduinoOutput.class)) {
    			this.output.logStatistics();
    		}

    		// update all generators
    		Collector.getInstance().updateSystem();
    		
    		framerate.waitForFps(cnt++); 
        }
	}


	/**
	 * 
	 * @return
	 */
	public String getVersion() {
		String version = this.getClass().getPackage().getImplementationVersion();
		if (StringUtils.isNotBlank(version)) {
			return "v"+version;
		}
		return "Developer Snapshot"; 
	}

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String args[]) {
		
		System.setProperty("java.awt.headless", "true");
		try {
			new PixelControllerConsole().mainLoop();	        			
		} catch (Exception e) {
			e.printStackTrace();
			LOG.log(Level.SEVERE, "PixelController Exception", e);
		}
	}


}
