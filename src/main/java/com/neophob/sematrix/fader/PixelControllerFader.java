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
package com.neophob.sematrix.fader;

import com.neophob.sematrix.fader.Fader.FaderName;
import com.neophob.sematrix.properties.ApplicationConfigurationHelper;

/**
 * 
 * @author michu
 *
 */
public final class PixelControllerFader {

	private int presetLoadingFadeTime;
	private int visualFadeTime;
	
	public PixelControllerFader(ApplicationConfigurationHelper ah) {
		presetLoadingFadeTime = ah.getPresetLoadingFadeTime();
		visualFadeTime = ah.getVisualFadeTime();
	}
	
	/* 
	 * FADER ======================================================
	 */

	/**
	 * return a NEW INSTANCE of a fader.
	 *
	 * @param faderName the fader name
	 * @return the fader
	 */
	public Fader getVisualFader(FaderName faderName) {
		switch (faderName) {
		case CROSSFADE:
			return new Crossfader(visualFadeTime);
		case SWITCH:
			return new Switch();
		case SLIDE_UPSIDE_DOWN:
			return new SlideUpsideDown(visualFadeTime);
		case SLIDE_LEFT_RIGHT:
			return new SlideLeftRight(visualFadeTime);
		}
		return null;
	}
	
	/**
	 * return a fader with default duration
	 * 
	 * @param index
	 * @return
	 */
	public Fader getVisualFader(int index) {
		switch (index) {
		case 0:
			return new Switch();
		case 1:
			return new Crossfader(visualFadeTime);
		case 2:
			return new SlideUpsideDown(visualFadeTime);
		case 3:
			return new SlideLeftRight(visualFadeTime);
		}
		return null;
	}	

	/**
	 * return a fader with a specific duration
	 * 
	 * @param index
	 * @return
	 */
	public Fader getPresetFader(int index) {
		switch (index) {
		case 0:
			return new Switch();
		case 1:
			return new Crossfader(presetLoadingFadeTime);
		case 2:
			return new SlideUpsideDown(presetLoadingFadeTime);
		case 3:
			return new SlideLeftRight(presetLoadingFadeTime);
		}
		return null;
	}


	/**
	 * 
	 * @return
	 */
	public int getFaderCount() {
		return FaderName.values().length;
	}



}
