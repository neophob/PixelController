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
package com.neophob.sematrix.core.resize;

/**
 * Biliner resize, a bit slower than the NearestNeighbour filter
 * 
 * @author michu
 * 
 */
class BilinearResize extends Resize {

    public BilinearResize() {
        super(ResizeName.QUALITY_RESIZE);
    }

    public int[] resizeImage(int[] buffer, int currentXSize, int currentYSize, int newX, int newY) {
        return resizeBilinear(buffer, currentXSize, currentYSize, newX, newY);
    }

    public int[] resizeBilinear(int[] pixels, int w, int h, int w2, int h2) {
        int[] temp = new int[w2 * h2];
        int a, b, c, d, x, y, index;
        float xRatio = ((float) (w - 1)) / w2;
        float yRatio = ((float) (h - 1)) / h2;
        float xDiff, yDiff, blue, red, green;
        int offset = 0;

        int centerOfs = 0;
        if (w > w2) {
            centerOfs += (w / w2) / 2;
        }
        if (h > h2) {
            centerOfs += (w * ((h / h2) / 2));
        }

        for (int i = 0; i < h2; i++) {
            y = (int) (yRatio * i);
            yDiff = (yRatio * i) - y;

            for (int j = 0; j < w2; j++) {
                x = (int) (xRatio * j);
                xDiff = (xRatio * j) - x;
                index = centerOfs + (y * w + x);
                a = pixels[index];
                b = pixels[index + 1];
                c = pixels[index + w];
                d = pixels[index + w + 1];

                // blue element
                // Yb = Ab(1-w)(1-h) + Bb(w)(1-h) + Cb(h)(1-w) + Db(wh)
                blue = (a & 0xff) * (1 - xDiff) * (1 - yDiff) + (b & 0xff) * (xDiff)
                        * (1 - yDiff) + (c & 0xff) * (yDiff) * (1 - xDiff) + (d & 0xff)
                        * (xDiff * yDiff);

                // green element
                // Yg = Ag(1-w)(1-h) + Bg(w)(1-h) + Cg(h)(1-w) + Dg(wh)
                green = ((a >> 8) & 0xff) * (1 - xDiff) * (1 - yDiff) + ((b >> 8) & 0xff)
                        * (xDiff) * (1 - yDiff) + ((c >> 8) & 0xff) * (yDiff) * (1 - xDiff)
                        + ((d >> 8) & 0xff) * (xDiff * yDiff);

                // red element
                // Yr = Ar(1-w)(1-h) + Br(w)(1-h) + Cr(h)(1-w) + Dr(wh)
                red = ((a >> 16) & 0xff) * (1 - xDiff) * (1 - yDiff) + ((b >> 16) & 0xff)
                        * (xDiff) * (1 - yDiff) + ((c >> 16) & 0xff) * (yDiff) * (1 - xDiff)
                        + ((d >> 16) & 0xff) * (xDiff * yDiff);

                temp[offset++] = 0xff000000
                        | // hardcode alpha
                        ((((int) red) << 16) & 0xff0000) | ((((int) green) << 8) & 0xff00)
                        | ((int) blue);
            }
        }
        return temp;
    }
}
