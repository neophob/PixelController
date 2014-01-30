package com.neophob.sematrix.core.output;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.junit.Assert;
import org.junit.Test;

import com.neophob.sematrix.core.glue.FileUtilsJunit;
import com.neophob.sematrix.core.jmx.PixelControllerStatus;
import com.neophob.sematrix.core.jmx.PixelControllerStatusMBean;
import com.neophob.sematrix.core.listener.MessageProcessor;
import com.neophob.sematrix.core.preset.PresetService;
import com.neophob.sematrix.core.preset.PresetServiceImpl;
import com.neophob.sematrix.core.preset.PresetSettings;
import com.neophob.sematrix.core.properties.ApplicationConfigurationHelper;
import com.neophob.sematrix.core.properties.ConfigConstant;
import com.neophob.sematrix.core.properties.ValidCommand;
import com.neophob.sematrix.core.resize.PixelControllerResize;
import com.neophob.sematrix.core.sound.SoundDummy;
import com.neophob.sematrix.core.visual.MatrixData;
import com.neophob.sematrix.core.visual.VisualState;
import com.neophob.sematrix.core.visual.color.IColorSet;

public class OutputWiringTest {

    private int[] processOutput(Properties p) {
        ApplicationConfigurationHelper ph = new ApplicationConfigurationHelper(p);
        Assert.assertEquals(10, ph.getDeviceXResolution());
        Assert.assertEquals(5, ph.getDeviceYResolution());

        PixelControllerStatusMBean pixConStat = new PixelControllerStatus((int) ph.parseFps());
        VisualState vs = VisualState.getInstance();
        List<IColorSet> cs = new ArrayList<IColorSet>();
        cs.add(new JunitColorSet());
        PresetService presetService = new PresetServiceImpl(new ArrayList<PresetSettings>());
        vs.init(new FileUtilsJunit(), ph, new SoundDummy(), cs, presetService);

        // load image HALF.JPG

        MessageProcessor.INSTANCE.processMsg(
                new String[] { ValidCommand.CURRENT_VISUAL + "", "0" }, false, null);
        MessageProcessor.INSTANCE.processMsg(new String[] { ValidCommand.CURRENT_COLORSET + "",
                "JunitColorSet" }, false, null);
        MessageProcessor.INSTANCE.processMsg(new String[] { ValidCommand.CHANGE_GENERATOR_A + "",
                "2" }, false, null);
        MessageProcessor.INSTANCE.processMsg(new String[] { ValidCommand.CHANGE_MIXER + "", "0" },
                false, null);
        MessageProcessor.INSTANCE.processMsg(
                new String[] { ValidCommand.CHANGE_EFFECT_A + "", "0" }, false, null);
        MessageProcessor.INSTANCE.processMsg(new String[] { ValidCommand.IMAGE + "", "half.png" },
                false, null);
        MessageProcessor.INSTANCE.processMsg(new String[] { ValidCommand.CHANGE_BRIGHTNESS + "",
                "100" }, false, null);

        // for (int i = 0; i < 50; i++)
        vs.updateSystem(pixConStat);

        MatrixData matrix = vs.getMatrix();
        PixelControllerResize res = new PixelControllerResize();
        res.initAll();
        JunitOutput output = new JunitOutput(matrix, res, ph);
        output.prepareOutputBuffer(vs);
        output.switchBuffers();
        output.prepareOutputBuffer(vs);
        return output.getTransformedBuffer();
    }

    @Test
    public void testSnakeCabling() {
        Properties p = new Properties();
        p.put(ConfigConstant.OUTPUT_DEVICE_RESOLUTION_X, "10");
        p.put(ConfigConstant.OUTPUT_DEVICE_RESOLUTION_Y, "5");
        p.put(ConfigConstant.OUTPUT_DEVICE_SNAKE_CABELING, "true");
        p.put(ConfigConstant.OUTPUT_DEVICE_LAYOUT, "NO_ROTATE");
        p.put(ConfigConstant.NULLOUTPUT_ROW1, "1");
        int[] b = processOutput(p);
        printArray(b);
        Assert.assertEquals(0x90, b[0], 2);
        Assert.assertEquals(0x90, b[9], 2);
        Assert.assertEquals(0x90, b[10], 2);
        Assert.assertEquals(0x0, b[30], 2);
        Assert.assertEquals(0x0, b[39], 2);
        Assert.assertEquals(0x0, b[40], 2);
        Assert.assertEquals(0x0, b[49], 2);
    }

    @Test
    public void testSnakeCabling90RotateSnake() {
        Properties p = new Properties();
        p.put(ConfigConstant.OUTPUT_DEVICE_RESOLUTION_X, "10");
        p.put(ConfigConstant.OUTPUT_DEVICE_RESOLUTION_Y, "5");
        p.put(ConfigConstant.OUTPUT_DEVICE_SNAKE_CABELING, "true");
        p.put(ConfigConstant.OUTPUT_DEVICE_LAYOUT, "ROTATE_90");
        p.put(ConfigConstant.NULLOUTPUT_ROW1, "1");
        int[] b = processOutput(p);
        printArray(b);
        Assert.assertEquals(0x0, b[0], 2);
        Assert.assertEquals(0x90, b[9], 2);
        Assert.assertEquals(0x90, b[10], 2);
        Assert.assertEquals(0x0, b[19], 2);
        Assert.assertEquals(0x0, b[20], 2);
        Assert.assertEquals(0x90, b[29], 2);
        Assert.assertEquals(0x90, b[30], 2);
        Assert.assertEquals(0x0, b[39], 2);
        Assert.assertEquals(0x0, b[40], 2);
        Assert.assertEquals(0x90, b[49], 2);
    }

    @Test
    public void testSnakeCabling90RotateNoSnake() {
        Properties p = new Properties();
        p.put(ConfigConstant.OUTPUT_DEVICE_RESOLUTION_X, "10");
        p.put(ConfigConstant.OUTPUT_DEVICE_RESOLUTION_Y, "5");
        p.put(ConfigConstant.OUTPUT_DEVICE_SNAKE_CABELING, "false");
        p.put(ConfigConstant.OUTPUT_DEVICE_LAYOUT, "ROTATE_90");
        p.put(ConfigConstant.NULLOUTPUT_ROW1, "1");
        int[] b = processOutput(p);
        printArray(b);
        Assert.assertEquals(0x0, b[0], 2);
        Assert.assertEquals(0x90, b[9], 2);
        Assert.assertEquals(0x0, b[10], 2);
        Assert.assertEquals(0x90, b[19], 2);
        Assert.assertEquals(0x0, b[20], 2);
        Assert.assertEquals(0x90, b[29], 2);
        Assert.assertEquals(0x0, b[30], 2);
        Assert.assertEquals(0x90, b[39], 2);
        Assert.assertEquals(0x0, b[40], 2);
        Assert.assertEquals(0x90, b[49], 2);
    }

    @Test
    public void testX() {
        // expected output for half.jpg:
        //
        // 0,0,90,90,90, 90,90,90,0,0
        Properties p = new Properties();
        p.put(ConfigConstant.OUTPUT_DEVICE_RESOLUTION_X, "10");
        p.put(ConfigConstant.OUTPUT_DEVICE_RESOLUTION_Y, "5");
        p.put(ConfigConstant.OUTPUT_DEVICE_SNAKE_CABELING, "false");
        p.put(ConfigConstant.OUTPUT_DEVICE_LAYOUT, "ROTATE_90");
        p.put(ConfigConstant.NULLOUTPUT_ROW1, "1");
        int[] b = processOutput(p);
        printArray(b);

    }

    private void printArray(int[] ret) {
        int o = 0;
        for (int i : ret) {
            System.out.print(Integer.toHexString(i) + ", ");
            if (o++ == 9) {
                System.out.println();
                o = 0;
            }
        }
        System.out.println();
    }
}
