/**
 * Copyright (C) 2011 Michael Vogt <michu@neophob.com>
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

package com.neophob.sematrix.color;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class defines a color set
 * 
 * @author michu
 *
 */
public class ColorSet implements Comparable<ColorSet> {

	private static final Logger LOG = Logger.getLogger(ColorSet.class.getName());

	private String name;

	private int[] colors;

	private int[] precalc;

	private int boarderCount;

	/**
	 * 
	 * @param name
	 * @param colors
	 */
	public ColorSet(String name, int[] colors) {
		this.name = name;
		this.colors = colors.clone();
		this.boarderCount = 255 / colors.length;

		//precalc colorset to save to cpu cycles
		precalc = new int[256];
		for (int i=0; i<256; i++) {
			int ofs=0;

			int pos = i;
			while (pos > boarderCount) {
				pos -= boarderCount;
				ofs++;
			}

			int targetOfs = ofs+1;

			precalc[i] = calcSmoothColor(colors[targetOfs%colors.length], colors[ofs%colors.length], pos);
		}
	}

	/**
	 * get ColorSet name
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * returns a random color of this set
	 * @return
	 */
	public int getRandomColor() {
		Random r = new Random();
		return this.colors[r.nextInt(colors.length)];
	}

	/**
	 * return a color defined in this color set 
	 * 
	 * @param pos
	 * @return
	 */
	public int getSmoothColor(int pos) {
		return precalc[pos];
	}

	/**
	 * 
	 * @param color
	 * @return
	 */
	public int getInvertedColor(int color) {

		int b= color&255;
		int g=(color>>8)&255;
		int r=(color>>16)&255;        

		//convert it to greyscale, not really correct
		int val = (int)(r*0.3f+g*0.59f+b*0.11f);

		return getSmoothColor(val+128);
	}


	/**
	 * 
	 * @param col1
	 * @param col2
	 * @param pos which position (which color offset)
	 * @return
	 */
	private int calcSmoothColor(int col1, int col2, int pos) {
		int b= col1&255;
		int g=(col1>>8)&255;
		int r=(col1>>16)&255;
		int b2= col2&255;
		int g2=(col2>>8)&255;
		int r2=(col2>>16)&255;

		int mul=pos*colors.length;
		int oppositeColor = 255-mul;

		r=(r*mul + r2*oppositeColor) >> 8;
		g=(g*mul + g2*oppositeColor) >> 8;
		b=(b*mul + b2*oppositeColor) >> 8;

		return (r << 16) | (g << 8) | (b);
	}

	/**
	 * convert entries from the properties file into colorset objects
	 * @param palette
	 * @return
	 */
	public static List<ColorSet> loadAllEntries(Properties palette) {
		List<ColorSet> ret = new LinkedList<ColorSet>();

		for (Entry<Object, Object> entry : palette.entrySet()) {
			try {
				String setName = (String)entry.getKey();
				String setColors = (String)entry.getValue();
				String[] colorsAsString = setColors.split(",");

				//convert hex string into int
				int[] colorsAsInt = new int[colorsAsString.length];
				int ofs=0;
				for (String s: colorsAsString) {
					colorsAsInt[ofs++] = Integer.decode(s.trim());
				}
				ColorSet cs = new ColorSet(setName, colorsAsInt);
				ret.add(cs);				
			} catch (Exception e) {
				LOG.log(Level.SEVERE, "Failed to load Palette entry: "+entry.getKey(), e);
			}
		}

		//sorty by name
		Collections.sort(ret);

		return ret;
	}


	/**
	 * colorize an image buffer
	 * 
	 * @param buffer 8bpp image
	 * @param cs ColorSet to apply
	 * @return 24 bpp image
	 */
	public int[] convertToColorSetImage(int[] buffer) {

		int[] ret = new int[buffer.length];

		for (int i=0; i<ret.length; i++){
			//use only 8bpp here!
			ret[i]=precalc[buffer[i]&255];
		}

		return ret;	
	}



	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(ColorSet otherColorSet) {
		if (otherColorSet.getName() == null && this.getName() == null) {
			return 0;
		}
		if (this.getName() == null) {
			return 1;
		}
		if (otherColorSet.getName() == null) {
			return -1;
		}
		return this.getName().compareTo(otherColorSet.getName());    
	}
}
