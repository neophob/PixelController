/**
 * Copyright (C) 2011-2014 Michael Vogt <michu@neophob.com>
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

/**
 * The Class ConfigConstant.
 */
public final class ConfigDefault {

    private ConfigDefault() {
        // Utility Class
    }

    public static final transient int DEFAULT_RESOLUTION = 8;

    public static final transient float DEFAULT_SOUND_THRESHOLD = 0.0005f;

    public static final int DEFAULT_FPS = 20;
    public static final int DEFAULT_REMOTE_CLIENT_FPS = 10;

    public static final int DEFAULT_VISUAL_FADE_TIME = 1500;
    public static final int DEFAULT_PRESET_LOADING_FADE_TIME = 500;

    public static final int DEFAULT_NET_OSC_LISTENING_PORT = 9876;

    public static final int DEFAULT_GUI_WINDOW_MAX_X_SIZE = 600;
    public static final int DEFAULT_GUI_WINDOW_MAX_Y_SIZE = 500;
    public static final int DEFAULT_GUI_PIXELSIZE = 16;

    public static final int DEFAULT_CAPTURE_WINDOW_SIZE_X = 0;
    public static final int DEFAULT_CAPTURE_WINDOW_SIZE_Y = 0;

    public static final int DEFAULT_STARTUP_LOAD_PRESET_NR = -1;

    public static final int DEFAULT_UDP_PORT = 6803;

    public static final int DEFAULT_OPC_PORT = 7890;

    public static final int DEFAULT_PIXELINVADERS_PANEL_RESOULTION = 8;
    public static final int DEFAULT_EXPEDITINVADERS_PANEL_RESOULTION = 4;

    public static final int DEFAULT_STARTUP_OUTPUT_GAIN = 100;
}
