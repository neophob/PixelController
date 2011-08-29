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
	public void rotateTest8x8() {
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
		
		ret = RotateBuffer.transformImage(buffer, DeviceConfig.NO_ROTATE, 8, 8);
		assertEquals(1, ret[0]);

		ret = RotateBuffer.transformImage(buffer, DeviceConfig.ROTATE_90, 8, 8);
		assertEquals(1, ret[7]);
		ret = RotateBuffer.transformImage(buffer, DeviceConfig.ROTATE_90_FLIPPEDY, 8, 8);
		assertEquals(1, ret[63]);

		ret = RotateBuffer.transformImage(buffer, DeviceConfig.ROTATE_180, 8, 8);
		assertEquals(1, ret[63]);
		ret = RotateBuffer.transformImage(buffer, DeviceConfig.ROTATE_180_FLIPPEDY, 8, 8);
		assertEquals(1, ret[7]);

		ret = RotateBuffer.transformImage(buffer, DeviceConfig.ROTATE_270, 8, 8);
		assertEquals(1, ret[56]);
		ret = RotateBuffer.transformImage(buffer, DeviceConfig.ROTATE_270_FLIPPEDY, 8, 8);
		assertEquals(1, ret[0]);
	}

	@Test
	public void rotateTest8x4() {
		int[] ret;
		int[] buffer = new int[] {
			1,2,3,0,0,0,0,0,
			0,0,0,0,0,0,0,0,
			0,0,0,0,0,0,0,0,
			0,0,0,0,0,0,0,4
		};
		
		ret = RotateBuffer.transformImage(buffer, DeviceConfig.NO_ROTATE, 8, 4);
		assertEquals(1, ret[0]);

		ret = RotateBuffer.transformImage(buffer, DeviceConfig.ROTATE_90, 8, 4);
		assertEquals(1, ret[7]);
		ret = RotateBuffer.transformImage(buffer, DeviceConfig.ROTATE_90_FLIPPEDY, 8, 4);
		assertEquals(1, ret[31]);

		ret = RotateBuffer.transformImage(buffer, DeviceConfig.ROTATE_180, 8, 4);
		assertEquals(1, ret[31]);
		ret = RotateBuffer.transformImage(buffer, DeviceConfig.ROTATE_180_FLIPPEDY, 8, 4);
		assertEquals(1, ret[7]);

		ret = RotateBuffer.transformImage(buffer, DeviceConfig.ROTATE_270, 8, 4);
		assertEquals(1, ret[24]);
		ret = RotateBuffer.transformImage(buffer, DeviceConfig.ROTATE_270_FLIPPEDY, 8, 4);
		assertEquals(1, ret[0]);
	}

}
