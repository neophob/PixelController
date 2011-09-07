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

package com.neophob.sematrix.output;

import processing.core.PApplet;

import com.neophob.sematrix.glue.Collector;
import com.neophob.sematrix.properties.PropertiesHelper;

import edu.umd.cs.findbugs.annotations.SuppressWarnings;

/**
 * The Class MatrixEmulator.
 */
public class MatrixEmulator extends Output {

	/** The Constant RAHMEN_SIZE. */
	private static final int RAHMEN_SIZE = 4;
	
	/** The Constant LED_SIZE. */
	private static final int LED_SIZE = 32;
	
	/** The Constant LED_ABSTAND. */
	private static final int LED_ABSTAND = 0;
	
	/** The frame. */
	private int frame = 0;
	
	/** The led size. */
	private int ledSize = LED_SIZE;

	/**
	 * Instantiates a new matrix emulator.
	 *
	 * @param controller the controller
	 */
	public MatrixEmulator(PixelControllerOutput controller) {
		super(controller, MatrixEmulator.class.toString());
		ledSize = PropertiesHelper.getInstance().getLedPixelSize();
		
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
	 * Gets the one matrix x size.
	 *
	 * @return the one matrix x size
	 */
	private int getOneMatrixXSize() {
		return LED_ABSTAND+RAHMEN_SIZE+matrixData.getDeviceXSize()*(RAHMEN_SIZE+ledSize);
	}
	
	/**
	 * Gets the one matrix y size.
	 *
	 * @return the one matrix y size
	 */
	private int getOneMatrixYSize() {
		return LED_ABSTAND+RAHMEN_SIZE+matrixData.getDeviceYSize()*(RAHMEN_SIZE+ledSize);
	}

	/* (non-Javadoc)
	 * @see com.neophob.sematrix.output.Output#update()
	 */
	@Override
	@SuppressWarnings
	public void update() {
		frame++;
		
		//show only each 2nd frame to reduce cpu load
		if (frame%2==1) {
			return;
		}
		
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
	 * draw the matrix simulation onscreen.
	 *
	 * @param nrX the nr x
	 * @param nrY the nr y
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

				//simulate 5bit color
				r >>= 3;
				g >>= 3;
				b >>= 3;
				r <<= 3;
				g <<= 3;
				b <<= 3;

				parent.fill(r,g,b);
				parent.rect(xOfs+RAHMEN_SIZE+x*(RAHMEN_SIZE+ledSize),
							yOfs+RAHMEN_SIZE+y*(RAHMEN_SIZE+ledSize),
							ledSize,ledSize);
			}		
		}
	}

	/* (non-Javadoc)
	 * @see com.neophob.sematrix.output.Output#close()
	 */
	@Override
	public void close() {
		// TODO Auto-generated method stub
	}


}
