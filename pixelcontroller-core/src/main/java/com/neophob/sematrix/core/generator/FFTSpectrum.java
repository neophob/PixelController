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
package com.neophob.sematrix.core.generator;

import com.neophob.sematrix.core.glue.MatrixData;
import com.neophob.sematrix.core.resize.Resize.ResizeName;
import com.neophob.sematrix.core.sound.ISound;


/**
 * idea ripped from http://www.macetech.com/blog/
 * 
 * @author mvogt
 * 
 */
public class FFTSpectrum extends Generator {

	/** The sound. */
	private ISound sound;
	
	/** The fft smooth. */
	private float[] fftSmooth;
	
	/** The y block. */
	private int yBlock;
	
	/**
	 * Instantiates a new fFT spectrum.
	 *
	 * @param controller the controller
	 */
	public FFTSpectrum(MatrixData matrix, ISound sound) {
		super(matrix, GeneratorName.FFT, ResizeName.PIXEL_RESIZE);
		this.sound = sound;
		
		int bands = sound.getFftAvg();
		fftSmooth = new float[bands];
		yBlock = this.internalBufferYSize / bands;
	}


	/* (non-Javadoc)
	 * @see com.neophob.sematrix.core.generator.Generator#update()
	 */
	@Override
	public void update() {
		int avg = sound.getFftAvg();
		
		for (int i = 0; i < avg; i++) {
			
			fftSmooth[i] = 0.3f * fftSmooth[i] + 0.7f * sound.getFftAvg(i);			
		    int h = (int)(Math.log(fftSmooth[i]*3.0f)*30);

		    h=255+h;
		    if (h>255) {
		    	h=255;
		    }
		    h = h*h/255;
		    rect(h, 0, i*yBlock, this.internalBufferXSize, i*yBlock+yBlock);
		}		
	}
	
	/**
	 * Rect.
	 *
	 * @param col the col
	 * @param x1 the x1
	 * @param y1 the y1
	 * @param x2 the x2
	 * @param y2 the y2
	 */
	private void rect(int col, int x1, int y1, int x2, int y2) {
		int ofs;
		for (int y=y1; y<y2; y++) {
			ofs = y*this.internalBufferXSize;
			for (int x=x1; x<x2; x++) {		
				this.internalBuffer[ofs++] = col;
			}
		}
	}

	
}
