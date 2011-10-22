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

	/** The outputDeviceEnum. */
	private OutputDeviceEnum outputDeviceEnum;
	
	/** The matrix data. */
	protected MatrixData matrixData;
	
	/** The layout. */
	protected Layout layout;
	
	/** The collector. */
	protected Collector collector;
	
	/** bit per pixel. */
	protected int bpp;
	
	private int totalNrOfOutputBuffers;
	private int switchBuffer;
	
	/** 
	 * this map contains twice as muchen entries as outputs exists
	 * for each output device two buffers exists, one to display and
	 * one to work with 
	 */
	private Map<Integer, int[]> bufferMap;

	/**
	 * Instantiates a new output.
	 *
	 * @param outputDeviceEnum the output device enum
	 * @param ph the ph
	 * @param controller the controller
	 * @param bpp the bpp
	 */
	public Output(OutputDeviceEnum outputDeviceEnum, PropertiesHelper ph, PixelControllerOutput controller, int bpp) {
		this.outputDeviceEnum = outputDeviceEnum;
		
		this.collector = Collector.getInstance();
		this.matrixData = this.collector.getMatrix();
		this.layout = ph.getLayout();
		this.bpp = bpp;
		
		this.bufferMap = new HashMap<Integer, int[]>();		
		this.totalNrOfOutputBuffers = this.collector.getNrOfScreens();
		this.switchBuffer=0;
		
		//add to list
		controller.addOutput(this);

		LOG.log(Level.INFO, "Output created: {0}, Layout: {1}, BPP: {2}"
				, new Object[] { this.outputDeviceEnum, layout.getLayoutName(), this.bpp });	
	}
	
	/**
	 * Update the output device
	 */
	public abstract void update();
	
	/**
	 * Close to output device
	 */
	public abstract void close(); 

	/**
	 * get buffer for a output, this method respect the mapping.
	 *
	 * @param screenNr the screen nr
	 * @return the buffer for screen
	 */
	public int[] getBufferForScreen(int screenNr) {
		return this.bufferMap.get(switchBuffer+screenNr);
	}
	

	/**
	 * fill the the preparedBufferMap instance with int[] buffers for all screens.
	 */
	public void prepareOutputBuffer() {
		int[] buffer;
		Visual v;
		
		for (int screen = 0; screen < this.collector.getNrOfScreens(); screen++) {
			LayoutModel lm = this.layout.getDataForScreen(screen);
			OutputMapping map = this.collector.getOutputMappings(screen);
			
			if (lm.screenDoesNotNeedStretching()) {
				v = this.collector.getVisual(lm.getFxInput());
				buffer = this.matrixData.getScreenBufferForDevice(v, map);
			} else {
				v = this.collector.getVisual(lm.getFxInput());
				buffer = this.matrixData.getScreenBufferForDevice(v, lm, map, this);
			}
			
			this.bufferMap.put(screen+switchBuffer, buffer);
		}				
	}

	/**
	 * switch currentBufferMap <-> preparedBufferMap instances
	 */
	public void switchBuffers() {
		if (switchBuffer==0) {
			switchBuffer = totalNrOfOutputBuffers;
		} else {
			switchBuffer = 0;
		}
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return this.outputDeviceEnum.toString();
	}

	/**
	 * debug output if possible.
	 */
	public void logStatistics() {
		//nothing to do per default
	}

	/**
	 * Gets the bpp.
	 *
	 * @return bpp (bit per pixel)
	 */
	public int getBpp() {
		return bpp;
	}
	
	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	public OutputDeviceEnum getType() {
		return this.outputDeviceEnum;
	}
}
