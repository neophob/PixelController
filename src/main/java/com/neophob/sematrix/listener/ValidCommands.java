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

package com.neophob.sematrix.listener;

/**
 * The Enum ValidCommands.
 */
public enum ValidCommands {
	
	/** The STATUS. */
	STATUS(0, "refresh whole gui"),
	
	/** The STATUS mini. */
	STATUS_MINI(0, "just refresh parts of the gui"),
	
	/** The CHANGE generator a. */
	CHANGE_GENERATOR_A(1, "<INT> change first generator for current visual"),
	
	/** The CHANGE generator b. */
	CHANGE_GENERATOR_B(1, "<INT> change first generator for current visual"),
	
	/** The CHANGE effect a. */
	CHANGE_EFFECT_A(1, "<INT> change first effect for current visual"),
	
	/** The CHANGE effect b. */
	CHANGE_EFFECT_B(1, "<INT> change second effect for current visual"),
	
	/** The CHANGE mixer. */
	CHANGE_MIXER(1, "<INT> change mixer for current visual"),
	
	//TODO
	/** The CHANGE output. */
	CHANGE_OUTPUT(0, "<INT> change visual for all outputs"),
	
	//TODO
	/** The CHANGE output effect. */
	CHANGE_OUTPUT_EFFECT(0, "<INT> change effect for all outputs"),
	
	//TODO
	/** The CHANGE fader. */
	CHANGE_FADER(0, "<INT> change fader for all outputs"),
	
	/** The CHANG e_ tint. */
	CHANGE_TINT(3, "<INT> <INT> <INT> select rgb value for the tint effect, 0-255"),
	
	/** The CHANGE present. */
	CHANGE_PRESENT(1, "<INT> select current present id"),
	
	/** The CHANGE shuffler select. */
	CHANGE_SHUFFLER_SELECT(14, "14 times <INT>, 14 parameter to enable or disable the shuffler option (gets changed in the random mode), 0=OFF, 1=ON"),
	
	/** The CHANGE threshold value. */
	CHANGE_THRESHOLD_VALUE(1, "<INT> select current threshold for the threshold effect, 0-255"),
	
	/** The CHANG e_ rotozoom. */
	CHANGE_ROTOZOOM(1, "<INT> select angle for the rotozoom effect, -127-127"),
	
	/** The SAVE present. */
	SAVE_PRESENT(0, "save current present settings"),
	
	/** The LOAD present. */
	LOAD_PRESENT(0, "load current present settings"),
	
	/** The BLINKEN. */
	BLINKEN(1, "<STRING> file to load for the blinkenlights generator"),
	
	/** The IMAGE. */
	IMAGE(1, "<STRING> image to load for the simple image generator"),
	
	/** The IMAGE zoomer. */
	IMAGE_ZOOMER(1, "<STRING> image to load for the image zoomer generator"),
	
	/** The TEXTDEF. */
	TEXTDEF(1, "<INT> select texture deformation option, 1-11"),
	
	/** The TEXTDE file. */
	TEXTDEF_FILE(1, "<STRING> image to load for the texture deformation generator"),
	
	/** The TEXTWRITER. */
	TEXTWR(1, "<STRING> update text for textwriter generator"),

	/** The RANDOM. */
	RANDOM(1, "<ON|OFF> enable/disable random mode" ),

	/** The RANDOMIZE. */
	RANDOMIZE(0, "one shot randomizer"),
	
	/** The PRESET random. */
	PRESET_RANDOM(0, "one shot randomizer, use a pre-stored present"),
	
	/** The CURRENT visual. */
	CURRENT_VISUAL(1, "<INT> select actual visual");
	
	/** The nr of params. */
	private int nrOfParams;
	
	/** The desc. */
	String desc;
	
	/**
	 * Instantiates a new valid commands.
	 *
	 * @param nrOfParams the nr of params
	 * @param desc the desc
	 */
	ValidCommands(int nrOfParams, String desc) {
		this.nrOfParams = nrOfParams;
		this.desc = desc;
	}

	/**
	 * Gets the nr of params.
	 *
	 * @return the nr of params
	 */
	public int getNrOfParams() {
		return nrOfParams;
	}

	/**
	 * Gets the desc.
	 *
	 * @return the desc
	 */
	public String getDesc() {
		return desc;
	}
	
}
