package com.neophob.sematrix.generator;

import processing.core.PConstants;

import com.neophob.sematrix.input.Sound;

/**
 * @author mvogt
 * TODO: select color, sensivity
 * 
 *
 */
public class VolumeDisplay extends Generator implements PConstants {

	private int lastColor, color;
	private float volume;
	private int offset;
	
	public VolumeDisplay(int offset) {
		super(GeneratorName.VOLUMEDISPLAY);
		this.lastColor = 0;
		this.offset = offset;
		if (this.offset>255) {
			this.offset = 255;
		}
	}
	
	@Override
	public void update() {
		volume = Sound.getInstance().getVolume();
		color = (int)(volume*(255-offset));
		if (color==0) {
			color = lastColor/2;
		}
		lastColor = color;
		color += offset;
		color = (int)(color << 16) | (color << 8) | (color);
		for (int x = 0; x < this.getInternalBufferYSize()*this.getInternalBufferXSize(); x++) {
			this.internalBuffer[x] = color;
			
		}		
		System.arraycopy(internalBuffer, 0, this.internalBuffer, 0, internalBuffer.length);
	}

	@Override
	public void close() {
		Sound.getInstance().shutdown();
	}
}
