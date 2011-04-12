package com.neophob.sematrix.generator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.neophob.sematrix.resize.Resize.ResizeName;


/**
 * moving cell
 * 
 * @author mvogt
 * 
 */
public class Cell extends Generator {

	private static final int BUBBLES=8;
	private static final int RENDERSIZE=2;

	private Random random=new Random();
	private List<Attractor> points=new ArrayList<Attractor>();
	private float[][] distlookup;

	/**
	 * 
	 */
	public Cell() {
		super(GeneratorName.CELL, ResizeName.QUALITY_RESIZE);

		//create LUT
		int hsize = (int)(Math.sqrt(internalBufferXSize*internalBufferYSize*2));
		distlookup=new float[hsize][hsize];
		for (int i=0;i<hsize;i++) {
			for (int j=0;j<hsize;j++) {
				distlookup[i][j]=(float)Math.sqrt(Math.pow(i,2)+Math.pow(j,2));
			}
		}

		for (int i=0;i<BUBBLES;i++) {
			points.add(new Attractor());   
		}

	}


	@Override
	public void update() {
		for (int x=0;x<internalBufferXSize/RENDERSIZE;x+=RENDERSIZE) {
			for (int y=0;y<internalBufferYSize/RENDERSIZE;y+=RENDERSIZE) {

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
				/*int l = (int)(255-4*closest);

				int r = l-a.r;
				if (r<0) {
					r=0;
				}
				int g = l-a.g;
				if (g<0) {
					g=0;
				}
				int b = l-a.b;
				if (b<0) {
					b=0;
				}*/				
				//int col = ((a.r/2+l/2) << 16) | ((a.g/2+l/2) << 8) | (a.b/2+l/2);
				
				int col = (a.r << 16) | (a.g << 8) | a.b;
				rect(x*RENDERSIZE,y*RENDERSIZE, RENDERSIZE*RENDERSIZE, RENDERSIZE*RENDERSIZE, col);				
			}
		}

		for (Attractor a: points) {			
			a.move();
		}   
	}

	/**
	 * draw rectangle in buffer
	 * 
	 * @param xofs
	 * @param yofs
	 * @param xsize
	 * @param ysize
	 * @param col
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
	
	@Override
	public void close() {
	}

	/**
	 * helper class
	 * 
	 * @author michu
	 *
	 */
	class Attractor {

		public int x;
		public int y;
		public int dx;
		public int dy;
		public int r,g,b;

		/**
		 * 
		 */
		public Attractor() {
			this.x=random.nextInt(internalBufferXSize/RENDERSIZE);
			this.y=random.nextInt(internalBufferYSize/RENDERSIZE);
			while (this.dx==0) {
				this.dx=-2+random.nextInt(4);
			}
			while (this.dy==0) {
				this.dy=-2+random.nextInt(4); 
			}
			this.r=random.nextInt(255);
			this.g=random.nextInt(255);
			this.b=random.nextInt(255);
		}

		/**
		 * 
		 */
		public void move() {
			// move with wrap-around
			this.x+=this.dx;
			this.y+=this.dy;
			if (this.x<0 || this.x>internalBufferXSize/RENDERSIZE) this.dx=-this.dx;
			if (this.y<0 || this.y>internalBufferYSize/RENDERSIZE) this.dy=-this.dy;

			int rnd = random.nextInt(111);
			if (rnd==3) this.r=random.nextInt(255);
			if (rnd==44) this.g=random.nextInt(255);
			if (rnd==99) this.b=random.nextInt(255);
		}
		
		/**
		 * 
		 * @param xx
		 * @param yy
		 * @return
		 */
		public float distanceTo(int xx,int yy) {
			// Euclidian Distance
			return distlookup[Math.abs(xx-this.x)][Math.abs(yy-this.y)]; 
		}
	}
}
