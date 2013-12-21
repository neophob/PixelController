package com.neophob.sematrix.core.osc.remotemodel;

import java.io.Serializable;

public class ImageBuffer implements Serializable {
	
	private int[][] outputBuffer;
	private int[][] visualBuffer;
	
	public ImageBuffer(int[][] outputBuffer, int[][] visualBuffer) {
		this.outputBuffer = outputBuffer;
		this.visualBuffer = visualBuffer;
	}
	public int[][] getOutputBuffer() {
		return outputBuffer;
	}
	public int[][] getVisualBuffer() {
		return visualBuffer;
	}
	
}
