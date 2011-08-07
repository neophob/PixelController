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

public class BoxLayout extends Layout {

	private int ioMappingSize;

	/**
	 * 
	 * @param row1Size
	 * @param row2Size
	 */
	public BoxLayout(int row1Size, int row2Size) {
		super(LayoutName.BOX, row1Size, row2Size);
		ioMappingSize = Collector.getInstance().getAllOutputMappings().size();
	}


	/**
	 * 
	 * @param fxInput
	 * @return
	 */
	private int howManyScreensShareThisFxOnTheXAxis(int fxInput) {
		int max=0;
		int min=10;
		OutputMapping o;

		//we only have 2 rows
		int xsize=ioMappingSize/2;
		for (int y=0; y<2; y++) {	
			for (int x=0; x<xsize; x++) {
				o = Collector.getInstance().getOutputMappings(xsize*y+x);
				if (o.getVisualId()==fxInput) {
					if (x<min) {
						min=x;
					}
					//save the maximal x position
					//if there are multiple fx'es, store the max position
					if (x+1>max) {
						max=x+1;
					}
				}
			}
		}
		return max-min;
	}

	/**
	 * 
	 * @param fxInput
	 * @return
	 */
	private int howManyScreensShareThisFxOnTheYAxis(int fxInput) {
		int max=0;
		int min=10;
		OutputMapping o;

		//we only have 2 rows
		int xsize=ioMappingSize/2;
		for (int x=0; x<xsize; x++) {
			for (int y=0; y<2; y++) {
				o = Collector.getInstance().getOutputMappings(xsize*y+x);

				if (o.getVisualId()==fxInput) {
					if (y<min) {
						min=y;
					}
					//save the maximal x position
					//if there are multiple fx'es, store the max position
					if (y+1>max) {
						max=y+1;
					}
				}
			}
		}
		return max-min;
	}


	/**
	 * return y offset of screen position
	 * (0=first row, 1=second row...)
	 * 
	 */
	private int getXOffsetForScreen(int screenNr, int fxOnHowMayScreens) {
		if (fxOnHowMayScreens==1 || screenNr==0) {
			return 0;
		}

		if (screenNr>=ioMappingSize/2) {
			screenNr-=ioMappingSize/2;
		}

		return screenNr;
	}

	/**
	 * return y offset of screen position if a visual is spread
	 * acros MULTIPLE outputs.
	 * 
	 * return 0 if the visuial is only shown on one screen 
	 * 
	 * (0=first row, 1=second row...)
	 * 
	 */
	private int getYOffsetForScreen(int screenNr, int fxOnHowMayScreens) {
		if (fxOnHowMayScreens==1 || screenNr==0) {
			return 0;
		}

		if (screenNr>=ioMappingSize/2) {
			//System.out.println(fxOnHowMayScreens);
			return 1;
		}

		return 0;
	}

	/**
	 * 
	 */
	public LayoutModel getDataForScreen(int screenNr) {
		int fxInput = Collector.getInstance().getOutputMappings(screenNr).getVisualId();

		int fxOnHowMayScreensX=this.howManyScreensShareThisFxOnTheXAxis(fxInput);
		int fxOnHowMayScreensY=this.howManyScreensShareThisFxOnTheYAxis(fxInput);
/*		System.out.println(screenNr+" howman: "+fxOnHowMayScreensX+", "+fxOnHowMayScreensY+
				", posX"+this.getXOffsetForScreen(screenNr, fxOnHowMayScreensX)+
				", posY"+this.getYOffsetForScreen(screenNr, fxOnHowMayScreensY)
		);
*/
		return new LayoutModel(
				fxOnHowMayScreensX, 
				fxOnHowMayScreensY,
				this.getXOffsetForScreen(screenNr, fxOnHowMayScreensX),
				this.getYOffsetForScreen(screenNr, fxOnHowMayScreensY),
				fxInput);
	}

}
