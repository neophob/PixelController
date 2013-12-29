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

import com.neophob.sematrix.core.glue.ShufflerOffset;
import com.neophob.sematrix.core.resize.Resize.ResizeName;
import com.neophob.sematrix.core.visual.MatrixData;
import com.neophob.sematrix.core.visual.VisualState;

/**
 * simplex noise generator
 * 
 * @author mvogt
 */
public class Noise extends Generator {

    /** The Constant RESIZE_TYP. */
    private static final ResizeName RESIZE_TYP = ResizeName.QUALITY_RESIZE;

    private static final float MUL = 200;
    private static final float SN = 0.6f;

    private float walk;

    /**
     * Instantiates a new image viewer
     * 
     * @param controller
     *            the controller
     * @param filename
     *            the filename
     */
    public Noise(MatrixData matrix) {
        super(matrix, GeneratorName.NOISE, RESIZE_TYP);
        walk = 0;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.neophob.sematrix.core.generator.Generator#update()
     */
    @Override
    public void update() {
        int ofs = 0;
        float xx, yy;
        float xstep = 1.0f / internalBufferXSize;
        float ystep = 1.0f / internalBufferYSize;
        yy = 0;

        for (int y = 0; y < internalBufferYSize; y++) {
            xx = 0;
            for (int x = 0; x < internalBufferXSize; x++) {
                this.internalBuffer[ofs++] = (int) (pattern2(xx, yy));
                xx += xstep;
            }
            yy += ystep;
        }
        walk += 0.005f;
    }

    // first domain wraping
    private float pattern2(float x, float y) {
        float qx = pattern(x, y);
        float qy = pattern(x + 5.2f, y + 1.3f);
        float val = pattern(x + 4f * qx, y + 4f * qy);
        return val * MUL;
    }

    private float pattern(float x, float y) {
        // return noise(x, y, walk);
        float f = (float) SimplexNoise.noise(x, y, walk);
        return (f * SN + f * SN / 2 + f * SN / 4) / 2;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.neophob.sematrix.core.generator.Generator#shuffle()
     */
    @Override
    public void shuffle() {
        if (VisualState.getInstance().getShufflerSelect(ShufflerOffset.IMAGE)) {
            // int nr = new Random().nextInt(imageFiles.size());
            // loadFile(imageFiles.get(nr));
        }
    }

}
