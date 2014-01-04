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
package com.neophob.sematrix.core.visual.generator.blinken;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.neophob.sematrix.core.visual.generator.blinken.jaxb.Blm;
import com.neophob.sematrix.core.visual.generator.blinken.jaxb.Frame;
import com.neophob.sematrix.core.visual.generator.blinken.jaxb.Row;

/**
 * some blinkenlights helper functions
 * 
 * @author michael vogt / neophob.com (c) 2010
 * 
 */
public final class BlinkenHelper {

    private static Logger log = Logger.getLogger(BlinkenHelper.class.getName());

    private BlinkenHelper() {
        // no instance allowed
    }

    /**
     * Structure of row data (http://blinkenlights.net/project/bml) Each single
     * row describes the pixel colour values in hexadecimal notation. If the
     * colour depth is between 1 and 4, one hexadecimal digit is used per colour
     * value (0-f). If the colour depth is between 5 and 8, two hexadecimal
     * digits are used per colour value (00-ff).
     * 
     * The is one value for each pixel, one after the other. In an RGB picture
     * with channels="3" there are three colour values in sequence.
     * 
     */
    public static BlinkenImage[] grabFrames(Blm blm, int color) throws NumberFormatException {
        int width = Integer.parseInt(blm.getWidth());
        int height = Integer.parseInt(blm.getHeight());
        int bits = Integer.parseInt(blm.getBits());
        float col = (float) (color & 255) / 255.f;

        int frames = blm.getFrame().size();
        BlinkenImage[] allFrames = new BlinkenImage[frames];

        for (int i = 0; i < frames; i++) {
            Frame f = blm.getFrame().get(i);
            BlinkenImage img = new BlinkenImage(width, height);
            for (Row r : f.getRow()) {
                int[] data;
                if (bits > 0 && bits < 5) {
                    // one char per color value
                    data = getDataFromOneCharRow(r.getvalue(), col);
                } else {
                    // two char per color value
                    data = getDataFromTwoCharRow(r.getvalue());
                }
                if (data.length != width) {
                    log.log(Level.WARNING, "Ooops: looks like here is an error: {0}!={1}",
                            new Object[] { width, data.length });
                }
                img.addData(data);
            }
            allFrames[i] = img;
        }

        return allFrames;
    }

    /**
     * convert string data to int[]
     * 
     * @param data
     * @param col
     * @return
     */
    private static int[] getDataFromOneCharRow(String data, float col) {
        int[] ret = new int[data.length()];
        int ofs = 0;

        for (char c : data.toCharArray()) {
            int i = Character.digit(c, 16);
            ret[ofs++] = (int) (i * col * 16);
        }
        return ret;
    }

    /**
     * convert string data to int[]
     * 
     * @param data
     * @return
     */
    private static int[] getDataFromTwoCharRow(String data) {
        int[] ret = new int[data.length() / 2];
        char[] convertedData = data.toCharArray();
        int dst = 0;
        int col;

        for (int i = 0; i < convertedData.length;) {
            col = (Character.digit(convertedData[i++], 16) << 4)
                    | Character.digit(convertedData[i++], 16);
            ret[dst++] = col << 16 | col << 8 | col;
        }
        return ret;
    }
}
