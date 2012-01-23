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
import com.neophob.sematrix.output.gui.GeneratorGui;
import com.neophob.sematrix.output.gui.MatrixEmulator;
import com.neophob.sematrix.properties.ConfigConstant;
import com.neophob.sematrix.properties.PropertiesHelper;

/**
 * The Class PixelController.
 *
 * @author michu
 */
public class PixelController extends PApplet {  

	/** The log. */
	private static final Logger LOG = Logger.getLogger(PixelController.class.getName());

	/** The Constant CONFIG_FILENAME. */
	private static final String CONFIG_FILENAME = "data/config.properties";

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -1336765543826338205L;
	
	/** The Constant FPS. */
	public static final int FPS = 20;
	
	private Collector collector;

	/** The output. */
	private Output output;
	
	private MatrixEmulator matrixEmulator;
	
	/**
	 * prepare.
	 */
	public void setup() {
	    LOG.log(Level.INFO, "");
	    LOG.log(Level.INFO, "-----------------------------------");
		LOG.log(Level.INFO, "--- PixelController Setup START ---");

		Properties config = new Properties();
		try {
			config.load(createInput(CONFIG_FILENAME));
			LOG.log(Level.INFO, "Config loaded, {0} entries", config.size());
		} catch (Exception e) {
			LOG.log(Level.SEVERE, "Failed to load Config", e);
			throw new IllegalArgumentException("Configuration error!", e);
		}

		PropertiesHelper ph = new PropertiesHelper(config);

		this.collector = Collector.getInstance();
		this.collector.init(this, ph);
		frameRate(ph.parseFps());
		noSmooth();
		
		OutputDeviceEnum outputDeviceEnum = ph.getOutputDevice();
		try {
			switch (outputDeviceEnum) {
			case PIXELINVADERS:
				this.output = new PixelInvadersDevice(ph, this.collector.getPixelControllerOutput());
				break;
			case RAINBOWDUINO:
				this.output = new RainbowduinoDevice(ph, this.collector.getPixelControllerOutput());
				break;
			case ARTNET:
				this.output = new ArtnetDevice(ph, this.collector.getPixelControllerOutput());
				break;
			case MINIDMX:
				this.output = new MiniDmxDevice(ph, this.collector.getPixelControllerOutput());
				break;
			case NULL:
				this.output = new NullDevice(ph, this.collector.getPixelControllerOutput());
				break;
			case ADAVISION:
				this.output = new AdaVision(ph, this.collector.getPixelControllerOutput());
				break;
			default:
				throw new IllegalArgumentException("Unable to initialize unknown output device: " + outputDeviceEnum);
			}
		} catch (Exception e) {
			LOG.log(Level.SEVERE,"Unable to initialize output device: " + outputDeviceEnum, e);
		}
		
		this.matrixEmulator = new MatrixEmulator(ph, this.output);
		
		if (ph.getProperty(ConfigConstant.SHOW_DEBUG_WINDOW).equalsIgnoreCase("true")) {
			new GeneratorGui(true, ph.getDebugWindowMaximalXSize());	
		}
		
		//start in random mode?
		if (ph.startRandommode()) {
			LOG.log(Level.INFO,"Random Mode enabled");
			Shuffler.manualShuffleStuff();
			this.collector.setRandomMode(true);
		}
		
		int presetNr = ph.loadPresetOnStart();
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
