package com.neophob.sematrix.core.output;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.neophob.sematrix.core.output.serial.ISerial;
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
public class OutputSerialTest {

    private static final int INTERNAL_SIZE = 64;
    private static final int DEVICE_SIZE = 8;

    @Mock
    private ISerial serialPort;

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

        when(serialPort.getAllSerialPorts())
                .thenReturn(new String[] { "COM77", "/dev/peter.pan2" });
        when(serialPort.isConnected()).thenReturn(false);
        when(serialPort.available()).thenReturn(0);
        when(serialPort.readBytes()).thenReturn(null);

        List<IColorSet> colorSets = new ArrayList<IColorSet>();
        colorSets.add(new JunitColorSet());
        /*
         * VisualState.getInstance().init(new FileUtilsJunit(), ph, new
         * SoundDummy(), colorSets, presetService);
         */
    }

    @Test
    public void testPixelInvadersDevice() {
        when(ph.getLpdDevice()).thenReturn(
                Arrays.asList(DeviceConfig.NO_ROTATE, DeviceConfig.ROTATE_180_FLIPPEDY));
        // test negative
        IOutput o = new PixelInvadersSerialDevice(matrix, res, ph, serialPort);
        Assert.assertFalse(o.isConnected());

        // test positive
        when(serialPort.isConnected()).thenReturn(true);
        when(serialPort.available()).thenReturn(0);
        when(serialPort.readBytes()).thenReturn("AK PXI".getBytes());
        o = new PixelInvadersSerialDevice(matrix, res, ph, serialPort);
        Assert.assertTrue(o.isConnected());
        o.prepareOutputBuffer(vs);
        o.switchBuffers();
        o.prepareOutputBuffer(vs);
        o.update();
        Assert.assertFalse(o.getConnectionStatus().isEmpty());
    }

    @Test
    public void testTPM2Device() {
        when(ph.getTpm2Device()).thenReturn("/dev/peter.pan2");
        // test negative
        IOutput o = new Tpm2(matrix, res, ph, serialPort);
        Assert.assertFalse(o.isConnected());

        // test positive
        when(serialPort.isConnected()).thenReturn(true);
        when(serialPort.available()).thenReturn(0);
        when(serialPort.readBytes()).thenReturn("".getBytes());
        o = new Tpm2(matrix, res, ph, serialPort);
        Assert.assertTrue(o.isConnected());
        o.prepareOutputBuffer(vs);
        o.switchBuffers();
        o.prepareOutputBuffer(vs);
        o.update();
    }

    @Test
    public void testMiniDmxDevice() {
        // test negative
        IOutput o = new MiniDmxDevice(matrix, res, ph, serialPort);
        Assert.assertFalse(o.isConnected());

        // test positive
        when(serialPort.isConnected()).thenReturn(true);
        when(serialPort.available()).thenReturn(3);
        when(serialPort.readBytes()).thenReturn(
                new byte[] { (byte) 0x05a, (byte) 0xC1, (byte) 0x0a5 });
        when(serialPort.getOutputStream()).thenReturn(new ByteArrayOutputStream());
        o = new MiniDmxDevice(matrix, res, ph, serialPort);
        Assert.assertTrue(o.isConnected());
        o.prepareOutputBuffer(vs);
        o.switchBuffers();
        o.prepareOutputBuffer(vs);
        o.update();
    }

    @Test
    public void testRainbowduinoV2Device() {
        // test negative
        IOutput o = new RainbowduinoV2Device(matrix, res, ph, serialPort);
        Assert.assertFalse(o.isConnected());

        // test positive
        when(serialPort.isConnected()).thenReturn(true);
        when(serialPort.available()).thenReturn(3);
        when(serialPort.readBytes()).thenReturn("AKK".getBytes());
        when(serialPort.getOutputStream()).thenReturn(new ByteArrayOutputStream());
        o = new RainbowduinoV2Device(matrix, res, ph, serialPort);
        Assert.assertTrue(o.isConnected());
        o.prepareOutputBuffer(vs);
        o.switchBuffers();
        o.prepareOutputBuffer(vs);
        o.update();
    }
}
