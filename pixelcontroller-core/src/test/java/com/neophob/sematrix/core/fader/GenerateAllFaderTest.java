/**
 * Copyright (C) 2011-2013 Michael Vogt <michu@neophob.com>
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
package com.neophob.sematrix.core.fader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.junit.Test;

import com.neophob.sematrix.core.properties.ApplicationConfigurationHelper;
import com.neophob.sematrix.core.visual.MatrixData;
import com.neophob.sematrix.core.visual.Visual;
import com.neophob.sematrix.core.visual.VisualState;
import com.neophob.sematrix.core.visual.color.ColorSet;
import com.neophob.sematrix.core.visual.color.IColorSet;
import com.neophob.sematrix.core.visual.effect.Effect;
import com.neophob.sematrix.core.visual.effect.PassThru;
import com.neophob.sematrix.core.visual.fader.IFader;
import com.neophob.sematrix.core.visual.fader.PixelControllerFader;
import com.neophob.sematrix.core.visual.generator.Generator;
import com.neophob.sematrix.core.visual.generator.PassThruGen;
import com.neophob.sematrix.core.visual.mixer.Mixer;
import com.neophob.sematrix.core.visual.mixer.PassThruMixer;

public class GenerateAllFaderTest {

    private IColorSet col;
    private ApplicationConfigurationHelper ph;

    @Test
    public void verifyGeneratorsDoNotCrash() {
        final int maxResolution = 17;

        String rootDir = System.getProperty("buildDirectory");
        if (rootDir == null) {
            // if unit test is run in eclipse
            rootDir = "." + File.separatorChar;
        }
        ph = new ApplicationConfigurationHelper(new Properties());
        col = new ColorSet("test", new int[] { 1, 2, 3 });

        for (int x = 1; x < maxResolution; x++) {
            for (int y = 1; y < maxResolution; y++) {
                testWithResolution(x, y);
                testWithResolution(y, x);
            }
        }

    }

    private void testWithResolution(int x, int y) {
        MatrixData matrix = new MatrixData(x, y);

        List<Visual> vlist = new ArrayList<Visual>();
        Visual v = createVisual(matrix, col);
        vlist.add(v);
        VisualState.getInstance().setAllVisuals(vlist);

        PixelControllerFader pcf = new PixelControllerFader(ph, matrix);
        for (int i = 0; i < 4; i++) {
            IFader f = pcf.getVisualFader(i, 1);
            f.startFade(0, 0);
            f.getBuffer(v.getBuffer(), v.getBuffer());
            f.cleanUp();

            f = pcf.getPresetFader(i, 1);
            f.startFade(0, v.getBuffer());
            f.getBuffer(v.getBuffer(), v.getBuffer());
        }

    }

    private Visual createVisual(MatrixData matrix, IColorSet col) {
        Generator g = new PassThruGen(matrix);
        Effect e = new PassThru(matrix);
        Mixer m = new PassThruMixer();
        return new Visual(g, e, m, col);
    }

}
