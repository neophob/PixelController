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
 * parent output class
 * 
 * 
 * @author michu
 *
 */
public abstract class Output {

	private static Logger log = Logger.getLogger(Output.class.getName());

	private String name;
	
	protected MatrixData matrixData;
	
	protected Layout layout;
	
	/**
	 * 
	 * @param name
	 */
	public Output(PixelControllerOutput controller, String name) {
		this.name = name;
				
		this.matrixData = Collector.getInstance().getMatrix();
		this.layout = PropertiesHelper.getInstance().getLayout();

		log.log(Level.INFO,
				"Output created: {0}, Layout: {1}"
				, new Object[] { this.name, layout.getLayoutName() });
	
		//add to list
		controller.addOutput(this);
	}
	
	public abstract void update();
	
	public abstract void close(); 

	/**
	 * get buffer for a output, this method respect the mapping
	 * 
	 * @param screenNr
	 * @return
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
	
	public String toString() {
		return name;
	}

}
