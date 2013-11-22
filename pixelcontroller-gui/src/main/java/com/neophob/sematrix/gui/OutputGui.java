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
package com.neophob.sematrix.gui;

import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

import processing.core.PApplet;

import com.neophob.sematrix.core.glue.Collector;
import com.neophob.sematrix.core.glue.MatrixData;
import com.neophob.sematrix.core.layout.Layout;
import com.neophob.sematrix.core.output.Output;
import com.neophob.sematrix.core.properties.ApplicationConfigurationHelper;
import com.neophob.sematrix.gui.model.LedSimulatorOutputWindow;
import com.neophob.sematrix.gui.model.Point;

/**
 * The low resolution software matrix 
 * 
 * @author michu
 * 
 */
public class OutputGui {

	private static final Logger LOG = Logger.getLogger(OutputGui.class.getName());

	private static final int MAX_BPP = 8;
			
	/** The frame. */
	private int frame = 0;
		
	private PApplet parent;
	
	/** The matrix data. */
	private MatrixData matrixData;
	
	/** The layout. */
	private Layout layout;
	
	private Collector collector;
	
	private Output output;
	
	private LedSimulatorOutputWindow lsow;
	
	/**
	 * Instantiates a new matrix emulator.
	 *
	 * @param controller the controller
	 */
	public OutputGui(ApplicationConfigurationHelper ph, Output output, PApplet papplet) {
		this.output = output;
		this.collector = Collector.getInstance();
		this.matrixData = this.collector.getMatrix();
		this.layout = ph.getLayout();
		
		lsow = new LedSimulatorOutputWindow(matrixData, ph.getLedPixelSize(), layout);
		LOG.log(Level.INFO, "Create LED Matrix simulator window: "+lsow);
		Point p = lsow.getWindowSize();
		
		this.parent = papplet;		
		this.parent.size(p.getX(), p.getY());
		this.parent.frame.setSize(p.getX(), p.getY());
		this.parent.frame.setTitle("PixelController Output Window");
		this.parent.frame.setIconImage(GeneratorGuiCreator.createLargeIcon(papplet));
		if (this.parent.frame.isAlwaysOnTopSupported()) {
			this.parent.frame.setAlwaysOnTop(true);			
		}
		
		//try to set large osx icon here...
		try {
			String osName = System.getProperties().getProperty("os.name");
			if (osName != null && osName.startsWith("Mac OS X")) {
				//error: package com.apple.eawt does not exist
				//com.apple.eawt.Application.getApplication().setDockIconImage(GeneratorGuiCreator.createLargeIcon());
                Class<?> appClass = Class.forName("com.apple.eawt.Application");
                Method getAppMethod = appClass.getMethod("getApplication");
                Object appInstance = getAppMethod.invoke(null);
                Method dockMethod = appInstance.getClass().getMethod("setDockIconImage", java.awt.Image.class);
                dockMethod.invoke(appInstance, GeneratorGuiCreator.createLargeIcon(papplet));				
			}
		} catch (Throwable e) {
			//nothing left todo...
		}
		this.parent.background(33,33,33);
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
		if (frame%2==1 || this.output==null) {
			return;
		}
		
		int cnt=0;
		int currentOutput = this.collector.getCurrentOutput();
		
		switch (layout.getLayoutName()) {
		case HORIZONTAL:
			for (int screen=0; screen<this.collector.getNrOfScreens(); screen++) {
				drawOutput(cnt++, screen, 0, this.output.getBufferForScreen(screen, false), currentOutput);
			}
			break;

		case BOX:
			int ofs=0;
			for (int screen=0; screen<layout.getRow1Size(); screen++) {
				drawOutput(cnt++, screen, 0, this.output.getBufferForScreen(screen, false), currentOutput);
				ofs++;
			}
			for (int screen=0; screen<layout.getRow2Size(); screen++) {
				drawOutput(cnt++, screen, 1, this.output.getBufferForScreen(ofs+screen, false), currentOutput);
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
		int xOfs = nrX*lsow.getOneMatrixXSize();
		int yOfs = nrY*lsow.getOneMatrixYSize();
		int ofs=0;
		int tmp,r,g,b;

		//mark the active visual
		if (nr == currentOutput) {
			parent.fill(66,66,66);
		} else {
			parent.fill(33,33,33);
		}
		parent.rect(xOfs, yOfs + LedSimulatorOutputWindow.OFS/2, lsow.getOneMatrixXSize(), lsow.getOneMatrixYSize());
		
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
				parent.rect(xOfs+lsow.getRahmenSize()+x*(lsow.getRahmenSize()+lsow.getLedSize()),
							yOfs+lsow.getRahmenSize()+y*(lsow.getRahmenSize()+lsow.getLedSize()) + LedSimulatorOutputWindow.OFS/2,
							lsow.getLedSize(), lsow.getLedSize());
			}
		}
	}
}