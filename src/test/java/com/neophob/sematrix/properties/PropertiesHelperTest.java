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

import com.neophob.sematrix.layout.Layout.LayoutName;
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
        ApplicationConfigurationHelper ph = new ApplicationConfigurationHelper(config);

        assertEquals(1, ph.getNrOfScreens());
        assertEquals(0, ph.getDeviceXResolution());
        assertEquals(0, ph.getDeviceYResolution());

        assertEquals(0, ph.getI2cAddr().size());
        assertEquals(0, ph.getLpdDevice().size());
        assertEquals(20, ph.parseFps());
        assertEquals(OutputDeviceEnum.NULL, ph.getOutputDevice());
    }

    @Test
    public void testPixelInvadersConfig() {     
        Properties config = new Properties();
        config.put(ConfigConstant.PIXELINVADERS_ROW1, "ROTATE_180,NO_ROTATE");
        config.put(ConfigConstant.PIXELINVADERS_ROW2, "ROTATE_90,NO_ROTATE");
        ApplicationConfigurationHelper ph = new ApplicationConfigurationHelper(config);

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
        config.put(ConfigConstant.RAINBOWDUINO_ROW1, "5,6");
        config.put(ConfigConstant.RAINBOWDUINO_ROW2, "0x7,8");
        ApplicationConfigurationHelper ph = new ApplicationConfigurationHelper(config);

        assertEquals(4, ph.getNrOfScreens());
        assertEquals(8, ph.getDeviceXResolution());
        assertEquals(8, ph.getDeviceYResolution());

        assertEquals(4, ph.getI2cAddr().size());
        assertEquals(0, ph.getLpdDevice().size());
        assertEquals(OutputDeviceEnum.RAINBOWDUINO, ph.getOutputDevice());
    }

    @Test
    public void testArtnetConfig() {     
        Properties config = new Properties();
        config.put(ConfigConstant.ARTNET_IP, "192.168.1.1");        
        ApplicationConfigurationHelper ph = new ApplicationConfigurationHelper(config);

        assertEquals(1, ph.getNrOfScreens());
        assertEquals(8, ph.getDeviceXResolution());
        assertEquals(8, ph.getDeviceYResolution());
        assertEquals(false, ph.isOutputSnakeCabeling());

        assertEquals(0, ph.getI2cAddr().size());
        assertEquals(0, ph.getLpdDevice().size());
        assertEquals(OutputDeviceEnum.ARTNET, ph.getOutputDevice());
    }    

    @Test
    public void testMiniDmxConfig() {     
        Properties config = new Properties();
        config.put(ConfigConstant.OUTPUT_DEVICE_RESOLUTION_X, "10");
        config.put(ConfigConstant.OUTPUT_DEVICE_RESOLUTION_Y, "13");
        config.put(ConfigConstant.OUTPUT_DEVICE_SNAKE_CABELING, "true");
        config.put(ConfigConstant.MINIDMX_BAUDRATE, "115200");
        ApplicationConfigurationHelper ph = new ApplicationConfigurationHelper(config);

        assertEquals(1, ph.getNrOfScreens());
        assertEquals(10, ph.getDeviceXResolution());
        assertEquals(13, ph.getDeviceYResolution());
        assertEquals(true, ph.isOutputSnakeCabeling());
        
        assertEquals(0, ph.getI2cAddr().size());
        assertEquals(0, ph.getLpdDevice().size());
        assertEquals(OutputDeviceEnum.MINIDMX, ph.getOutputDevice());
    }  
    
    @Test
    public void testNullConfig() {     
        Properties config = new Properties();
        config.put(ConfigConstant.NULLOUTPUT_ROW1, "4");
        config.put(ConfigConstant.NULLOUTPUT_ROW2, "4");
        ApplicationConfigurationHelper ph = new ApplicationConfigurationHelper(config);

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
        config.put(ConfigConstant.RAINBOWDUINO_ROW1, "4");
        config.put(ConfigConstant.ARTNET_IP, "192.168.4.2");
        new ApplicationConfigurationHelper(config);
    }


    @Test(expected = IllegalArgumentException.class)
    public void testMultiple2Config() {     
        Properties config = new Properties();
        config.put(ConfigConstant.RAINBOWDUINO_ROW1, "4");
        config.put(ConfigConstant.PIXELINVADERS_ROW2, "NO_ROTATE");
        new ApplicationConfigurationHelper(config);
    }    

    @Test(expected = IllegalStateException.class)
    public void testLayout() {     
        Properties config = new Properties();
        config.put(ConfigConstant.NULLOUTPUT_ROW1, "4");
        config.put(ConfigConstant.NULLOUTPUT_ROW2, "2");
        new ApplicationConfigurationHelper(config).getLayout();        
    }    

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidCabling() {     
        Properties config = new Properties();
        config.put(ConfigConstant.OUTPUT_MAPPING, "4,6,5,4,2,1,8");
        config.put(ConfigConstant.OUTPUT_DEVICE_SNAKE_CABELING, "true");
        new ApplicationConfigurationHelper(config).getLayout();        
    }    

    @Test
    public void testValidMapping() {     
        Properties config = new Properties();
        config.put(ConfigConstant.OUTPUT_DEVICE_RESOLUTION_X, "2");
        config.put(ConfigConstant.OUTPUT_DEVICE_RESOLUTION_Y, "2");
        config.put(ConfigConstant.OUTPUT_MAPPING, "4,6,5,7");
        config.put(ConfigConstant.ARTNET_IP, "1.1.1.1");        
        new ApplicationConfigurationHelper(config).getLayout();        
    }        

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidMappingCount() {     
        Properties config = new Properties();
        config.put(ConfigConstant.OUTPUT_DEVICE_RESOLUTION_X, "2");
        config.put(ConfigConstant.OUTPUT_DEVICE_RESOLUTION_Y, "2");
        config.put(ConfigConstant.OUTPUT_MAPPING, "4,6,5,4,2,1,8");
        config.put(ConfigConstant.ARTNET_IP, "1.1.1.1");
        new ApplicationConfigurationHelper(config).getLayout();        
    }    

    @Test
    public void testHorizontalLayout() {     
        Properties config = new Properties();
        config.put(ConfigConstant.NULLOUTPUT_ROW1, "3");
        config.put(ConfigConstant.NULLOUTPUT_ROW2, "0");
        ApplicationConfigurationHelper ph = new ApplicationConfigurationHelper(config);        
        assertEquals(3, ph.getNrOfScreens());
        assertEquals(LayoutName.HORIZONTAL, ph.getLayout().getLayoutName());
    }    

    @Test
    public void testBoxLayout() {     
        Properties config = new Properties();
        config.put(ConfigConstant.NULLOUTPUT_ROW1, "3");
        config.put(ConfigConstant.NULLOUTPUT_ROW2, "3");
        ApplicationConfigurationHelper ph = new ApplicationConfigurationHelper(config);        
        assertEquals(6, ph.getNrOfScreens());
        assertEquals(LayoutName.BOX, ph.getLayout().getLayoutName());
    }    
    
    @Test
    public void testLoadPresetOnStartup() {
        Properties config = new Properties();
        ApplicationConfigurationHelper ph = new ApplicationConfigurationHelper(config);
        int presetNr = ph.loadPresetOnStart();
        assertEquals(-1, presetNr);
        
        config.put(ConfigConstant.STARTUP_LOAD_PRESET_NR, "22");
        ph = new ApplicationConfigurationHelper(config);
        presetNr = ph.loadPresetOnStart();
        assertEquals(22, presetNr);
        
        config = new Properties();
        config.put(ConfigConstant.STARTUP_LOAD_PRESET_NR, "2222");
        ph = new ApplicationConfigurationHelper(config);
        presetNr = ph.loadPresetOnStart();
        assertEquals(-1, presetNr);
    }
    
    @Test
    public void testAdaVision() {
        Properties config = new Properties();        
        config.put(ConfigConstant.ADAVISION_DEVICE, "true");
        ApplicationConfigurationHelper ph = new ApplicationConfigurationHelper(config);
        assertEquals(OutputDeviceEnum.NULL, ph.getOutputDevice());
    }
    
    @Test
    public void testAdaVisionCorrect() {
        Properties config = new Properties();        
        config.put(ConfigConstant.ADAVISION_DEVICE, "true");
        config.put(ConfigConstant.OUTPUT_DEVICE_RESOLUTION_X, "15");
        config.put(ConfigConstant.OUTPUT_DEVICE_RESOLUTION_Y, "10");
        ApplicationConfigurationHelper ph = new ApplicationConfigurationHelper(config);
        assertEquals(OutputDeviceEnum.ADAVISION, ph.getOutputDevice());
        assertEquals(null, ph.getAdavisionSerialPort());
    }

    @Test
    public void testAdaVisionSerialPort() {
        Properties config = new Properties();        
        config.put(ConfigConstant.ADAVISION_DEVICE, "true");
        config.put(ConfigConstant.OUTPUT_DEVICE_RESOLUTION_X, "15");
        config.put(ConfigConstant.OUTPUT_DEVICE_RESOLUTION_Y, "10");
        config.put(ConfigConstant.ADAVISION_SERIAL_PORT, "/dev/xxx");
        ApplicationConfigurationHelper ph = new ApplicationConfigurationHelper(config);
        assertEquals(OutputDeviceEnum.ADAVISION, ph.getOutputDevice());
        assertEquals("/dev/xxx", ph.getAdavisionSerialPort());
    }
    
    @Test
    public void testAdaVisionSerialPortSpeed() {
        Properties config = new Properties();        
        config.put(ConfigConstant.ADAVISION_DEVICE, "true");
        config.put(ConfigConstant.OUTPUT_DEVICE_RESOLUTION_X, "15");
        config.put(ConfigConstant.OUTPUT_DEVICE_RESOLUTION_Y, "10");
        ApplicationConfigurationHelper ph = new ApplicationConfigurationHelper(config);
        assertEquals(0, ph.getAdavisionSerialPortSpeed());
        
        config.put(ConfigConstant.ADAVISION_SERIAL_SPEED, "115200");
        ph = new ApplicationConfigurationHelper(config);
        assertEquals(115200, ph.getAdavisionSerialPortSpeed());
    }
    
    @Test
    public void testMissingRgbValue() {
        Properties config = new Properties();
        config.put(ConfigConstant.PIXELINVADERS_ROW1, "ROTATE_180,NO_ROTATE");
        config.put(ConfigConstant.CFG_PANEL_COLOR_ORDER, "RBG");
        ApplicationConfigurationHelper ph = new ApplicationConfigurationHelper(config);

        assertEquals(2, ph.getNrOfScreens());
        assertEquals(ColorFormat.RBG, ph.getColorFormat().get(0));        
        assertEquals(ColorFormat.RGB, ph.getColorFormat().get(1));        
    }

}
