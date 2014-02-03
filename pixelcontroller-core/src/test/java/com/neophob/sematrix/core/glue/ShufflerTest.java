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
package com.neophob.sematrix.core.glue;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

import org.junit.Assert;
import org.junit.Test;

import com.neophob.sematrix.core.jmx.PixelControllerStatus;
import com.neophob.sematrix.core.jmx.PixelControllerStatusMBean;
import com.neophob.sematrix.core.preset.PresetService;
import com.neophob.sematrix.core.preset.PresetServiceImpl;
import com.neophob.sematrix.core.preset.PresetSettings;
import com.neophob.sematrix.core.properties.ApplicationConfigurationHelper;
import com.neophob.sematrix.core.sound.ISound;
import com.neophob.sematrix.core.sound.SoundDummy;
import com.neophob.sematrix.core.visual.VisualState;
import com.neophob.sematrix.core.visual.color.ColorSet;
import com.neophob.sematrix.core.visual.color.IColorSet;

public class ShufflerTest {

    private static final Logger LOG = Logger.getLogger(ShufflerTest.class.getName());

    @Test
    public void testManualShuffler() {
        ISound sound = new SoundDummy();
        List<IColorSet> colorSets = new ArrayList<IColorSet>();
        colorSets.add(new ColorSet("Blah", new int[] { 1, 2, 3 }));
        VisualState vs = VisualState.getInstance();
        ApplicationConfigurationHelper ph = new ApplicationConfigurationHelper(new Properties());
        PresetService presetService = new PresetServiceImpl(new ArrayList<PresetSettings>());
        vs.init(new FileUtilsJunit(), ph, sound, colorSets, presetService);
        PixelControllerStatusMBean pixConStat = new PixelControllerStatus(20);
        vs.updateSystem(pixConStat);

        List<Boolean> shufflerSelect = new ArrayList<Boolean>();
        Assert.assertFalse(RandomModeShuffler.shuffleStuff(shufflerSelect, true, true, true));
        for (int i = 0; i < 20; i++) {
            shufflerSelect.add(true);
        }
        for (int i = 0; i < 10; i++) {
            LOG.info("---------");
            RandomModeShuffler.shuffleStuff(shufflerSelect, true, true, true);
        }

        for (int i = 0; i < 10; i++) {
            LOG.info("---------");
            Shuffler.manualShuffleStuff(vs);
        }
    }

}
