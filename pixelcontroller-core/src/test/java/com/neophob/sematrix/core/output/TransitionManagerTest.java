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
import com.neophob.sematrix.core.properties.Configuration;
import com.neophob.sematrix.core.properties.ConfigConstant;
import com.neophob.sematrix.core.properties.ValidCommand;
import com.neophob.sematrix.core.resize.PixelControllerResize;
import com.neophob.sematrix.core.sound.SoundDummy;
import com.neophob.sematrix.core.visual.MatrixData;
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

        Configuration ph = new Configuration(p);
        Assert.assertEquals(2, ph.getDeviceXResolution());
        Assert.assertEquals(2, ph.getDeviceYResolution());

        PixelControllerStatusMBean pixConStat = new PixelControllerStatus((int) ph.parseFps());
        VisualState vs = VisualState.getInstance();
        List<IColorSet> cs = new ArrayList<IColorSet>();
        cs.add(new JunitColorSet());
        PresetService presetService = new PresetServiceImpl(new ArrayList<PresetSettings>());
        vs.init(new FileUtilsJunit(), ph, new SoundDummy(), cs, presetService);

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

        MatrixData matrix = vs.getMatrix();
        PixelControllerResize res = new PixelControllerResize();
        res.initAll();

        JunitOutput output = new JunitOutput(matrix, res, ph);

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
