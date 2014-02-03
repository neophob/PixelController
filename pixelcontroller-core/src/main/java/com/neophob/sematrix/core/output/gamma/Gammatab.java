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
package com.neophob.sematrix.core.output.gamma;

/**
 * create gamma tab for color correction code ripped from
 * https://github.com/scottjgibson/lightingPi/blob/master/lightingPi.py
 * 
 * @author michu
 * 
 */
public final class Gammatab {

    private Gammatab() {
        // no inatance
    }

    // use it for lpd6803 based led devices
    private static int[] gamma20 = generateGammaTab(2.0f);

    private static int[] gamma22 = generateGammaTab(2.2f);

    // use it for ws2801 based led devices
    private static int[] gamma25 = generateGammaTab(2.5f);

    private static int[] gamma30 = generateGammaTab(3.0f);

    // gamma correction found in ledstyles.de forum
    private static int[] aspecialGammaTab1 = { 0, 0, 1, 1, 1, 1, 1, 2, 2, 2, 2, 3, 3, 3, 4, 4, 4,
            5, 5, 5, 6, 6, 7, 7, 8, 8, 9, 9, 10, 10, 11, 11, 12, 12, 13, 13, 14, 14, 15, 15, 16,
            16, 17, 17, 18, 18, 19, 19, 20, 20, 21, 21, 22, 22, 23, 23, 24, 24, 25, 25, 26, 27, 28,
            29, 30, 30, 31, 32, 33, 34, 35, 36, 36, 37, 38, 39, 40, 41, 42, 43, 43, 44, 45, 46, 47,
            48, 49, 50, 51, 52, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 68,
            69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 88, 89, 90, 91,
            92, 93, 94, 95, 96, 97, 98, 99, 100, 102, 103, 104, 105, 106, 107, 108, 109, 110, 112,
            113, 114, 115, 116, 117, 119, 120, 121, 122, 123, 124, 126, 127, 128, 129, 130, 132,
            133, 134, 135, 136, 138, 139, 140, 141, 142, 144, 145, 146, 147, 149, 150, 151, 152,
            154, 155, 156, 158, 159, 160, 161, 162, 164, 165, 167, 168, 169, 171, 172, 173, 174,
            176, 177, 178, 180, 181, 182, 184, 185, 187, 188, 189, 191, 192, 193, 195, 196, 197,
            199, 200, 202, 203, 204, 206, 207, 208, 210, 211, 213, 214, 216, 217, 218, 220, 221,
            223, 224, 226, 227, 228, 230, 231, 233, 234, 236, 237, 239, 240, 242, 243, 245, 246,
            248, 249, 251, 252, 254, 255 };

    /**
     * create gammatab for lpd6803 based led strips
     * 
     * @return
     */
    private static int[] generateGammaTab(float gamma) {
        int[] ret = new int[256];

        for (int i = 0; i < 256; i++) {
            ret[i] = (int) (Math.pow((float) (i) / 255.0f, gamma) * 255.0f + 0.5f);
        }

        return ret;
    }

    /**
     * apply brightness level and gamma correction
     * 
     * @param buffer
     * @param brightness
     * @return
     */
    public static int[] applyBrightnessAndGammaTab(int[] buffer, GammaType type, float brightness) {
        int[] ret = new int[buffer.length];
        int ofs = 0;
        int r, g, b;

        for (int n = 0; n < buffer.length; n++) {
            int tmp = buffer[ofs];
            r = (int) ((tmp >> 16) & 255);
            g = (int) ((tmp >> 8) & 255);
            b = (int) (tmp & 255);

            // apply brightness
            r = (int) (r * brightness);
            g = (int) (g * brightness);
            b = (int) (b * brightness);

            // apply gamma
            switch (type) {
                case GAMMA_20:
                    r = gamma20[r];
                    g = gamma20[g];
                    b = gamma20[b];
                    break;

                case GAMMA_22:
                    r = gamma22[r];
                    g = gamma22[g];
                    b = gamma22[b];
                    break;

                case GAMMA_25:
                    r = gamma25[r];
                    g = gamma25[g];
                    b = gamma25[b];
                    break;

                case GAMMA_30:
                    r = gamma30[r];
                    g = gamma30[g];
                    b = gamma30[b];
                    break;

                case SPECIAL1:
                    r = aspecialGammaTab1[r];
                    g = aspecialGammaTab1[g];
                    b = aspecialGammaTab1[b];
                    break;

                case NONE:
                default:
                    break;
            }

            ret[ofs++] = (r << 16) | (g << 8) | b;
        }

        return ret;
    }

}
