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

import java.util.Random;

import com.neophob.sematrix.resize.Resize.ResizeName;


/**
 * ripped from http://stachelig.de/
 * 		 
 * @author mvogt
 *
 */
public class PlasmaAdvanced extends Generator {

	private static final float TWO_PI = 6.283185307f;

	private static final int GRADIENTLEN = 900;//1500;
	// use this factor to make things faster, esp. for high resolutions
	private static final int SPEEDUP = 3;

	private static final int FADE_STEPS = 50;

	// swing/wave function parameters
	private static final int SWINGLEN = GRADIENTLEN*3;
	private static final int SWINGMAX = GRADIENTLEN / 2 - 1;

	//TODO make them configable
	private static final int MIN_FACTOR = 4;
	private static final int MAX_FACTOR = 10;

	
	private int rf = 4;
	private int gf = 2;
	private int bf = 1;
	private int rd = 0;
	private int gd = GRADIENTLEN / gf;
	private int bd = GRADIENTLEN / bf / 2;

	// gradient & swing curve arrays
	private int fadeColorSteps = 0;
	private int[] colorGrad  = new int[GRADIENTLEN];
	private int[] colorGradTmp  = new int[GRADIENTLEN];

	private int fadeSwingSteps = 0;
	private int[] swingCurve = new int[SWINGLEN];
	private int[] swingCurveTmp = new int[SWINGLEN];

	private int frameCount;
	private Random random;

	/**
	 * 
	 */
	public PlasmaAdvanced(PixelControllerGenerator controller) {
		super(controller, GeneratorName.PLASMA_ADVANCED, ResizeName.QUALITY_RESIZE);
		frameCount=1;
		random = new Random();
		makeGradient();
		makeSwingCurve();		
	}

	@Override
	public void update() {
		frameCount++;

		if (frameCount%55==3) {
			makeGradient();
		}
		if (fadeColorSteps>0) {
			fadeColorGradient();
		}

		if (frameCount%57==33) {
			makeSwingCurve();
		}
		if (fadeSwingSteps>0) {
			fadeSwingCurve();
		}
		
		int t = frameCount*SPEEDUP;
		int swingT = swing(t); // swingT/-Y/-YT variables are used for a little tuning ...

		for (int y = 0; y < this.internalBufferYSize; y++) {
			int swingY  = swing(y);
			int swingYT = swing(y + t);
			for (int x = 0; x < this.internalBufferXSize; x++) {
				// this is where the magic happens: map x, y, t around
				// the swing curves and lookup a color from the gradient
				// the "formula" was found by a lot of experimentation
				this.internalBuffer[y*internalBufferXSize+x] = gradient(
						swing(swing(x + swingT) + swingYT) +
						swing(swing(x + t     ) + swingY ));
			}
		}
	}

	// fill the given array with a nice swingin' curve
	// three cos waves are layered together for that
	// the wave "wraps" smoothly around, uh, if you know what i mean ;-)
	void makeSwingCurve() {		
		int factor1=random.nextInt(MAX_FACTOR)+MIN_FACTOR;
		int factor2=random.nextInt(MAX_FACTOR)+MIN_FACTOR;
		int factor3=random.nextInt(MAX_FACTOR)+MIN_FACTOR;

		int halfmax = SWINGMAX/factor1;

		for( int i=0; i<SWINGLEN; i++ ) {
			float ni = i*TWO_PI/SWINGLEN; // ni goes [0..TWO_PI] -> one complete cos wave
			swingCurveTmp[i]=
				(int)( Math.cos( ni*factor1 ) * Math.cos( ni*factor2 ) * Math.cos( ni*factor3 ) * halfmax + halfmax );
		}
		fadeSwingSteps = FADE_STEPS;
	}


	// create a smooth, colorful gradient by cosinus curves in the RGB channels
	private void makeGradient() {
		int val = random.nextInt(12);
		switch (val) {
		case 0: rf = random.nextInt(4)+1;
		break;
		case 1: gf = random.nextInt(4)+1;
		break;
		case 2: bf = random.nextInt(4)+1;
		break;
		case 3: rd = random.nextInt(GRADIENTLEN);
		break;
		case 4: gd = random.nextInt(GRADIENTLEN);
		break;
		case 5: bd = random.nextInt(GRADIENTLEN);
		break;
		}
		//System.out.println("Gradient factors("+rf+","+gf+","+bf+"), displacement("+rd+","+gd+","+bd+")");

		// fill gradient array
		for (int i = 0; i < GRADIENTLEN; i++) {
			int r = cos256(GRADIENTLEN / rf, i + rd);
			int g = cos256(GRADIENTLEN / gf, i + gd);
			int b = cos256(GRADIENTLEN / bf, i + bd);
			colorGradTmp[i] = color(r, g, b);
			fadeColorSteps = FADE_STEPS;
		}
	}

	private int getR(int col) {
		return (col>>16)&255;
	}
	private int getG(int col) {
		return (col>>8)&255;
	}
	private int getB(int col) {
		return (col&255);
	}

	//---------------------------
	private void fadeColorGradient() {
		fadeColorSteps--;

		if (fadeColorSteps==0) {
			//arraycopy(Object src, int srcPos, Object dest, int destPos, int length) 
			System.arraycopy(colorGradTmp, 0, colorGrad, 0, GRADIENTLEN);
			return;
		}

		for (int i = 0; i < GRADIENTLEN; i++) {
			int colorS = colorGradTmp[i]; //target
			int colorD = colorGrad[i];    //current value

			int r = getR(colorD)+( (getR(colorS) - getR(colorD)) / fadeColorSteps);
			int g = getG(colorD)+( (getG(colorS) - getG(colorD)) / fadeColorSteps);
			int b = getB(colorD)+( (getB(colorS) - getB(colorD)) / fadeColorSteps);

			colorGrad[i] = color(r, g, b);
			//		    colorGrad[i] = color(g, g, g);
		}

	}

	/**
	 * 
	 */
	private void fadeSwingCurve() {
		fadeSwingSteps--;
		if (fadeSwingSteps==0) {
			//arraycopy(Object src, int srcPos, Object dest, int destPos, int length) 
			System.arraycopy(swingCurveTmp, 0, swingCurve, 0, SWINGLEN);


			return;
		}

		for (int i = 0; i < SWINGLEN; i++) {
			int x = swingCurve[i] + ( (swingCurveTmp[i]-swingCurve[i]) / fadeSwingSteps);
			swingCurve[i] = x;
		}

	}

	// helper: get cosinus sample normalized to 0..255
	private int cos256(int amplitude, int x) {
		return (int) (Math.cos(x * TWO_PI / amplitude) * 127 + 127);
	}

	// helper: get a swing curve sample
	private int swing(int i) {
		return swingCurve[i % SWINGLEN];
	}

	// helper: get a gradient sample
	private int gradient(int i) {
		return colorGrad[i % GRADIENTLEN];
	}

	public final int color(int x, int y, int z) {
		if (x > 255) {x = 255;} else if (x < 0) {x = 0;}
		if (y > 255) {y = 255;} else if (y < 0) {y = 0;}
		if (z > 255) {z = 255;} else if (z < 0) {z = 0;}

		return 0xff000000 | (x << 16) | (y << 8) | z;
	}

}
