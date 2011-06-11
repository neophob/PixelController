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

public class PixelControllerOutput implements PixelControllerElement {

	private List<Output> allOutputs;
	
	public PixelControllerOutput() {
		allOutputs = new CopyOnWriteArrayList<Output>();
	}
	
	/**
	 * 
	 */
	public void initAll() {

	}
	
	/**
	 * 
	 */
	public List<String> getCurrentState() {
		List<String> ret = new ArrayList<String>();
		
		return ret;
	}

	@Override
	public void update() {
		for (Output o: allOutputs) {
			o.update();
		}
	}




	/*
	 * OUTPUT ======================================================
	 */

	public List<Output> getAllOutputs() {
		return allOutputs;
	}

	public void addOutput(Output output) {
		allOutputs.add(output);
	}


}
