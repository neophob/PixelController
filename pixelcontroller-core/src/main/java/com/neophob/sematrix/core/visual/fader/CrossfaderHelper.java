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

/**
 * The Class CrossfaderHelper.
 * 
 * @author michu
 */
public final class CrossfaderHelper {

    /**
     * Instantiates a new crossfader helper.
     */
    private CrossfaderHelper() {
        // no instance allowed
    }

    /**
     * Gets the buffer.
     * 
     * @param f
     *            the f
     * @param oldBuffer
     *            the old buffer
     * @param newBuffer
     *            the new buffer
     * @return the buffer
     */
    public static int[] getBuffer(float f, int[] oldBuffer, int[] newBuffer) {
        int[] ret = new int[oldBuffer.length];
        int oTmp, nTmp;
        short or, og, ob;
        short nr, ng, nb;

        for (int i = 0; i < oldBuffer.length; i++) {
            oTmp = oldBuffer[i];
            nTmp = newBuffer[i];

            or = (short) (((oTmp >> 16) & 255) * (1.0f - f));
            og = (short) (((oTmp >> 8) & 255) * (1.0f - f));
            ob = (short) ((oTmp & 255) * (1.0f - f));

            nr = (short) (((nTmp >> 16) & 255) * f);
            ng = (short) (((nTmp >> 8) & 255) * f);
            nb = (short) ((nTmp & 255) * f);

            ret[i] = ((or << 16) | (og << 8) | ob) + ((nr << 16) | (ng << 8) | nb);
        }
        return ret;
    }

    public static int[] getBuffer8Bit(float f, int[] oldBuffer, int[] newBuffer) {
        int[] ret = new int[oldBuffer.length];

        int oTmp, nTmp;

        for (int i = 0; i < oldBuffer.length; i++) {
            oTmp = oldBuffer[i] & 0xff;
            nTmp = newBuffer[i] & 0xff;

            ret[i] = (int) (oTmp * (1.0f - f) + nTmp * f);
        }

        return ret;
    }
}
