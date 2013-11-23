package com.neophob.sematrix.gui.model;

/**
 * 
 * @author michu
 *
 */
public class Point {

	private int x;
	private int y;

	public Point(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format("Point [x=%s, y=%s]", x, y);
	}

	
}
