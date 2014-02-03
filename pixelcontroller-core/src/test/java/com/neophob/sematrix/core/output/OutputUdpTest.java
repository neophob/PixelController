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
package com.neophob.sematrix.core.output;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.neophob.sematrix.core.output.transport.ethernet.IEthernetUdp;
import com.neophob.sematrix.core.preset.PresetService;
import com.neophob.sematrix.core.properties.ApplicationConfigurationHelper;
import com.neophob.sematrix.core.properties.ColorFormat;
import com.neophob.sematrix.core.properties.DeviceConfig;
import com.neophob.sematrix.core.resize.PixelControllerResize;
import com.neophob.sematrix.core.resize.Resize.ResizeName;
import com.neophob.sematrix.core.visual.MatrixData;
import com.neophob.sematrix.core.visual.OutputMapping;
import com.neophob.sematrix.core.visual.Visual;
import com.neophob.sematrix.core.visual.VisualState;
import com.neophob.sematrix.core.visual.color.IColorSet;
import com.neophob.sematrix.core.visual.fader.IFader;
import com.neophob.sematrix.core.visual.layout.Layout;
import com.neophob.sematrix.core.visual.layout.LayoutModel;

@RunWith(MockitoJUnitRunner.class)
public class OutputUdpTest {

    private static final int INTERNAL_SIZE = 64;
    private static final int DEVICE_SIZE = 8;

    @Mock
    private IEthernetUdp udp;

    @Mock
    private ApplicationConfigurationHelper ph;

    @Mock
    private PresetService presetService;

    @Mock
    private VisualState vs;

    @Mock
    private Visual visual;

    @Mock
    private MatrixData matrix;

    @Mock
    private IFader fader;

    @Mock
    private Layout layout;

    private PixelControllerResize res = new PixelControllerResize();

    @Before
    public void setUp() {
        when(ph.getLayout()).thenReturn(layout);
        when(ph.getDeviceXResolution()).thenReturn(DEVICE_SIZE);
        when(ph.getDeviceYResolution()).thenReturn(DEVICE_SIZE);
        when(ph.getNrOfScreens()).thenReturn(2);
        when(ph.getPanelOrder()).thenReturn(Arrays.asList(0, 1));
        when(ph.getColorFormat()).thenReturn(Arrays.asList(ColorFormat.BGR, ColorFormat.GRB));
        when(ph.getOutputMappingValues()).thenReturn(new int[0]);

        when(matrix.getDeviceXSize()).thenReturn(DEVICE_SIZE);
        when(matrix.getDeviceYSize()).thenReturn(DEVICE_SIZE);
        when(matrix.getBufferXSize()).thenReturn(INTERNAL_SIZE);
        when(matrix.getBufferYSize()).thenReturn(INTERNAL_SIZE);

        when(layout.getDataForScreen(anyInt(), anyListOf(OutputMapping.class))).thenReturn(
                new LayoutModel(1, 1, 0, 0, 0));

        res.initAll();
        when(vs.getPixelControllerResize()).thenReturn(res);
        when(vs.getAllOutputMappings()).thenReturn(
                Arrays.asList(new OutputMapping(fader, 0), new OutputMapping(fader, 1)));
        when(vs.getVisual(0)).thenReturn(visual);
        when(vs.getVisual(1)).thenReturn(visual);

        when(visual.getResizeOption()).thenReturn(ResizeName.QUALITY_RESIZE);
        when(visual.getBuffer()).thenReturn(new int[INTERNAL_SIZE * INTERNAL_SIZE]);

        List<IColorSet> colorSets = new ArrayList<IColorSet>();
        colorSets.add(new JunitColorSet());
    }

    @Test
    public void testUdpDevice() {
        when(ph.getUdpIp()).thenReturn("127.0.0.1");
        when(ph.getUdpPort()).thenReturn(10000);

        // test negative
        when(udp.initializeEthernet(anyString(), anyInt())).thenReturn(false);
        IOutput o = new UdpDevice(matrix, res, ph, udp);
        Assert.assertFalse(o.isConnected());

        // test positive
        when(udp.initializeEthernet(anyString(), anyInt())).thenReturn(true);

        o = new UdpDevice(matrix, res, ph, udp);
        Assert.assertTrue(o.isConnected());
        o.prepareOutputBuffer(vs);
        o.switchBuffers();
        o.prepareOutputBuffer(vs);
        o.update();
        Assert.assertFalse(o.getConnectionStatus().isEmpty());
        o.close();
    }

    @Test
    public void testTpm2netDevice() {
        when(ph.getTpm2NetDevice()).thenReturn(
                Arrays.asList(DeviceConfig.ROTATE_180_FLIPPEDY, DeviceConfig.FLIPPEDY));
        when(ph.getTpm2NetIpAddress()).thenReturn("127.0.0.1");

        // test negative
        when(udp.initializeEthernet(anyString(), anyInt())).thenReturn(false);
        IOutput o = new Tpm2Net(matrix, res, ph, udp);
        Assert.assertFalse(o.isConnected());

        // test positive
        when(udp.initializeEthernet(anyString(), anyInt())).thenReturn(true);
        o = new Tpm2Net(matrix, res, ph, udp);
        Assert.assertTrue(o.isConnected());
        o.prepareOutputBuffer(vs);
        o.switchBuffers();
        o.prepareOutputBuffer(vs);
        o.update();
        Assert.assertFalse(o.getConnectionStatus().isEmpty());
        o.close();
    }

    @Test
    public void testE131netDevice() {
        when(ph.getE131Device()).thenReturn(
                Arrays.asList(DeviceConfig.ROTATE_180_FLIPPEDY, DeviceConfig.FLIPPEDY));
        when(ph.getE131PixelsPerUniverse()).thenReturn(170);
        when(ph.getE131StartUniverseId()).thenReturn(4);
        when(ph.getE131Ip()).thenReturn("127.0.0.1");

        // test negative
        when(udp.initializeEthernet(anyString(), anyInt())).thenReturn(false);
        IOutput o = new E1_31Device(matrix, res, ph, udp);
        Assert.assertFalse(o.isConnected());

        // test positive
        when(udp.initializeEthernet(anyString(), anyInt())).thenReturn(true);
        o = new E1_31Device(matrix, res, ph, udp);
        Assert.assertTrue(o.isConnected());
        o.prepareOutputBuffer(vs);
        o.switchBuffers();
        o.prepareOutputBuffer(vs);
        o.update();
        Assert.assertFalse(o.getConnectionStatus().isEmpty());
        o.close();

        // test positive multicase
        when(ph.getE131Ip()).thenReturn("239.255.0.0");
        o = new E1_31Device(matrix, res, ph, udp);
        Assert.assertTrue(o.isConnected());
        o.prepareOutputBuffer(vs);
        o.switchBuffers();
        o.prepareOutputBuffer(vs);
        o.update();
        Assert.assertFalse(o.getConnectionStatus().isEmpty());
        o.close();
    }

}
