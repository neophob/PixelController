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

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import processing.core.PApplet;

import com.neophob.sematrix.glue.Collector;
import com.neophob.sematrix.glue.Shuffler;
import com.neophob.sematrix.output.ArduinoOutput;
import com.neophob.sematrix.output.ArtnetDevice;
import com.neophob.sematrix.output.Lpd6803Device;
import com.neophob.sematrix.output.MatrixEmulator;
import com.neophob.sematrix.output.MiniDmxDevice;
import com.neophob.sematrix.output.NullDevice;
import com.neophob.sematrix.output.Output;
import com.neophob.sematrix.output.OutputDeviceEnum;
import com.neophob.sematrix.output.RainbowduinoDevice;
import com.neophob.sematrix.output.emulatorhelper.InternalDebugWindow;
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

	/** The output. */
	private Output output;
	
	/**
	 * prepare.
	 */
	public void setup() {
	    LOG.log(Level.INFO,"--- PixelController Setup START ---");
	    
	    Properties config = new Properties();
        try {
            config.load(createInput(CONFIG_FILENAME));
            LOG.log(Level.INFO, "Config loaded, {0} entries", config.size());
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Failed to load Config", e);
            throw new IllegalArgumentException("Configuration error!", e);
        }

	    PropertiesHelper ph = new PropertiesHelper(config);
	    
	    
		Collector col = Collector.getInstance(); 				
		col.init(this, FPS, ph);
		frameRate(FPS);
		noSmooth();
						
		OutputDeviceEnum outputDeviceEnum = ph.getOutputDevice();
		try {
			switch (outputDeviceEnum) {
			case PIXELINVADERS:
				this.output = new Lpd6803Device(ph, col.getPixelControllerOutput());
				break;
			case RAINBOWDUINO:
				this.output = new RainbowduinoDevice(ph, col.getPixelControllerOutput());
				break;
			case ARTNET:
				this.output = new ArtnetDevice(ph, col.getPixelControllerOutput());
				break;
			case MINIDMX:
				this.output = new MiniDmxDevice(ph, col.getPixelControllerOutput());
				break;
			case NULL:
				this.output = new NullDevice(ph, col.getPixelControllerOutput());
				break;
			default:
				throw new IllegalArgumentException("Unable to initialize unknown output device: " + outputDeviceEnum);
			}
		} catch (Exception e) {
			LOG.log(Level.SEVERE,"Unable to initialize output device: " + outputDeviceEnum, e);
		}
		
		new MatrixEmulator(ph, col.getPixelControllerOutput(), this.output.getBpp());				
		
		if (ph.getProperty("show.debug.window").equalsIgnoreCase("true")) {
			new InternalDebugWindow(true);	
		}
		
		//start in random mode?
		if (ph.startRandommode()) {
			LOG.log(Level.INFO,"Random Mode enabled");
			Shuffler.manualShuffleStuff();
			col.setRandomMode(true);
		}
		
		LOG.log(Level.INFO,"--- PixelController Setup END ---");
	}

	/* (non-Javadoc)
	 * @see processing.core.PApplet#draw()
	 */
	public void draw() { 
		//update all generators
 
		Collector.getInstance().updateSystem();

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
		PApplet.main(new String[] { "com.neophob.PixelController" });
	}
}
