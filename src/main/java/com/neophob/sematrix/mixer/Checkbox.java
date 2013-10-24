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
package com.neophob.sematrix.mixer;

import com.neophob.sematrix.generator.Generator;
import com.neophob.sematrix.glue.Collector;
import com.neophob.sematrix.glue.Visual;
import com.neophob.sematrix.resize.Resize.ResizeName;

/**
 * checkbox mixer.
 *
 * @author mvogt
 */
public class Checkbox extends Mixer {
    
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
	public Checkbox(PixelControllerMixer controller) {
		super(controller, MixerName.CHECKBOX, ResizeName.PIXEL_RESIZE);
		checkBoxSizeX = Collector.getInstance().getMatrix().getDeviceXSize();
		checkBoxSizeY = Collector.getInstance().getMatrix().getDeviceYSize();
	}

	/* (non-Javadoc)
	 * @see com.neophob.sematrix.mixer.Mixer#getBuffer(com.neophob.sematrix.glue.Visual)
	 */
	public int[] getBuffer(Visual visual) {
		
		if (visual.getEffect2() == null) {
			return visual.getEffect1Buffer();
		}

		Generator gen1 = visual.getGenerator1();
		
		//lazy init
		if (flpX == -1) {
	        this.flpX= gen1.getInternalBufferXSize()/checkBoxSizeX;	        
	        this.flpY = gen1.getInternalBufferXSize()*gen1.getInternalBufferYSize()/checkBoxSizeY;	
		}
		
		int[] src1 = visual.getEffect1Buffer();
		int[] src2 = visual.getEffect2Buffer();
		int[] dst = new int [gen1.internalBuffer.length];
		
		boolean flip=true;
		for (int i=0; i<src1.length; i++) {
			if (i%flpX==0) {
				flip=!flip;
			}
            if (i%flpY==0) {
                flip=!flip;
            }
			
			if (flip) {
				dst[i] = src2[i];
			} else {
				dst[i] = src1[i];
			}
			
		}

		return dst;
	}

}
