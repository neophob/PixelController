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
public final class ConfigConstant {

    private ConfigConstant() {
        // Utility Class
    }

    /** The Constant DELIM. */
    public static final String DELIM = ",";

    /** The Constant RAINBOWDUINO_V2_ROW1. */
    public static final String RAINBOWDUINO_V2_ROW1 = "layout.row1.i2c.addr";

    /** The Constant RAINBOWDUINO_V2_ROW2. */
    public static final String RAINBOWDUINO_V2_ROW2 = "layout.row2.i2c.addr";

    public static final String RAINBOWDUINO_V3_ROW1 = "layout.row1.serial.devices";
    public static final String RAINBOWDUINO_V3_ROW2 = "layout.row2.serial.devices";

    /** The Constant CFG_PANEL_COLOR_ORDER. */
    public static final String CFG_PANEL_COLOR_ORDER = "panel.color.order";

    public static final String CFG_PANEL_GAMMA_TAB = "panel.gamma.tab";

    /** The Constant NULLOUTPUT_ROW1. */
    public static final String NULLOUTPUT_ROW1 = "nulloutput.devices.row1";

    /** The Constant NULLOUTPUT_ROW2. */
    public static final String NULLOUTPUT_ROW2 = "nulloutput.devices.row2";

    /** The Constant ARTNET_IP. */
    public static final String ARTNET_IP = "artnet.ip";
    public static final String ARTNET_PIXELS_PER_UNIVERSE = "artnet.pixels.per.universe";
    public static final String ARTNET_FIRST_UNIVERSE_ID = "artnet.first.universe.id";
    public static final String ARTNET_BROADCAST_ADDR = "artnet.broadcast.address";
    public static final String ARTNET_ROW1 = "artnet.layout.row1";
    public static final String ARTNET_ROW2 = "artnet.layout.row2";

    public static final String E131_IP = "e131.ip";
    public static final String E131_PIXELS_PER_UNIVERSE = "e131.pixels.per.universe";
    public static final String E131_FIRST_UNIVERSE_ID = "e131.first.universe.id";
    public static final String E131_ROW1 = "e131.layout.row1";
    public static final String E131_ROW2 = "e131.layout.row2";

    public static final String UDP_IP = "udp.ip";
    public static final String UDP_PORT = "udp.port";

    /** The Constant OUTPUT_DEVICE_RESOLUTION_X. */
    public static final String OUTPUT_DEVICE_RESOLUTION_X = "output.resolution.x";

    /** The Constant OUTPUT_DEVICE_RESOLUTION_Y. */
    public static final String OUTPUT_DEVICE_RESOLUTION_Y = "output.resolution.y";

    /** The Constant OUTPUT_DEVICE_SNAKE_CABELING */
    public static final String OUTPUT_DEVICE_SNAKE_CABELING = "output.snake.cabling";

    /** The Constant MINIDMX_BAUDRATE */
    public static final String OUTPUT_DEVICE_LAYOUT = "output.layout";

    /** The Constant MINIDMX_BAUDRATE */
    public static final String MINIDMX_BAUDRATE = "minidmx.baudrate";

    public static final String TPM2_BAUDRATE = "tpm2.baudrate";
    public static final String TPM2_DEVICE = "tpm2.device";

    /** The Constant STARTUP_IN_RANDOM_MODE. */
    public static final String STARTUP_IN_RANDOM_MODE = "startup.in.randommode";
    public static final String STARTUP_LOAD_PRESET_NR = "startup.load.preset.nr";

    /** The Constant STARTUP_IN_RANDOM_MODE. */
    public static final String SOUND_AWARE_GENERATORS = "sound.analyze.enabled";

    /** The Constant CFG_PIXEL_SIZE. */
    public static final String CFG_PIXEL_SIZE = "led.pixel.size";

    /** The Constant PIXELINVADERS_ROW1. */
    public static final String PIXELINVADERS_ROW1 = "pixelinvaders.layout.row1";

    /** The Constant PIXELINVADERS_ROW2. */
    public static final String PIXELINVADERS_ROW2 = "pixelinvaders.layout.row2";

    public static final String PIXELINVADERS_BLACKLIST = "pixelinvaders.blacklist.devices";

    public static final String PIXELINVADERS_PANEL_ORDER = "pixelinvaders.panel.order";

    public static final String PIXELINVADERS_NET_IP = "pixelinvaders.panel.ip";

    public static final String PIXELINVADERS_NET_PORT = "pixelinvaders.panel.port";

    public static final String PIXELINVADERS_IS_AN_EXPEDITINVADER = "pixelinvaders.is.a.expeditinvader";

    public static final String NET_OSC_LISTENING_PORT = "osc.listening.port";

    /** The Constant ADDITIONAL_VISUAL_SCREENS. */
    public static final String ADDITIONAL_VISUAL_SCREENS = "additional.visual.screens";

    public static final String OUTPUT_MAPPING = "output.mapping";

    public static final String SHOW_DEBUG_WINDOW = "show.debug.window";

    public static final String DEBUG_WINDOW_MAX_X_SIZE = "gui.window.maximal.width";
    public static final String DEBUG_WINDOW_MAX_Y_SIZE = "gui.window.maximal.height";

    public static final String CAPTURE_OFFSET = "screen.capture.offset";

    public static final String CAPTURE_WINDOW_SIZE_X = "screen.capture.window.size.x";
    public static final String CAPTURE_WINDOW_SIZE_Y = "screen.capture.window.size.y";

    public static final String FPS = "fps";

    public static final String TPM2NET_IP = "tpm2net.ip";
    public static final String TPM2NET_ROW1 = "tpm2net.layout.row1";
    public static final String TPM2NET_ROW2 = "tpm2net.layout.row2";

    public static final String PIXELINVADERS_COLORADJUST_R = "pixelinvaders.coloradjust.r.";
    public static final String PIXELINVADERS_COLORADJUST_G = "pixelinvaders.coloradjust.g.";
    public static final String PIXELINVADERS_COLORADJUST_B = "pixelinvaders.coloradjust.b.";

    public static final String SOUND_SILENCE_THRESHOLD = "sound.silence.threshold";

    public static final String PRESET_LOADING_FADE_TIME = "preset.loading.fade.time";
    public static final String VISUAL_FADE_TIME = "visual.fade.time";

    public static final String RESOURCE_PATH = "resource.root.path";

    public static final String REMOTE_CLIENT_FPS = "remote.client.fps";
    public static final String REMOTE_CLIENT_USE_COMPRESSION = "remote.client.use.compression";

    public static final String RPI_WS2801_SPI_SPEED = "rpiws2801.spi.speed";

    public static final String RANDOMMODE_LIFETIME = "randommode.lifetime.in.s";
}
