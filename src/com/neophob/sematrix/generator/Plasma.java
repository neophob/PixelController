package com.neophob.sematrix.generator;


/**
 * TODO: multiple palettes
 * 		 various sizes
 * @author mvogt
 *
 */
public class Plasma extends Generator {

	private int pos1 = 0, pos2 = 0, pos3 = 0, pos4 = 0, tpos1, tpos2, tpos3, tpos4;
	private static int[] aSin = new int[512];
	private static MyCol[] colors;

	public Plasma() {
		super(GeneratorName.PLASMA);
		
		double rad;
		/*create sin lookup table */
		for (int i = 0; i < 512; i++) {
			rad =  ((double)i * 0.703125) * 0.0174532; /* 360 / 512 * degree to rad, 360 degrees spread over 512 values to be able to use AND 512-1 instead of using modulo 360*/
			aSin[i] = (int)(Math.sin(rad) * 1024); /*using fixed point math with 1024 as base*/
		}
		
		colors = new MyCol[256];
		for (int i = 0; i < 64; ++i) {
			colors[i] = new MyCol(i<<2, 255 - ((i << 2) + 1), 0);
			colors[i+64] = new MyCol(255, (i << 2) + 1, 0);
			colors[i+128] = new MyCol(255 - ((i << 2) + 1), 255 - ((i << 2) + 1), 0);
			colors[i+192] = new MyCol(0, (i << 2) + 1, 0);
		} 	
	}
	
	@Override
	public void update() {
		int x, index;
		int xSizeOfScreen = this.getInternalBufferXSize();
		tpos4 = pos4;
		tpos3 = pos3;
		int n=0;
		
		for (int i = 0; i < this.getInternalBufferYSize(); ++i) {
			tpos1 = pos1 + 5;
			tpos2 = pos2 + 3;
			tpos3 &= 511;
			tpos4 &= 511;

			for (int j = 0; j < xSizeOfScreen; ++j) {
				tpos1 &= 511;
				tpos2 &= 511;

				x = aSin[tpos1] + aSin[tpos2] + aSin[tpos3] + aSin[tpos4]; /*actual plasma calculation*/
				index = Math.abs(128 + (x >> 4)); /*fixed point multiplication but optimized so basically it says (x * (64 * 1024) / (1024 * 1024)), x is already multiplied by 1024*/				
				MyCol col = colors[index%255];
				int color = (int)(col.r << 16) | (col.g << 8) | (col.b);
				this.internalBuffer[n++] = color;
				
			 	tpos1 += 5; 
				tpos2 += 3; 
			}
			tpos4 += 3;
			tpos3 += 1;
		}

		/* move plasma */
		pos1 +=9;
		pos3 +=8;	}

	@Override
	public void close() {	}

	
	class MyCol {
		private int r,g,b;
		
		public MyCol(int r, int g, int b) {
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
		
	}
}
