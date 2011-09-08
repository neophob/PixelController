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
