package com.neophob.sematrix.resize;

import java.awt.image.BufferedImage;

public interface IResize {

	int[] getBuffer(int[] buffer, int newX, int newY, int currentXSize, int currentYSize);
	
	int[] getBuffer(BufferedImage bi, int newX, int newY);

	int getId();

	BufferedImage createImage(int[] buffer, int currentXSize, int currentYSize);
}
