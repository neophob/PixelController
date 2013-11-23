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
