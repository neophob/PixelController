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
package com.neophob.sematrix.core.properties;

import java.util.ArrayList;
import java.util.List;

/**
 * The Enum ValidCommands.
 */
public enum ValidCommand {

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

    /** The CHANGE preset. */
    CHANGE_PRESET(CommandGroup.MISC, 1, "<INT> select current preset id"),

    /** The CHANGE shuffler select. */
    CHANGE_SHUFFLER_SELECT(
            CommandGroup.MISC,
            15,
            "<INT>, parameter contains 15 nibbles to enable or disable the shuffler option (gets changed in the random mode), 0=OFF, 1=ON, example: 0 0 0 0 0 1 1 1 1 1 0 0 0 0 0"),

    /** The CHANGE threshold value. */
    CHANGE_THRESHOLD_VALUE(CommandGroup.EFFECT, 1,
            "<INT> select current threshold for the threshold effect, 0-255"),

    /** The CHANG e_ rotozoom. */
    CHANGE_ROTOZOOM(CommandGroup.EFFECT, 1, "<INT> select angle for the rotozoom effect, -127-127"),

    /** The SAVE preset. */
    SAVE_PRESET(CommandGroup.MISC, 1, "<NAME> save current preset settings with name NAME"),

    /** The LOAD present. */
    LOAD_PRESET(CommandGroup.MISC, 0, "<NO PARAM> load current preset settings"),

    /** The BLINKEN. */
    BLINKEN(CommandGroup.GENERATOR, 1, "<STRING> file to load for the blinkenlights generator"),

    /** The IMAGE. */
    IMAGE(CommandGroup.GENERATOR, 1, "<STRING> image to load for the simple image generator"),

    /** The TEXTDEF. */
    TEXTDEF(CommandGroup.GENERATOR, 1, "<INT> select texture deformation option, 1-11"),

    ZOOMOPT(CommandGroup.GENERATOR, 1, "<INT> select zoom options 1-4"),

    /** The COLOR_SCROLL_OPT. */
    COLOR_SCROLL_OPT(CommandGroup.GENERATOR, 1, "<INT> select color scroll fading direction, 1-14"),

    /** The TEXTWRITER. */
    TEXTWR(CommandGroup.GENERATOR, 1, "<STRING> update text for textwriter generator"),

    TEXTWR_OPTION(CommandGroup.GENERATOR, 1,
            "<INT> set mode textwriter (pingpong scroller, left scroller)"),

    /** The RANDOM. */
    RANDOM(CommandGroup.MISC, 1, "<ON|OFF> enable/disable random mode"),

    /** The RANDOM preset. */
    RANDOM_PRESET_MODE(CommandGroup.MISC, 1, "<ON|OFF> enable/disable random preset mode"),

    /** The RANDOMIZE. */
    RANDOMIZE(CommandGroup.MISC, 0, "<NO PARAM> one shot randomizer"),

    /** The PRESET random. */
    PRESET_RANDOM(CommandGroup.MISC, 0, "<NO PARAM> one shot randomizer, use a pre-stored present"),

    /** The CURRENT visual. */
    CURRENT_VISUAL(CommandGroup.VISUAL, 1, "<INT> select actual visual"),

    CURRENT_COLORSET(CommandGroup.VISUAL, 1, "<INT> select actual ColorSet"),

    /** The CURRENT output. */
    CURRENT_OUTPUT(CommandGroup.OUTPUT, 1, "<INT> select current output"),

    SCREENSHOT(CommandGroup.MISC, 0, "<NO PARAM> save screenhot"),

    FREEZE(CommandGroup.MISC, 0, "<NO PARAM> toggle pause mode"),

    CHANGE_BRIGHTNESS(CommandGroup.GENERATOR, 1, "<INT> output brightness 0 .. 100"),

    GENERATOR_SPEED(CommandGroup.GENERATOR, 1,
            "<INT> generator speed 0 .. 200 (default speed is 100)"),

    BEAT_WORKMODE(CommandGroup.GENERATOR, 1, "<INT> change beat workmode"),

    OSC_GENERATOR1(CommandGroup.GENERATOR, 1,
            "<BLOB> contains Xres*Yres*8bpp bytes or Xres*Yres*24bpp bytes raw imagedata"),

    OSC_GENERATOR2(CommandGroup.GENERATOR, 1,
            "<BLOB> contains Xres*Yres*8bpp bytes or Xres*Yres*24bpp bytes raw imagedata"),

    ROTATE_COLORSET(CommandGroup.VISUAL, 0, "Select next Colorset"),

    ROTATE_GENERATOR_A(CommandGroup.VISUAL, 0, "Select next Generator A"),

    ROTATE_GENERATOR_B(CommandGroup.VISUAL, 0, "Select next Generator B"),

    ROTATE_EFFECT_A(CommandGroup.VISUAL, 0, "Select next Effect A"),

    ROTATE_EFFECT_B(CommandGroup.VISUAL, 0, "Select next Effect A"),

    ROTATE_MIXER(CommandGroup.VISUAL, 0, "Select next Mixer"),

    ROTATE_IMAGE(CommandGroup.VISUAL, 0, "Select next Image"),

    ROTATE_BLINKEN(CommandGroup.VISUAL, 0, "Select next Blinkenlight file"),

    DUMMY(CommandGroup.VISUAL, 0, "Do nothing"),

    /* INTERNAL COMMANDS, USED BY INTENRAL API */
    GET_CONFIGURATION(CommandGroup.INTERNAL, 0, "Return the application configuration", true),

    GET_VERSION(CommandGroup.INTERNAL, 0, "Return the Version", true),

    GET_MATRIXDATA(CommandGroup.INTERNAL, 0, "Return the MatrixData", true),

    GET_COLORSETS(CommandGroup.INTERNAL, 0, "Return the loaded colorsets", true),

    GET_OUTPUTMAPPING(CommandGroup.INTERNAL, 0, "Return the output mapping", true),

    GET_OUTPUT(CommandGroup.INTERNAL, 0, "Return the output buffer", true),

    GET_GUISTATE(CommandGroup.INTERNAL, 0, "Return the gui state", true),

    GET_PRESETSETTINGS(CommandGroup.INTERNAL, 0, "Return data about current preset", true),

    GET_JMXSTATISTICS(CommandGroup.INTERNAL, 0, "Return JMX statistics", true),

    GET_FILELOCATION(CommandGroup.INTERNAL, 0,
            "Return core file locations (blinkenlight and image files)", true),

    GET_IMAGEBUFFER(CommandGroup.INTERNAL, 0, "Return output and visual buffer", true),

    REGISTER_VISUALOBSERVER(CommandGroup.INTERNAL, 0, "register a remote visual observer"),

    UNREGISTER_VISUALOBSERVER(CommandGroup.INTERNAL, 0, "unregister a remote visual observer"),

    ;

    /** The nr of params. */
    private int nrOfParams;

    /** The desc. */
    private String desc;

    /** expect an answer from server? (ie. is this a client request) */
    private boolean waitForServerResponse;

    /** The group. */
    private CommandGroup group;

    /**
     * Instantiates a new valid commands.
     * 
     * @param group
     *            the group
     * @param nrOfParams
     *            the nr of params
     * @param desc
     *            the desc
     */
    ValidCommand(CommandGroup group, int nrOfParams, String desc) {
        this(group, nrOfParams, desc, false);
    }

    ValidCommand(CommandGroup group, int nrOfParams, String desc, boolean waitForServerResponse) {
        this.group = group;
        this.nrOfParams = nrOfParams;
        this.desc = desc;
        this.waitForServerResponse = waitForServerResponse;
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

    public boolean isWaitForServerResponse() {
        return waitForServerResponse;
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
     * @param group
     *            the group
     * @return the list
     */
    public static List<ValidCommand> getCommandsByGroup(CommandGroup group) {
        List<ValidCommand> list = new ArrayList<ValidCommand>();
        for (ValidCommand vc : ValidCommand.values()) {
            if (vc.getGroup() == group) {
                list.add(vc);
            }
        }

        return list;
    }
}
