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
package com.neophob.sematrix.core.output;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.neophob.sematrix.core.output.OutputHelper;

/**
 * verify the scanline flip code
 * @author michu
 *
 */
public class MiniDmxDeviceTest {
	
    @Test
    public void speedTestOld() {
		int[] buffer = new int[] {
				1,2,3,0,0,0,0,0,
				8,8,8,8,0,0,0,0,
				0,0,0,0,0,0,0,0,
				0,0,0,0,0,0,0,4
			};
		buffer = OutputHelper.flipSecondScanline(buffer, 8, 4);
    	//RotateBufferTest.dumpBuffer(buffer);
		assertEquals(1, buffer[0]);
		assertEquals(2, buffer[1]);
		assertEquals(8, buffer[12]);
		assertEquals(4, buffer[24]);
    }
    
}
