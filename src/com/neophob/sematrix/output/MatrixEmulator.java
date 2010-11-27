package com.neophob.sematrix.output;

import processing.core.PApplet;

import com.neophob.sematrix.glue.Collector;

public class MatrixEmulator extends Output {

	private static final int RAHMEN_SIZE = 4;
	private static final int LED_SIZE = 16;
	private static final int LED_ABSTAND = 0;

	/**
	 * 
	 */
	public MatrixEmulator() {
		super(MatrixEmulator.class.toString());

		int x,y;
		switch (layout.getLayoutName()) {
		case HORIZONTAL:
			x = getOneMatrixXSize()*layout.getRow1Size()+layout.getRow2Size();
			y = getOneMatrixYSize();
			break;
			
		default: //AKA BOX
			int xsize = (layout.getRow1Size()+layout.getRow2Size())/2;
			x = getOneMatrixXSize()*xsize;
			y = getOneMatrixYSize()*2; //2 rows
			break;
		}
		
		Collector.getInstance().getPapplet().size(x, y);
		Collector.getInstance().getPapplet().background(33,33,33);
	}

	/**
	 * 
	 * @return
	 */
	private int getOneMatrixXSize() {
		return LED_ABSTAND+RAHMEN_SIZE+matrixData.getDeviceXSize()*(RAHMEN_SIZE+LED_SIZE);
	}
	
	/**
	 * 
	 * @return
	 */
	private int getOneMatrixYSize() {
		return LED_ABSTAND+RAHMEN_SIZE+matrixData.getDeviceYSize()*(RAHMEN_SIZE+LED_SIZE);
	}

	@Override
	public void update() {
		switch (layout.getLayoutName()) {
		case HORIZONTAL:
			for (int screen=0; screen<Collector.getInstance().getNrOfScreens(); screen++) {
				drawOutput(screen, 0, super.getBufferForScreen(screen));
			}			
			break;

		case BOX:
			int ofs=0;
			for (int screen=0; screen<layout.getRow1Size(); screen++) {
				drawOutput(screen, 0, super.getBufferForScreen(screen));
				ofs++;
			}			
			for (int screen=0; screen<layout.getRow2Size(); screen++) {
				drawOutput(screen, 1, super.getBufferForScreen(ofs+screen));
			}			
			break;
		}
	}

	/**
	 * draw the matrix simulation onscreen
	 * @param n - x offset nr (0..n)
	 * @param buffer - the buffer to draw
	 */
	private void drawOutput(int nrX, int nrY, int buffer[]) {
		int xOfs = nrX*(getOneMatrixXSize()+LED_ABSTAND);
		int yOfs = nrY*(getOneMatrixYSize()+LED_ABSTAND);
		int ofs=0;
		int tmp,r,g,b;

		PApplet parent = Collector.getInstance().getPapplet();
		for (int y=0; y<matrixData.getDeviceYSize(); y++) {
			for (int x=0; x<matrixData.getDeviceXSize(); x++) {					
				tmp = buffer[ofs++];
				r = (int) ((tmp>>16) & 255);
				g = (int) ((tmp>>8)  & 255);       
				b = (int) ( tmp      & 255);

				//simulate 4bit color
				r >>= 4;
				g >>= 4;
				b >>= 4;
				r <<= 4;
				g <<= 4;
				b <<= 4;

				parent.fill(r,g,b);
				parent.rect(xOfs+RAHMEN_SIZE+x*(RAHMEN_SIZE+LED_SIZE),
							yOfs+RAHMEN_SIZE+y*(RAHMEN_SIZE+LED_SIZE),
							LED_SIZE,LED_SIZE);
			}		
		}
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
	}


}
