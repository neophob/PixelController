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

package com.neophob;

import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import processing.core.PApplet;

import com.neophob.sematrix.color.ColorSet;
import com.neophob.sematrix.glue.Collector;
import com.neophob.sematrix.glue.Shuffler;
import com.neophob.sematrix.jmx.TimeMeasureItemGlobal;
import com.neophob.sematrix.output.AdaVision;
import com.neophob.sematrix.output.ArduinoOutput;
import com.neophob.sematrix.output.ArtnetDevice;
import com.neophob.sematrix.output.MiniDmxDevice;
import com.neophob.sematrix.output.NullDevice;
import com.neophob.sematrix.output.Output;
import com.neophob.sematrix.output.OutputDeviceEnum;
import com.neophob.sematrix.output.PixelInvadersDevice;
import com.neophob.sematrix.output.RainbowduinoDevice;
import com.neophob.sematrix.output.gui.GeneratorGuiCreator;
import com.neophob.sematrix.output.gui.OutputGui;
import com.neophob.sematrix.properties.ConfigConstant;
import com.neophob.sematrix.properties.ApplicationConfigurationHelper;

/**
 * The Class PixelController.
 *
 * @author michu
 */
public class PixelController extends PApplet {  

	/** The log. */
	private static final Logger LOG = Logger.getLogger(PixelController.class.getName());

	/** The Constant APPLICATION_CONFIG_FILENAME. */
	private static final String APPLICATION_CONFIG_FILENAME = "data/config.properties";
	private static final String PALETTE_CONFIG_FILENAME = "data/palette.properties";

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -1336765543826338205L;
	
	/** The Constant FPS. */
	public static final int FPS = 20;
	
	private Collector collector;

	/** The output. */
	private Output output;
	
	private OutputGui matrixEmulator;
	
	/**
	 * 
	 * @return
	 * @throws IllegalArgumentException
	 */
	protected List<ColorSet> getColorPalettes() throws IllegalArgumentException {
		//load palette
		Properties palette = new Properties();
		try {
			palette.load(createInput(PALETTE_CONFIG_FILENAME));
			List<ColorSet> colorSets = ColorSet.loadAllEntries(palette);

			LOG.log(Level.INFO, "ColorSets loaded, {0} entries", colorSets.size());
			return colorSets;
		} catch (Exception e) {
			LOG.log(Level.SEVERE, "Failed to load Config", e);
			throw new IllegalArgumentException("Configuration error!", e);
		}				
	}
	
	/**
	 * 
	 * @return
	 * @throws IllegalArgumentException
	 */
	protected ApplicationConfigurationHelper getAppliactionConfiguration() throws IllegalArgumentException {
		Properties config = new Properties();
		try {
			config.load(createInput(APPLICATION_CONFIG_FILENAME));
			LOG.log(Level.INFO, "Config loaded, {0} entries", config.size());
		} catch (Exception e) {
			LOG.log(Level.SEVERE, "Failed to load Config", e);
			throw new IllegalArgumentException("Configuration error!", e);
		}
		return new ApplicationConfigurationHelper(config);		
	}

	/**
	 * 
	 * @param applicationConfig
	 * @throws IllegalArgumentException
	 */
	protected void getOutputDevice(ApplicationConfigurationHelper applicationConfig) throws IllegalArgumentException {
		OutputDeviceEnum outputDeviceEnum = applicationConfig.getOutputDevice();
		try {
			switch (outputDeviceEnum) {
			case PIXELINVADERS:
				this.output = new PixelInvadersDevice(applicationConfig, this.collector.getPixelControllerOutput());
				break;
			case RAINBOWDUINO:
				this.output = new RainbowduinoDevice(applicationConfig, this.collector.getPixelControllerOutput());
				break;
			case ARTNET:
				this.output = new ArtnetDevice(applicationConfig, this.collector.getPixelControllerOutput());
				break;
			case MINIDMX:
				this.output = new MiniDmxDevice(applicationConfig, this.collector.getPixelControllerOutput());
				break;
			case NULL:
				this.output = new NullDevice(applicationConfig, this.collector.getPixelControllerOutput());
				break;
			case ADAVISION:
				this.output = new AdaVision(applicationConfig, this.collector.getPixelControllerOutput());
				break;
			case TPM2:
				//TODO
			default:
				throw new IllegalArgumentException("Unable to initialize unknown output device: " + outputDeviceEnum);
			}
		} catch (Exception e) {
			LOG.log(Level.SEVERE,"Unable to initialize output device: " + outputDeviceEnum, e);
		}
	}
	
	/**
	 * prepare.
	 */
	public void setup() {
	    LOG.log(Level.INFO, "");
	    LOG.log(Level.INFO, "-----------------------------------");
		LOG.log(Level.INFO, "--- PixelController Setup START ---");

		ApplicationConfigurationHelper applicationConfig = getAppliactionConfiguration();		
		this.collector = Collector.getInstance();
		
		//load palette
		List<ColorSet> colorSets = getColorPalettes();
		this.collector.setColorSets(colorSets);
		this.collector.setCurrentColorSet(0);
		
		this.collector.init(this, applicationConfig);
		
		//set processing related settings
		frameRate(applicationConfig.parseFps());
		noSmooth();
				
		//load output device
		getOutputDevice(applicationConfig);
				
		this.matrixEmulator = new OutputGui(applicationConfig, this.output);
		
		if (applicationConfig.getProperty(ConfigConstant.SHOW_DEBUG_WINDOW).equalsIgnoreCase("true")) {
			new GeneratorGuiCreator(true, applicationConfig.getDebugWindowMaximalXSize());	
		}
		
		//start in random mode?
		if (applicationConfig.startRandommode()) {
			LOG.log(Level.INFO,"Random Mode enabled");
			Shuffler.manualShuffleStuff();
			this.collector.setRandomMode(true);
		}
		
		//load saves presets
		int presetNr = applicationConfig.loadPresetOnStart();
		if (presetNr != -1) {
		    LOG.log(Level.INFO,"Load preset "+presetNr);
	        List<String> present = this.collector.getPresent().get(presetNr).getPresent();
	        if (present!=null) { 
	            this.collector.setCurrentStatus(present);
	        }		    
		}
		
		LOG.log(Level.INFO, "--- PixelController Setup END ---");
		LOG.log(Level.INFO, "---------------------------------");
		LOG.log(Level.INFO, "");
	}

	/* (non-Javadoc)
	 * @see processing.core.PApplet#draw()
	 */
	public void draw() { 
		// update all generators
		Collector.getInstance().updateSystem();
		// update matrixEmulator instance
		long startTime = System.currentTimeMillis();
		this.matrixEmulator.update();
		this.collector.getPixConStat().trackTime(TimeMeasureItemGlobal.MATRIX_EMULATOR_WINDOW, System.currentTimeMillis() - startTime);
		
		if (this.output != null && this.output.getClass().isAssignableFrom(ArduinoOutput.class)) {
			this.output.logStatistics();
		}
	}

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String args[]) {
		PApplet.main(new String[] { PixelController.class.getName().toString() });
	}
}
