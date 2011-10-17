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

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.neophob.sematrix.resize.Resize.ResizeName;

/**
 *
 * @author McGyver
 */
public class ColorFade extends Generator {

    private int colorFadeTime;
    private int maxFrames;
    private List<Color> colorMap;
    private int frameCount;

    /**
     * Instantiates a new colorscroll
     *
     * @param controller the controller
     */
    public ColorFade(PixelControllerGenerator controller) {
        super(controller, GeneratorName.COLOR_FADE, ResizeName.QUALITY_RESIZE);

        colorMap = new ArrayList<Color>();
        
        //put some colors for testing
        colorMap.add(new Color(0, 0, 128));
        colorMap.add(new Color(0, 0, 0));

        colorFadeTime = 30;

        maxFrames = colorMap.size() * colorFadeTime;
    }

    @Override
    public void update() {
        frameCount = (frameCount + 1) % maxFrames;

        int colornumber = (int) ((Math.round(Math.floor(frameCount / colorFadeTime))) % colorMap.size());
        int nextcolornumber = (colornumber + 1) % colorMap.size();
        double ratio = (frameCount % colorFadeTime) / (float)colorFadeTime;
        
        int rThis = colorMap.get(colornumber).getRed();
        int rNext = colorMap.get(nextcolornumber).getRed();
        int gThis = colorMap.get(colornumber).getGreen();
        int gNext = colorMap.get(nextcolornumber).getGreen();
        int bThis = colorMap.get(colornumber).getBlue();
        int bNext = colorMap.get(nextcolornumber).getBlue();

        int r = rThis - (int) Math.round((rThis - rNext) * (ratio));
        int g = gThis - (int) Math.round((gThis - gNext) * (ratio));
        int b = bThis - (int) Math.round((bThis - bNext) * (ratio));
        
        Arrays.fill(this.internalBuffer, (r << 16) | (g << 8) | b);
    }

    /**
     * 
     * @param colorFadeTime
     */
    void setFadeTime(int colorFadeTime) {
        this.colorFadeTime = colorFadeTime;
    }
}
