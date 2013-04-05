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

import static org.junit.Assert.assertTrue;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Test;

import processing.core.PApplet;

import com.neophob.sematrix.fader.PixelControllerFader;
import com.neophob.sematrix.glue.Collector;

/**
 * test start
 * @author michu
 *
 */
public class PixelControllerTest {

	/*class TestProcessingclass extends PApplet {
		public void setup() {
		    Properties config = new Properties();
	        config.put(ConfigConstant.NULLOUTPUT_ROW1, "1");
	        config.put(ConfigConstant.NULLOUTPUT_ROW2, "0");
		    ApplicationConfigurationHelper ph = new ApplicationConfigurationHelper(config);
			Collector.getInstance().init(this, ph);
			LOG.log(Level.INFO, "TestProcessingclass initialized");
		}
	}*/
	
    private static final Logger LOG = Logger.getLogger(PixelControllerTest.class.getName());

	@Test
	public void testMain() {
		//Jenkins is headless, so this test would not work
		if (!java.awt.GraphicsEnvironment.isHeadless()) {
		    LOG.log(Level.INFO,"public void setup");			
			PApplet.main(new String[] { "com.neophob.PixelController" });			
		}
	}

	
	@Test
	public void testCollector() {
		assertTrue(Collector.getInstance().getPresets().size() > 0);
		assertTrue(PixelControllerFader.getFaderCount()>3);
	}
	

}
