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

import java.util.List;

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
	private RainbowduinoV3[] rainbowduinoV3Devices;

	public RainbowduinoV3Device(ApplicationConfigurationHelper ph, PixelControllerOutput controller) {
		super(OutputDeviceEnum.RAINBOWDUINO_V3, ph, controller, 8);
		
		// initialize internal variables
		List<String> devices = ph.getRainbowduinoV3SerialDevices();
		this.rainbowduinoV3Devices = new RainbowduinoV3[devices.size()];
		GammaTable gammaTable = new GammaTable();
		// construct RainbowduinoV3 instances
		for (int i = 0; i < devices.size(); i++) {
			this.rainbowduinoV3Devices[i] = new RainbowduinoV3(
					devices.get(i),
					gammaTable
			);
		}
	}

	@Override
	public void update() {
		for (int i = 0; i < this.rainbowduinoV3Devices.length; i++) {
			this.rainbowduinoV3Devices[i].sendFrame(super.getBufferForScreen(i));
		}
	}

	@Override
	public void close() {
		for (RainbowduinoV3 rainbowduinoV3 : this.rainbowduinoV3Devices) {
			rainbowduinoV3.close();
		}
	}
}
