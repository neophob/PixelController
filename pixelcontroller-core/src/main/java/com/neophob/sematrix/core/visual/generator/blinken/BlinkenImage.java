/**
 * Copyright (C) 2011-2014 Michael Vogt <michu@neophob.com>
 *
 * This file is part of PixelController.
 *
 * PixelController is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PixelController is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with PixelController.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.neophob.sematrix.core.visual.generator.blinken;

public class BlinkenImage {

	private final int width, height;	
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

	public void addData(int[] fragment) {
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
