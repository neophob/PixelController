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
package com.neophob.sematrix.gui.model;

import org.junit.Assert;
import org.junit.Test;

public class WindowSizeCalculatorTest {

	@Test
	public void testWindowSizePixelInvaders() {
		int bufferSize = 64;
		int nrOfVisuals = 3;
		int maximalWindowHeight = 500;
		int maximalWindowWidth = 820;
		WindowSizeCalculator wsc = new WindowSizeCalculator(bufferSize, bufferSize, maximalWindowWidth,
				maximalWindowHeight, nrOfVisuals);
		
		System.out.println(wsc);
		Assert.assertEquals(maximalWindowWidth, wsc.getWindowWidth());
		Assert.assertEquals(maximalWindowHeight, wsc.getWindowHeight());
		Assert.assertTrue(wsc.getSingleVisualWidth() > 0);
		Assert.assertTrue(wsc.getSingleVisualHeight() > 0);
		Assert.assertEquals(wsc.getSingleVisualWidth(), wsc.getSingleVisualHeight());
	}

	@Test
	public void testWindowSizePixelInvadersInsane() {
		int bufferSize = 64;
		int nrOfVisuals = 20;
		int maximalWindowHeight = 500;
		int maximalWindowWidth = 820;
		WindowSizeCalculator wsc = new WindowSizeCalculator(bufferSize, bufferSize, maximalWindowWidth,
				maximalWindowHeight, nrOfVisuals);
		
		System.out.println(wsc);
		Assert.assertEquals(maximalWindowWidth, wsc.getWindowWidth());
		Assert.assertTrue(maximalWindowHeight >= wsc.getWindowHeight());
		Assert.assertTrue(wsc.getSingleVisualWidth() > 0);
		Assert.assertTrue(wsc.getSingleVisualHeight() > 0);
		Assert.assertEquals(wsc.getSingleVisualWidth(), wsc.getSingleVisualHeight());
	}

	
	@Test
	public void testFancyResolutionW() {
		int w = 120;
		int h = 12;
		int nrOfVisuals = 16;
		int maximalWindowHeight = 500;
		int maximalWindowWidth = 820;
		WindowSizeCalculator wsc = new WindowSizeCalculator(w, h, maximalWindowWidth,
				maximalWindowHeight, nrOfVisuals);
		
		System.out.println(wsc);
		Assert.assertEquals(maximalWindowWidth, wsc.getWindowWidth());
		Assert.assertTrue(maximalWindowHeight >= wsc.getWindowHeight());
		Assert.assertTrue(wsc.getSingleVisualWidth() > 0);
		Assert.assertTrue(wsc.getSingleVisualHeight() > 0);
		Assert.assertTrue(wsc.getSingleVisualWidth() > wsc.getSingleVisualHeight());
	}

	@Test
	public void testFancyResolutionH() {
		int w = 12;
		int h = 120;
		int nrOfVisuals = 16;
		int maximalWindowHeight = 1;
		int maximalWindowWidth = 1;
		WindowSizeCalculator wsc = new WindowSizeCalculator(w, h, maximalWindowWidth,
				maximalWindowHeight, nrOfVisuals);
		
		System.out.println(wsc);
		Assert.assertEquals(WindowSizeCalculator.MINIMAL_WINDOW_WIDTH, wsc.getWindowWidth());
		Assert.assertEquals(WindowSizeCalculator.MINIMAL_WINDOW_HEIGHT, wsc.getWindowHeight());
		Assert.assertTrue(wsc.getSingleVisualWidth() > 0);
		Assert.assertTrue(wsc.getSingleVisualHeight() > 0);
		Assert.assertTrue(wsc.getSingleVisualWidth() < wsc.getSingleVisualHeight());
	}

	@Test
	public void testFancyResolutionHtwo() {
		int w = 1;
		int h = 120;
		int nrOfVisuals = 4;
		int maximalWindowHeight = 1;
		int maximalWindowWidth = 1;
		WindowSizeCalculator wsc = new WindowSizeCalculator(w, h, maximalWindowWidth,
				maximalWindowHeight, nrOfVisuals);
		
		System.out.println(wsc);
		Assert.assertEquals(WindowSizeCalculator.MINIMAL_WINDOW_WIDTH, wsc.getWindowWidth());
		Assert.assertEquals(WindowSizeCalculator.MINIMAL_WINDOW_HEIGHT, wsc.getWindowHeight());
		Assert.assertTrue(wsc.getSingleVisualWidth() > 0);
		Assert.assertTrue(wsc.getSingleVisualHeight() > 0);
		Assert.assertTrue(wsc.getSingleVisualWidth() < wsc.getSingleVisualHeight());
	}

	@Test
	public void test8x13() {
		int w = 64;
		int h = 104;
		int nrOfVisuals = 3;
		int maximalWindowHeight = 500;
		int maximalWindowWidth = 820;
		WindowSizeCalculator wsc = new WindowSizeCalculator(w, h, maximalWindowWidth,
				maximalWindowHeight, nrOfVisuals);
		
		System.out.println(wsc);
		Assert.assertEquals(maximalWindowWidth, wsc.getWindowWidth());
		Assert.assertTrue(maximalWindowHeight >= wsc.getWindowHeight());
		Assert.assertTrue(wsc.getSingleVisualWidth() > 0);
		Assert.assertTrue(wsc.getSingleVisualHeight() > 0);
		Assert.assertTrue(wsc.getSingleVisualWidth() < wsc.getSingleVisualHeight());
	}

}
