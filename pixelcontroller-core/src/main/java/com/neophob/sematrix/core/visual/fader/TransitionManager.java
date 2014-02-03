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
package com.neophob.sematrix.core.visual.fader;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.neophob.sematrix.core.visual.OutputMapping;
import com.neophob.sematrix.core.visual.VisualState;

/**
 * the transition manager handle smooth transition for the output visuals
 * 
 * @author michu
 * 
 */
public class TransitionManager {

    private static final Logger LOG = Logger.getLogger(TransitionManager.class.getName());

    private int[][] savedVisuals;
    private VisualState visualState;
    private Map<Integer, Integer> outputMapping = new HashMap<Integer, Integer>();

    /**
     * save current visual output, used for preset fading
     * 
     * @param col
     */
    public TransitionManager(VisualState visualState) {
        this.visualState = visualState;
        this.savedVisuals = new int[visualState.getAllVisuals().size()][];
        int i = 0;
        for (OutputMapping om : visualState.getAllOutputMappings()) {
            savedVisuals[i++] = visualState.getVisual(om.getVisualId()).getBuffer().clone();
        }

        LOG.log(Level.INFO, "Transition Manager created, saved " + i + " visual output buffer(s)");
    }

    public void addOutputMapping(int outputNr, int newVisualNr) {
        outputMapping.put(outputNr, newVisualNr);
    }

    /**
     * start crossfading
     * 
     * @param visualState
     */
    public void startCrossfader() {
        int i = 0;
        for (OutputMapping om : visualState.getAllOutputMappings()) {
            // set crossfader
            om.setFader(visualState.getPixelControllerFader().getPresetFader(1,
                    visualState.getFpsSpeed()));

            // set new visual
            int newVisualId = om.getVisualId();
            if (outputMapping.containsKey(i)) {
                newVisualId = outputMapping.get(i);
            }
            om.getFader().startFade(newVisualId, i, savedVisuals[i]);
            LOG.log(Level.INFO, "Started fader for output {0}, new Visual: {1}", new Object[] { i,
                    newVisualId, });
            i++;
        }
    }

}
