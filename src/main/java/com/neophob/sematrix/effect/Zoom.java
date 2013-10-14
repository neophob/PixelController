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
package com.neophob.sematrix.effect;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.neophob.sematrix.resize.Resize.ResizeName;


/**
 * The Class RotoZoom.
 *
 * @author michu
 * 
 * ripped from http://www.openprocessing.org/visuals/?visualID=8030
 */
public class Zoom extends RotoZoomEffect {

	private static final Logger LOG = Logger.getLogger(Zoom.class.getName());
	
	/**
	 * 
	 * @author mvogt
	 *
	 */
	public enum ZoomMode {
		ZOOM_IN,
		ZOOM_OUT,
		HORIZONTAL,
		VERTICAL,
	}

	private ZoomMode zoomMode = ZoomMode.ZOOM_IN; 
			
	/**
	 * Instantiates a new roto zoom.
	 *
	 * @param controller the controller
	 * @param scale the scale
	 * @param angle the angle
	 */
	public Zoom(PixelControllerEffect controller) {
		super(controller, EffectName.ZOOM, ResizeName.QUALITY_RESIZE);
	}


	/* (non-Javadoc)
	 * @see com.neophob.sematrix.effect.Effect#getBuffer(int[])
	 */
	public int[] getBuffer(int[] buffer) {		
		switch (zoomMode) {
			case ZOOM_IN:
				return zoom(2f, 2f, buffer);
				
			case ZOOM_OUT:
				return zoom(0.5f, 0.5f, buffer);
				
			case HORIZONTAL:
				return zoom(1f, 2f, buffer);
				
			case VERTICAL:
				return zoom(2f, 1f, buffer);
				
			default:
				return buffer;	
		}					
	}


	public void setZoomMode(int mode) {
		try {
			this.zoomMode = ZoomMode.values()[mode-1];	
		} catch (Exception e) {
			LOG.log(Level.WARNING, "Failed to set zoom level, use default.", e);
		}
	}
	
	public int getZoomMode() {
		return zoomMode.ordinal()+1;
	}
}
