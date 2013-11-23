package com.neophob.sematrix.gui.model;

import org.junit.Assert;
import org.junit.Test;

import com.neophob.sematrix.core.glue.MatrixData;
import com.neophob.sematrix.core.layout.HorizontalLayout;
import com.neophob.sematrix.core.layout.Layout;


public class TestLedSimulatorOutputWindow {

	final int LED_SIZE = 16;
	Layout l = new HorizontalLayout(1);
	
	@Test
	public void testWindowSizePixelInvaders() {
		if (java.awt.GraphicsEnvironment.isHeadless()) {
			return;
		}
		MatrixData matrixData = new MatrixData(8, 8);
		LedSimulatorOutputWindow lsow = new LedSimulatorOutputWindow(matrixData, LED_SIZE, l);
		Point p = lsow.getWindowSize();
	}

	
	@Test
	public void testWindowSizeStripInvaders() {
		if (java.awt.GraphicsEnvironment.isHeadless()) {
			return;
		}
		MatrixData matrixData = new MatrixData(400, 8);
		LedSimulatorOutputWindow lsow = new LedSimulatorOutputWindow(matrixData, LED_SIZE, l);
		Point p = lsow.getWindowSize();
		Assert.assertTrue(p.getX()>p.getY());
	}
	
}
