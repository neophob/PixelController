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
package com.neophob.sematrix.glue;

import java.util.List;

import processing.core.PApplet;
import processing.core.PImage;

import com.neophob.sematrix.color.ColorSet;
import com.neophob.sematrix.effect.Effect;
import com.neophob.sematrix.effect.Effect.EffectName;
import com.neophob.sematrix.generator.Generator;
import com.neophob.sematrix.generator.Generator.GeneratorName;
import com.neophob.sematrix.mixer.Mixer;
import com.neophob.sematrix.mixer.Mixer.MixerName;
import com.neophob.sematrix.resize.Resize.ResizeName;

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
	private int colorSetIndex;

	/**
	 * initialize default.
	 *
	 * @param generatorName the generator name
	 */
	public Visual(GeneratorName generatorName) {
		Collector col = Collector.getInstance();
		
		this.generator1 = col.getPixelControllerGenerator().getGenerator(generatorName);
		this.generator2 = col.getPixelControllerGenerator().getGenerator(GeneratorName.PASSTHRU);		
		this.effect1 = col.getPixelControllerEffect().getEffect(EffectName.PASSTHRU);
		this.effect2 = col.getPixelControllerEffect().getEffect(EffectName.PASSTHRU);
		this.mixer = col.getPixelControllerMixer().getMixer(MixerName.PASSTHRU);

		this.colorSet = col.getColorSets().get(0);
		col.addVisual(this);
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
		int fxInput = Collector.getInstance().getFxInputForScreen(screenNr);
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
		Generator g = Collector.getInstance().getPixelControllerGenerator().getGenerator(index);
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
		Generator g = Collector.getInstance().getPixelControllerGenerator().getGenerator(index);
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
		Effect e = Collector.getInstance().getPixelControllerEffect().getEffect(index);
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
		Effect e = Collector.getInstance().getPixelControllerEffect().getEffect(index);
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
		Mixer m = Collector.getInstance().getPixelControllerMixer().getMixer(index);
		if (m!=null) {
			this.mixer = m;			
		}
	}
	
	/**
	 * set color set by index
	 * @param index
	 */
	public void setColorSet(int index) {
	    List<ColorSet> allColorSets = Collector.getInstance().getColorSets();
	    if (index > allColorSets.size()) {
	        index = 0;
	    }
	    this.colorSet = allColorSets.get(index);
	    this.colorSetIndex = index;
	}

	/**
	 * set color set by name
	 * @param index
	 */
	public void setColorSet(String name) {
	    List<ColorSet> allColorSets = Collector.getInstance().getColorSets();
	    int idx=0;
	    for (ColorSet cs: allColorSets) {
	    	if (cs.getName().equalsIgnoreCase(name)) {
	    	    this.colorSet = cs;
	    	    this.colorSetIndex = idx;	    		
	    	}
	    	idx++;
	    }
	}

	/**
	 * 
	 * @return
	 */
	public int getColorSetIndex() {
	    return colorSetIndex;
	}
	
	/**
     * @return the colorSet
     */
    public ColorSet getColorSet() {
        return colorSet;
    }

    /**
	 * 
	 * @param buffer
	 * @return
	 */
	private PImage getBufferAsImage(int[] buffer) {
		PImage pImage = Collector.getInstance().getPapplet().createImage
							(generator1.getInternalBufferXSize() , generator1.getInternalBufferYSize(), PApplet.RGB );
		pImage.loadPixels();
		System.arraycopy(buffer, 0, pImage.pixels, 0, buffer.length);
		pImage.updatePixels();
		return pImage;
	}

	/**
	 * get screenshot of generator
	 * 
	 * @param ofs
	 * @return
	 */
	public PImage getGeneratorAsImage(int ofs) {
		if (ofs==0) {
			return getBufferAsImage(colorSet.convertToColorSetImage(generator1.internalBuffer));			
		}
		return getBufferAsImage(colorSet.convertToColorSetImage(generator2.internalBuffer));
	}

	/**
	 * get screenshot of effects
	 * @param ofs
	 * @return
	 */
	public PImage getEffectAsImage(int ofs) {
		if (ofs==0) {
			return getBufferAsImage(
					colorSet.convertToColorSetImage(effect1.getBuffer(generator1.internalBuffer))
			);			
		}
		return getBufferAsImage(
				colorSet.convertToColorSetImage(effect2.getBuffer(generator2.internalBuffer))
		);			
	}
	
	/**
	 * 
	 * @return
	 */
	public PImage getMixerAsImage() {
		return getBufferAsImage(getMixerBuffer());
	}
	
	//TODO make configurable
	private static final int MAX_NR_OF_VISUALS = 12;
	
	/**
	 * initialize the visuals...
	 * 
	 * TODO move me away
	 *
	 * @param nrOfScreens the nr of screens
	 */
	public static void initializeVisuals(int nrOfScreens) {
		for (int n=0; n<nrOfScreens && n<MAX_NR_OF_VISUALS; n++) {
			switch (n%10) {
			case 0:
				new Visual(GeneratorName.BLINKENLIGHTS);
				break;
			case 1:
				new Visual(GeneratorName.METABALLS);
				break;
			case 2:
				new Visual(GeneratorName.COLOR_SCROLL);
				break;
			case 3:
				new Visual(GeneratorName.PLASMA);
				break;
			case 4:
				new Visual(GeneratorName.IMAGE);
				break;
			case 5:
				new Visual(GeneratorName.FIRE);
				break;
			case 6:
				new Visual(GeneratorName.FFT);
				break;
			case 7:
				new Visual(GeneratorName.CELL);
				break;
			case 8:
				new Visual(GeneratorName.DROPS);
				break;
			case 9:
				new Visual(GeneratorName.PLASMA_ADVANCED);
				break;
			case 10:
				new Visual(GeneratorName.PIXELIMAGE);
				break;
			}
		}
		
	}

}
