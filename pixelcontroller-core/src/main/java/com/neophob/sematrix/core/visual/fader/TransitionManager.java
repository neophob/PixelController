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

import com.neophob.sematrix.core.visual.OutputMapping;
import com.neophob.sematrix.core.visual.VisualState;

/**
 * the transition manager handle smooth transition for the output visuals
 * 
 * @author michu
 * 
 */
public class TransitionManager {

    private int[][] savedVisuals;
    private VisualState visualState;
    private float fps;

    /**
     * save current visual output, used for preset fading
     * 
     * @param col
     */
    public TransitionManager(VisualState visualState, float configuredFps) {
        this.visualState = visualState;
        savedVisuals = new int[visualState.getAllVisuals().size()][];
        this.fps = configuredFps;
        int i = 0;
        for (OutputMapping om : visualState.getAllOutputMappings()) {
            savedVisuals[i++] = visualState.getVisual(om.getVisualId()).getBuffer().clone();
        }
    }

    /**
     * start crossfading
     * 
     * @param visualState
     */
    public void startCrossfader() {
        int i = 0;
        for (OutputMapping om : visualState.getAllOutputMappings()) {
            om.setFader(visualState.getPixelControllerFader().getPresetFader(1,
                    (int) (visualState.getFpsSpeed() * fps)));
            om.getFader().startFade(om.getVisualId(), savedVisuals[i++]);
        }
    }

}
