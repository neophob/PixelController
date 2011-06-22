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
import com.neophob.sematrix.listener.MessageProcessor.ValidCommands;

public class PixelControllerEffect implements PixelControllerElement {

	private List<Effect> allEffects;
	private Tint tint;
	private Threshold threshold;
	
	/**
	 * 
	 */
	public PixelControllerEffect() {
		allEffects = new CopyOnWriteArrayList<Effect>();
	}
	
	@Override
	public void update() {
		for (Effect e: allEffects) {
			e.update();
		}
	}
	
	/**
	 * initialize all effects
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
	
	/**
	 * 
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
	
	public int getSize() {
		return allEffects.size();
	}

	public Effect getEffect(EffectName name) {
		for (Effect fx: allEffects) {
			if (fx.getId() == name.getId()) {
				return fx;
			}
		}
		return null;
	}


	public List<Effect> getAllEffects() {
		return allEffects;
	}

	public Effect getEffect(int index) {
		for (Effect fx: allEffects) {
			if (fx.getId() == index) {
				return fx;
			}
		}
		return null;
	}

	public void addEffect(Effect effect) {
		allEffects.add(effect);
	}

	
	public void setThresholdValue(int val) {
		this.threshold.setThreshold(val);
	}
	
	public int getThresholdValue() {
		return this.threshold.getThreshold();
	}
	
	
	public void setRGB(int r, int g, int b) {
		tint.setColor(r, g, b);
	}

	public int getR() {
		return tint.getR();
	}

	public int getG() {
		return tint.getG();
	}

	public int getB() {
		return tint.getB();
	}




}
