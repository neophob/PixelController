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

import com.neophob.sematrix.core.visual.MatrixData;
import com.neophob.sematrix.core.visual.layout.HorizontalLayout;
import com.neophob.sematrix.core.visual.layout.Layout;


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
		System.out.println(lsow);
	}

	@Test
	public void testWindowSizeStripInvadersInverted() {
		if (java.awt.GraphicsEnvironment.isHeadless()) {
			return;
		}
		MatrixData matrixData = new MatrixData(8, 200);
		LedSimulatorOutputWindow lsow = new LedSimulatorOutputWindow(matrixData, LED_SIZE, l);
		Point p = lsow.getWindowSize();
		Assert.assertTrue(p.getX()<p.getY());
		System.out.println(lsow);
	}

}
