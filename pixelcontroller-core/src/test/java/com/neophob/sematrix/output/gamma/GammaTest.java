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
package com.neophob.sematrix.output.gamma;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;

/**
 * @author michu
 *
 */
public class GammaTest {
	
	@Test
	public void applyGammaTest() {
		int[] buffer = new int[] {0,32,64,96,128,192,224};
		int[] resultNone = Gammatab.applyBrightnessAndGammaTab(buffer, GammaType.NONE, 1f);
		assertTrue(Arrays.equals(buffer, resultNone));
		
		int[] resultg20 = Gammatab.applyBrightnessAndGammaTab(buffer, GammaType.GAMMA_20, 1f);
		assertFalse(Arrays.equals(buffer, resultg20));
		
		int[] resultg22 = Gammatab.applyBrightnessAndGammaTab(buffer, GammaType.GAMMA_22, 1f);
		assertFalse(Arrays.equals(buffer, resultg22));
		assertFalse(Arrays.equals(resultg20, resultg22));
		
		int[] resultg25 = Gammatab.applyBrightnessAndGammaTab(buffer, GammaType.GAMMA_25, 1f);
		assertFalse(Arrays.equals(buffer, resultg25));
		assertFalse(Arrays.equals(resultg20, resultg25));
		
		int[] resultb5 = Gammatab.applyBrightnessAndGammaTab(buffer, GammaType.GAMMA_25, 0.5f);
		assertFalse(Arrays.equals(resultg25, resultb5));
		assertFalse(Arrays.equals(buffer, resultb5));
	}
    
}
