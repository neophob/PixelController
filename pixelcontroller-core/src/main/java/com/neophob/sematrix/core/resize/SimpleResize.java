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
package com.neophob.sematrix.core.resize;

import java.awt.image.BufferedImage;

/**
 * This filter is optimized for pixel oriented images.
 * 
 * @author michu
 */
class SimpleResize extends Resize {

    /**
     * Instantiates a new pixel resize.
     * 
     * @param controller
     *            the controller
     */
    public SimpleResize() {
        super(ResizeName.SIMPLE_RESIZE);
    }

    @Override
    public int[] getBuffer(BufferedImage bi, int newX, int newY) {
        return null;
    }

    @Override
    public int[] resizeImage(int[] buffer, int currentXSize, int currentYSize, int newX, int newY) {
        int[] rawOutput = new int[newX * newY];

        // YD compensates for the x loop by subtracting the width back out
        int YD = (currentYSize / newY) * currentXSize - currentXSize;
        int YR = currentYSize % newY;
        int XD = currentXSize / newX;
        int XR = currentXSize % newX;
        int outOffset = 0;
        int inOffset = 0;

        for (int y = newY, YE = 0; y > 0; y--) {
            for (int x = newX, XE = 0; x > 0; x--) {
                rawOutput[outOffset++] = buffer[inOffset];
                inOffset += XD;
                XE += XR;
                if (XE >= newX) {
                    XE -= newX;
                    inOffset++;
                }
            }
            inOffset += YD;
            YE += YR;
            if (YE >= newY) {
                YE -= newY;
                inOffset += currentXSize;
            }
        }
        return rawOutput;
    }
}
