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
	
	private OutputDeviceEnum outputDeviceEnum;
	
	/** The log. */
	private static final Logger LOG = Logger.getLogger(Output.class.getName());
	
	/** The matrix data. */
	protected MatrixData matrixData;
	
	/** The layout. */
	protected Layout layout;
	
	/** *bit per pixel */
	protected int bpp;
	
	/**
	 * Instantiates a new output.
	 *
	 * @param controller the controller
	 * @param name the name
	 */
	public Output(OutputDeviceEnum outputDeviceEnum, PropertiesHelper ph, PixelControllerOutput controller, int bpp) {
		this.outputDeviceEnum = outputDeviceEnum;
				
		this.matrixData = Collector.getInstance().getMatrix();
		this.layout = ph.getLayout();
		this.bpp = bpp;
		
		LOG.log(Level.INFO, "Output created: {0}, Layout: {1}, BPP: {2}"
				, new Object[] { this.outputDeviceEnum, layout.getLayoutName(), this.bpp });
		
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
		Collector c = Collector.getInstance();
		LayoutModel lm = layout.getDataForScreen(screenNr);
		OutputMapping map = c.getOutputMappings(screenNr);
		
		if (lm.screenDoesNotNeedStretching()) {
			Visual v = c.getVisual(lm.getFxInput());
			return matrixData.getScreenBufferForDevice(v, map);
		} else {
			Visual v = c.getVisual(lm.getFxInput());
			return matrixData.getScreenBufferForDevice(v, lm, map);
		}
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return this.outputDeviceEnum.name();
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

	public OutputDeviceEnum getOutputDeviceEnum() {
		return this.outputDeviceEnum;
	}
}
