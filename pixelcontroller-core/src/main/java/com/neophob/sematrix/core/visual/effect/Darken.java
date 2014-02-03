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
package com.neophob.sematrix.core.visual.effect;

import com.neophob.sematrix.core.resize.Resize.ResizeName;
import com.neophob.sematrix.core.visual.MatrixData;

/**
 * Due the color scroll mechanism, 128 is the brightest color. so instead of a
 * regular color value 0..128..255 darken modifies the color to 0..127..0
 */
public class Darken extends Effect {

    /**
     * Instantiates a new inverter.
     * 
     * @param controller
     *            the controller
     */
    public Darken(MatrixData matrix) {
        super(matrix, EffectName.DARKEN, ResizeName.QUALITY_RESIZE);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.neophob.sematrix.core.effect.Effect#getBuffer(int[])
     */
    public int[] getBuffer(int[] buffer) {
        int[] ret = new int[buffer.length];
        for (int i = 0; i < buffer.length; i++) {
            int aa = buffer[i] & 0xff;
            if (aa > 127) {
                aa = (255 - aa) / 2;
            } else {
                aa /= 2;
            }

            ret[i] = aa;
        }
        return ret;
    }

}
