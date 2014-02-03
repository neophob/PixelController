/**
 * Copyright (C) 2011-2014 Michael Vogt <michu@neophob.com>
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
package com.neophob.sematrix.core.visual.mixer;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.neophob.sematrix.core.resize.Resize.ResizeName;
import com.neophob.sematrix.core.visual.MatrixData;
import com.neophob.sematrix.core.visual.Visual;
import com.neophob.sematrix.core.visual.generator.Generator;

/**
 * checkbox mixer.
 *
 * @author mvogt
 */
public class Checkbox extends Mixer {

	private static final Logger LOG = Logger.getLogger(Checkbox.class.getName());
	
	/** The pixels per line. */
	private int flpX = -1;
	private int flpY = -1;

	private int checkBoxSizeX;
	private int checkBoxSizeY;

	/**
	 * Instantiates a new checkbox.
	 *
	 * @param controller the controller
	 */
	public Checkbox(MatrixData matrix) {
		super(MixerName.CHECKBOX, ResizeName.PIXEL_RESIZE);
		checkBoxSizeX = matrix.getDeviceXSize();
		checkBoxSizeY = matrix.getDeviceYSize();
	}

	/* (non-Javadoc)
	 * @see com.neophob.sematrix.core.mixer.Mixer#getBuffer(com.neophob.sematrix.core.glue.Visual)
	 */
	public int[] getBuffer(Visual visual) {

		if (visual.getEffect2() == null) {
			return visual.getEffect1Buffer();
		}

		Generator gen1 = visual.getGenerator1();

		//lazy init
		if (flpX == -1) {
			this.flpX = gen1.getInternalBufferXSize()/checkBoxSizeX;	        
			//this.flpY = gen1.getInternalBufferYSize()/checkBoxSizeY;
			this.flpY = gen1.getInternalBufferXSize()*gen1.getInternalBufferYSize()/checkBoxSizeY;
			LOG.log(Level.FINE, "Checkbox Mixer lazy init, flpx: {0}, flpY: {1}", new Integer[] {flpX, flpY});
		}

		int[] src1 = visual.getEffect1Buffer();
		int[] src2 = visual.getEffect2Buffer();
		int[] dst = new int [src1.length];		

		boolean flip=true;
		
		boolean flipY=true;

	/* complicated but working implementation
	 * 
	 * for (int y=0; y<checkBoxSizeY; y++) {			
			flip=!flip;
			for (int m=0; m<flpY; m++) {						
				drawHorizontalLine(flip, src1, src2, dst);	
			}

		}*/

		for (int i=0; i<src1.length; i++) {
			if (i%flpX==0) {				
				flip=!flip;
			}
			if (i%flpY==0) {
				flipY=!flipY;
			}
			
			//reset flip state on the beginning of a line
			if (i%gen1.getInternalBufferXSize()==0) {
				flip=flipY;
			}

			try {
				if (flip) {
					dst[i] = src2[i];
				} else {
					dst[i] = src1[i];
				}				
			} catch (Exception e) {
				LOG.log(Level.WARNING, "Checkbox Mixer error detected!", e);
				LOG.log(Level.WARNING, "Details: i: {0}, dst: {1}, src1: {2}, src2: {3}",
						new String[] {i+"", dst.length+"", src1.length+"", src2.length+""});
			}

		}	
		return dst;
	}

	/**
	 * draws a horizontal line, checkbox pattern, checkbox width is checkBoxSizeX 
	 * @param src1
	 * @param src2
	 * @param dst
	 */
/*	private void drawHorizontalLine(boolean flip, int[] src1, int[] src2, int[] dst) {
		boolean flipTmp = flip;
		for (int x=0; x<checkBoxSizeX; x++) {

			for (int n=0; n<flpX; n++) {

				if (flipTmp) {
					dst[i] = src2[i];
				} else {
					dst[i] = src1[i];
				}
				i++;					
			}

			flipTmp=!flipTmp;
		}		
	}*/

}
