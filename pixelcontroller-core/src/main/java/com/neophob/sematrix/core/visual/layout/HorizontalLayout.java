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
package com.neophob.sematrix.core.visual.layout;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.neophob.sematrix.core.visual.OutputMapping;

/**
 * Horizontal Layout, features:
 * -"unlimited" width
 * -exact one panels height.
 *
 * @author michu
 */
public class HorizontalLayout extends Layout {

	private static transient final Logger LOG = Logger.getLogger(HorizontalLayout.class.getName());
	
	/**
	 * Instantiates a new horizontal layout.
	 *
	 * @param row1Size the row1 size
	 * @param row2Size the row2 size
	 */
	public HorizontalLayout(int row1Size) {
		super(LayoutName.HORIZONTAL, row1Size, 0);
		
		LOG.log(Level.INFO,	"HorizontalLayout created, size row1: {0}, row2:{1}", new Object[] {row1Size, row2Size});

	}

	/**
	 * How many screens share this fx on the x axis.
	 *
	 * @param fxInput the fx input
	 * @return the int
	 */
	private int howManyScreensShareThisFxOnTheXAxis(int fxInput, List<OutputMapping> ioMapping) {
		int ret=0;
		for (OutputMapping o: ioMapping) {
			if (o.getVisualId()==fxInput) {
				ret++;
			}
		}
		return ret;			
	}
	
	/**
	 * check which offset position the fx at this screen is.
	 *
	 * @param fxInput the fx input
	 * @param screenNr the screen nr
	 * @return the x offset for screen
	 */
	private int getXOffsetForScreen(int fxInput, int screenNr, List<OutputMapping> ioMapping) {
		int ret=0;

		for (int i=0; i<screenNr; i++) {
			if (ioMapping.get(i).getVisualId()==fxInput) {
				ret++;
			}
		}

		return ret;
	}
	

	/* (non-Javadoc)
	 * @see com.neophob.sematrix.core.layout.Layout#getDataForScreen(int)
	 */
	public LayoutModel getDataForScreen(int screenNr, List<OutputMapping> ioMapping) {
		int fxInput = ioMapping.get(screenNr).getVisualId();

		return new LayoutModel(
				this.howManyScreensShareThisFxOnTheXAxis(fxInput, ioMapping), 
				1,
				this.getXOffsetForScreen(fxInput, screenNr, ioMapping),
				0,
				fxInput);
	}

}
