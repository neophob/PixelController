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
import com.neophob.sematrix.fader.Fader;
import com.neophob.sematrix.fader.Fader.FaderName;

/**
 * class used to store input/ouput mapping
 * @author michu
 *
 */
public class OutputMapping {

	/**
	 * the visual input object
	 */
	private int visualId;
	
	/**
	 * the output screen nr
	 */
	private int screenNr;
	private Fader fader;
	private Effect effect;
	
	/**
	 * default setting
	 */
	public OutputMapping() {
		this.visualId = 0;
		this.screenNr = 0;
		
		Collector col = Collector.getInstance();
		this.fader = col.getFader(FaderName.SWITCH);
		this.effect = col.getPixelControllerEffect().getEffect(EffectName.PASSTHRU);
	}

	public OutputMapping(int visualId, int screenNr) {
		this();
		this.visualId = visualId;
		this.screenNr = screenNr;
	}

	public int getVisualId() {
		return visualId;
	}

	public void setVisualId(int visualId) {
		this.visualId = visualId;
	}

	public int getScreenNr() {
		return screenNr;
	}

	public void setScreenNr(int screenNr) {
		this.screenNr = screenNr;
	}
	
	public Effect getEffect() {
		return effect;
	}

	public void setEffect(Effect effect) {
		this.effect = effect;
	}

	public Fader getFader() {
		return fader;
	}

	public void setFader(Fader fader) {
		this.fader = fader;
	}
	
}
