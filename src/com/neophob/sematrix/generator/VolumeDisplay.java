package com.neophob.sematrix.generator;

import com.neophob.sematrix.input.Sound;



/**
 * @author mvogt
 * TODO: synchronize sound buffer size with fps and steps size 
 * 
 */
public class VolumeDisplay extends Generator {

	private int steps;
	private int col;

	public VolumeDisplay() {
		super(GeneratorName.VOLUMEDISPLAY);
		steps = 8;
		col = (255 << 16) | (255 << 8) | (255);
	}

	@Override
	public void update() {
		//split up in 5 pieces
		float sequence[] = Sound.getInstance().getVolume(steps);
		int ysize = internalBufferYSize/steps;
		int ofs=0;
		for (int i=0; i<steps; i++) {
			for (int y=0; y<ysize; y++) {
				int xsize = (int)(internalBufferXSize*sequence[i]);
				for (int x=0; x<xsize; x++) {
					internalBuffer[ofs+y*internalBufferXSize+x] = col;
				}					
				for (int x=xsize; x<internalBufferXSize; x++) {					
					internalBuffer[ofs+y*internalBufferXSize+x] = 0;						
				}					
			}
			ofs+=internalBufferXSize*ysize;
		}
	}

	@Override
	public void close() {	}


}
