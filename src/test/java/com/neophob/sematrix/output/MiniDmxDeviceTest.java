package com.neophob.sematrix.output;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

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
