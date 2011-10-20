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

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.neophob.sematrix.glue.Collector;
import com.neophob.sematrix.glue.MatrixData;
import com.neophob.sematrix.glue.OutputMapping;
import com.neophob.sematrix.glue.Visual;
import com.neophob.sematrix.layout.Layout;
import com.neophob.sematrix.layout.LayoutModel;
import com.neophob.sematrix.properties.PropertiesHelper;

/**
 * parent output class.
 *
 * @author michu
 */
public abstract class Output {

	/** The log. */
	private static final Logger LOG = Logger.getLogger(Output.class.getName());

	/** The name. */
	private String name;
	
	/** The matrix data. */
	protected MatrixData matrixData;
	
	/** The layout. */
	protected Layout layout;
	
	protected Collector collector;
	
	/** *bit per pixel */
	protected int bpp;
	
	private Map<Integer, int[]> currentBufferMap;
	private Map<Integer, int[]> preparedBufferMap;
	
	/**
	 * Instantiates a new output.
	 *
	 * @param controller the controller
	 * @param name the name
	 */
	public Output(PropertiesHelper ph, PixelControllerOutput controller, String name, int bpp) {
		this.name = name;
		
		this.collector = Collector.getInstance();
		this.matrixData = this.collector.getMatrix();
		this.layout = ph.getLayout();
		this.bpp = bpp;
		
		this.currentBufferMap = new HashMap<Integer, int[]>();
		this.preparedBufferMap = new HashMap<Integer, int[]>();
		
		LOG.log(Level.INFO, "Output created: {0}, Layout: {1}, BPP: {2}"
				, new Object[] { this.name, layout.getLayoutName(), this.bpp });
	
		//add to list
		controller.addOutput(this);
	}
	
	/**
	 * Update.
	 */
	public abstract void update();
	
	/**
	 * Close.
	 */
	public abstract void close(); 

	/**
	 * get buffer for a output, this method respect the mapping.
	 *
	 * @param screenNr the screen nr
	 * @return the buffer for screen
	 */
	public int[] getBufferForScreen(int screenNr) {
		return this.currentBufferMap.get(screenNr);
	}
	
	// fill the the preparedBufferMap instance with int[] buffers for all screens
	public void prepare() {
		for (int screen = 0; screen < this.collector.getNrOfScreens(); screen++) {
			LayoutModel lm = this.layout.getDataForScreen(screen);
			OutputMapping map = this.collector.getOutputMappings(screen);
			int[] buffer;
			if (lm.screenDoesNotNeedStretching()) {
				Visual v = this.collector.getVisual(lm.getFxInput());
				buffer = this.matrixData.getScreenBufferForDevice(v, map);
			} else {
				Visual v = this.collector.getVisual(lm.getFxInput());
				buffer = this.matrixData.getScreenBufferForDevice(v, lm, map);
			}
			this.preparedBufferMap.put(Integer.valueOf(screen), buffer);
		}
	}

	// switch currentBufferMap <-> preparedBufferMap instances
	public void switchBuffers() {
		Map<Integer, int[]> currentMap = this.currentBufferMap;
		this.currentBufferMap = this.preparedBufferMap;
		this.preparedBufferMap = currentMap;
		this.preparedBufferMap.clear();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return name;
	}

	/**
	 * debug output if possible
	 */
	public void logStatistics() {
		
	}

	/**
	 * 
	 * @return bpp (bit per pixel)
	 */
	public int getBpp() {
		return bpp;
	}
}
