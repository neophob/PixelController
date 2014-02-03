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
package com.neophob.sematrix.core.visual.mixer;

import com.neophob.sematrix.core.resize.Resize.ResizeName;
import com.neophob.sematrix.core.visual.Visual;

/**
 * HalfHalf split screen vertical
 */
public class HalfHalfVertical extends Mixer {

    /**
     * Instantiates a new multiply.
     *
     * @param controller the controller
     */
    public HalfHalfVertical() {
        super(MixerName.HALFHALFVERTICAL, ResizeName.PIXEL_RESIZE);
    }

    /* (non-Javadoc)
     * @see com.neophob.sematrix.core.mixer.Mixer#getBuffer(com.neophob.sematrix.core.glue.Visual)
     */
    public int[] getBuffer(Visual visual) {
        if (visual.getEffect2() == null) {
            return visual.getEffect1Buffer();
        }

        int width = visual.getGenerator1().getInternalBufferXSize();
        int halfWidth=width/2;
        int height = visual.getGenerator1().getInternalBufferYSize();
        int[] src1 = visual.getEffect1Buffer();
        int[] src2 = visual.getEffect2Buffer();
        int[] dst = new int [src1.length];

        int ofs=0;
        for (int i=0; i<height; i++){
        	for (int j=0; j<halfWidth; j++){
                dst[ofs]=src1[ofs]&255;
                dst[ofs+halfWidth]=src2[ofs+halfWidth]&255;
                ofs++;
        	}
        	ofs+=halfWidth;
        }

        return dst;
    }

}
