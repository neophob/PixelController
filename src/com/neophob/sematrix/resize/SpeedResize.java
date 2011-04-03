package com.neophob.sematrix.resize;

import java.awt.image.BufferedImage;

import com.neophob.sematrix.glue.image.ScalrOld;

/**
 * 
 * 
 * @author michu
 *
 */
public class SpeedResize extends Resize {

	public SpeedResize() {
		super(ResizeName.SPEED_RESIZE);
	}
	
	public int[] getBuffer(int[] buffer, int deviceXSize, int deviceYSize, int currentXSize, int currentYSize) {
		BufferedImage bi = createImage(buffer, currentXSize, currentYSize);
		
		bi = ScalrOld.resize(bi, ScalrOld.Method.SPEED, deviceXSize, deviceYSize);

		int[] ret = getPixelsFromImage(bi, deviceXSize, deviceYSize);
		
		//destroy image
		bi.flush();
		
		return ret;
	}
}
