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

import com.neophob.sematrix.resize.Resize.ResizeName;

import processing.core.PApplet;


/**
 * TODO: multiple palettes
 * 		 various sizes
 * @author mvogt
 *
 */
public class Plasma2 extends Generator {

	private int frameCount;
	
	public Plasma2(PixelControllerGenerator controller) {
		super(controller, GeneratorName.PLASMA, ResizeName.QUALITY_RESIZE);
		frameCount=1;
	}

	@Override
	public void update() {
		float  xc = 20;

		// Enable this to control the speed of animation regardless of CPU power
		// int timeDisplacement = millis()/30;

		// This runs plasma as fast as your computer can handle
		int timeDisplacement = frameCount++;

		// No need to do this math for every pixel
		float calculation1 = PApplet.sin( PApplet.radians(timeDisplacement * 0.61655617f));
		float calculation2 = PApplet.sin( PApplet.radians(timeDisplacement * -3.6352262f));
		
		int aaa = 1024;
		int ySize = getInternalBufferYSize();
		// Plasma algorithm
		for (int x = 0; x < getInternalBufferXSize(); x++, xc++) {
			float yc = 20;
			float s1 = aaa + aaa * PApplet.sin(PApplet.radians(xc) * calculation1 );

			for (int y = 0; y < ySize; y++, yc++) {
				float s2 = aaa + aaa * PApplet.sin(PApplet.radians(yc) * calculation2 );
				float s3 = aaa + aaa * PApplet.sin(PApplet.radians((xc + yc + timeDisplacement * 5) / 2));  
				float s  = (s1+ s2 + s3) / (6f*255f);
				this.internalBuffer[y*this.getInternalBufferXSize()+x] = Color.HSBtoRGB(s, 0.98f, 0.9f);
			}
		}   
	}

}
