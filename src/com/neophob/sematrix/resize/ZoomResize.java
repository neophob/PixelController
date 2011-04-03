package com.neophob.sematrix.resize;

import java.awt.image.BufferedImage;

import com.neophob.sematrix.resize.util.ScalrOld;


/**
 * 
 * 
 * @author michu
 *
 */
public class ZoomResize extends Resize {

	public ZoomResize() {
		super(ResizeName.ZOOM_RESIZE);
	}
	
	public int[] getBuffer(int[] buffer, int deviceXSize, int deviceYSize, int currentXSize, int currentYSize) {
		//zoom image
		int[] resizedBuffer = new int[(currentXSize/2) * (currentYSize/2)];
		int ofs=0;
		for (int y=0; y<currentYSize/2; y++) {
			for (int x=0; x<currentXSize/2; x++) {
				resizedBuffer[ofs++] = buffer[currentXSize*y + x];
			}
		}
		
		BufferedImage bi = createImage(resizedBuffer, currentXSize/2, currentYSize/2);
		
		bi = ScalrOld.resize(bi, ScalrOld.Method.SPEED, deviceXSize, deviceYSize);

		int[] ret = getPixelsFromImage(bi, deviceXSize, deviceYSize);
		
		//destroy image
		bi.flush();
		
		return ret;	
	}
}
