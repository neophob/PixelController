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
import com.neophob.sematrix.core.sound.SoundDummy;
import com.neophob.sematrix.core.visual.VisualState;
import com.neophob.sematrix.core.visual.color.IColorSet;
import com.neophob.sematrix.core.visual.fader.TransitionManager;

public class TransitionManagerTest {

    @Test
    public void testTransitionManager() {
        Properties p = new Properties();
        p.put(ConfigConstant.OUTPUT_DEVICE_RESOLUTION_X, "2");
        p.put(ConfigConstant.OUTPUT_DEVICE_RESOLUTION_Y, "2");
        p.put(ConfigConstant.OUTPUT_DEVICE_SNAKE_CABELING, "true");
        p.put(ConfigConstant.OUTPUT_DEVICE_LAYOUT, "NO_ROTATE");
        p.put(ConfigConstant.NULLOUTPUT_ROW1, "1");

        ApplicationConfigurationHelper ph = new ApplicationConfigurationHelper(p);
        Assert.assertEquals(2, ph.getDeviceXResolution());
        Assert.assertEquals(2, ph.getDeviceYResolution());

        PixelControllerStatusMBean pixConStat = new PixelControllerStatus((int) ph.parseFps());
        VisualState vs = VisualState.getInstance();
        List<IColorSet> cs = new ArrayList<IColorSet>();
        cs.add(new JunitColorSet());
        vs.init(new FileUtilsJunit(), ph, new SoundDummy(), cs, new PresetServiceDummy());

        MessageProcessor.INSTANCE.processMsg(
                new String[] { ValidCommand.CURRENT_VISUAL + "", "0" }, false, null);
        MessageProcessor.INSTANCE.processMsg(new String[] { ValidCommand.CURRENT_COLORSET + "",
                "JunitColorSet" }, false, null);
        MessageProcessor.INSTANCE.processMsg(new String[] { ValidCommand.CHANGE_GENERATOR_A + "",
                "2" }, false, null);
        MessageProcessor.INSTANCE.processMsg(
                new String[] { ValidCommand.CHANGE_EFFECT_A + "", "0" }, false, null);
        MessageProcessor.INSTANCE.processMsg(new String[] { ValidCommand.IMAGE + "", "half.jpg" },
                false, null);
        MessageProcessor.INSTANCE.processMsg(new String[] { ValidCommand.CHANGE_MIXER + "", "0" },
                false, null);
        MessageProcessor.INSTANCE.processMsg(new String[] { ValidCommand.CHANGE_BRIGHTNESS + "",
                "100" }, false, null);

        // for (int i = 0; i < 50; i++)
        vs.updateSystem(pixConStat);

        JunitOutput output = new JunitOutput(ph);

        PixelControllerOutput pixOutput = new PixelControllerOutput(pixConStat);
        pixOutput.addOutput(output);
        pixOutput.update();
        printArray(output.getTransformedBuffer());

        TransitionManager transition = new TransitionManager(vs);
        vs.setBrightness(0);
        transition.startCrossfader();
        pixOutput.update();
        printArray(output.getTransformedBuffer());

        pixOutput.update();
        printArray(output.getTransformedBuffer());

        pixOutput.update();
        printArray(output.getTransformedBuffer());
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
