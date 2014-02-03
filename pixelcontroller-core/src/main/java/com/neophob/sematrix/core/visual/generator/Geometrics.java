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
package com.neophob.sematrix.core.visual.generator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import com.neophob.sematrix.core.resize.Resize.ResizeName;
import com.neophob.sematrix.core.sound.ISound;
import com.neophob.sematrix.core.visual.MatrixData;

/**
 * create some drops
 * 
 * TODO add more geometrics forms (ellipse, rectangle...) replace Math.sqrt with
 * something faster
 * 
 * @author michu
 */
public class Geometrics extends Generator {

    /** The Constant THICKNESS. */
    private static final int THICKNESS = 10;

    /** The drops. */
    private List<Drop> drops;

    /** The tmp. */
    private List<Drop> tmp;

    /** The sound. */
    private ISound sound;

    public int[] internalBufferTmp;

    /** The rnd gen. */
    private Random rndGen = new Random();

    /**
     * Instantiates a new geometrics.
     * 
     * @param controller
     *            the controller
     */
    public Geometrics(MatrixData matrix, ISound sound) {
        super(matrix, GeneratorName.DROPS, ResizeName.QUALITY_RESIZE);
        drops = new ArrayList<Drop>();
        tmp = new ArrayList<Drop>();
        this.sound = sound;

        internalBufferTmp = new int[internalBuffer.length];
    }

    /**
     * Random.
     * 
     * @param min
     *            the min
     * @param max
     *            the max
     * @return the int
     */
    private int random(int min, int max) {
        int ret = rndGen.nextInt(Math.abs(max - min));
        return ret + min;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.neophob.sematrix.core.generator.Generator#update()
     */
    @Override
    public void update(int amount) {
        // maximal 3 active drops
        if ((sound.isHat() || sound.isKick() || drops.isEmpty()) && drops.size() < 3) {
            drops.add(new Drop(random(THICKNESS, internalBufferXSize), random(THICKNESS,
                    internalBufferYSize), random(0, 255)));
        }

        tmp.clear();

        // clear background
        Arrays.fill(this.internalBufferTmp, 0);
        for (Drop d : drops) {
            d.update(amount);
            if (d.done()) {
                tmp.add(d);
            }
        }

        // copy temp buffer to internal buffer, fixes flickering
        System.arraycopy(internalBufferTmp, 0, internalBuffer, 0, internalBuffer.length);

        // remove drops that are updated
        if (!tmp.isEmpty()) {
            drops.removeAll(tmp);
        }
    }

    /**
     * Class for Raindrops effect.
     * 
     * @author michu
     */
    private final class Drop {

        /** The drop size. */
        int xpos, ypos, dropcolor, dropSize;

        /** The finished. */
        boolean finished;

        /**
         * Instantiates a new drop.
         * 
         * @param x
         *            the x
         * @param y
         *            the y
         * @param c
         *            the c
         */
        private Drop(int x, int y, int color) {
            xpos = x;
            ypos = y;
            dropcolor = color;
            finished = false;
        }

        /**
         * Update.
         */
        private void update(int amount) {
            for (int n = 0; n < amount; n++) {
                if (!finished) {
                    if (dropSize < internalBufferXSize * 2) {
                        dropSize++;
                    } else {
                        finished = true;
                    }
                }
            }
            drawCircle();
        }

        /**
         * Done.
         * 
         * @return true, if successful
         */
        private boolean done() {
            return finished;
        }

        /**
         * draw circle
         */
        private void drawCircle() {
            int dropsizeThickness = dropSize - THICKNESS;

            boolean drawOnscreen = false;
            for (int i = 0; i < internalBufferXSize; i++) {
                for (int j = 0; j < internalBufferYSize; j++) {
                    // calculate distance to center:
                    int x = xpos - i;
                    int y = ypos - j;
                    double r = Math.sqrt((x * x) + (y * y));

                    if (r < dropSize && r > dropsizeThickness) {
                        if (j >= 0 && j < internalBufferYSize && i >= 0 && i < internalBufferXSize) {
                            internalBufferTmp[j * internalBufferXSize + i] = dropcolor;
                            drawOnscreen = true;
                        }
                    }
                }
            }

            // detect if the circle is finished
            if (dropSize > THICKNESS && !drawOnscreen) {
                finished = true;
            }
        }
    }

}
