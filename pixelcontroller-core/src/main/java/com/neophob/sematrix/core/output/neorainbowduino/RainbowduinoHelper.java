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
package com.neophob.sematrix.core.output.neorainbowduino;

/**
 * Various Helper Methods <br>
 * <br>
 * part of the neorainbowduino library
 * 
 * @author Michael Vogt / neophob.com
 * 
 */
public class RainbowduinoHelper {

    private static final int BUFFERSIZE = Rainbowduino.NR_OF_LED_HORIZONTAL
            * Rainbowduino.NR_OF_LED_VERTICAL;

    // the home made gamma table - please note:
    // the rainbowduino has a color resoution if 4096 colors (12bit)
    private static final int[] GAMMA_TAB = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 16, 16,
            16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16,
            16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32,
            32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32,
            32, 32, 32, 32, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48,
            48, 48, 48, 48, 48, 48, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64,
            64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 80, 80, 80, 80, 80, 80, 80, 80, 80, 80,
            80, 80, 80, 80, 80, 80, 96, 96, 96, 96, 96, 96, 96, 96, 96, 96, 96, 96, 96, 96, 96, 96,
            112, 112, 112, 112, 112, 112, 112, 112, 128, 128, 128, 128, 128, 128, 128, 128, 144,
            144, 144, 144, 144, 144, 144, 144, 160, 160, 160, 160, 160, 160, 160, 160, 176, 176,
            176, 176, 176, 176, 176, 176, 192, 192, 192, 192, 192, 192, 192, 192, 208, 208, 208,
            208, 224, 224, 224, 224, 240, 240, 240, 240, 240, 255, 255, 255 };

    /**
	 * 
	 */
    private RainbowduinoHelper() {
        // no instance allowed
    }

    /**
     * convert rgb image data to rainbowduino compatible format format 8x8x4
     * 
     * @param data
     *            the rgb image as int[64]
     * @return rainbowduino compatible format as byte[3*8*4]
     */
    public static byte[] convertRgbToRainbowduino(int[] data) throws IllegalArgumentException {
        if (data == null) {
            throw new IllegalArgumentException("data is null!");
        }
        if (data.length != 64) {
            throw new IllegalArgumentException("data lenght must be 64 bytes!");
        }
        byte[] converted = new byte[3 * 8 * 4];
        int[] r = new int[BUFFERSIZE];
        int[] g = new int[BUFFERSIZE];
        int[] b = new int[BUFFERSIZE];
        int tmp;
        int ofs = 0;
        int dst;

        // step#1: split up r/g/b and apply gammatab
        for (int n = 0; n < BUFFERSIZE; n++) {
            // one int contains the rgb color
            tmp = data[ofs];

            // the buffer on the rainbowduino takes GRB, not RGB
            g[ofs] = GAMMA_TAB[(int) ((tmp >> 16) & 255)]; // r
            r[ofs] = GAMMA_TAB[(int) ((tmp >> 8) & 255)]; // g
            b[ofs] = GAMMA_TAB[(int) (tmp & 255)]; // b
            ofs++;
        }
        // step#2: convert 8bit to 4bit
        // Each color byte, aka two pixels side by side, gives you 4 bit
        // brightness control,
        // first 4 bits for the left pixel and the last 4 for the right pixel.
        // -> this means a value from 0 (min) to 15 (max) is possible for each
        // pixel
        ofs = 0;
        dst = 0;
        for (int i = 0; i < 32; i++) {
            // 240 = 11110000 - delete the lower 4 bits, then add the (shr-ed)
            // 2nd color
            converted[00 + dst] = (byte) (((r[ofs] & 240) + (r[ofs + 1] >> 4)) & 255); // r
            converted[32 + dst] = (byte) (((g[ofs] & 240) + (g[ofs + 1] >> 4)) & 255); // g
            converted[64 + dst] = (byte) (((b[ofs] & 240) + (b[ofs + 1] >> 4)) & 255); // b

            ofs += 2;
            dst++;
        }

        return converted;
    }

}
