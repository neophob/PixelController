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

package com.neophob.sematrix.layout;

import com.neophob.sematrix.glue.Collector;
import com.neophob.sematrix.glue.OutputMapping;

public class HorizontalLayout extends Layout {

	public HorizontalLayout(int row1Size, int row2Size) {
		super(LayoutName.HORIZONTAL, row1Size, row2Size);
	}

	/**
	 * 
	 * @param fxInput
	 * @return
	 */
	private int howManyScreensShareThisFxOnTheXAxis(int fxInput) {
		int ret=0;
		for (OutputMapping o: Collector.getInstance().getAllOutputMappings()) {
			if (o.getVisualId()==fxInput) {
				ret++;
			}
		}
		return ret;			
	}
	
	/**
	 * check which offset position the fx at this screen is
	 * @param screenOutput
	 * @return
	 */
	private int getXOffsetForScreen(int fxInput, int screenNr) {
		int ret=0;

		for (int i=0; i<screenNr; i++) {
			if (Collector.getInstance().getOutputMappings(i).getVisualId()==fxInput) {
				ret++;
			}
		}

		return ret;
	}
	

	/**
	 * 
	 */
	public LayoutModel getDataForScreen(int screenNr) {
		int fxInput = Collector.getInstance().getOutputMappings(screenNr).getVisualId();

		return new LayoutModel(
				this.howManyScreensShareThisFxOnTheXAxis(fxInput), 
				1,
				this.getXOffsetForScreen(fxInput, screenNr),
				0,
				fxInput);
	}

}
