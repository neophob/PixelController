/**
 * Copyright (C) 2011 Michael Vogt <michu@neophob.com>
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

package com.neophob.sematrix.generator;

import java.util.Arrays;

import com.neophob.sematrix.resize.Resize.ResizeName;

/**
 *              
 * @author McGyver
 */
public class ColorFade extends Generator {

    private int colorFadeTime;
    private int frameCount;

    /**
     * Instantiates a new colorscroll
     *
     * @param controller the controller
     */
    public ColorFade(PixelControllerGenerator controller) {
        super(controller, GeneratorName.COLOR_FADE, ResizeName.QUALITY_RESIZE);

        colorFadeTime = 30;
    }

    @Override
    public void update() {
        //use sinus as cross over function for much smoother transitions DOES NOT WORK YET
        //float ratio = (float)(Math.cos((s-colornumber) * Math.PI + Math.PI) + 1) / 2;        
        //int col = super.getColor(colornumber, nextcolornumber, ratio);
        int col = frameCount;
        Arrays.fill(this.internalBuffer, col);
        
        frameCount++;
    }


    /**
     * @return the colorFadeTime
     */
    public int getColorFadeTime() {
        return colorFadeTime;
    }

    /**
     * @param colorFadeTime the colorFadeTime to set
     */
    public void setColorFadeTime(int colorFadeTime) {
        this.colorFadeTime = colorFadeTime;
    }
    
    
}