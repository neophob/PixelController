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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.neophob.sematrix.glue.PixelControllerElement;

/**
 * The Class PixelControllerOutput.
 */
public class PixelControllerOutput implements PixelControllerElement {

	/** The all outputs. */
	private List<Output> allOutputs;
	
	/**
	 * Instantiates a new pixel controller output.
	 */
	public PixelControllerOutput() {
		allOutputs = new CopyOnWriteArrayList<Output>();
	}
	
	/* (non-Javadoc)
	 * @see com.neophob.sematrix.glue.PixelControllerElement#initAll()
	 */
	public void initAll() {
		//nothing to init here
	}
	
	/* (non-Javadoc)
	 * @see com.neophob.sematrix.glue.PixelControllerElement#getCurrentState()
	 */
	public List<String> getCurrentState() {
		//no status to store
		return new ArrayList<String>();
	}

	/* (non-Javadoc)
	 * @see com.neophob.sematrix.glue.PixelControllerElement#update()
	 */
	@Override
	public void update() {
		for (Output o: allOutputs) {
			o.update();
		}
	}




	/*
	 * OUTPUT ======================================================
	 */

	/**
	 * Gets the all outputs.
	 *
	 * @return the all outputs
	 */
	public List<Output> getAllOutputs() {
		return allOutputs;
	}

	/**
	 * Adds the output.
	 *
	 * @param output the output
	 */
	public void addOutput(Output output) {
		allOutputs.add(output);
	}


}
