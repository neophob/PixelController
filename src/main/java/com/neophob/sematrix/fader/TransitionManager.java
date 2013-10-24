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
package com.neophob.sematrix.fader;

import com.neophob.sematrix.glue.Collector;
import com.neophob.sematrix.glue.OutputMapping;

/**
 * the transition manager handle smooth transition for the output visuals
 * 
 * @author michu
 *
 */
public class TransitionManager {

	private int[][] savedVisuals;
	private Collector col;
	
	/**
	 * save current visual output, used for preset fading
	 * 
	 * @param col
	 */
	public TransitionManager(Collector col) {
		this.col = col;
		savedVisuals = new int[col.getAllVisuals().size()][];
		int i = 0;
		for (OutputMapping om: col.getAllOutputMappings()) {							
			savedVisuals[i++] = col.getVisual(om.getVisualId()).getBuffer();
		}		
	}
	
	/**
	 * start crossfading
	 * 
	 * @param col
	 */
	public void startCrossfader() {
		int i=0;
		for (OutputMapping om: col.getAllOutputMappings()) {				
			om.setFader(col.getPixelControllerFader().getPresetFader(1));
			om.getFader().startFade(om.getVisualId(), savedVisuals[i++]);
		}		
	}
	
}
