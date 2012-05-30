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

import java.util.Random;

/**
 * This class defines a color set
 * 
 * @author michu
 *
 */
public class ColorSet {

  private String name;

  private int[] colors;
  
  private int boarderCount;

  public ColorSet(String name, int[] colors) {
    this.name = name;
    this.colors = colors.clone();
    this.boarderCount = 255 / colors.length;
  }

  public String getName() {
    return name;
  }

  public int getRandomColor() {
	Random r = new Random();
    return this.colors[r.nextInt(colors.length)];
  }

  int getSmoothColor(int pos) {
    pos %= 255;
    int ofs=0;
    while (pos > boarderCount) {
      pos -= boarderCount;
      ofs++;
    }
    
    int targetOfs = (ofs+1)%colors.length;
    //println("ofs:"+ofs+" targetofs:"+targetOfs);
    return calcSmoothColor(colors[targetOfs], colors[ofs], pos);
  }
  
  
  private int calcSmoothColor(int col1, int col2, int pos) {
    int b= col1&255;
    int g=(col1>>8)&255;
    int r=(col1>>16)&255;
    int b2= col2&255;
    int g2=(col2>>8)&255;
    int r2=(col2>>16)&255;

    int mul=pos*colors.length;
    int oppisiteColor = 255-mul;
    r=(r*mul)/255;
    g=(g*mul)/255;
    b=(b*mul)/255;
    r+=(r2*oppisiteColor)/255;
    g+=(g2*oppisiteColor)/255;
    b+=(b2*oppisiteColor)/255;

    return (r << 16) | (g << 8) | (b);
  }
  
}
