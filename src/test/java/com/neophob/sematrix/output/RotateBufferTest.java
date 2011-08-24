package com.neophob.sematrix.output;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.neophob.sematrix.properties.DeviceConfig;

/**
 * verify the rotate buffer code
 * @author michu
 *
 */
public class RotateBufferTest {

	@SuppressWarnings("unused")
	private static void dumpBuffer(int[] ret) {
		int a=0;
		for (int r: ret) {
			System.out.print(r+", ");
			a++;
			if (a==8) {System.out.println();a=0;}
		}
	}
	
	@Test
	public void rotateTest() {
		int[] ret;
		int[] buffer = new int[] {
			1,2,3,0,0,0,0,0,
			0,0,0,0,0,0,0,0,
			0,0,0,0,0,0,0,0,
			0,0,0,0,0,0,0,0,
			0,0,0,0,0,0,0,0,
			0,0,0,0,0,0,0,0,
			0,0,0,0,0,0,0,0,
			0,0,0,0,0,0,0,4
		};
		
		ret = RotateBuffer.transformImage(buffer, DeviceConfig.NO_ROTATE);
		assertEquals(1, ret[0]);

		ret = RotateBuffer.transformImage(buffer, DeviceConfig.ROTATE_90);
		assertEquals(1, ret[7]);
		ret = RotateBuffer.transformImage(buffer, DeviceConfig.ROTATE_90_FLIPPEDY);
		assertEquals(1, ret[63]);

		ret = RotateBuffer.transformImage(buffer, DeviceConfig.ROTATE_180);
		assertEquals(1, ret[63]);
		ret = RotateBuffer.transformImage(buffer, DeviceConfig.ROTATE_180_FLIPPEDY);
		assertEquals(1, ret[7]);

		ret = RotateBuffer.transformImage(buffer, DeviceConfig.ROTATE_270);
		assertEquals(1, ret[56]);
		ret = RotateBuffer.transformImage(buffer, DeviceConfig.ROTATE_270_FLIPPEDY);
		assertEquals(1, ret[0]);

	}
}
