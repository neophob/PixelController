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

import java.util.Random;

import com.neophob.sematrix.core.resize.Resize.ResizeName;
import com.neophob.sematrix.core.visual.MatrixData;

/**
 * The Class Fire.
 * 
 * @author mvogt ripped from
 *         http://demo-effects.cvs.sourceforge.net/viewvc/demo-
 *         effects/demo-effects
 *         /FIRE/fire.c?revision=1.5&content-type=text%2Fplain
 */
public class Fire extends Generator {

    /** The r. */
    private Random r;

    /* fire buffer, contains 0..255 */
    /** The buffer. */
    private int[] buffer;

    /**
     * Instantiates a new fire.
     * 
     * @param controller
     *            the controller
     */
    public Fire(MatrixData matrix) {
        super(matrix, GeneratorName.FIRE, ResizeName.QUALITY_RESIZE);

        this.buffer = new int[internalBufferXSize * (internalBufferYSize + 10)];
        r = new Random();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.neophob.sematrix.core.generator.Generator#update()
     */
    @Override
    public void update(int amount) {
        int random;
        int temp;

        for (int n = 0; n < amount; n++) {
            int j = this.getInternalBufferXSize() * (this.getInternalBufferYSize() + 1);
            for (int i = 0; i < this.getInternalBufferXSize(); i++) {
                random = r.nextInt(16);
                /*
                 * the lower the value, the intense the fire, compensate a lower
                 * value with a higher decay value
                 */
                if (random > 8) {
                    /* maximum heat */
                    this.buffer[j + i] = 255;
                } else {
                    this.buffer[j + i] = 0;
                }
            }

            /* move fire upwards, start at bottom */
            for (int index = 0; index < internalBufferYSize + 1; index++) {
                for (int i = 0; i < internalBufferXSize; i++) {
                    if (i == 0) {
                        /* at the left border */
                        temp = buffer[j];
                        temp += buffer[j + 1];
                        temp += buffer[j - internalBufferXSize];
                        temp /= 3;
                    } else if (i == this.getInternalBufferXSize()) {
                        /* at the right border */
                        temp = buffer[j + i];
                        temp += buffer[j - internalBufferXSize + i];
                        temp += buffer[j + i - 1];
                        temp /= 3;
                    } else {
                        temp = buffer[j + i];
                        temp += buffer[j + i + 1];
                        temp += buffer[j + i - 1];
                        temp += buffer[j - internalBufferXSize + i];
                        temp >>= 2;
                    }
                    if (temp > 1) {
                        /* decay */
                        temp--;
                    }

                    int dofs = j - internalBufferXSize + i;
                    this.buffer[dofs] = temp;
                    if (dofs < this.internalBuffer.length) {
                        this.internalBuffer[dofs] = temp;
                    }
                }
                j -= this.getInternalBufferXSize();
            }
        }

    }

}
