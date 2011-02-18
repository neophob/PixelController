package com.neophob.sematrix.generator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


/**
 * @author mvogt
 * 
 */
public class Cell extends Generator {

	private static final int BUBBLES=7;
	private static final int RENDERSIZE=2;

	private Random die=new Random();
	private List<Attractor> points=new ArrayList<Attractor>();
	private float[][] distlookup;

	public Cell() {
		super(GeneratorName.CELL);

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
		//background(0);   // Set the background to black
		//fill(255);

		for (int x=0;x<internalBufferXSize/RENDERSIZE;x+=RENDERSIZE) {
			for (int y=0;y<internalBufferYSize/RENDERSIZE;y+=RENDERSIZE) {

				int nearest=0;
				float closest=1000.0f;      

				for (int p=0;p<points.size();p++) {
					Attractor a=(Attractor)points.get(p);
					float dist=a.distanceTo(x,y);
					if (dist<closest) {
						nearest=p;
						closest=dist;
					}
				}

				Attractor a=(Attractor)points.get(nearest);
				int l= (int)(255-6*closest);
				//fill(a.r/2+l/2,a.g/2+l/2,a.b/2+l/2);      
				//		      fill(a.r,a.g,a.b);      
				//		      fill(l-a.r,l-a.g,l-a.b);      

				//		      int l= (int)(255-8*closest);
				//		      fill(l,l,l);

				int col = ((a.r/2+l/2) << 16) | ((a.g/2+l/2) << 8) | (a.b/2+l/2);
				rect(x*RENDERSIZE,y*RENDERSIZE, RENDERSIZE*RENDERSIZE, RENDERSIZE*RENDERSIZE, col);				
			}
		}

		for (Attractor a: points) {			
			a.move();
		}   
	}

	/**
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


	class Attractor {

		public int x;
		public int y;
		public int dx;
		public int dy;
		public int r,g,b;

		public Attractor() {
			this.x=die.nextInt(internalBufferXSize/RENDERSIZE);
			this.y=die.nextInt(internalBufferYSize/RENDERSIZE);
			while (this.dx==0) {
				this.dx=-2+die.nextInt(4);
			}
			while (this.dy==0) {
				this.dy=-2+die.nextInt(4); 
			}
			this.r=die.nextInt(255);
			this.g=die.nextInt(255);
			this.b=die.nextInt(255);
		}

		public void move() {
			// move with wrap-around
			this.x+=this.dx;
			this.y+=this.dy;
			if (this.x<0 || this.x>internalBufferXSize/RENDERSIZE) this.dx=-this.dx;
			if (this.y<0 || this.y>internalBufferYSize/RENDERSIZE) this.dy=-this.dy;

			int r = die.nextInt(111);
			if (r==3) this.r=die.nextInt(255);
			if (r==44) this.g=die.nextInt(255);
			if (r==99) this.b=die.nextInt(255);

		}

		public float distanceTo(int xx,int yy) {
			// Euclidian Distance
			return distlookup[Math.abs(xx-this.x)][Math.abs(yy-this.y)]; 
		}
	}
}
