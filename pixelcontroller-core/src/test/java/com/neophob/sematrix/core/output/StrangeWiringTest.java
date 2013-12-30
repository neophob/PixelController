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
import com.neophob.sematrix.core.listener.PresetServiceDummy;
import com.neophob.sematrix.core.properties.ApplicationConfigurationHelper;
import com.neophob.sematrix.core.properties.ConfigConstant;
import com.neophob.sematrix.core.properties.ValidCommand;
import com.neophob.sematrix.core.sound.SoundMinim;
import com.neophob.sematrix.core.visual.VisualState;
import com.neophob.sematrix.core.visual.color.ColorSet;

public class StrangeWiringTest {

    private static Properties getConfig() {
        Properties p = new Properties();

        p.put(ConfigConstant.OUTPUT_DEVICE_RESOLUTION_X, "10");
        p.put(ConfigConstant.OUTPUT_DEVICE_RESOLUTION_Y, "5");
        p.put(ConfigConstant.OUTPUT_DEVICE_SNAKE_CABELING, "true");
        p.put(ConfigConstant.OUTPUT_DEVICE_LAYOUT, "ROTATE_90");
        p.put(ConfigConstant.NULLOUTPUT_ROW1, "1");

        return p;
    }

    @Test
    public void testStrangeWiring() {
        ApplicationConfigurationHelper ph = new ApplicationConfigurationHelper(getConfig());
        Assert.assertEquals(10, ph.getDeviceXResolution());
        Assert.assertEquals(5, ph.getDeviceYResolution());

        PixelControllerStatusMBean pixConStat = new PixelControllerStatus((int) ph.parseFps());
        VisualState vs = VisualState.getInstance();
        List<ColorSet> cs = new ArrayList<ColorSet>();
        cs.add(new ColorSet("aa", new int[] { 0x111111, 0xffffff }));
        vs.init(new FileUtilsJunit(), ph, new SoundMinim(0), cs, new PresetServiceDummy());

        // load image HALF.JPG
        MessageProcessor.INSTANCE.processMsg(new String[] { ValidCommand.CHANGE_GENERATOR_A + "",
                "2" }, false, null);
        MessageProcessor.INSTANCE.processMsg(new String[] { ValidCommand.IMAGE + "", "half.jpg" },
                false, null);

        // for (int i = 0; i < 50; i++)
        vs.updateSystem(pixConStat);

        StrangeWiringOutput output = new StrangeWiringOutput(ph);
        output.prepareOutputBuffer();
        output.switchBuffers();
        output.prepareOutputBuffer();
        int[] b = output.getTransformedBuffer();
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
