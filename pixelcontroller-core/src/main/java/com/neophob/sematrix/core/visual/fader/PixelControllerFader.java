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
package com.neophob.sematrix.core.visual.fader;

import com.neophob.sematrix.core.properties.ApplicationConfigurationHelper;
import com.neophob.sematrix.core.visual.MatrixData;
import com.neophob.sematrix.core.visual.fader.Fader.FaderName;

/**
 * 
 * @author michu
 * 
 */
public final class PixelControllerFader {

    private int presetLoadingFadeTime;
    private int visualFadeTime;
    private MatrixData matrix;
    private int fps;

    public PixelControllerFader(ApplicationConfigurationHelper ah, MatrixData matrix, int fps) {
        presetLoadingFadeTime = ah.getPresetLoadingFadeTime();
        visualFadeTime = ah.getVisualFadeTime();
        this.matrix = matrix;
        this.fps = fps;
    }

    /*
     * FADER ======================================================
     */

    /**
     * return a NEW INSTANCE of a fader.
     * 
     * @param faderName
     *            the fader name
     * @return the fader
     */
    public IFader getVisualFader(FaderName faderName) {
        switch (faderName) {
            case CROSSFADE:
                return new Crossfader(matrix, visualFadeTime, fps);
            case SWITCH:
                return new Switch(matrix, fps);
            case SLIDE_UPSIDE_DOWN:
                return new SlideUpsideDown(matrix, visualFadeTime, fps);
            case SLIDE_LEFT_RIGHT:
                return new SlideLeftRight(matrix, visualFadeTime, fps);
        }
        return null;
    }

    /**
     * return a fader with default duration
     * 
     * @param index
     * @return
     */
    public IFader getVisualFader(int index) {
        switch (index) {
            case 0:
                return new Switch(matrix, fps);
            case 1:
                return new Crossfader(matrix, visualFadeTime, fps);
            case 2:
                return new SlideUpsideDown(matrix, visualFadeTime, fps);
            case 3:
                return new SlideLeftRight(matrix, visualFadeTime, fps);
        }
        return null;
    }

    /**
     * return a fader with a specific duration
     * 
     * @param index
     * @return
     */
    public IFader getPresetFader(int index, int fps) {
        switch (index) {
            case 0:
                return new Switch(matrix, fps);
            case 1:
                return new Crossfader(matrix, presetLoadingFadeTime, fps);
            case 2:
                return new SlideUpsideDown(matrix, presetLoadingFadeTime, fps);
            case 3:
                return new SlideLeftRight(matrix, presetLoadingFadeTime, fps);
        }
        return null;
    }

    /**
     * 
     * @return
     */
    public int getFaderCount() {
        return FaderName.values().length;
    }

}
