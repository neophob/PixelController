package com.neophob.sematrix.output;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * @author michu
 *
 */
public class OutputHelperTest {
	
	@Test
	public void testFlip3x8() {
		int[] ret;
		int[] buffer = new int[] {
			0,0,0,
			1,2,3,			
			4,5,6,
			7,8,9,
			0,1,1,
			0,2,2,
			0,3,3,
			0,4,4,
		};
		
		ret = OutputHelper.flipSecondScanline(buffer, 3, 8);
		assertEquals(0, ret[0]);
		assertEquals(3, ret[3]);
		assertEquals(2, ret[4]);
		assertEquals(1, ret[5]);
		assertEquals(4, ret[6]);
	}

	@Test
	public void testOutputMapping() {
		int[] ret;
		int[] mapping = new int[] { 3,1,4,5,2,0 };
		int[] buffer = new int[] {
				1,2,3,			
				4,5,6
		};

		ret = OutputHelper.manualMapping(buffer, mapping, 3, 2);
		RotateBufferTest.dumpBuffer(ret);
		
		assertEquals(4, ret[0]);
		assertEquals(2, ret[1]);
		assertEquals(5, ret[2]);
		assertEquals(6, ret[3]);
		assertEquals(3, ret[4]);
		assertEquals(1, ret[5]);
	}

	@Test
	public void testOutputMappingWrong() {
		int[] mapping = new int[160];
		int[] buffer = new int[160];
		for (int i=0; i<160; i++) {
			mapping[i] = i+1;
		}
		OutputHelper.manualMapping(buffer, mapping, 8, 20);
	}
}
