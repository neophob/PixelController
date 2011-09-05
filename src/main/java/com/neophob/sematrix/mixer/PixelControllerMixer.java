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

/**
 * The Class PixelControllerMixer.
 */
public class PixelControllerMixer implements PixelControllerElement {

	/** The all mixer. */
	private List<Mixer> allMixer;
	
	/**
	 * Instantiates a new pixel controller mixer.
	 */
	public PixelControllerMixer() {
		allMixer = new CopyOnWriteArrayList<Mixer>();
	}
	
	/* (non-Javadoc)
	 * @see com.neophob.sematrix.glue.PixelControllerElement#initAll()
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
	
	/* (non-Javadoc)
	 * @see com.neophob.sematrix.glue.PixelControllerElement#getCurrentState()
	 */
	public List<String> getCurrentState() {
		List<String> ret = new ArrayList<String>();
		
		return ret;
	}

	/* (non-Javadoc)
	 * @see com.neophob.sematrix.glue.PixelControllerElement#update()
	 */
	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}

	/*
	 * MIXER ======================================================
	 */

	/**
	 * Gets the size.
	 *
	 * @return the size
	 */
	public int getSize() {
		return allMixer.size();
	}
	
	/**
	 * Gets the mixer.
	 *
	 * @param name the name
	 * @return the mixer
	 */
	public Mixer getMixer(MixerName name) {
		for (Mixer mix: allMixer) {
			if (mix.getId() == name.getId()) {
				return mix;
			}
		}
		return null;
	}

	/**
	 * Gets the all mixer.
	 *
	 * @return the all mixer
	 */
	public List<Mixer> getAllMixer() {
		return allMixer;
	}

	/**
	 * Gets the mixer.
	 *
	 * @param index the index
	 * @return the mixer
	 */
	public Mixer getMixer(int index) {
		for (Mixer mix: allMixer) {
			if (mix.getId() == index) {
				return mix;
			}
		}
		return null;
	}

	/**
	 * Adds the mixer.
	 *
	 * @param mixer the mixer
	 */
	public void addMixer(Mixer mixer) {
		allMixer.add(mixer);
	}

}
