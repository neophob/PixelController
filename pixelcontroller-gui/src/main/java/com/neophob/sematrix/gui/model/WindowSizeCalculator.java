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
package com.neophob.sematrix.gui.model;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * helper class to calculate the window size
 * @author michu
 *
 */
public class WindowSizeCalculator {

	private static final Logger LOG = Logger.getLogger(WindowSizeCalculator.class.getName());

	//defined by the gui
	public static final int MINIMAL_WINDOW_WIDTH = 820;
	public static final int MINIMAL_WINDOW_HEIGHT = 440;
	public static final int MINIMAL_VISUAL_WIDTH = 40;
	public static final int MINIMAL_VISUAL_HEIGHT = 40;


	private int singleVisualWidth;
	private int singleVisualHeight;

	private int windowHeight;
	private int windowWidth;

	//input values
	private int internalBufferWidth;
	private int internalBufferHeight;
	private int maxWindowHeight;
	private int nrOfScreens;

	/**
	 * 
	 * @param internalBufferWidth
	 * @param internalBufferHeight
	 * @param maximalWindowWidth
	 * @param maximalWindowHeigh
	 */
	public WindowSizeCalculator(int internalBufferWidth, int internalBufferHeight, int maximalWindowWidth, 
			int maximalWindowHeight, int nrOfScreens) {

		this.internalBufferWidth = internalBufferWidth;
		this.internalBufferHeight = internalBufferHeight; 
		this.nrOfScreens = nrOfScreens;

		if (maximalWindowWidth<MINIMAL_WINDOW_WIDTH) {
			windowWidth = MINIMAL_WINDOW_WIDTH;
			LOG.log(Level.WARNING, "Adjusted window width to minimal value {0}, configured value was {1}", new Object[] {MINIMAL_WINDOW_WIDTH, maximalWindowWidth});
		} else {
			windowWidth = maximalWindowWidth;			
		}

		if (maximalWindowHeight<MINIMAL_WINDOW_HEIGHT) {
			maxWindowHeight = MINIMAL_WINDOW_HEIGHT;
			LOG.log(Level.WARNING, "Adjusted window height to minimal value {0}, configured value was {1}", new Object[] {MINIMAL_WINDOW_HEIGHT, maximalWindowHeight});
		} else {
			maxWindowHeight = maximalWindowHeight;
		}		
		
		calculateWidth();
		calculateHeight();
	}

	/**
	 * calculate 1) window size and 2) single visual size
	 */
	private void calculateWidth() {
		//calculate optimal visual with
		singleVisualWidth = windowWidth/nrOfScreens;

		//apply factor to height
		float aspect = (float)singleVisualWidth/(float)internalBufferWidth;
		singleVisualHeight = (int)((float)internalBufferHeight*aspect+0.5f);

		while (singleVisualHeight>maxWindowHeight) {
			singleVisualHeight/=2;
			singleVisualWidth/=2;
		}
		
		//make sure the visual is visible
		if (singleVisualWidth < MINIMAL_VISUAL_WIDTH) {
			singleVisualWidth = MINIMAL_VISUAL_WIDTH;
		}


	}

	/**
	 * 
	 */
	private void calculateHeight() {
		//calculate optimal height
		//int oldSingleVisualHeight = singleVisualHeight;
		int newSingleVisualHeight = maxWindowHeight-MINIMAL_WINDOW_HEIGHT+MINIMAL_VISUAL_HEIGHT;

		//apply factor to width
		float aspect = (float)newSingleVisualHeight/(float)singleVisualHeight;
		int newSingleVisualWidth = (int)(singleVisualWidth*aspect+0.5f);

		//shrinking of the single visual is allowed
		if (singleVisualWidth > newSingleVisualWidth) {
			singleVisualWidth = newSingleVisualWidth;
			singleVisualHeight = newSingleVisualHeight;
			windowHeight = maxWindowHeight;
		} else {
			//else shrink window height
			int newWindowHeight = maxWindowHeight - (newSingleVisualHeight - singleVisualHeight);
			if (newWindowHeight > MINIMAL_WINDOW_HEIGHT) {
				LOG.log(Level.INFO, "Shrink window by "+(newSingleVisualHeight-singleVisualHeight));
				windowHeight = newWindowHeight;
			} else {
				LOG.log(Level.INFO, "Shrink window to "+MINIMAL_WINDOW_HEIGHT);
				windowHeight = MINIMAL_WINDOW_HEIGHT;
			}
		}

		//make sure the visual is visible
		if (singleVisualHeight < MINIMAL_VISUAL_HEIGHT) {
			singleVisualHeight = MINIMAL_VISUAL_HEIGHT;
		}

	}


	public int getWindowWidth() {
		return windowWidth;
	}

	public int getWindowHeight() {
		return windowHeight;
	}

	public int getSingleVisualWidth() {
		return singleVisualWidth;
	}

	public int getSingleVisualHeight() {
		return singleVisualHeight;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String
				.format("WindowSizeCalculator [singleVisualWidth=%s, singleVisualHeight=%s, windowHeight=%s, windowWidth=%s]",
						singleVisualWidth, singleVisualHeight, windowHeight,
						windowWidth);
	}

}
