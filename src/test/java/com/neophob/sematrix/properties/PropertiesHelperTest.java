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
import static org.junit.Assert.assertTrue;

import java.util.List;
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
        assertEquals(8, ph.getDeviceXResolution());
        assertEquals(8, ph.getDeviceYResolution());

        assertEquals(0, ph.getI2cAddr().size());
        assertEquals(0, ph.getLpdDevice().size());
        assertEquals(20, ph.parseFps());
        assertEquals(OutputDeviceEnum.NULL, ph.getOutputDevice());
    }

    @Test
    public void testPixelInvadersDefaultConfig() {     
        Properties config = new Properties();
        config.put(ConfigConstant.PIXELINVADERS_ROW1, "ROTATE_180,NO_ROTATE");
        config.put(ConfigConstant.PIXELINVADERS_ROW2, "ROTATE_90,NO_ROTATE");       
        ApplicationConfigurationHelper ph = new ApplicationConfigurationHelper(config);
        
        List<Integer> order = ph.getPanelOrder(); 
        assertEquals(Integer.valueOf(0), order.get(0));
        assertEquals(Integer.valueOf(1), order.get(1));
        assertEquals(Integer.valueOf(2), order.get(2));
        assertEquals(Integer.valueOf(3), order.get(3));
        
        List<ColorFormat> colorFormat = ph.getColorFormat();
        assertEquals(ColorFormat.RGB, colorFormat.get(0));
        assertEquals(ColorFormat.RGB, colorFormat.get(1));
        assertEquals(ColorFormat.RGB, colorFormat.get(2));
        assertEquals(ColorFormat.RGB, colorFormat.get(3));
    }
    
    @Test
    public void testPixelInvadersConfig() {     
        Properties config = new Properties();
        config.put(ConfigConstant.PIXELINVADERS_ROW1, "ROTATE_180, NO_ROTATE");
        config.put(ConfigConstant.PIXELINVADERS_ROW2, "ROTATE_90, NO_ROTATE");
        config.put(ConfigConstant.PIXELINVADERS_PANEL_ORDER, "0,3, 1,2");
        ApplicationConfigurationHelper ph = new ApplicationConfigurationHelper(config);

        assertEquals(4, ph.getNrOfScreens());
        assertEquals(8, ph.getDeviceXResolution());
        assertEquals(8, ph.getDeviceYResolution());
        
        List<Integer> order = ph.getPanelOrder(); 
        assertEquals(Integer.valueOf(0), order.get(0));
        assertEquals(Integer.valueOf(3), order.get(1));
        assertEquals(Integer.valueOf(1), order.get(2));
        assertEquals(Integer.valueOf(2), order.get(3));
        
        assertEquals(0, ph.getI2cAddr().size());
        assertEquals(4, ph.getLpdDevice().size());
        assertEquals(OutputDeviceEnum.PIXELINVADERS, ph.getOutputDevice());
    }

    @Test
    public void testPixelInvadersAdvancedConfig() {     
        Properties config = new Properties();
        config.put(ConfigConstant.PIXELINVADERS_ROW1, "ROTATE_180_FLIPPEDY,NO_ROTATE,NO_ROTATE");
        config.put(ConfigConstant.PIXELINVADERS_ROW2, "ROTATE_180_FLIPPEDY,NO_ROTATE,NO_ROTATE");
        config.put(ConfigConstant.PIXELINVADERS_PANEL_ORDER, "0,3,1,4,2,5");
        ApplicationConfigurationHelper ph = new ApplicationConfigurationHelper(config);

        assertEquals(6, ph.getNrOfScreens());
        assertEquals(8, ph.getDeviceXResolution());
        assertEquals(8, ph.getDeviceYResolution());
        
        List<ColorFormat> colorFormat = ph.getColorFormat();
        List<Integer> order = ph.getPanelOrder();
        List<DeviceConfig> displayOptions = ph.getLpdDevice();
        
        for (int ofs=0; ofs<ph.getNrOfScreens(); ofs++) {
            int panelNr = order.get(ofs);            
            System.out.println("visual "+ofs+", panel ofs: "+panelNr+", layout: "+displayOptions.get(panelNr)+
            		" colorformat: "+colorFormat.get(panelNr));
        }
    }
    
    
    @Test
    public void testInvalidPixelInvadersConfigOne() {     
        Properties config = new Properties();
        config.put(ConfigConstant.PIXELINVADERS_ROW1, "ROTATE_180,NO_ROTATE");
        config.put(ConfigConstant.PIXELINVADERS_ROW2, "ROTATE_90,NO_ROTATE");
        config.put(ConfigConstant.PIXELINVADERS_PANEL_ORDER, "0,4,1,2");
        ApplicationConfigurationHelper ph = new ApplicationConfigurationHelper(config);

        //panel order is ignored, due invalid value
        List<Integer> order = ph.getPanelOrder(); 
        assertEquals(Integer.valueOf(0), order.get(0));
        assertEquals(Integer.valueOf(1), order.get(1));
        assertEquals(Integer.valueOf(2), order.get(2));
        assertEquals(Integer.valueOf(3), order.get(3));
    }

    @Test
    public void testInvalidPixelInvadersConfigTwo() {     
        Properties config = new Properties();
        config.put(ConfigConstant.PIXELINVADERS_ROW1, "ROTATE_180,NO_ROTATE");
        config.put(ConfigConstant.PIXELINVADERS_ROW2, "ROTATE_90,NO_ROTATE");
        config.put(ConfigConstant.PIXELINVADERS_PANEL_ORDER, "0");
        ApplicationConfigurationHelper ph = new ApplicationConfigurationHelper(config);

        //panel order is ignored, due invalid value
        List<Integer> order = ph.getPanelOrder(); 
        assertEquals(Integer.valueOf(0), order.get(0));
        assertEquals(Integer.valueOf(1), order.get(1));
        assertEquals(Integer.valueOf(2), order.get(2));
        assertEquals(Integer.valueOf(3), order.get(3));
    }

    @Test
    public void testRainbowduinosConfig() {     
        Properties config = new Properties();
        config.put(ConfigConstant.RAINBOWDUINO_V2_ROW1, "5, 6");
        config.put(ConfigConstant.RAINBOWDUINO_V2_ROW2, "0x7,8");
        ApplicationConfigurationHelper ph = new ApplicationConfigurationHelper(config);

        assertEquals(4, ph.getNrOfScreens());
        assertEquals(8, ph.getDeviceXResolution());
        assertEquals(8, ph.getDeviceYResolution());

        assertEquals(4, ph.getI2cAddr().size());
        assertEquals(0, ph.getLpdDevice().size());
        assertEquals(OutputDeviceEnum.RAINBOWDUINO_V2, ph.getOutputDevice());
    }

    @Test
    public void testRainbowduinosV3Config() {     
        Properties config = new Properties();
        config.put(ConfigConstant.RAINBOWDUINO_V3_ROW1, "/dev/aaa,/dev/bbb");
        config.put(ConfigConstant.RAINBOWDUINO_V3_ROW2, "/dev/ccc, /dev/ddd");
        ApplicationConfigurationHelper ph = new ApplicationConfigurationHelper(config);

        assertEquals(4, ph.getNrOfScreens());
        assertEquals(8, ph.getDeviceXResolution());
        assertEquals(8, ph.getDeviceYResolution());
        
        assertEquals(4, ph.getRainbowduinoV3SerialDevices().size());
        assertEquals(0, ph.getLpdDevice().size());
        assertEquals(OutputDeviceEnum.RAINBOWDUINO_V3, ph.getOutputDevice());
        
        assertTrue(ph.getRainbowduinoV3SerialDevices().contains("/dev/aaa"));
        assertTrue(ph.getRainbowduinoV3SerialDevices().contains("/dev/ddd"));
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
        config.put(ConfigConstant.RAINBOWDUINO_V2_ROW1, "4");
        config.put(ConfigConstant.ARTNET_IP, "192.168.4.2");
        new ApplicationConfigurationHelper(config);
    }


    @Test(expected = IllegalArgumentException.class)
    public void testMultiple2Config() {     
        Properties config = new Properties();
        config.put(ConfigConstant.RAINBOWDUINO_V2_ROW1, "4");
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
        config.put(ConfigConstant.OUTPUT_MAPPING, "4,6,5, 4,2,1,8");
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
    public void testAdaVisionCorrect() {
        Properties config = new Properties();        
        config.put(ConfigConstant.ADAVISION_SERIAL_PORT, "/dev/pille.palle");
        config.put(ConfigConstant.OUTPUT_DEVICE_RESOLUTION_X, "15");
        config.put(ConfigConstant.OUTPUT_DEVICE_RESOLUTION_Y, "10");
        ApplicationConfigurationHelper ph = new ApplicationConfigurationHelper(config);
        assertEquals(OutputDeviceEnum.ADAVISION, ph.getOutputDevice());
        assertEquals("/dev/pille.palle", ph.getAdavisionSerialPort());
    }

    @Test
    public void testAdaVisionSerialPort() {
        Properties config = new Properties();                
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
        config.put(ConfigConstant.ADAVISION_SERIAL_PORT, "/dev/pille.palle");
        config.put(ConfigConstant.OUTPUT_DEVICE_RESOLUTION_X, "15");
        config.put(ConfigConstant.OUTPUT_DEVICE_RESOLUTION_Y, "10");
        ApplicationConfigurationHelper ph = new ApplicationConfigurationHelper(config);
        assertEquals(0, ph.getAdavisionSerialPortSpeed());
        
        config.put(ConfigConstant.ADAVISION_SERIAL_SPEED, "115200");
        ph = new ApplicationConfigurationHelper(config);
        assertEquals(115200, ph.getAdavisionSerialPortSpeed());
    }

    @Test
    public void testUdpDevice() {
        Properties config = new Properties();        
        config.put(ConfigConstant.UDP_IP, "1.2.3.4");
        config.put(ConfigConstant.UDP_PORT, "15");
        ApplicationConfigurationHelper ph = new ApplicationConfigurationHelper(config);
        assertEquals(OutputDeviceEnum.UDP, ph.getOutputDevice());
        assertEquals("1.2.3.4", ph.getUdpIp());
        assertEquals(15, ph.getUdpPort());
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
    
    @Test
    public void testRgbValue() {
        Properties config = new Properties();
        config.put(ConfigConstant.PIXELINVADERS_ROW1, "ROTATE_180,NO_ROTATE");
        config.put(ConfigConstant.CFG_PANEL_COLOR_ORDER, "RBG, BRG");
        ApplicationConfigurationHelper ph = new ApplicationConfigurationHelper(config);

        assertEquals(2, ph.getNrOfScreens());
        assertEquals(ColorFormat.RBG, ph.getColorFormat().get(0));        
        assertEquals(ColorFormat.BRG, ph.getColorFormat().get(1));        
    }
    
    @Test
    public void testPixelInvadersBlacklist() {     
        final String devOne = "/dev/blah";
        final String devTwo = "/dev/two";
        
        Properties config = new Properties();
        config.put(ConfigConstant.PIXELINVADERS_ROW1, "ROTATE_180,NO_ROTATE");
        config.put(ConfigConstant.PIXELINVADERS_BLACKLIST, devOne);
        ApplicationConfigurationHelper ph = new ApplicationConfigurationHelper(config);        
        assertEquals(ph.getPixelInvadersBlacklist().get(0), devOne);

        config = new Properties();
        config.put(ConfigConstant.PIXELINVADERS_ROW1, "ROTATE_180,NO_ROTATE");
        config.put(ConfigConstant.PIXELINVADERS_BLACKLIST, devOne+","+devTwo);
        ph = new ApplicationConfigurationHelper(config);
        
        boolean foundOne=false, foundTwo=false;
        for (String s: ph.getPixelInvadersBlacklist()) {
            if (s.equalsIgnoreCase(devOne)) {
                foundOne = true;
            }
            if (s.equalsIgnoreCase(devTwo)) {
                foundTwo = true;
            }
        }
        assertEquals(foundOne, true);
        assertEquals(foundTwo, true);
    }
    
    @Test
    public void testTpm2() {
        Properties config = new Properties();        
        config.put(ConfigConstant.TPM2_BAUDRATE, "128000");
        config.put(ConfigConstant.TPM2_DEVICE, "/dev/blah");
        ApplicationConfigurationHelper ph = new ApplicationConfigurationHelper(config);
        assertEquals(OutputDeviceEnum.TPM2, ph.getOutputDevice());
    }

    @Test
    public void testTpm2Net() {
        Properties config = new Properties();        
        config.put(ConfigConstant.TPM2NET_IP, "1.2.3.4");
        config.put(ConfigConstant.TPM2NET_ROW1, "ROTATE_180,NO_ROTATE");
        config.put(ConfigConstant.TPM2NET_ROW2, "NO_ROTATE, NO_ROTATE");
        ApplicationConfigurationHelper ph = new ApplicationConfigurationHelper(config);
        assertEquals(OutputDeviceEnum.TPM2NET, ph.getOutputDevice());
        assertEquals(4, ph.getTpm2NetDevice().size());
        assertEquals("1.2.3.4", ph.getTpm2NetIpAddress());
    }

    @Test
    public void testNetworkSettings() {
        Properties config = new Properties();        
        config.put(ConfigConstant.NET_LISTENING_ADDR, "1.2.3.4");
        config.put(ConfigConstant.NET_LISTENING_PORT, "4444");
        ApplicationConfigurationHelper ph = new ApplicationConfigurationHelper(config);
        
        int fudiPort = Integer.parseInt(ph.getProperty(ConfigConstant.NET_LISTENING_PORT, "1") );
        assertEquals(4444, fudiPort);
    }


    @Test
    public void testNegativeSettings() {
        Properties config = new Properties();        
        config.put(ConfigConstant.ADDITIONAL_VISUAL_SCREENS, "-20");        
        config.put(ConfigConstant.NULLOUTPUT_ROW1, "1");
        ApplicationConfigurationHelper ph = new ApplicationConfigurationHelper(config);
        
        assertEquals(0, ph.getNrOfAdditionalVisuals());
    }

}
