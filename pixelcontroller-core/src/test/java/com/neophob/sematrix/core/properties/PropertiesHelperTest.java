/**
 * Copyright (C) 2011-2014 Michael Vogt <michu@neophob.com>
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
package com.neophob.sematrix.core.properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.neophob.sematrix.core.output.ArtnetDevice;
import com.neophob.sematrix.core.output.E1_31Device;
import com.neophob.sematrix.core.output.NullDevice;
import com.neophob.sematrix.core.output.OutputDeviceEnum;
import com.neophob.sematrix.core.output.UdpDevice;
import com.neophob.sematrix.core.output.gamma.RGBAdjust;
import com.neophob.sematrix.core.output.transport.ethernet.IEthernetUdp;
import com.neophob.sematrix.core.resize.PixelControllerResize;
import com.neophob.sematrix.core.visual.MatrixData;
import com.neophob.sematrix.core.visual.layout.Layout.LayoutName;

/**
 * test start
 * 
 * @author michu
 * 
 */
@RunWith(MockitoJUnitRunner.class)
public class PropertiesHelperTest {

    @Mock
    private MatrixData matrixData;

    @Mock
    private PixelControllerResize resizeHelper;

    @Mock
    private IEthernetUdp udp;

    @Test
    public void testEmptyConfig() {
        Properties config = new Properties();
        ApplicationConfigurationHelper ph = new ApplicationConfigurationHelper(config);

        assertEquals(1, ph.getNrOfScreens());
        assertEquals(8, ph.getDeviceXResolution());
        assertEquals(8, ph.getDeviceYResolution());

        assertEquals(0, ph.getI2cAddr().size());
        assertEquals(0, ph.getLpdDevice().size());
        assertEquals(20, ph.parseFps(), 0.01);
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

        config.put(ConfigConstant.PIXELINVADERS_COLORADJUST_R + "1", "100");
        config.put(ConfigConstant.PIXELINVADERS_COLORADJUST_G + "1", "130  ");
        config.put(ConfigConstant.PIXELINVADERS_COLORADJUST_B + "1", "150");

        config.put(ConfigConstant.PIXELINVADERS_COLORADJUST_R + "2", "4");
        config.put(ConfigConstant.PIXELINVADERS_COLORADJUST_G + "2", "-222");
        config.put(ConfigConstant.PIXELINVADERS_COLORADJUST_B + "2", "zzz");

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

        Map<Integer, RGBAdjust> correction = ph.getPixelInvadersCorrectionMap();
        assertFalse(correction.containsKey(ConfigConstant.PIXELINVADERS_COLORADJUST_R + "0"));
        assertFalse(correction.containsKey(ConfigConstant.PIXELINVADERS_COLORADJUST_G + "0"));
        assertFalse(correction.containsKey(ConfigConstant.PIXELINVADERS_COLORADJUST_B + "0"));

        RGBAdjust corr = correction.get(1);
        assertTrue(corr != null);
        assertEquals(100, corr.getR());
        assertEquals(130, corr.getG());
        assertEquals(150, corr.getB());

        corr = correction.get(2);
        assertTrue(corr != null);
        assertEquals(4, corr.getR());
        assertEquals(0, corr.getG());
        assertEquals(0, corr.getB());
    }

    @Test
    public void testPixelInvadersNetConfig() {
        Properties config = new Properties();
        config.put(ConfigConstant.PIXELINVADERS_ROW1, "ROTATE_180, NO_ROTATE");
        config.put(ConfigConstant.PIXELINVADERS_ROW2, "ROTATE_90, NO_ROTATE");
        config.put(ConfigConstant.PIXELINVADERS_PANEL_ORDER, "0,3, 1,2");
        config.put(ConfigConstant.PIXELINVADERS_NET_IP, "127.0.0.1");
        config.put(ConfigConstant.PIXELINVADERS_NET_PORT, "5333");
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
        assertEquals(OutputDeviceEnum.PIXELINVADERS_NET, ph.getOutputDevice());

        Map<Integer, RGBAdjust> correction = ph.getPixelInvadersCorrectionMap();
        assertTrue(correction != null);
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

        for (int ofs = 0; ofs < ph.getNrOfScreens(); ofs++) {
            int panelNr = order.get(ofs);
            System.out.println("visual " + ofs + ", panel ofs: " + panelNr + ", layout: "
                    + displayOptions.get(panelNr) + " colorformat: " + colorFormat.get(panelNr));
        }
    }

    @Test
    public void testPixelInvadersAndNullOutputConfig() {
        Properties config = new Properties();
        config.put(ConfigConstant.PIXELINVADERS_ROW1, "ROTATE_180,NO_ROTATE");
        config.put(
                ConfigConstant.PIXELINVADERS_BLACKLIST,
                "/dev/tty.Bluetooth-SerialP5-1,/dev/cu.Bluetooth-SerialP5-1,/dev/cu.Bluetooth-Modem,/dev/cu.Bluetooth-SerialP5-2,/dev/cu.Bluetooth-PDA-Sync,/dev/tty.Bluetooth-PDA-Sync,/dev/cu.Bluetooth-Modem,/dev/tty.Bluetooth-Modem,/dev/tty.Bluetooth-SerialP5-2");
        config.put(ConfigConstant.NULLOUTPUT_ROW1, "1");
        config.put(ConfigConstant.NULLOUTPUT_ROW2, "0");
        config.put(ConfigConstant.OUTPUT_DEVICE_RESOLUTION_X, "16");
        config.put(ConfigConstant.OUTPUT_DEVICE_RESOLUTION_Y, "16");
        ApplicationConfigurationHelper ph = new ApplicationConfigurationHelper(config);

        assertEquals(8, ph.getDeviceXResolution());
        assertEquals(8, ph.getDeviceYResolution());
        assertEquals(2, ph.getNrOfScreens());
    }

    @Test
    public void testPixelInvadersNetAndNullOutputConfig() {
        Properties config = new Properties();
        config.put(ConfigConstant.PIXELINVADERS_ROW1, "ROTATE_180,NO_ROTATE");
        config.put(
                ConfigConstant.PIXELINVADERS_BLACKLIST,
                "/dev/tty.Bluetooth-SerialP5-1,/dev/cu.Bluetooth-SerialP5-1,/dev/cu.Bluetooth-Modem,/dev/cu.Bluetooth-SerialP5-2,/dev/cu.Bluetooth-PDA-Sync,/dev/tty.Bluetooth-PDA-Sync,/dev/cu.Bluetooth-Modem,/dev/tty.Bluetooth-Modem,/dev/tty.Bluetooth-SerialP5-2");
        config.put(ConfigConstant.PIXELINVADERS_NET_IP, "127.0.0.1");
        config.put(ConfigConstant.NULLOUTPUT_ROW1, "1");
        config.put(ConfigConstant.NULLOUTPUT_ROW2, "0");
        config.put(ConfigConstant.OUTPUT_DEVICE_RESOLUTION_X, "16");
        config.put(ConfigConstant.OUTPUT_DEVICE_RESOLUTION_Y, "16");
        ApplicationConfigurationHelper ph = new ApplicationConfigurationHelper(config);

        assertEquals(8, ph.getDeviceXResolution());
        assertEquals(8, ph.getDeviceYResolution());
        assertEquals(2, ph.getNrOfScreens());
    }

    @Test
    public void testInvalidPixelInvadersConfigOne() {
        Properties config = new Properties();
        config.put(ConfigConstant.PIXELINVADERS_ROW1, "ROTATE_180,NO_ROTATE");
        config.put(ConfigConstant.PIXELINVADERS_ROW2, "ROTATE_90,NO_ROTATE");
        config.put(ConfigConstant.PIXELINVADERS_PANEL_ORDER, "0,4,1,2");
        ApplicationConfigurationHelper ph = new ApplicationConfigurationHelper(config);

        // panel order is ignored, due invalid value
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

        // panel order is ignored, due invalid value
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
    public void testArtnetConfigSimple() {
        Properties config = new Properties();
        config.put(ConfigConstant.ARTNET_IP, "192.168.1.1");
        config.put(ConfigConstant.ARTNET_ROW1, "NO_ROTATE");
        config.put(ConfigConstant.ARTNET_BROADCAST_ADDR, "255.0.0.0");
        ApplicationConfigurationHelper ph = new ApplicationConfigurationHelper(config);

        assertEquals(1, ph.getNrOfScreens());
        assertEquals(8, ph.getDeviceXResolution());
        assertEquals(8, ph.getDeviceYResolution());
        assertEquals(false, ph.isOutputSnakeCabeling());

        assertEquals(0, ph.getI2cAddr().size());
        assertEquals(0, ph.getLpdDevice().size());
        assertEquals(OutputDeviceEnum.ARTNET, ph.getOutputDevice());

        ArtnetDevice device = new ArtnetDevice(matrixData, resizeHelper, ph);
        assertTrue(device.isConnected());
        assertEquals(170, device.getPixelsPerUniverse());
        assertEquals(1, device.getNrOfUniverse());
        assertEquals(0, device.getFirstUniverseId());
    }

    @Test
    public void testArtnetConfigAdvanced() {
        Properties config = new Properties();
        config.put(ConfigConstant.ARTNET_IP, "192.168.1.1");
        config.put(ConfigConstant.ARTNET_PIXELS_PER_UNIVERSE, "333");
        config.put(ConfigConstant.ARTNET_ROW1, "NO_ROTATE");
        config.put(ConfigConstant.ARTNET_ROW2, "NO_ROTATE");
        config.put(ConfigConstant.OUTPUT_DEVICE_RESOLUTION_X, "10");
        config.put(ConfigConstant.OUTPUT_DEVICE_RESOLUTION_Y, "8");
        config.put(ConfigConstant.OUTPUT_DEVICE_SNAKE_CABELING, "true");
        ApplicationConfigurationHelper ph = new ApplicationConfigurationHelper(config);

        assertEquals(OutputDeviceEnum.ARTNET, ph.getOutputDevice());
        assertEquals(2, ph.getNrOfScreens());
        assertEquals(10, ph.getDeviceXResolution());
        assertEquals(8, ph.getDeviceYResolution());
        assertEquals(true, ph.isOutputSnakeCabeling());

        ArtnetDevice device = new ArtnetDevice(matrixData, resizeHelper, ph);
        assertEquals(170, device.getPixelsPerUniverse());
    }

    @Test
    public void testE131Config() {
        Properties config = new Properties();
        config.put(ConfigConstant.E131_IP, "192.168.1.1");
        config.put(ConfigConstant.E131_ROW1, "NO_ROTATE");
        ApplicationConfigurationHelper ph = new ApplicationConfigurationHelper(config);

        assertEquals(1, ph.getNrOfScreens());
        assertEquals(8, ph.getDeviceXResolution());
        assertEquals(8, ph.getDeviceYResolution());
        assertEquals(false, ph.isOutputSnakeCabeling());

        assertEquals(0, ph.getI2cAddr().size());
        assertEquals(0, ph.getLpdDevice().size());
        assertEquals(OutputDeviceEnum.E1_31, ph.getOutputDevice());

        E1_31Device device = new E1_31Device(matrixData, resizeHelper, ph, udp);
        assertFalse(device.isSendMulticast());
        assertEquals(170, device.getPixelsPerUniverse());
        assertEquals(1, device.getNrOfUniverse());
        assertEquals(0, device.getFirstUniverseId());

        config = new Properties();
        config.put(ConfigConstant.E131_IP, "239.255.1.1");
        config.put(ConfigConstant.E131_ROW1, "NO_ROTATE");
        config.put(ConfigConstant.E131_FIRST_UNIVERSE_ID, "1");
        config.put(ConfigConstant.E131_PIXELS_PER_UNIVERSE, "333");
        config.put(ConfigConstant.OUTPUT_DEVICE_RESOLUTION_X, "10");
        config.put(ConfigConstant.OUTPUT_DEVICE_SNAKE_CABELING, "true");
        ph = new ApplicationConfigurationHelper(config);

        assertEquals(OutputDeviceEnum.E1_31, ph.getOutputDevice());
        assertEquals(1, ph.getNrOfScreens());
        assertEquals(10, ph.getDeviceXResolution());
        assertEquals(8, ph.getDeviceYResolution());
        assertEquals(true, ph.isOutputSnakeCabeling());

        assertEquals(0, ph.getI2cAddr().size());
        assertEquals(0, ph.getLpdDevice().size());
        assertEquals(OutputDeviceEnum.E1_31, ph.getOutputDevice());

        device = new E1_31Device(matrixData, resizeHelper, ph, udp);
        assertTrue(device.isSendMulticast());
        assertEquals(1, device.getFirstUniverseId());
        assertEquals(170, device.getPixelsPerUniverse());
    }

    @Test
    public void testMultipleE131Config() {
        Properties config = new Properties();
        config.put(ConfigConstant.E131_IP, "192.168.1.1");
        config.put(ConfigConstant.E131_ROW1, "NO_ROTATE,NO_ROTATE");
        config.put(ConfigConstant.E131_ROW2, "NO_ROTATE,NO_ROTATE");
        config.put(ConfigConstant.OUTPUT_DEVICE_RESOLUTION_X, "10");
        config.put(ConfigConstant.OUTPUT_DEVICE_RESOLUTION_Y, "8");
        config.put(ConfigConstant.OUTPUT_DEVICE_SNAKE_CABELING, "true");
        ApplicationConfigurationHelper ph = new ApplicationConfigurationHelper(config);

        assertEquals(4, ph.getNrOfScreens());
        assertEquals(10, ph.getDeviceXResolution());
        assertEquals(8, ph.getDeviceYResolution());
        assertEquals(true, ph.isOutputSnakeCabeling());
        assertEquals(4, ph.getPanelOrder().size());
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

        NullDevice device = new NullDevice(matrixData, resizeHelper, ph);
        assertTrue(device.isConnected());
    }

    @Test
    public void testRpiWs2801Speed() {
        Properties config = new Properties();
        config.put(ConfigConstant.RPI_WS2801_SPI_SPEED, "1000000");
        ApplicationConfigurationHelper ph = new ApplicationConfigurationHelper(config);

        assertEquals(1, ph.getNrOfScreens());
        assertEquals(8, ph.getDeviceXResolution());
        assertEquals(8, ph.getDeviceYResolution());
        assertEquals(1000000, ph.getRpiWs2801SpiSpeed());
        assertEquals(OutputDeviceEnum.RPI_2801, ph.getOutputDevice());

    }

    @Test(expected = IllegalArgumentException.class)
    public void testMultipleConfig() {
        Properties config = new Properties();
        config.put(ConfigConstant.RAINBOWDUINO_V2_ROW1, "4");
        config.put(ConfigConstant.ARTNET_IP, "192.168.4.2");
        config.put(ConfigConstant.ARTNET_ROW1, "NO_ROTATE");
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

    @Test
    public void testShortMappingCount() {
        Properties config = new Properties();
        config.put(ConfigConstant.OUTPUT_DEVICE_RESOLUTION_X, "2");
        config.put(ConfigConstant.OUTPUT_DEVICE_RESOLUTION_Y, "2");
        config.put(ConfigConstant.OUTPUT_MAPPING, "4,6");
        config.put(ConfigConstant.ARTNET_IP, "1.1.1.1");
        new ApplicationConfigurationHelper(config).getLayout();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidMappingCount() {
        Properties config = new Properties();
        config.put(ConfigConstant.OUTPUT_DEVICE_RESOLUTION_X, "2");
        config.put(ConfigConstant.OUTPUT_DEVICE_RESOLUTION_Y, "2");
        config.put(ConfigConstant.OUTPUT_MAPPING, "4,6,3,1,5");
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

    // TODO fixme
    @Test
    @Ignore
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
    public void testUdpDevice() {
        Properties config = new Properties();
        config.put(ConfigConstant.UDP_IP, "1.2.3.4");
        config.put(ConfigConstant.UDP_PORT, "15");
        ApplicationConfigurationHelper ph = new ApplicationConfigurationHelper(config);
        assertEquals(OutputDeviceEnum.UDP, ph.getOutputDevice());
        assertEquals("1.2.3.4", ph.getUdpIp());
        assertEquals(15, ph.getUdpPort());

        when(matrixData.getDeviceXSize()).thenReturn(8);
        when(matrixData.getDeviceYSize()).thenReturn(8);
        UdpDevice device = new UdpDevice(matrixData, resizeHelper, ph, udp);
        assertFalse(device.isConnected());
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
        config.put(ConfigConstant.PIXELINVADERS_BLACKLIST, devOne + "," + devTwo);
        ph = new ApplicationConfigurationHelper(config);

        boolean foundOne = false, foundTwo = false;
        for (String s : ph.getPixelInvadersBlacklist()) {
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
        config.put(ConfigConstant.TPM2NET_IP, "127.0.0.1");
        config.put(ConfigConstant.TPM2NET_ROW1, "ROTATE_180,NO_ROTATE");
        config.put(ConfigConstant.TPM2NET_ROW2, "NO_ROTATE, NO_ROTATE");
        ApplicationConfigurationHelper ph = new ApplicationConfigurationHelper(config);
        assertEquals(OutputDeviceEnum.TPM2NET, ph.getOutputDevice());
        assertEquals(4, ph.getTpm2NetDevice().size());
        assertEquals("127.0.0.1", ph.getTpm2NetIpAddress());
    }

    @Test
    public void testNegativeSettings() {
        Properties config = new Properties();
        config.put(ConfigConstant.ADDITIONAL_VISUAL_SCREENS, "-20");
        config.put(ConfigConstant.NULLOUTPUT_ROW1, "1");
        ApplicationConfigurationHelper ph = new ApplicationConfigurationHelper(config);

        assertEquals(0, ph.getNrOfAdditionalVisuals());
    }

    @Test
    public void testSoundSilence() {
        Properties config = new Properties();
        config.put(ConfigConstant.SOUND_SILENCE_THRESHOLD, "0.06f");
        ApplicationConfigurationHelper ph = new ApplicationConfigurationHelper(config);
        assertEquals(0.06f, ph.getSoundSilenceThreshold(), 0.001);

        config = new Properties();
        config.put(ConfigConstant.SOUND_SILENCE_THRESHOLD, "  0.06   ");
        ph = new ApplicationConfigurationHelper(config);
        assertEquals(0.06f, ph.getSoundSilenceThreshold(), 0.001);

        config = new Properties();
        config.put(ConfigConstant.SOUND_SILENCE_THRESHOLD, "");
        ph = new ApplicationConfigurationHelper(config);
        assertEquals(0.0005f, ph.getSoundSilenceThreshold(), 0.001);

        config = new Properties();
        config.put(ConfigConstant.SOUND_SILENCE_THRESHOLD, "0.pillepalle");
        ph = new ApplicationConfigurationHelper(config);
        assertEquals(0.0005f, ph.getSoundSilenceThreshold(), 0.001);

        config = new Properties();
        ph = new ApplicationConfigurationHelper(config);
        assertEquals(0.0005f, ph.getSoundSilenceThreshold(), 0.001);

        config = new Properties();
        config.put(ConfigConstant.SOUND_SILENCE_THRESHOLD, "-0.5");
        ph = new ApplicationConfigurationHelper(config);
        assertEquals(0.0005f, ph.getSoundSilenceThreshold(), 0.001);

        config = new Properties();
        config.put(ConfigConstant.SOUND_SILENCE_THRESHOLD, "1.5f");
        ph = new ApplicationConfigurationHelper(config);
        assertEquals(0.0005f, ph.getSoundSilenceThreshold(), 0.001);
    }

    @Test
    public void testRemote() {
        Properties config = new Properties();
        config.put(ConfigConstant.REMOTE_CLIENT_FPS, "1.23f");
        config.put(ConfigConstant.REMOTE_CLIENT_USE_COMPRESSION, "true");
        ApplicationConfigurationHelper ph = new ApplicationConfigurationHelper(config);
        assertEquals(1.23f, ph.parseRemoteFps(), 0.001f);
        assertTrue(ph.parseRemoteConnectionUseCompression());
    }
}
