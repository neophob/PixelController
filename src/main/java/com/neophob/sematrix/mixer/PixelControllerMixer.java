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
package com.neophob.sematrix.mixer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.neophob.sematrix.PixelControllerElement;
import com.neophob.sematrix.glue.Collector;
import com.neophob.sematrix.glue.MatrixData;
import com.neophob.sematrix.input.SeSound;
import com.neophob.sematrix.input.Sound;
import com.neophob.sematrix.mixer.Mixer.MixerName;

/**
 * The Class PixelControllerMixer.
 */
public class PixelControllerMixer implements PixelControllerElement {

    private static final Logger LOG = Logger.getLogger(PixelControllerMixer.class.getName());

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
		MatrixData matrix = Collector.getInstance().getMatrix();
		SeSound sound = Sound.getInstance();
		
		//create mixer
		allMixer.add(new AddSat());
		allMixer.add(new Multiply());
		allMixer.add(new Mix());
		allMixer.add(new PassThruMixer());
		allMixer.add(new NegativeMultiply());
		allMixer.add(new Checkbox(matrix));
		allMixer.add(new Voluminizer(sound));
		allMixer.add(new Either());
		allMixer.add(new SubSat());
		allMixer.add(new HalfHalf());
		allMixer.add(new HalfHalfVertical());
		allMixer.add(new Maximum());
		allMixer.add(new Minimum());
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
		
        LOG.log(Level.WARNING, "Invalid Mixer name selected: {0}", name);
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
		
        LOG.log(Level.WARNING, "Invalid Mixer index selected: {0}", index);
		return null;
	}

}
