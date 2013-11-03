/**
 * Copyright (C) 2011-2013 Michael Vogt <michu@neophob.com>
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

import com.neophob.sematrix.glue.MatrixData;
import com.neophob.sematrix.resize.Resize.ResizeName;


/**
 * ripped from http://stachelig.de/
 * 		 
 * @author mvogt
 *
 */
public class PlasmaAdvanced extends Generator {

	/** The Constant TWO_PI. */
	private static final float TWO_PI = 6.283185307f;

	/** The Constant GRADIENTLEN. */
	private static final int GRADIENTLEN = 900;//1500;
	// use this factor to make things faster, esp. for high resolutions
	/** The Constant SPEEDUP. */
	private static final int SPEEDUP = 3;

	/** The Constant FADE_STEPS. */
	private static final int FADE_STEPS = 50;

	// swing/wave function parameters
	/** The Constant SWINGLEN. */
	private static final int SWINGLEN = GRADIENTLEN*3;

	/** The Constant SWINGMAX. */
	private static final int SWINGMAX = GRADIENTLEN / 2 - 1;

	//TODO make them configable
	/** The Constant MIN_FACTOR. */
	private static final int MIN_FACTOR = 4;

	/** The Constant MAX_FACTOR. */
	private static final int MAX_FACTOR = 10;

	/** The fade swing steps. */
	private int fadeSwingSteps = 0;

	/** The swing curve. */
	private int[] swingCurve = new int[SWINGLEN];

	/** The swing curve tmp. */
	private int[] swingCurveTmp = new int[SWINGLEN];

	/** The frame count. */
	private int frameCount;

	/** The random. */
	private Random random;

	/**
	 * Instantiates a new plasma advanced.
	 *
	 * @param controller the controller
	 */
	public PlasmaAdvanced(MatrixData matrix) {
		super(matrix, GeneratorName.PLASMA_ADVANCED, ResizeName.QUALITY_RESIZE);
		frameCount=1;
		random = new Random();
		makeSwingCurve();		
	}

	/* (non-Javadoc)
	 * @see com.neophob.sematrix.generator.Generator#update()
	 */
	@Override
	public void update() {
		frameCount++;

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
				this.internalBuffer[y*internalBufferXSize+x] = 				
						swing(swing(x + swingT) + swingYT) +
						swing(swing(x + t     ) + swingY );
			}
		}
	}

	// fill the given array with a nice swingin' curve
	// three cos waves are layered together for that
	// the wave "wraps" smoothly around, uh, if you know what i mean ;-)
	/**
	 * Make swing curve.
	 */
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
	

	/**
	 * Fade swing curve.
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


	// helper: get a swing curve sample
	/**
	 * Swing.
	 *
	 * @param i the i
	 * @return the int
	 */
	private int swing(int i) {
		return swingCurve[i % SWINGLEN];
	}


}
