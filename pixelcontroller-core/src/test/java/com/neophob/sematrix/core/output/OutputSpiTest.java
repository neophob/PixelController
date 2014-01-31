package com.neophob.sematrix.core.output;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyListOf;
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

import com.neophob.sematrix.core.output.transport.spi.ISpi;
import com.neophob.sematrix.core.preset.PresetService;
import com.neophob.sematrix.core.properties.ApplicationConfigurationHelper;
import com.neophob.sematrix.core.properties.ColorFormat;
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
public class OutputSpiTest {

    private static final int INTERNAL_SIZE = 64;
    private static final int DEVICE_SIZE = 8;

    @Mock
    private ISpi spi;

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
    public void testRpi2801Device() {
        when(ph.getRpiWs2801SpiSpeed()).thenReturn(1000000);

        // test negative
        when(spi.initializeSpi(anyInt(), anyInt())).thenReturn(false);
        IOutput o = new RaspberrySpi2801(matrix, res, ph, spi);
        Assert.assertFalse(o.isConnected());

        // test positive
        when(spi.initializeSpi(anyInt(), anyInt())).thenReturn(true);
        when(spi.writeSpiData(any(byte[].class))).thenReturn(true);

        o = new RaspberrySpi2801(matrix, res, ph, spi);
        Assert.assertTrue(o.isConnected());
        o.prepareOutputBuffer(vs);
        o.switchBuffers();
        o.prepareOutputBuffer(vs);
        o.update();
        Assert.assertFalse(o.getConnectionStatus().isEmpty());
        o.close();
    }
}
