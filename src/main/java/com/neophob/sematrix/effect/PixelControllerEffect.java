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

package com.neophob.sematrix.effect;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.neophob.sematrix.effect.Effect.EffectName;
import com.neophob.sematrix.glue.PixelControllerElement;
import com.neophob.sematrix.properties.ValidCommands;

/**
 * The Class PixelControllerEffect.
 */
public class PixelControllerEffect implements PixelControllerElement {

	/** The all effects. */
	private List<Effect> allEffects;
	
	/** The tint. */
	private Tint tint;
	
	/** The threshold. */
	private Threshold threshold;
	
	/**
	 * Instantiates a new pixel controller effect.
	 */
	public PixelControllerEffect() {
		allEffects = new CopyOnWriteArrayList<Effect>();
	}
	
	/* (non-Javadoc)
	 * @see com.neophob.sematrix.glue.PixelControllerElement#update()
	 */
	@Override
	public void update() {
		for (Effect e: allEffects) {
			e.update();
		}
	}
	
	/**
	 * initialize all effects.
	 */
	@Override
	public void initAll() {
		//create effects
		new Inverter(this);
		new PassThru(this);
		new RotoZoom(this, 1.5f, 2.3f);
		new BeatVerticalShift(this);
		new BeatHorizShift(this);
		new Voluminize(this);
		tint = new Tint(this);
		threshold = new Threshold(this);
		new Emboss(this);

	}
	
	/* (non-Javadoc)
	 * @see com.neophob.sematrix.glue.PixelControllerElement#getCurrentState()
	 */
	@Override
	public List<String> getCurrentState() {
		List<String> ret = new ArrayList<String>();
				
		ret.add(ValidCommands.CHANGE_TINT+" "+tint.getR()+" "+tint.getG()+" "+tint.getB());
		ret.add(ValidCommands.CHANGE_ROTOZOOM+" "+((RotoZoom)getEffect(EffectName.ROTOZOOM)).getAngle());
		ret.add(ValidCommands.CHANGE_THRESHOLD_VALUE +" "+threshold.getThreshold());

		return ret;
	}


	/*
	 * EFFECT ======================================================
	 */
	
	/**
	 * Gets the size.
	 *
	 * @return the size
	 */
	public int getSize() {
		return allEffects.size();
	}

	/**
	 * Gets the effect.
	 *
	 * @param name the name
	 * @return the effect
	 */
	public Effect getEffect(EffectName name) {
		for (Effect fx: allEffects) {
			if (fx.getId() == name.getId()) {
				return fx;
			}
		}
		return null;
	}


	/**
	 * Gets the all effects.
	 *
	 * @return the all effects
	 */
	public List<Effect> getAllEffects() {
		return allEffects;
	}

	/**
	 * Gets the effect.
	 *
	 * @param index the index
	 * @return the effect
	 */
	public Effect getEffect(int index) {
		for (Effect fx: allEffects) {
			if (fx.getId() == index) {
				return fx;
			}
		}
		return null;
	}

	/**
	 * Adds the effect.
	 *
	 * @param effect the effect
	 */
	public void addEffect(Effect effect) {
		allEffects.add(effect);
	}

	
	/**
	 * Sets the threshold value.
	 *
	 * @param val the new threshold value
	 */
	public void setThresholdValue(int val) {
		this.threshold.setThreshold(val);
	}
	
	/**
	 * Gets the threshold value.
	 *
	 * @return the threshold value
	 */
	public int getThresholdValue() {
		return this.threshold.getThreshold();
	}
	
	
	/**
	 * Sets the rgb.
	 *
	 * @param r the r
	 * @param g the g
	 * @param b the b
	 */
	public void setRGB(int r, int g, int b) {
		tint.setColor(r, g, b);
	}

	/**
	 * Gets the r.
	 *
	 * @return the r
	 */
	public int getR() {
		return tint.getR();
	}

	/**
	 * Gets the g.
	 *
	 * @return the g
	 */
	public int getG() {
		return tint.getG();
	}

	/**
	 * Gets the b.
	 *
	 * @return the b
	 */
	public int getB() {
		return tint.getB();
	}




}
