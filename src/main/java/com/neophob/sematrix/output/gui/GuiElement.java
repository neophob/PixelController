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

package com.neophob.sematrix.output.gui;

public enum GuiElement {

	CURRENT_VISUAL,
	CURRENT_OUTPUT,
	
	GENERATOR_ONE_DROPDOWN,
	GENERATOR_TWO_DROPDOWN,
	EFFECT_ONE_DROPDOWN,
	EFFECT_TWO_DROPDOWN,
	MIXER_DROPDOWN,
	
	BUTTON_RANDOM_CONFIGURATION,
	BUTTON_RANDOM_PRESENT,
	BUTTON_TOGGLE_RANDOM_MODE,
	
	THRESHOLD,
	COLOR_PICKER,

	BLINKENLIGHTS_DROPDOWN,
	IMAGE_DROPDOWN,
	
	TEXTUREDEFORM_IMAGE_DROPDOWN,
	TEXTUREDEFORM_OPTIONS,
	
	COLORSCROLL_OPTIONS,
	
	OUTPUT_SELECTED_VISUAL_DROPDOWN,
	OUTPUT_EFFECT_DROPDOWN,
	OUTPUT_FADER_DROPDOWN,
	;
	
	public static GuiElement getGuiElement(String s) {
		for (GuiElement gw: GuiElement.values()) {
			if (s.equalsIgnoreCase(gw.toString())) {
				return gw;
			}
		}
		return null;
	}
}
