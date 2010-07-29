package com.neophob.sematrix.generator;

import java.awt.Color;

import processing.core.PApplet;


/**
 * TODO: multiple palettes
 * 		 various sizes
 * @author mvogt
 *
 */
public class Plasma2 extends Generator {

	private int frameCount;
	
	public Plasma2() {
		super(GeneratorName.PLASMA);
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
				this.internalBuffer[y*this.getInternalBufferXSize()+x] = Color.HSBtoRGB(s, 0.8f, 0.9f);
			}
		}   
	}

	@Override
	public void close() {	}


}
