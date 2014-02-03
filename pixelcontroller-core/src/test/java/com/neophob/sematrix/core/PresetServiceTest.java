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
package com.neophob.sematrix.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.junit.Assert;
import org.junit.Test;

import com.neophob.sematrix.core.glue.FileUtils;
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
import com.neophob.sematrix.core.sound.SoundDummy;
import com.neophob.sematrix.core.visual.OutputMapping;
import com.neophob.sematrix.core.visual.VisualState;
import com.neophob.sematrix.core.visual.color.ColorSet;
import com.neophob.sematrix.core.visual.color.IColorSet;

public class PresetServiceTest {

    private static final String PRESET = "CURRENT_VISUAL 0;CHANGE_GENERATOR_A 8;CHANGE_GENERATOR_B 3;CHANGE_EFFECT_A 0;CHANGE_EFFECT_B 0;CHANGE_MIXER 0;CURRENT_COLORSET RGB;CURRENT_VISUAL 1;CHANGE_GENERATOR_A 2;CHANGE_GENERATOR_B 12;CHANGE_EFFECT_A 0;CHANGE_EFFECT_B 0;CHANGE_MIXER 0;CURRENT_COLORSET Cake;CURRENT_VISUAL 2;CHANGE_GENERATOR_A 4;CHANGE_GENERATOR_B 10;CHANGE_EFFECT_A 0;CHANGE_EFFECT_B 0;CHANGE_MIXER 3;CURRENT_COLORSET Wayyou;CURRENT_OUTPUT 0;CHANGE_OUTPUT_FADER 1;CHANGE_OUTPUT_VISUAL 0;CURRENT_OUTPUT 1;CHANGE_OUTPUT_FADER 1;CHANGE_OUTPUT_VISUAL 0;CHANGE_BRIGHTNESS 100;GENERATOR_SPEED 40;CHANGE_ROTOZOOM -31;CHANGE_THRESHOLD_VALUE 150;TEXTDEF 10;ZOOMOPT 3;BLINKEN torus.bml;IMAGE _logo.gif;TEXTWR PIXELINVADERS;TEXTWR_OPTION 0;COLOR_SCROLL_OPT 14;BEAT_WORKMODE 1;CHANGE_SHUFFLER_SELECT  1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1;CHANGE_PRESET 0;presetname=Default;";

    @Test
    public void loadPreset() throws InterruptedException {
        // load custom preset
        PresetSettings preset = new PresetSettings();
        preset.setPreset(PRESET.split(";"));
        Assert.assertEquals("Default", preset.getName());

        List<PresetSettings> presets = new ArrayList<PresetSettings>();
        presets.add(preset);

        // init system
        PresetService presetService = new PresetServiceImpl(presets);
        FileUtils fileUtils = new FileUtilsJunit();
        MessageProcessor.INSTANCE.init(presetService, fileUtils);
        VisualState vs = VisualState.getInstance();
        List<IColorSet> colorSets = new ArrayList<IColorSet>();
        colorSets.add(new ColorSet("dummy", new int[] { 0, 255 }));
        Properties props = new Properties();
        props.put(ConfigConstant.NULLOUTPUT_ROW1, "3");
        ApplicationConfigurationHelper ph = new ApplicationConfigurationHelper(props);
        vs.init(fileUtils, ph, new SoundDummy(), colorSets, presetService);

        presetService.setSelectedPreset(0);

        // load preset
        MessageProcessor.INSTANCE.processMsg(new String[] { ValidCommand.LOAD_PRESET.toString() },
                false, null);

        PixelControllerStatusMBean statistic = new PixelControllerStatus(20);
        // verify preset
        vs.updateSystem(statistic);
        List<OutputMapping> outputMapping = vs.getAllOutputMappings();
        System.out.println(outputMapping.get(0));
        System.out.println(outputMapping.get(1));
        Assert.assertEquals(0, outputMapping.get(0).getFader().getNewVisual());
        Assert.assertEquals(0, outputMapping.get(1).getFader().getNewVisual());

        int[] tmp = vs.getVisual(0).getGenerator1().getBuffer();
        for (OutputMapping om : vs.getAllOutputMappings()) {
            for (int i = 0; i < 100; i++) {
                om.getFader().getBuffer(tmp, tmp);
            }
        }

        vs.updateSystem(statistic);
        System.out.println(outputMapping.get(0));
        System.out.println(outputMapping.get(1));
        Assert.assertEquals(0, outputMapping.get(0).getVisualId());
        Assert.assertEquals(0, outputMapping.get(1).getVisualId());
    }
}
