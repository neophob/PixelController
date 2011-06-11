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

package com.neophob.sematrix.glue;

import com.neophob.sematrix.effect.Effect;
import com.neophob.sematrix.effect.Effect.EffectName;
import com.neophob.sematrix.generator.Generator;
import com.neophob.sematrix.generator.Generator.GeneratorName;
import com.neophob.sematrix.mixer.Mixer;
import com.neophob.sematrix.mixer.Mixer.MixerName;
import com.neophob.sematrix.resize.Resize.ResizeName;

/**
 * this model holds 2 generators, 2 effects and a mixer instance
 * @author mvogt
 *
 */
public class Visual {

	private Generator generator1;
	private Generator generator2;
	private Effect effect1;
	private Effect effect2;
	private Mixer mixer;

	/**
	 * initialize default
	 * @param generatorName
	 */
	public Visual(GeneratorName generatorName) {
		Collector col = Collector.getInstance();
		
		this.generator1 = col.getPixelControllerGenerator().getGenerator(generatorName);
		this.generator2 = col.getPixelControllerGenerator().getGenerator(GeneratorName.PASSTHRU);		
		this.effect1 = col.getPixelControllerEffect().getEffect(EffectName.PASSTHRU);
		this.effect2 = col.getPixelControllerEffect().getEffect(EffectName.PASSTHRU);
		this.mixer = col.getPixelControllerMixer().getMixer(MixerName.PASSTHRU);

		col.addVisual(this);
	}

	public int[] getBuffer() {
		return this.getMixerBuffer();
	}

	public boolean isVisualOnScreen(int screenNr) {
		int fxInput = Collector.getInstance().getFxInputForScreen(screenNr);
		if (fxInput == getGenerator1Idx()) {
			return true;
		}
		return false;
	}

	//check the resize option to return
	public ResizeName getResizeOption() {
		if (this.generator1.getResizeOption() == ResizeName.PIXEL_RESIZE || this.generator2.getResizeOption() == ResizeName.PIXEL_RESIZE ||
				this.effect1.getResizeOption() == ResizeName.PIXEL_RESIZE || this.effect2.getResizeOption() == ResizeName.PIXEL_RESIZE ||
				this.mixer.getResizeOption() == ResizeName.PIXEL_RESIZE) {
			return ResizeName.PIXEL_RESIZE;
		}
		
		return ResizeName.QUALITY_RESIZE;
	}
	
	public Generator getGenerator1() {
		return generator1;
	}

	public int getGenerator1Idx() {
		return generator1.getId();
	}

	public void setGenerator1(Generator generator1) {
		this.generator1 = generator1;
	}

	public void setGenerator1(int index) {
		this.generator1 = Collector.getInstance().getPixelControllerGenerator().getGenerator(index);
	}

	public Generator getGenerator2() {
		return generator2;
	}

	public int getGenerator2Idx() {
		return generator2.getId();
	}

	public void setGenerator2(Generator generator2) {
		this.generator2 = generator2;
	}

	public void setGenerator2(int index) {
		this.generator2 = Collector.getInstance().getPixelControllerGenerator().getGenerator(index);
	}

	public Effect getEffect1() {
		return effect1;
	}

	public int getEffect1Idx() {
		return effect1.getId();
	}

	public int[] getEffect1Buffer() {
		return effect1.getBuffer(generator1.getBuffer());
	}

	public void setEffect1(Effect effect1) {
		this.effect1 = effect1;
	}

	public void setEffect1(int index) {
		this.effect1 = Collector.getInstance().getPixelControllerEffect().getEffect(index);
	}

	public Effect getEffect2() {
		return effect2;
	}

	public int getEffect2Idx() {
		return effect2.getId();
	}

	public int[] getEffect2Buffer() {
		return effect2.getBuffer(generator2.getBuffer());
	}

	public void setEffect2(Effect effect2) {
		this.effect2 = effect2;
	}

	public void setEffect2(int index) {
		this.effect2 = Collector.getInstance().getPixelControllerEffect().getEffect(index);
	}

	public Mixer getMixer() {
		return mixer;
	}

	public int[] getMixerBuffer() {
		return mixer.getBuffer(this);
	}

	public int getMixerIdx() {
		return mixer.getId();
	}

	public void setMixer(Mixer mixer1) {
		this.mixer = mixer1;
	}

	public void setMixer(int index) {
		this.mixer = Collector.getInstance().getPixelControllerMixer().getMixer(index);
	}

	/**
	 * initialize the visuals...
	 * 
	 * @param n
	 */
	public static void initializeVisuals(int n) {
		switch (n%5) {
		case 0:
			new Visual(GeneratorName.BLINKENLIGHTS);
			break;
		case 1:
			new Visual(GeneratorName.METABALLS);
			break;
		case 2:
			new Visual(GeneratorName.SIMPLECOLORS);
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
		}
		
	}

}
