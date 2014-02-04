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

import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.neophob.sematrix.core.config.Config;
import com.neophob.sematrix.core.glue.FileUtils;
import com.neophob.sematrix.core.glue.FileUtilsJunit;
import com.neophob.sematrix.core.listener.MessageProcessor;
import com.neophob.sematrix.core.output.JunitColorSet;
import com.neophob.sematrix.core.preset.PresetFactory;
import com.neophob.sematrix.core.preset.PresetService;
import com.neophob.sematrix.core.preset.PresetServiceImpl;
import com.neophob.sematrix.core.preset.PresetSettings;
import com.neophob.sematrix.core.properties.ApplicationConfigurationHelper;
import com.neophob.sematrix.core.properties.ValidCommand;
import com.neophob.sematrix.core.sound.ISound;
import com.neophob.sematrix.core.visual.VisualState;
import com.neophob.sematrix.core.visual.color.IColorSet;

@RunWith(MockitoJUnitRunner.class)
public class PresetTest {

    private static final Logger LOG = Logger.getLogger(PresetTest.class.getName());

    @Mock
    Config config;

    @Mock
    ApplicationConfigurationHelper oldConfig;

    @Mock
    ISound sound;

    @Test
    public void loadAllPresetTest() {
        FileUtils fu = new FileUtilsJunit();
        List<PresetSettings> presets = PresetFactory.loadPresetsFile(fu.getDataDir());
        PresetService ps = new PresetServiceImpl(presets);
        Assert.assertTrue(ps.getAllPresets().size() > 0);
        List<IColorSet> col = new ArrayList<IColorSet>();
        col.add(new JunitColorSet());

        when(oldConfig.getNrOfScreens()).thenReturn(1);

        VisualState.getInstance().init(fu, oldConfig, config, sound, col, ps);

        MessageProcessor.INSTANCE.init(ps, fu);
        for (int i = 0; i < ps.getAllPresets().size(); i++) {
            LOG.info("Load Preset " + i);
            ps.setSelectedPreset(i);
            MessageProcessor.INSTANCE.processMsg(
                    new String[] { ValidCommand.LOAD_PRESET.toString() }, false, null);
        }
    }
}
