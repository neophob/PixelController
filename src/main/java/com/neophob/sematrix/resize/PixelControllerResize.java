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
import java.util.logging.Level;
import java.util.logging.Logger;

import com.neophob.sematrix.glue.PixelControllerElement;
import com.neophob.sematrix.resize.Resize.ResizeName;

/**
 * 
 * collector class which holds a reference to different resizer.
 * 
 * @author michu
 *
 */
public class PixelControllerResize implements PixelControllerElement {

	/** The Constant LOG. */
	private static final Logger LOG = Logger.getLogger(PixelControllerResize.class.getName());
	
	/** The all resizers. */
	private List<Resize> allResizers;
	
	/**
	 * Instantiates a new pixel controller resize.
	 */
	public PixelControllerResize() {
		allResizers = new CopyOnWriteArrayList<Resize>();
	}
	
	/* (non-Javadoc)
	 * @see com.neophob.sematrix.glue.PixelControllerElement#update()
	 */
	@Override
	public void update() {
	}
	
	/**
	 * initialize all effects.
	 */
	@Override
	public void initAll() {
		new PixelResize(this);
		new QualityResize(this);
	}
	
	/* (non-Javadoc)
	 * @see com.neophob.sematrix.glue.PixelControllerElement#getCurrentState()
	 */
	@Override
	public List<String> getCurrentState() {
		List<String> ret = new ArrayList<String>();
				
		return ret;
	}
	
	/**
	 * Resize image.
	 *
	 * @param resizeTyp the resize typ
	 * @param inputBuffer the input buffer
	 * @param currentX the current x
	 * @param currentY the current y
	 * @param newX the new x
	 * @param newY the new y
	 * @return the int[]
	 */
	public int[] resizeImage(ResizeName resizeTyp, int[] inputBuffer, int currentX, int currentY, int newX, int newY) {
		
		Resize r=null;
		r = getResize(resizeTyp);

		if (r==null) {
			LOG.log(Level.WARNING, "invalid resize typ selected: "+resizeTyp);
			return null;
		}
		
		return r.getBuffer(inputBuffer, newX, newY, currentX, currentY);		
	}

	/*
	 * RESIZER ======================================================
	 */
	
	/**
	 * Gets the all resizers.
	 *
	 * @return the all resizers
	 */
	public List<Resize> getAllResizers() {
		return allResizers;
	}
	
	/**
	 * Gets the resize.
	 *
	 * @param name the name
	 * @return the resize
	 */
	public Resize getResize(ResizeName name) {
		for (Resize r: allResizers) {
			if (r.getId() == name.getId()) {
				return r;
			}
		}
		return null;
	}
	
	/**
	 * Adds the resize.
	 *
	 * @param resize the resize
	 */
	public void addResize(Resize resize) {
		allResizers.add(resize);
	}


}
