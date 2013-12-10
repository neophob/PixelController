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
package com.neophob.sematrix.core.visual;

import java.util.List;

import com.neophob.sematrix.core.resize.Resize.ResizeName;
import com.neophob.sematrix.core.visual.color.ColorSet;
import com.neophob.sematrix.core.visual.effect.Effect;
import com.neophob.sematrix.core.visual.generator.Generator;
import com.neophob.sematrix.core.visual.mixer.Mixer;

/**
 * this model holds 2 generators, 2 effects and a mixer instance.
 *
 * @author mvogt
 */
public class Visual {

	/** The generator1. */
	private Generator generator1;
	
	/** The generator2. */
	private Generator generator2;
	
	/** The effect1. */
	private Effect effect1;
	
	/** The effect2. */
	private Effect effect2;
	
	/** The mixer. */
	private Mixer mixer;
	
	private ColorSet colorSet;
	
	/**
	 * 
	 * @param generator1
	 * @param generator2
	 * @param effect1
	 * @param effect2
	 * @param mixer
	 * @param colorSet
	 */
	public Visual(Generator generator1, Generator generator2, Effect effect1,
			Effect effect2, Mixer mixer, ColorSet colorSet) {
		this.generator1 = generator1;
		this.generator2 = generator2;
		this.effect1 = effect1;
		this.effect2 = effect2;
		this.mixer = mixer;
		this.colorSet = colorSet;
	}

	/**
	 * 
	 * @param g
	 * @param e
	 * @param m
	 * @param c
	 */
	public Visual(Generator g, Effect e, Mixer m, ColorSet c) {
		this.generator1 = g;
		this.generator2 = g;		
		this.effect1 = e;
		this.effect2 = e;
		this.mixer = m;
		this.colorSet = c;
	}
		
	/**
	 * Gets the buffer.
	 *
	 * @return the buffer
	 */
	public int[] getBuffer() {
		return this.getMixerBuffer();
	}

	/**
	 * Checks if is visual on screen.
	 *
	 * @param screenNr the screen nr
	 * @return true, if is visual on screen
	 */
	public boolean isVisualOnScreen(int screenNr) {
		int fxInput = VisualState.getInstance().getFxInputForScreen(screenNr);
		if (fxInput == getGenerator1Idx()) {
			return true;
		}
		return false;
	}

	//check the resize option to return
	/**
	 * Gets the resize option.
	 *
	 * @return the resize option
	 */
	public ResizeName getResizeOption() {
		if (this.generator1.getResizeOption() == ResizeName.PIXEL_RESIZE || this.generator2.getResizeOption() == ResizeName.PIXEL_RESIZE ||
				this.effect1.getResizeOption() == ResizeName.PIXEL_RESIZE || this.effect2.getResizeOption() == ResizeName.PIXEL_RESIZE ||
				this.mixer.getResizeOption() == ResizeName.PIXEL_RESIZE) {
			return ResizeName.PIXEL_RESIZE;
		}
		
		return ResizeName.QUALITY_RESIZE;
	}
	
	/**
	 * Gets the generator1.
	 *
	 * @return the generator1
	 */
	public Generator getGenerator1() {
		return generator1;
	}

	/**
	 * Gets the generator1 idx.
	 *
	 * @return the generator1 idx
	 */
	public int getGenerator1Idx() {
		return generator1.getId();
	}

	/**
	 * Sets the generator1.
	 *
	 * @param generator1 the new generator1
	 */
	public void setGenerator1(Generator generator1) {
		this.generator1 = generator1;
	}

	/**
	 * Sets the generator1.
	 *
	 * @param index the new generator1
	 */
	public void setGenerator1(int index) {
		Generator g = VisualState.getInstance().getPixelControllerGenerator().getGenerator(index);
		if (g!=null) {
			this.generator1 = g;			
		}
	}

	/**
	 * Gets the generator2.
	 *
	 * @return the generator2
	 */
	public Generator getGenerator2() {
		return generator2;
	}

	/**
	 * Gets the generator2 idx.
	 *
	 * @return the generator2 idx
	 */
	public int getGenerator2Idx() {
		return generator2.getId();
	}

	/**
	 * Sets the generator2.
	 *
	 * @param generator2 the new generator2
	 */
	public void setGenerator2(Generator generator2) {
		this.generator2 = generator2;
	}

	/**
	 * Sets the generator2.
	 *
	 * @param index the new generator2
	 */
	public void setGenerator2(int index) {
		Generator g = VisualState.getInstance().getPixelControllerGenerator().getGenerator(index);
		if (g!=null) {
			this.generator2 = g;
		}
	}

	/**
	 * Gets the effect1.
	 *
	 * @return the effect1
	 */
	public Effect getEffect1() {
		return effect1;
	}

	/**
	 * Gets the effect1 idx.
	 *
	 * @return the effect1 idx
	 */
	public int getEffect1Idx() {
		return effect1.getId();
	}

	/**
	 * Gets the effect1 buffer.
	 *
	 * @return the effect1 buffer
	 */
	public int[] getEffect1Buffer() {
		return effect1.getBuffer(generator1.getBuffer());
	}

	/**
	 * Sets the effect1.
	 *
	 * @param effect1 the new effect1
	 */
	public void setEffect1(Effect effect1) {
		this.effect1 = effect1;
	}

	/**
	 * Sets the effect1.
	 *
	 * @param index the new effect1
	 */
	public void setEffect1(int index) {
		Effect e = VisualState.getInstance().getPixelControllerEffect().getEffect(index);
		if (e!=null) {
			this.effect1 = e;			
		}
	}

	/**
	 * Gets the effect2.
	 *
	 * @return the effect2
	 */
	public Effect getEffect2() {
		return effect2;
	}

	/**
	 * Gets the effect2 idx.
	 *
	 * @return the effect2 idx
	 */
	public int getEffect2Idx() {
		return effect2.getId();
	}

	/**
	 * Gets the effect2 buffer.
	 *
	 * @return the effect2 buffer
	 */
	public int[] getEffect2Buffer() {
		return effect2.getBuffer(generator2.getBuffer());
	}

	/**
	 * Sets the effect2.
	 *
	 * @param effect2 the new effect2
	 */
	public void setEffect2(Effect effect2) {
		this.effect2 = effect2;
	}

	/**
	 * Sets the effect2.
	 *
	 * @param index the new effect2
	 */
	public void setEffect2(int index) {
		Effect e = VisualState.getInstance().getPixelControllerEffect().getEffect(index);
		if (e!=null) {
			this.effect2 = e;			
		}
	}

	/**
	 * Gets the mixer.
	 *
	 * @return the mixer
	 */
	public Mixer getMixer() {
		return mixer;
	}

	/**
	 * Gets the mixer buffer.
	 *
	 * @return the mixer buffer
	 */
	public int[] getMixerBuffer() {
		if (generator1.isPassThoughModeActive()) {
			return generator1.getBuffer();
		}
		if (generator2.isPassThoughModeActive()) {
			return generator2.getBuffer();
		}

		//get gryscale buffer
		int buffer[] = mixer.getBuffer(this);

		return colorSet.convertToColorSetImage(buffer);
	}
	
	/**
	 * Gets the mixer idx.
	 *
	 * @return the mixer idx
	 */
	public int getMixerIdx() {
		return mixer.getId();
	}

	/**
	 * Sets the mixer.
	 *
	 * @param mixer1 the new mixer
	 */
	public void setMixer(Mixer mixer) {
		this.mixer = mixer;
	}

	/**
	 * Sets the mixer.
	 *
	 * @param index the new mixer
	 */
	public void setMixer(int index) {
		Mixer m = VisualState.getInstance().getPixelControllerMixer().getMixer(index);
		if (m!=null) {
			this.mixer = m;			
		}
	}
	
	/**
	 * set color set by index
	 * @param index
	 */
	public void setColorSet(int index) {
	    List<ColorSet> allColorSets = VisualState.getInstance().getColorSets();
	    if (index > allColorSets.size()) {
	        index = 0;
	    }
	    this.colorSet = allColorSets.get(index);
	}

	/**
	 * set color set by name
	 * @param index
	 */
	public void setColorSet(String name) {
	    List<ColorSet> allColorSets = VisualState.getInstance().getColorSets();
	    for (ColorSet cs: allColorSets) {
	    	if (cs.getName().equalsIgnoreCase(name)) {
	    	    this.colorSet = cs;
	    	}
	    }
	}

	
	/**
     * @return the colorSet
     */
    public ColorSet getColorSet() {
        return colorSet;
    }
	
}
