package com.neophob.sematrix.output.gamma;

public class RGBAdjust {

	private int r,g,b;
	
	public RGBAdjust(int r, int g, int b) {
		this.r = r;
		this.g = g;
		this.b = b;
	}

	public int getR() {
		return r;
	}

	public int getG() {
		return g;
	}

	public int getB() {
		return b;
	}

	@Override
	public String toString() {
		return "[r=" + r + ", g=" + g + ", b=" + b + "]";
	}
	
	

}
