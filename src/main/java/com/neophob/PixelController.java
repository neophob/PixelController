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

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import processing.core.PApplet;

import com.neophob.sematrix.glue.Collector;
import com.neophob.sematrix.output.ArduinoOutput;
import com.neophob.sematrix.output.ArtnetDevice;
import com.neophob.sematrix.output.Lpd6803Device;
import com.neophob.sematrix.output.MatrixEmulator;
import com.neophob.sematrix.output.Output;
import com.neophob.sematrix.output.OutputDeviceEnum;
import com.neophob.sematrix.output.RainbowduinoDevice;
import com.neophob.sematrix.output.emulatorhelper.NewWindowHelper;
import com.neophob.sematrix.properties.PropertiesHelper;

/**
 * 
 * @author michu
 * 
 * TODO:
 * make image resize option (speed/quality) user selectable
 * make zoom option, usefull for one screen
 *
 */
public class PixelController extends PApplet {

	private static Logger log = Logger.getLogger(PixelController.class.getName());

	private static final long serialVersionUID = -1336765543826338205L;
	
	private static final int DEVICE_SIZE = 8;

	public static final int FPS = 20;
	//96*2*25 = 4800bytes

	Output output;
	
	NewWindowHelper nwh;
	long lastHeartbeat;
	int error=0;
	int frameCounter=0;
	MatrixEmulator osd;

	/**
	 * prepare
	 */
	public void setup() {
		//		ImageIcon titlebaricon = new ImageIcon(loadBytes("logo.jpg"));
		//		super.frame.setIconImage(titlebaricon.getImage()); 
		//		super.frame.setTitle("This is in the titlebar!");

		Collector col = Collector.getInstance(); 
		col.init(this, FPS, DEVICE_SIZE, DEVICE_SIZE);
		frameRate(FPS);
		noSmooth();
		
		osd = new MatrixEmulator(col.getPixelControllerOutput());
		PropertiesHelper ph = PropertiesHelper.getInstance();
		
		OutputDeviceEnum outputDeviceEnum = ph.getOutputDevice();
		try {
			switch (outputDeviceEnum) {
			case LPD6803:
				this.output = new Lpd6803Device(col.getPixelControllerOutput(), ph.getLpdDevice(), ph.getColorFormat());
				break;
			case RAINBOWDUINO:
				this.output = new RainbowduinoDevice(col.getPixelControllerOutput(), ph.getI2cAddr());
				break;
			case ARTNET:
				this.output = new ArtnetDevice(col.getPixelControllerOutput());
				break;
			default:
				throw new IllegalArgumentException("Unable to initialize unknown output device: " + outputDeviceEnum);
			}
		} catch (Exception e) {
			log.log(Level.SEVERE,"Unable to initialize output device: " + outputDeviceEnum, e);
		}
		
		if (ph.getProperty("show.debug.window").equalsIgnoreCase("true")) {
			nwh = new NewWindowHelper(true);	
		}
	}

	@SuppressWarnings("deprecation")
	public void draw() { 
		//update all generators
 
		Collector.getInstance().updateSystem();

		if (this.output != null && this.output.getClass().isAssignableFrom(ArduinoOutput.class)) {
			ArduinoOutput arduinoOutput = (ArduinoOutput) this.output;
			if (arduinoOutput.getArduinoErrorCounter() > 0) {
				this.error = arduinoOutput.getArduinoErrorCounter();
				log.log(Level.SEVERE,"error at: {0}, errorcnt: {1}, buffersize: {2}",
						new Object[] {
							new Date(arduinoOutput.getLatestHeartbeat()).toGMTString(),
							this.error,
							arduinoOutput.getArduinoBufferSize()
						}
				);
			}
		}
		frameCounter++;
	}

	public static void main(String args[]) {
		PApplet.main(new String[] { "com.neophob.PixelController" });
	}
}
