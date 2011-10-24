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

import processing.core.PApplet;

import com.neophob.sematrix.resize.Resize.ResizeName;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * TODO: multiple palettes
 * various sizes.
 *
 * @author mvogt
 */
public class Plasma2 extends Generator {

	/** The log. */
         private static final Logger LOG = Logger.getLogger(ColorFade.class.getName());
        
        /** The color map. */
        private List<Color> colorMap;

        /** The frame count. */
	private int frameCount;
	
	/**
	 * Instantiates a new plasma2.
	 *
	 * @param controller the controller
	 */
	public Plasma2(PixelControllerGenerator controller, List<Integer> colorList) {
		super(controller, GeneratorName.PLASMA, ResizeName.QUALITY_RESIZE);
		frameCount=1;
                
                colorMap = new ArrayList<Color>();
                for (Integer i: colorList) {
                        colorMap.add(new Color(i));
                }

                //add default value if nothing is configured
                if (colorMap.isEmpty()) {
                    colorMap.add(new Color(255, 128, 128));
                    colorMap.add(new Color(255, 255, 128));
                    colorMap.add(new Color(128, 255, 128));
                    colorMap.add(new Color(128, 255, 255));
                    colorMap.add(new Color(128, 128, 255));
                    colorMap.add(new Color(255, 128, 255));
                }
	}

	/* (non-Javadoc)
	 * @see com.neophob.sematrix.generator.Generator#update()
	 */
	@Override
	public void update() {
		float  xc = 20;

		// Enable this to control the speed of animation regardless of CPU power
		// int timeDisplacement = millis()/30;

		// This runs plasma as fast as your computer can handle
		int timeDisplacement = frameCount++;

		// No need to do this math for every pixel
		float calculation1 = (float)Math.sin( PApplet.radians(timeDisplacement * 0.61655617f));
		float calculation2 = (float)Math.sin( PApplet.radians(timeDisplacement * -3.6352262f));
		
		int aaa = 1024;
		int ySize = internalBufferYSize;
		// Plasma algorithm
		for (int x = 0; x < internalBufferXSize; x++, xc++) {
			float yc = 20;
			float s1 = aaa + aaa * (float)Math.sin(PApplet.radians(xc) * calculation1 );

			for (int y = 0; y < ySize; y++, yc++) {
				float s2 = aaa + aaa * (float)Math.sin(PApplet.radians(yc) * calculation2 );
				float s3 = aaa + aaa * (float)Math.sin(PApplet.radians((xc + yc + timeDisplacement * 5) / 2));  
				float s  = (s1+ s2 + s3) / (6f*255f);
				this.internalBuffer[y*internalBufferXSize+x] = getColor(s);
			}
		}   
	}

    private int getColor(float s) {
        //reduce s to [0-1]
        s = (s - (float) Math.floor(s)) * colorMap.size();
        
        int colornumber = (int) Math.floor(s);
        int nextcolornumber = (colornumber + 1) % colorMap.size();
        
        //use sinus as cross over function for much smoother transitions
        double ratio = ( Math.cos((s-colornumber) * Math.PI + Math.PI) + 1) / 2;
        
        int rThis = colorMap.get(colornumber).getRed();
        int rNext = colorMap.get(nextcolornumber).getRed();
        int gThis = colorMap.get(colornumber).getGreen();
        int gNext = colorMap.get(nextcolornumber).getGreen();
        int bThis = colorMap.get(colornumber).getBlue();
        int bNext = colorMap.get(nextcolornumber).getBlue();

        int r = rThis - (int) Math.round((rThis - rNext) * (ratio));
        int g = gThis - (int) Math.round((gThis - gNext) * (ratio));
        int b = bThis - (int) Math.round((bThis - bNext) * (ratio));
        
        return (r << 16) | (g << 8) | b;
    }

    void setColorMap(String colorMap) {
        if (colorMap==null) {
    		this.colorMap =  new ArrayList<Color>();
    	}

    	String[] tmp = colorMap.trim().split("_");
    	if (tmp==null || tmp.length==0) {
    		this.colorMap =  new ArrayList<Color>();
    	}
    	
    	List<Color> list = new ArrayList<Color>();
    	for (String s: tmp) {
    		try {
    			list.add( new Color(Integer.decode(s.trim())) );
    		} catch (Exception e) {
    			LOG.log(Level.WARNING, "Failed to parse {0}", s);
		}	
    	}
        this.colorMap = list;
    }
}