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
/**
 ˚ * Copyright (C) 2011 Michael Vogt <michu@neophob.com>
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

import java.util.logging.Level;
import java.util.logging.Logger;

import com.neophob.sematrix.core.visual.MatrixData;

/**
 * crossfader.
 * 
 * @author michu
 */
public class Crossfader extends Fader {

    private static final transient Logger LOG = Logger.getLogger(Crossfader.class.getName());

    /**
     * Instantiates a new crossfader.
     */
    public Crossfader(MatrixData matrix, float fps) {
        this(matrix, DEFAULT_FADER_DURATION, fps);
    }

    /**
     * Instantiates a new crossfader.
     * 
     * @param time
     *            the time
     */
    public Crossfader(MatrixData matrix, int time, float fps) {
        super(matrix, FaderName.CROSSFADE, time, fps);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.neophob.sematrix.core.fader.Fader#getBuffer(int[])
     */
    @Override
    public int[] getBuffer(int[] visual1Buffer, int[] visual2Buffer) {
        currentStep++;

        try {
            if (super.isDone()) {
                return visual2Buffer;
            }

            if (presetFader) {
                return CrossfaderHelper.getBuffer(getCurrentStep(), oldBuffer, visual2Buffer);
            }

            return CrossfaderHelper.getBuffer(getCurrentStep(), visual1Buffer, visual2Buffer);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "getBuffer failed, ignore error", e);
            super.setDone();
            return visual1Buffer;
        }
    }

}
