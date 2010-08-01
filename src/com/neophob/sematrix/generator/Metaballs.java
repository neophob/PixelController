package com.neophob.sematrix.generator;


public class Metaballs extends Generator {
	int numBlobs = 3;

	//TODO make movement dynamic
	int[] blogPx = { 0, 40, 40 };
	int[] blogPy = { 0, 60, 45 };

	// Movement vector for each blob
	int[] blogDx = { 1, 1, 1 };
	int[] blogDy = { 1, 1, 1 };
	int[][] vy,vx; 

	public Metaballs() {
		super(GeneratorName.METABALLS);
		vy = new int[numBlobs][getInternalBufferYSize()];
		vx = new int[numBlobs][getInternalBufferXSize()];
	}

	@Override
	public void update() {
		int xSize = getInternalBufferXSize();
		int ySize = getInternalBufferYSize();
		for (int i=0; i<numBlobs; ++i) {
			blogPx[i]+=blogDx[i];
			blogPy[i]+=blogDy[i];

			// bounce across screen
			if (blogPx[i] < 0) {
				blogDx[i] = 1;
			}
			if (blogPx[i] > xSize) {
				blogDx[i] = -1;
			}
			if (blogPy[i] < 0) {
				blogDy[i] = 1;
			}
			if (blogPy[i] > ySize) {
				blogDy[i]=-1;
			}

			for (int x = 0; x < xSize; x++) {
				vx[i][x] = (blogPx[i]-x)*(blogPx[i]-x);
			}

			for (int y = 0; y < ySize; y++) {
				vy[i][y] = (blogPy[i]-y)*(blogPy[i]-y); 
			}
		}

		for (int y = 0; y < ySize; y++) {
			for (int x = 0; x < xSize; x++) {
				int m = 1;
				for (int i = 0; i < numBlobs; i++) {
					// Increase this number to make your blobs bigger
					m += 60000/(vy[i][y] + vx[i][x]+1);
				}
				//pg.pixels[x+y*pg.width] = color(0, m+x, (x+m+y)/2);
				int g = m+x;
				int b = (x+m+y)/3;
				if (g>255) g=255;
				if (b>255) b=255;
				this.internalBuffer[y*xSize+x] = (0 << 16) | (g << 8) | (b);
			}
		}

	}

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}
}
