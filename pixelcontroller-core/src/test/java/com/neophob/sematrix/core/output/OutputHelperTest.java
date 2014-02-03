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
package com.neophob.sematrix.core.output;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.neophob.sematrix.core.properties.ColorFormat;

/**
 * @author michu
 * 
 */
public class OutputHelperTest {

    @Test
    public void testFlip3x8() {
        int[] ret;
        int[] buffer = new int[] { 0, 0, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 0, 1, 1, 0, 2, 2, 0, 3, 3,
                0, 4, 4, };

        ret = OutputHelper.flipSecondScanline(buffer, 3, 8);
        assertEquals(0, ret[0]);
        assertEquals(3, ret[3]);
        assertEquals(2, ret[4]);
        assertEquals(1, ret[5]);
        assertEquals(4, ret[6]);
    }

    @Test
    public void testOutputMapping() {
        int[] ret;
        int[] mapping = new int[] { 3, 1, 4, 5, 2, 0 };
        int[] buffer = new int[] { 1, 2, 3, 4, 5, 6 };

        ret = OutputHelper.manualMapping(buffer, mapping, 3, 2);

        assertEquals(4, ret[0]);
        assertEquals(2, ret[1]);
        assertEquals(5, ret[2]);
        assertEquals(6, ret[3]);
        assertEquals(3, ret[4]);
        assertEquals(1, ret[5]);
    }

    @Test
    public void testOutputMappingWrong() {
        int[] mapping = new int[160];
        int[] buffer = new int[160];
        for (int i = 0; i < 160; i++) {
            mapping[i] = i + 1;
        }
        OutputHelper.manualMapping(buffer, mapping, 8, 20);
    }

    @Test
    public void testColorConvert() {
        // byte[] convertBufferTo24bit(int[] data, ColorFormat colorFormat)
        int[] data = new int[4]; // 2 pixel buffer
        data[0] = 0xffffff; // full white
        data[1] = 0x0000ff; // b
        data[2] = 0x00ff00; // g
        data[3] = 0xff0000; // r

        // -- 24 bit --
        byte[] result = OutputHelper.convertBufferTo24bit(data, ColorFormat.BGR);

        assertEquals(data.length * 3, result.length); // verify size of array

        assertEquals((byte) 255, result[0]); // verify white
        assertEquals((byte) 255, result[1]);
        assertEquals((byte) 255, result[2]);

        assertEquals((byte) 255, result[3]); // verify blue
        assertEquals((byte) 0, result[4]);
        assertEquals((byte) 0, result[5]);

        assertEquals((byte) 0, result[6]); // verify green
        assertEquals((byte) 255, result[7]);
        assertEquals((byte) 0, result[8]);

        assertEquals((byte) 0, result[9]); // verify red
        assertEquals((byte) 0, result[10]);
        assertEquals((byte) 255, result[11]);

        result = OutputHelper.convertBufferTo24bit(data, ColorFormat.RGB);
        assertEquals(data.length * 3, result.length); // verify size of array
        assertEquals((byte) 0, result[3]); // verify blue
        assertEquals((byte) 0, result[4]);
        assertEquals((byte) 255, result[5]);

        // -- 15 bit --
        result = OutputHelper.convertBufferTo15bit(data, ColorFormat.RBG);
        assertEquals(data.length * 2, result.length); // verify size of array, 1
                                                      // rgb needs two bytes

        assertEquals((byte) 127, result[0]); // verify white 0111 1111 1111 1111
        assertEquals((byte) 255, result[1]);

        assertEquals((byte) 3, result[2]); // verify blue 0000 0011 1110 0000
        assertEquals((byte) 224, result[3]);

        assertEquals((byte) 0, result[4]); // verify green 0000 0000 0001 1111
        assertEquals((byte) 31, result[5]);

        assertEquals((byte) 124, result[6]); // verify red 0111 1100 0000 0000
        assertEquals((byte) 0, result[7]);

        result = OutputHelper.convertBufferTo15bit(data, ColorFormat.RGB);
        assertEquals(data.length * 2, result.length); // verify size of array
        assertEquals((byte) 0, result[2]); // verify blue
        assertEquals((byte) 31, result[3]);
    }

}
