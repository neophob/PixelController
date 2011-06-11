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

package com.neophob.sematrix.resize;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.neophob.sematrix.glue.PixelControllerElement;
import com.neophob.sematrix.resize.Resize.ResizeName;

public class PixelControllerResize implements PixelControllerElement {

	private List<Resize> allResizers;
	
	/**
	 * 
	 */
	public PixelControllerResize() {
		allResizers = new CopyOnWriteArrayList<Resize>();
	}
	
	@Override
	public void update() {
	}
	
	/**
	 * initialize all effects
	 */
	@Override
	public void initAll() {
		//create effects
		//create resizer
		new PixelResize(this);
		new QualityResize(this);
	}
	
	/**
	 * 
	 */
	@Override
	public List<String> getCurrentState() {
		List<String> ret = new ArrayList<String>();
				
		return ret;
	}

	/*
	 * RESIZER ======================================================
	 */
	
	public List<Resize> getAllResizers() {
		return allResizers;
	}
	
	public Resize getResize(ResizeName name) {
		for (Resize r: allResizers) {
			if (r.getId() == name.getId()) {
				return r;
			}
		}
		return null;
	}
	
	public void addResize(Resize resize) {
		allResizers.add(resize);
	}


}
