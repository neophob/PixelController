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

import com.neophob.sematrix.core.properties.DeviceConfig;

/**
 * verify the rotate buffer code
 * 
 * @author michu
 * 
 */
public class RotateBufferTest {

    private static final int RESOLUTION = 4;

    public static void dumpBuffer(int[] ret, String txt) {
        dumpBuffer(ret, txt, RESOLUTION);
    }

    public static void dumpBuffer(int[] ret, String txt, int num) {
        int a = 0;
        for (int r : ret) {
            System.out.print(r + ", ");
            a++;
            if (a == num) {
                System.out.println();
                a = 0;
            }
        }
        System.out.println("<<< " + txt);
        System.out.println();
    }

    @Test
    public void rotateTest4x4() {
        int[] ret;
        int[] buffer = new int[] {
        /* first line */
        1, 2, 3, 0,
        /* */
        0, 0, 0, 0,
        /* */
        0, 0, 5, 0,
        /* */
        0, 0, 0, 9 };

        ret = RotateBuffer.transformImage(buffer, DeviceConfig.NO_ROTATE, RESOLUTION, RESOLUTION);
        dumpBuffer(ret, "NO_ROTATE");
        assertEquals(1, ret[0]);
        assertEquals(2, ret[1]);

        ret = RotateBuffer.transformImage(buffer, DeviceConfig.FLIPPEDY, RESOLUTION, RESOLUTION);
        dumpBuffer(ret, "FLIPPEDY");
        assertEquals(1, ret[3]);
        assertEquals(2, ret[2]);

        ret = RotateBuffer.transformImage(buffer, DeviceConfig.ROTATE_90, RESOLUTION, RESOLUTION);
        dumpBuffer(ret, "ROTATE_90");
        assertEquals(1, ret[3]);
        assertEquals(2, ret[7]);
        ret = RotateBuffer.transformImage(buffer, DeviceConfig.ROTATE_90_FLIPPEDY, RESOLUTION,
                RESOLUTION);
        dumpBuffer(ret, "ROTATE_90_FLIPPEDY");
        assertEquals(1, ret[0]);
        assertEquals(2, ret[4]);

        ret = RotateBuffer.transformImage(buffer, DeviceConfig.ROTATE_180, RESOLUTION, RESOLUTION);
        dumpBuffer(ret, "ROTATE_180");
        assertEquals(1, ret[15]);
        assertEquals(2, ret[14]);
        ret = RotateBuffer.transformImage(buffer, DeviceConfig.ROTATE_180_FLIPPEDY, RESOLUTION,
                RESOLUTION);
        dumpBuffer(ret, "ROTATE_180_FLIPPEDY");
        assertEquals(1, ret[12]);
        assertEquals(2, ret[13]);

        ret = RotateBuffer.transformImage(buffer, DeviceConfig.ROTATE_270, RESOLUTION, RESOLUTION);
        dumpBuffer(ret, "ROTATE_270");
        assertEquals(1, ret[12]);
        assertEquals(2, ret[8]);
    }

    @Test
    public void rotateTest8x4() {
        int[] ret;
        int[] buffer = new int[] {
        /* */
        1, 2, 3, 0, 0, 0, 0, 0,
        /* */
        0, 0, 0, 0, 0, 0, 0, 0,
        /* */
        0, 0, 0, 0, 0, 0, 5, 0,
        /* */
        0, 0, 0, 0, 0, 0, 0, 4 };

        ret = RotateBuffer.transformImage(buffer, DeviceConfig.NO_ROTATE, 8, 4);
        assertEquals(1, ret[0]);
        assertEquals(3, ret[2]);

        ret = RotateBuffer.transformImage(buffer, DeviceConfig.ROTATE_90, 8, 4);
        dumpBuffer(ret, "ROTATE_90", 8);
        assertEquals(1, ret[7]);
        assertEquals(2, ret[15]);
        assertEquals(4, ret[24]);
        ret = RotateBuffer.transformImage(buffer, DeviceConfig.ROTATE_90_FLIPPEDY, 8, 4);
        dumpBuffer(ret, "ROTATE_90_FLIPPEDY", 8);
        assertEquals(1, ret[0]);
        assertEquals(2, ret[8]);
        assertEquals(4, ret[31]);

        ret = RotateBuffer.transformImage(buffer, DeviceConfig.ROTATE_180, 8, 4);
        dumpBuffer(ret, "ROTATE_180", 8);
        assertEquals(1, ret[31]);
        assertEquals(2, ret[30]);
        assertEquals(4, ret[0]);
        ret = RotateBuffer.transformImage(buffer, DeviceConfig.ROTATE_180_FLIPPEDY, 8, 4);
        dumpBuffer(ret, "ROTATE_180_FLIPPEDY", 8);
        assertEquals(1, ret[24]);
        assertEquals(2, ret[25]);
        assertEquals(4, ret[7]);

        ret = RotateBuffer.transformImage(buffer, DeviceConfig.ROTATE_270, 8, 4);
        assertEquals(1, ret[24]);
        assertEquals(2, ret[16]);
        assertEquals(4, ret[7]);
    }

}
