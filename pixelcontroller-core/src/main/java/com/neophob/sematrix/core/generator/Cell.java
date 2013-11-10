/**
 * Copyright (C) 2011-2013 Michael Vogt <michu@neophob.com>
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
package com.neophob.sematrix.core.generator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.neophob.sematrix.core.glue.MatrixData;
import com.neophob.sematrix.core.resize.Resize.ResizeName;


/**
 * moving cell.
 *
 * @author mvogt
 */
public class Cell extends Generator {

	/** The Constant BUBBLES. */
	private static final int NR_OF_CELLS=5;
	
	/** The Constant RENDERSIZE. */
	private static final int RENDERSIZE=2;

	private static final int RENDERSIZE_SQRT=RENDERSIZE*RENDERSIZE;

	/** The random. */
	private Random random=new Random();
	
	/** The points. */
	private List<Attractor> points=new ArrayList<Attractor>();
	
	/** The distlookup. */
	private float[][] distlookup;
	
	private int lowXRes, lowYRes;
	private int hsize;

	/**
	 * Instantiates a new cell.
	 *
	 * @param controller the controller
	 */
	public Cell(MatrixData matrix) {
		super(matrix, GeneratorName.CELL, ResizeName.QUALITY_RESIZE);

		//create LUT
		hsize = (int)(Math.sqrt(internalBufferXSize*internalBufferYSize*2));
		distlookup=new float[hsize][hsize];
		for (int i=0;i<hsize;i++) {
			for (int j=0;j<hsize;j++) {
				distlookup[i][j]=(float)Math.sqrt(Math.pow(i,2)+Math.pow(j,2));
			}
		}

		for (int i=0;i<NR_OF_CELLS;i++) {
			points.add(new Attractor());   
		}

		lowXRes = (int)Math.floor(internalBufferXSize/(float)RENDERSIZE);
		lowYRes = (int)Math.floor(internalBufferYSize/(float)RENDERSIZE);
		System.out.println(lowXRes+"  "+lowYRes);
	}


	/* (non-Javadoc)
	 * @see com.neophob.sematrix.core.generator.Generator#update()
	 */
	@Override
	public void update() {
		int ofs = 0;
		for (int x=0;x<lowXRes;x+=RENDERSIZE) {
			for (int y=0;y<lowYRes;y+=RENDERSIZE) {

				int nearest=0;
				float closest=1000.0f;      

				for (int p=0; p<points.size(); p++) {
					Attractor a=(Attractor)points.get(p);
					float dist=a.distanceTo(x,y);
					if (dist<closest) {
						nearest=p;
						closest=dist;
					}
				}

				Attractor a=(Attractor)points.get(nearest);
				//rect(x*RENDERSIZE,y*RENDERSIZE, RENDERSIZE_SQRT, RENDERSIZE_SQRT, a.color);
				this.internalBuffer[ofs++] = a.color;
			}
		}
System.out.println("CL: "+ofs);
		for (Attractor a: points) {			
			a.move();
		}   
	}

	/**
	 * draw rectangle in buffer.
	 *
	 * @param xofs the xofs
	 * @param yofs the yofs
	 * @param xsize the xsize
	 * @param ysize the ysize
	 * @param col the col
	 */
	private void rect(int xofs, int yofs, int xsize, int ysize, int col) {
		if (ysize+yofs>internalBufferYSize) {
			ysize=ysize+yofs-internalBufferYSize;
		}
		if (xsize+xofs>internalBufferXSize) {
			xsize=xsize+xofs-internalBufferXSize;
		}
		
		for (int y=0; y<ysize; y++) {
			int ofs=(yofs+y)*internalBufferXSize+xofs;
			for (int x=0; x<xsize; x++) {				
				this.internalBuffer[ofs++] = col;
			}
		}
	}
	
	/**
	 * helper class.
	 *
	 * @author michu
	 */
	class Attractor {

		/** The x. */
		int x;
		
		/** The y. */
		int y;
		
		/** The dx. */
		int dx;
		
		/** The dy. */
		int dy;
		
		/** The color */
		int color;

		/**
		 * Instantiates a new attractor.
		 */
		public Attractor() {
			if (lowXRes>0) {
				this.x=random.nextInt(lowXRes);				
			} else {
				this.x=1;
			}
			
			if (lowYRes>0) {
				this.y=random.nextInt(lowYRes);				
			} else {
				this.y=1;
			}
			while (this.dx==0) {
				this.dx=-1+random.nextInt(2);
			}
			while (this.dy==0) {
				this.dy=-1+random.nextInt(2); 
			}
			this.color=random.nextInt(255);			
		}

		/**
		 * Move.
		 */
		public void move() {
			// move with wrap-around
			this.x+=this.dx;
			this.y+=this.dy;
			if (this.x<0 || this.x>lowXRes) {
				this.dx=-this.dx;
			}
			if (this.y<0 || this.y>lowYRes) {
				this.dy=-this.dy;
			}
			
			int rnd = random.nextInt(100);
			if (rnd==3) {
				this.color=random.nextInt(255);
				System.out.println("CellCol2: "+color);
			}
			
		}
		
		/**
		 * Distance to.
		 *
		 * @param xx the xx
		 * @param yy the yy
		 * @return the float
		 */
		public float distanceTo(int xx,int yy) {
			// Euclidian Distance
			return distlookup[Math.abs(xx-this.x)%hsize][Math.abs(yy-this.y)%hsize]; 
		}
	}
}
