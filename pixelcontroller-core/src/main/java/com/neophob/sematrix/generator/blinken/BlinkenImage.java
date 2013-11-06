package com.neophob.sematrix.generator.blinken;

public class BlinkenImage {

	final private int width, height;	
	private int pointer;
	private int[] data;
	
	public BlinkenImage(int width, int height) {
		this.width = width;
		this.height = height;
		this.pointer = 0;
		this.data = new int[width*height];
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format("BlinkenImage [width=%s, height=%s, pointer=%s]",
				width, height, pointer);
	}

	public void addData(int fragment[]) {
		if (pointer + fragment.length > data.length) {
			return;
		}
		
		System.arraycopy(fragment, 0, data, pointer, fragment.length);
		pointer += fragment.length;
	}

	/**
	 * @return the width
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * @return the height
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * @return the pointer
	 */
	public int getPointer() {
		return pointer;
	}

	/**
	 * @return the data
	 */
	public int[] getData() {
		return data;
	}
	
	
	
	
}
