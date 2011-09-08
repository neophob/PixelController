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

package com.neophob.sematrix.properties;

import static org.junit.Assert.assertEquals;

import java.util.Properties;

import org.junit.Test;

import com.neophob.sematrix.output.OutputDeviceEnum;

/**
 * test start
 * @author michu
 *
 */
public class PropertiesHelperTest {

    @Test
    public void testEmptyConfig() {	    
        Properties config = new Properties();     
        PropertiesHelper ph = new PropertiesHelper(config);

        assertEquals(1, ph.getNrOfScreens());
        assertEquals(8, ph.getDeviceXResolution());
        assertEquals(8, ph.getDeviceYResolution());

        assertEquals(0, ph.getI2cAddr().size());
        assertEquals(0, ph.getLpdDevice().size());
        assertEquals(OutputDeviceEnum.NULL, ph.getOutputDevice());
    }

    @Test
    public void testPixelInvadersConfig() {     
        Properties config = new Properties();
        config.put(ConfigConstants.PIXELINVADERS_ROW1, "ROTATE_180,NO_ROTATE");
        config.put(ConfigConstants.PIXELINVADERS_ROW2, "ROTATE_90,NO_ROTATE");
        PropertiesHelper ph = new PropertiesHelper(config);

        assertEquals(4, ph.getNrOfScreens());
        assertEquals(8, ph.getDeviceXResolution());
        assertEquals(8, ph.getDeviceYResolution());

        assertEquals(0, ph.getI2cAddr().size());
        assertEquals(4, ph.getLpdDevice().size());
        assertEquals(OutputDeviceEnum.PIXELINVADERS, ph.getOutputDevice());
    }

    @Test
    public void testRainbowduinosConfig() {     
        Properties config = new Properties();
        config.put(ConfigConstants.RAINBOWDUINO_ROW1, "5,6");
        config.put(ConfigConstants.RAINBOWDUINO_ROW2, "0x7,8");
        PropertiesHelper ph = new PropertiesHelper(config);

        assertEquals(4, ph.getNrOfScreens());
        assertEquals(8, ph.getDeviceXResolution());
        assertEquals(8, ph.getDeviceYResolution());

        assertEquals(4, ph.getI2cAddr().size());
        assertEquals(0, ph.getLpdDevice().size());
        assertEquals(OutputDeviceEnum.RAINBOWDUINO, ph.getOutputDevice());
    }

    @Test
    public void testMiniDmxConfig() {     
        Properties config = new Properties();
        config.put(ConfigConstants.MINIDMX_RESOLUTION_X, "10");
        config.put(ConfigConstants.MINIDMX_RESOLUTION_Y, "13");
        PropertiesHelper ph = new PropertiesHelper(config);

        assertEquals(1, ph.getNrOfScreens());
        assertEquals(10, ph.getDeviceXResolution());
        assertEquals(13, ph.getDeviceYResolution());

        assertEquals(0, ph.getI2cAddr().size());
        assertEquals(0, ph.getLpdDevice().size());
        assertEquals(OutputDeviceEnum.MINIDMX, ph.getOutputDevice());
    }    

    @Test
    public void testNullConfig() {     
        Properties config = new Properties();
        config.put(ConfigConstants.NULLOUTPUT_ROW1, "4");
        config.put(ConfigConstants.NULLOUTPUT_ROW2, "4");
        PropertiesHelper ph = new PropertiesHelper(config);

        assertEquals(8, ph.getNrOfScreens());
        assertEquals(8, ph.getDeviceXResolution());
        assertEquals(8, ph.getDeviceYResolution());

        assertEquals(0, ph.getI2cAddr().size());
        assertEquals(0, ph.getLpdDevice().size());
        assertEquals(OutputDeviceEnum.NULL, ph.getOutputDevice());
    }     

    @Test(expected = IllegalArgumentException.class)
    public void testMultipleConfig() {     
        Properties config = new Properties();
        config.put(ConfigConstants.RAINBOWDUINO_ROW1, "4");
        config.put(ConfigConstants.MINIDMX_RESOLUTION_X, "13");
        config.put(ConfigConstants.MINIDMX_RESOLUTION_Y, "1");
        new PropertiesHelper(config);
    }


    @Test(expected = IllegalArgumentException.class)
    public void testMultiple2Config() {     
        Properties config = new Properties();
        config.put(ConfigConstants.RAINBOWDUINO_ROW1, "4");
        config.put(ConfigConstants.PIXELINVADERS_ROW2, "NO_ROTATE");
        new PropertiesHelper(config);
    }    

}
