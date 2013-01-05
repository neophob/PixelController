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

package com.neophob.sematrix.output.gui;

import processing.core.PApplet;

import com.neophob.sematrix.glue.Collector;
import com.neophob.sematrix.glue.MatrixData;
import com.neophob.sematrix.layout.Layout;
import com.neophob.sematrix.output.Output;
import com.neophob.sematrix.properties.ApplicationConfigurationHelper;

/**
 * The low resolution software matrix 
 * 
 * @author michu
 * 
 */
public class OutputGui {

	private static final int MAX_BPP = 8;
	
	/** The Constant RAHMEN_SIZE. */
	private static final int RAHMEN_SIZE = 4;
	
	/** The Constant LED_ABSTAND. */
	private static final int LED_ABSTAND = 0;
	
	/** The frame. */
	private int frame = 0;
	
	/** The led size. */
	private int ledSize;
	
	private PApplet parent;
	
	/** The matrix data. */
	private MatrixData matrixData;
	
	/** The layout. */
	private Layout layout;
	
	private Collector collector;
	
	private Output output;

	/**
	 * Instantiates a new matrix emulator.
	 *
	 * @param controller the controller
	 */
	public OutputGui(ApplicationConfigurationHelper ph, Output output) {
		this.output = output;
		this.ledSize = ph.getLedPixelSize();
		this.collector = Collector.getInstance();
		this.matrixData = this.collector.getMatrix();
		this.layout = ph.getLayout();
		
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
		
		x+=RAHMEN_SIZE;
		y+=20+2*RAHMEN_SIZE;
		this.parent = this.collector.getPapplet();		
		this.parent.size(x, y);
		this.parent.frame.setSize(x,y);
		this.parent.frame.setTitle("PixelController Output Window");
		this.parent.frame.setIconImage(GeneratorGuiCreator.createIcon());
		if (this.parent.frame.isAlwaysOnTopSupported()) {
			this.parent.frame.setAlwaysOnTop(true);			
		}
		this.parent.background(33,33,33);
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

	/**
	 * draw simulated output
	 */
	public void update() {
		frame++;
		
		//a little hack to place this window on top of the gui window
		if (frame==20) {
			if (this.parent.frame.isAlwaysOnTopSupported()) {
				this.parent.frame.setAlwaysOnTop(false);			
			}			
		}
		
		//show only each 2nd frame to reduce cpu load
		if (frame%2==1) {
			return;
		}
		
		int cnt=0;
		int currentOutput = this.collector.getCurrentOutput();
		
		switch (layout.getLayoutName()) {
		case HORIZONTAL:
			for (int screen=0; screen<this.collector.getNrOfScreens(); screen++) {
				drawOutput(cnt++, screen, 0, this.output.getBufferForScreen(screen), currentOutput);
			}
			break;

		case BOX:
			int ofs=0;
			for (int screen=0; screen<layout.getRow1Size(); screen++) {
				drawOutput(cnt++, screen, 0, this.output.getBufferForScreen(screen), currentOutput);
				ofs++;
			}
			for (int screen=0; screen<layout.getRow2Size(); screen++) {
				drawOutput(cnt++, screen, 1, this.output.getBufferForScreen(ofs+screen), currentOutput);
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
	private void drawOutput(int nr, int nrX, int nrY, int buffer[], int currentOutput) {
		int xOfs = nrX*(getOneMatrixXSize()+LED_ABSTAND);
		int yOfs = nrY*(getOneMatrixYSize()+LED_ABSTAND);
		int ofs=0;
		int tmp,r,g,b;

		//mark the active visual
		if (nr == currentOutput) {
			parent.fill(66,66,66);
		} else {
			parent.fill(33,33,33);
		}
		parent.rect(xOfs, yOfs, getOneMatrixXSize(), getOneMatrixYSize());
		
		int shift = MAX_BPP - this.output.getBpp();
		
		for (int y=0; y<matrixData.getDeviceYSize(); y++) {
			for (int x=0; x<matrixData.getDeviceXSize(); x++) {
				tmp = buffer[ofs++];
				r = (int) ((tmp>>16) & 255);
				g = (int) ((tmp>>8)  & 255);
				b = (int) ( tmp      & 255);

				//simulate lower bpp
				if (shift>0) {
					r >>= shift;
					g >>= shift;
					b >>= shift;
					r <<= shift;
					g <<= shift;
					b <<= shift;
				}
				parent.fill(r,g,b);
				parent.rect(xOfs+RAHMEN_SIZE+x*(RAHMEN_SIZE+ledSize),
							yOfs+RAHMEN_SIZE+y*(RAHMEN_SIZE+ledSize),
							ledSize,ledSize);
			}
		}
	}
}