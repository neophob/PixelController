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

package com.neophob.sematrix.mixer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.neophob.sematrix.glue.PixelControllerElement;
import com.neophob.sematrix.mixer.Mixer.MixerName;

public class PixelControllerMixer implements PixelControllerElement {

	private List<Mixer> allMixer;
	
	public PixelControllerMixer() {
		allMixer = new CopyOnWriteArrayList<Mixer>();
	}
	
	/**
	 * 
	 */
	public void initAll() {
		//create mixer
		new AddSat(this);
		new Multiply(this);
		new Mix(this);
		new PassThruMixer(this);
		new NegativeMultiply(this);
		new Checkbox(this);
		new Voluminizer(this);
		new Xor(this);
		new MinusHalf(this);
		new Either(this);
	}
	
	/**
	 * 
	 */
	public List<String> getCurrentState() {
		List<String> ret = new ArrayList<String>();
		
		return ret;
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}

	/*
	 * MIXER ======================================================
	 */

	public int getSize() {
		return allMixer.size();
	}
	
	public Mixer getMixer(MixerName name) {
		for (Mixer mix: allMixer) {
			if (mix.getId() == name.getId()) {
				return mix;
			}
		}
		return null;
	}

	public List<Mixer> getAllMixer() {
		return allMixer;
	}

	public Mixer getMixer(int index) {
		for (Mixer mix: allMixer) {
			if (mix.getId() == index) {
				return mix;
			}
		}
		return null;
	}

	public void addMixer(Mixer mixer) {
		allMixer.add(mixer);
	}

}
