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

package com.neophob.sematrix.properties;

import java.util.ArrayList;
import java.util.List;

/**
 * The Enum ValidCommands.
 */
public enum ValidCommands {
	
	/** The STATUS. */
	STATUS(CommandGroup.MISC, 0, "refresh whole gui"),
	
	/** The STATUS mini. */
	STATUS_MINI(CommandGroup.MISC, 0, "just refresh parts of the gui"),
	
	/** The CHANGE generator a. */
	CHANGE_GENERATOR_A(CommandGroup.VISUAL, 1, "<INT> change first generator for current visual"),
	
	/** The CHANGE generator b. */
	CHANGE_GENERATOR_B(CommandGroup.VISUAL, 1, "<INT> change first generator for current visual"),
	
	/** The CHANGE effect a. */
	CHANGE_EFFECT_A(CommandGroup.VISUAL, 1, "<INT> change first effect for current visual"),
	
	/** The CHANGE effect b. */
	CHANGE_EFFECT_B(CommandGroup.VISUAL, 1, "<INT> change second effect for current visual"),
	
	/** The CHANGE mixer. */
	CHANGE_MIXER(CommandGroup.VISUAL, 1, "<INT> change mixer for current visual"),
	
	/** The CHANGE output. */
	CHANGE_OUTPUT_VISUAL(CommandGroup.OUTPUT, 1, "<INT> change visual for current output"),
	
	/** The CHANGE fader. */
	CHANGE_OUTPUT_FADER(CommandGroup.OUTPUT, 1, "<INT> change fader for current output"),

	/** The CHANGE output. */
	CHANGE_ALL_OUTPUT_VISUAL(CommandGroup.OUTPUT, 1, "<INT> change visual for all outputs"),
	
	/** The CHANGE fader. */
	CHANGE_ALL_OUTPUT_FADER(CommandGroup.OUTPUT, 1, "<INT> change fader for all outputs"),

	/** The CHANGE present. */
	CHANGE_PRESENT(CommandGroup.MISC, 1, "<INT> select current present id"),
	
	/** The CHANGE shuffler select. */
	CHANGE_SHUFFLER_SELECT(CommandGroup.MISC, 14, "14 times <INT>, 14 parameter to enable or disable the shuffler option (gets changed in the random mode), 0=OFF, 1=ON"),
	
	/** The CHANGE threshold value. */
	CHANGE_THRESHOLD_VALUE(CommandGroup.EFFECT, 1, "<INT> select current threshold for the threshold effect, 0-255"),
	
	/** The CHANG e_ rotozoom. */
	CHANGE_ROTOZOOM(CommandGroup.EFFECT, 1, "<INT> select angle for the rotozoom effect, -127-127"),
	
	/** The SAVE present. */
	SAVE_PRESENT(CommandGroup.MISC, 0, "save current present settings"),
	
	/** The LOAD present. */
	LOAD_PRESENT(CommandGroup.MISC, 0, "load current present settings"),
	
	/** The BLINKEN. */
	BLINKEN(CommandGroup.GENERATOR, 1, "<STRING> file to load for the blinkenlights generator"),
	
	/** The IMAGE. */
	IMAGE(CommandGroup.GENERATOR, 1, "<STRING> image to load for the simple image generator"),
	
	/** The TEXTDEF. */
	TEXTDEF(CommandGroup.GENERATOR, 1, "<INT> select texture deformation option, 1-11"),
	
	/** The COLOR_SCROLL_OPT. */
	COLOR_SCROLL_OPT(CommandGroup.GENERATOR, 1, "<INT> select color scroll fading direction, 1-14"),
		
	/** The COLOR_SCROLL_OPT. */
	COLOR_FADE_LENGTH (CommandGroup.GENERATOR, 1, "<INT> fading length for the color fade generator"),
	
	/** The COLOR_SCROLL_OPT. */
	COLOR_SCROLL_LENGTH(CommandGroup.GENERATOR, 1, "<INT> fading distance for the color scroll generator"),
	
	/** The TEXTWRITER. */
	TEXTWR(CommandGroup.GENERATOR, 1, "<STRING> update text for textwriter generator"),

	/** The RANDOM. */
	RANDOM(CommandGroup.MISC, 1, "<ON|OFF> enable/disable random mode" ),

	/** The RANDOMIZE. */
	RANDOMIZE(CommandGroup.MISC, 0, "one shot randomizer"),
	
	/** The PRESET random. */
	PRESET_RANDOM(CommandGroup.MISC, 0, "one shot randomizer, use a pre-stored present"),
	
	/** The CURRENT visual. */
	CURRENT_VISUAL(CommandGroup.VISUAL, 1, "<INT> select actual visual"),

	CURRENT_COLORSET(CommandGroup.VISUAL, 1, "<INT> select actual ColorSet"),

	/** The CURRENT output. */
	CURRENT_OUTPUT(CommandGroup.OUTPUT, 1, "<INT> select current output"),

	/** The CURRENT output. */
	JMX_STAT(CommandGroup.MISC, 0, "show JMX runtime statistic, default port: 1337"),

	SCREENSHOT(CommandGroup.MISC, 0, "save screenhot");
	
	/** The nr of params. */
	private int nrOfParams;
	
	/** The desc. */
	private String desc;
	
	/** The group. */
	private CommandGroup group;
	
	/**
	 * Instantiates a new valid commands.
	 *
	 * @param group the group
	 * @param nrOfParams the nr of params
	 * @param desc the desc
	 */
	ValidCommands(CommandGroup group, int nrOfParams, String desc) {
		this.group = group;
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
	
	
	/**
	 * Gets the group.
	 *
	 * @return the group
	 */
	public CommandGroup getGroup() {
		return group;
	}

	/**
	 * getCommandsByGroup
	 *
	 * @param group the group
	 * @return the list
	 */
	public static List<ValidCommands> getCommandsByGroup(CommandGroup group) {
		List<ValidCommands> list = new ArrayList<ValidCommands>();
		for (ValidCommands vc: ValidCommands.values()) {
			if (vc.getGroup() == group) {
				list.add(vc);
			}
		}
		
		return list;
	}
}
