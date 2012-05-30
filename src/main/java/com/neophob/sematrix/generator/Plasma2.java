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

import processing.core.PApplet;

import com.neophob.sematrix.glue.Collector;
import com.neophob.sematrix.resize.Resize.ResizeName;


/**
 * Plasma Generator, ripped form openprocessing
 *
 * @author mvogt
 */
public class Plasma2 extends Generator {

	/** The frame count. */
	private int frameCount;

	/**
	 * Instantiates a new plasma2.
	 *
	 * @param controller the controller
	 */
	public Plasma2(PixelControllerGenerator controller) {
		super(controller, GeneratorName.PLASMA, ResizeName.QUALITY_RESIZE);
		frameCount=1;
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
//				this.internalBuffer[y*internalBufferXSize+x] = colorSet.getSmoothColor((int)(s));
			}
		}   
	}

	/**
	 * 
	 * @param s
	 * @return
	 */
	private int getColor(float s) {		
		//reduce s to [0-1]
//		s = (s - (float) Math.floor(s)) * colorMap.size();

//		int colornumber = (int) Math.floor(s);
//		int nextcolornumber = (colornumber + 1) % colorMap.size();

		//use sinus as cross over function for much smoother transitions
//		float ratio = (float)(Math.cos((s-colornumber) * Math.PI + Math.PI) + 1) / 2;

//		return super.getColor(colornumber, nextcolornumber, ratio);
		return Collector.getInstance().getActiveColorSet().getSmoothColor((int)(s*255));
	}

}