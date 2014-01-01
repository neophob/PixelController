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
package com.neophob.sematrix.core.visual.generator;

import com.neophob.sematrix.core.resize.Resize.ResizeName;
import com.neophob.sematrix.core.visual.MatrixData;

/**
 * Plasma Generator, ripped from openprocessing.org
 * 
 * @author mvogt
 */
public class Plasma2 extends Generator {

    private static final float DEG_TO_RAD = (float) Math.PI / 180.0f;

    /** The frame count. */
    private int frameCount;

    /**
     * Instantiates a new plasma2.
     * 
     * @param controller
     *            the controller
     */
    public Plasma2(MatrixData matrix) {
        super(matrix, GeneratorName.PLASMA, ResizeName.QUALITY_RESIZE);
        frameCount = 1;
    }

    /**
     * ripped from PApplet
     * 
     * @param degrees
     * @return
     */
    private float radians(float degrees) {
        return degrees * DEG_TO_RAD;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.neophob.sematrix.core.generator.Generator#update()
     */
    @Override
    public void update(int amount) {
        float xc = 20;
        // This runs plasma as fast as your computer can handle
        frameCount += amount;
        int timeDisplacement = frameCount;

        // No need to do this math for every pixel
        float calculation1 = (float) Math.sin(radians(timeDisplacement * 0.61655617f));
        float calculation2 = (float) Math.sin(radians(timeDisplacement * -3.6352262f));

        int aaa = 128;
        int ySize = internalBufferYSize;
        // Plasma algorithm
        for (int x = 0; x < internalBufferXSize; x++, xc++) {
            float yc = 20;
            float s1 = aaa + aaa * (float) Math.sin(radians(xc) * calculation1);

            for (int y = 0; y < ySize; y++, yc++) {
                float s2 = aaa + aaa * (float) Math.sin(radians(yc) * calculation2);
                float s3 = aaa + aaa
                        * (float) Math.sin(radians((xc + yc + timeDisplacement * 3) / 2));
                float s = (s1 + s2 + s3) / 255;

                int aa = (int) (s * 255f + 0.5f);
                this.internalBuffer[y * internalBufferXSize + x] = aa % 0xff;
            }
        }
    }
}