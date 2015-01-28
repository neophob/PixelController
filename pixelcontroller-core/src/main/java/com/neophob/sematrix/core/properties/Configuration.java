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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;

import com.neophob.sematrix.core.output.OutputDeviceEnum;
import com.neophob.sematrix.core.output.gamma.GammaType;
import com.neophob.sematrix.core.output.gamma.RGBAdjust;
import com.neophob.sematrix.core.visual.layout.BoxLayout;
import com.neophob.sematrix.core.visual.layout.HorizontalLayout;
import com.neophob.sematrix.core.visual.layout.Layout;

/**
 * load and save properties files.
 * 
 * note: fields marked with transient are not included in the serialization this
 * means all options that are not relevant for the gui should be marked as
 * transient
 * 
 * @author michu
 */
public class Configuration implements Serializable {

    private static final long serialVersionUID = -742970229384663801L;

    /** The log. */
    private static final transient Logger LOG = Logger.getLogger(Configuration.class.getName());

    /** The Constant ERROR_MULTIPLE_DEVICES_CONFIGURATED. */
    private static final transient String ERROR_MULTIPLE_DEVICES_CONFIGURATED = "Multiple devices configured, illegal configuration!";

    private static final transient String ERROR_MULTIPLE_CABLING_METHOD_CONFIGURATED = "Multiple cabling options (snake cabling and custom mapping) configured, illegal configuration!";

    private static final transient String ERROR_INVALID_OUTPUT_MAPPING = "Invalid output mapping entries, output.mapping > output.resolution.x*output.resolution.y";

    /** The Constant FAILED_TO_PARSE. */
    private static final transient String FAILED_TO_PARSE = "Failed to parse {0}";

    private static final transient int MAXIMAL_PIXELS_PER_UNIVERSE = 170;

    /** The config. */
    protected Properties config = null;

    /** The output device enum. */
    private OutputDeviceEnum outputDeviceEnum = null;

    // output specific settings
    /** The i2c addr. */
    private List<Integer> i2cAddr = null;

    private List<String> rainbowduinoV3SerialDevices = null;

    /** The lpd device. */
    private List<DeviceConfig> lpdDevice = null;

    private List<DeviceConfig> tpm2netDevice = null;
    private List<DeviceConfig> artNetDevice = null;
    private List<DeviceConfig> e131Device = null;

    /** The color format. */
    private List<ColorFormat> colorFormat = null;

    /** define how the panels are arranged, used by pixelinvaders panels */
    private transient List<Integer> panelOrder = null;

    private transient List<String> pixelInvadersBlacklist;

    private transient Map<Integer, RGBAdjust> pixelInvadersCorrectionMap = new HashMap<Integer, RGBAdjust>();

    /** The devices in row1. */
    private int devicesInRow1 = 0;

    /** The devices in row2. */
    private int devicesInRow2 = 0;

    /** The output device x resolution. */
    private int deviceXResolution = 0;

    /** The output device y resolution. */
    private int deviceYResolution = 0;

    /** user selected gamma correction */
    private GammaType gammaType;

    /** user selected startup output gain */
    private int startupOutputGain = 0;

    private String pixelinvadersNetIp;
    private int pixelinvadersNetPort;

    /**
     * Instantiates a new properties helper.
     * 
     * @param input
     *            the input
     */
    public Configuration(Properties config) {
        this.config = config;

        int nullDevices = parseNullOutputAddress();
        int rainbowduinoV2Devices = parseI2cAddress();
        int rainbowduinoV3Devices = parseRainbowduinoV3Config();
        int pixelInvadersDevices = parsePixelInvaderConfig();
        int pixelInvadersNetDevices = parsePixelInvaderNetConfig();
        int artnetDevices = parseArtNetDevices();
        int e131Devices = parseE131Devices();
        int miniDmxDevices = parseMiniDmxDevices();
        int tpm2Devices = parseTpm2Devices();
        int tpm2NetDevices = parseTpm2NetDevices();
        int udpDevices = parseUdpDevices();
        int rpi2801Devices = parseRpi2801Devices();
        int opcDevices = parseOPCDevices();
        // track how many output systems are enabled
        int enabledOutputs = 0;

        // track how many ouput devices are configured
        int totalDevices = 0;

        if (rainbowduinoV2Devices > 0) {
            enabledOutputs++;
            totalDevices = rainbowduinoV2Devices;
            LOG.log(Level.INFO, "found RainbowduinoV2 device: " + totalDevices);
            this.outputDeviceEnum = OutputDeviceEnum.RAINBOWDUINO_V2;
        }
        if (rainbowduinoV3Devices > 0) {
            enabledOutputs++;
            totalDevices = rainbowduinoV3Devices;
            LOG.log(Level.INFO, "found RainbowduinoV3 device: " + totalDevices);
            this.outputDeviceEnum = OutputDeviceEnum.RAINBOWDUINO_V3;
        }
        if (pixelInvadersDevices > 0) {
            enabledOutputs++;
            totalDevices = pixelInvadersDevices;
            LOG.log(Level.INFO, "found PixelInvaders device: " + totalDevices);
            this.outputDeviceEnum = OutputDeviceEnum.PIXELINVADERS;
        }
        if (pixelInvadersNetDevices > 0) {
            enabledOutputs++;
            totalDevices = pixelInvadersNetDevices;
            LOG.log(Level.INFO, "found PixelInvaders Net device: " + totalDevices);
            this.outputDeviceEnum = OutputDeviceEnum.PIXELINVADERS_NET;
        }
        if (artnetDevices > 0) {
            enabledOutputs++;
            totalDevices = artnetDevices;
            LOG.log(Level.INFO, "found Artnet device: " + totalDevices);
            this.outputDeviceEnum = OutputDeviceEnum.ARTNET;
        }
        if (e131Devices > 0) {
            enabledOutputs++;
            totalDevices = e131Devices;
            LOG.log(Level.INFO, "found E1.31 device: " + totalDevices);
            this.outputDeviceEnum = OutputDeviceEnum.E1_31;
        }
        if (miniDmxDevices > 0) {
            enabledOutputs++;
            totalDevices = miniDmxDevices;
            LOG.log(Level.INFO, "found miniDMX device: " + totalDevices);
            this.outputDeviceEnum = OutputDeviceEnum.MINIDMX;
        }
        if (tpm2Devices > 0) {
            enabledOutputs++;
            totalDevices = tpm2Devices;
            LOG.log(Level.INFO, "found Tpm2 serial device: " + totalDevices);
            this.outputDeviceEnum = OutputDeviceEnum.TPM2;
        }
        if (tpm2NetDevices > 0) {
            enabledOutputs++;
            totalDevices = tpm2NetDevices;
            LOG.log(Level.INFO, "found Tpm2 Net device: " + totalDevices);
            this.outputDeviceEnum = OutputDeviceEnum.TPM2NET;
        }
        if (udpDevices > 0) {
            enabledOutputs++;
            totalDevices = udpDevices;
            LOG.log(Level.INFO, "found UDP device: " + totalDevices);
            this.outputDeviceEnum = OutputDeviceEnum.UDP;
        }
        if (rpi2801Devices > 0) {
            enabledOutputs++;
            totalDevices = rpi2801Devices;
            LOG.log(Level.INFO, "found RPI2801 device: " + totalDevices);
            this.outputDeviceEnum = OutputDeviceEnum.RPI_2801;
        }
        if(opcDevices > 0) {
            enabledOutputs++;
            totalDevices = opcDevices;
            LOG.log(Level.INFO, "found OPC device: " + totalDevices);
            this.outputDeviceEnum = OutputDeviceEnum.OPEN_PIXEL_CONTROL;
        }


        if (nullDevices > 0) {
            // enable null output only if configured AND no other output is
            // enabled.
            if (enabledOutputs == 0) {
                enabledOutputs++;
                totalDevices = nullDevices;
                LOG.log(Level.INFO, "found Null device: " + totalDevices);
                this.outputDeviceEnum = OutputDeviceEnum.NULL;
            } else {
                LOG.log(Level.INFO,
                        "Null device is configured - but ignored due another configured output");
            }
        }

        if (enabledOutputs > 1) {
            LOG.log(Level.SEVERE, ERROR_MULTIPLE_DEVICES_CONFIGURATED + ": " + enabledOutputs);
            throw new IllegalArgumentException(ERROR_MULTIPLE_DEVICES_CONFIGURATED);
        }

        int outputMappingSize = getOutputMappingValues().length;
        if (isOutputSnakeCabeling() && outputMappingSize > 0) {
            LOG.log(Level.SEVERE, ERROR_MULTIPLE_CABLING_METHOD_CONFIGURATED);
            throw new IllegalArgumentException(ERROR_MULTIPLE_CABLING_METHOD_CONFIGURATED);
        }

        int entries = this.deviceXResolution * this.deviceYResolution;
        if (outputMappingSize > 0 && outputMappingSize > entries) {
            String s = " (" + outputMappingSize + ">" + entries + ")";
            LOG.log(Level.SEVERE, ERROR_INVALID_OUTPUT_MAPPING + s);
            throw new IllegalArgumentException(ERROR_INVALID_OUTPUT_MAPPING + s);
        }

        // nothing was configured, use 8x8 null device as default
        if (enabledOutputs == 0 || totalDevices == 0) {
            enabledOutputs = 1;
            totalDevices = 1;
            devicesInRow1 = 1;
            this.deviceXResolution = ConfigDefault.DEFAULT_PIXELINVADERS_PANEL_RESOULTION;
            this.deviceYResolution = ConfigDefault.DEFAULT_PIXELINVADERS_PANEL_RESOULTION;
            LOG.log(Level.WARNING, "no output device defined, use NULL output");
            this.outputDeviceEnum = OutputDeviceEnum.NULL;
        }

        // add default color format RGB if nothing is configured
        int nrOfColorFormat = getColorFormatFromCfg();
        if (nrOfColorFormat < totalDevices) {
            if (nrOfColorFormat > 0) {
                LOG.log(Level.WARNING, "ColorFormat count mismatch, use RGB as default value!");
            }
            for (int i = nrOfColorFormat; i < totalDevices; i++) {
                colorFormat.add(ColorFormat.RGB);
            }
        }

        // add default order if nothing is configured
        int nrOfPanelOrder = getPanelOrderFromCfg(totalDevices);
        if (nrOfPanelOrder < totalDevices) {
            if (nrOfPanelOrder > 0) {
                LOG.log(Level.WARNING, "PixelInvaders Panel Order count mismatch, use default!");
            }
            this.panelOrder.clear();
            for (int i = 0; i < totalDevices; i++) {
                this.panelOrder.add(i);
            }
        }

        gammaType = parseGammaCorrection();

        startupOutputGain = parseStartupOutputGain();
    }

    /**
     * Parses the boolean.
     * 
     * @param property
     *            the property
     * @return true, if successful
     */
    private boolean parseBoolean(String property) {
        String rawConfig = config.getProperty(property);
        if (StringUtils.isNotBlank(rawConfig)) {
            try {
                return Boolean.parseBoolean(rawConfig);
            } catch (Exception e) {
                LOG.log(Level.WARNING, FAILED_TO_PARSE, rawConfig);
            }
        }
        return false;
    }

    /**
     * get a int value from the config file.
     * 
     * @param property
     *            the property
     * @return the int
     */
    private int parseInt(String property, int defaultValue) {
        String rawConfig = config.getProperty(property);
        if (StringUtils.isNotBlank(rawConfig)) {
            try {
                int val = Integer.parseInt(StringUtils.strip(rawConfig));
                if (val >= 0) {
                    return val;
                } else {
                    LOG.log(Level.WARNING, "Ignored negative value {0}", rawConfig);
                }
            } catch (Exception e) {
                LOG.log(Level.WARNING, FAILED_TO_PARSE, rawConfig);
            }
        }
        return defaultValue;
    }

    private float parseFloat(String property, float defaultValue) {
        String rawConfig = config.getProperty(property);
        if (StringUtils.isNotBlank(rawConfig)) {
            try {
                float val = Float.parseFloat(StringUtils.strip(rawConfig));
                if (val >= 0) {
                    return val;
                } else {
                    LOG.log(Level.WARNING, "Ignored negative value {0}", rawConfig);
                }
            } catch (Exception e) {
                LOG.log(Level.WARNING, FAILED_TO_PARSE, rawConfig);
            }
        }
        return defaultValue;
    }

    /**
     * 
     * @param property
     * @return
     */
    private int parseInt(String property) {
        return parseInt(property, 0);
    }

    /**
     * Gets the property.
     * 
     * @param key
     *            the key
     * @return the property
     */
    public String getProperty(String key) {
        return config.getProperty(key);
    }

    /**
     * Gets the property.
     * 
     * @param key
     *            the key
     * @param defaultValue
     *            the default value
     * @return the property
     */
    public String getProperty(String key, String defaultValue) {
        return config.getProperty(key, defaultValue);
    }

    /**
     * 
     * @return
     */
    public DeviceConfig getOutputDeviceLayout() {
        String value = config.getProperty(ConfigConstant.OUTPUT_DEVICE_LAYOUT);
        try {
            if (value != null) {
                return DeviceConfig.valueOf(value);
            }
        } catch (Exception e) {
            LOG.log(Level.WARNING, FAILED_TO_PARSE, value);
        }

        return DeviceConfig.NO_ROTATE;
    }

    /**
     * Parses the lpd address.
     * 
     * @return the int
     */
    private int parsePixelInvaderConfig() {
        lpdDevice = new ArrayList<DeviceConfig>();

        String value = config.getProperty(ConfigConstant.PIXELINVADERS_ROW1);
        if (StringUtils.isNotBlank(value)) {

            if (parseBoolean(ConfigConstant.PIXELINVADERS_IS_AN_EXPEDITINVADER)) {
                this.deviceXResolution = ConfigDefault.DEFAULT_EXPEDITINVADERS_PANEL_RESOULTION;
                this.deviceYResolution = ConfigDefault.DEFAULT_EXPEDITINVADERS_PANEL_RESOULTION;
            } else {
                this.deviceXResolution = ConfigDefault.DEFAULT_PIXELINVADERS_PANEL_RESOULTION;
                this.deviceYResolution = ConfigDefault.DEFAULT_PIXELINVADERS_PANEL_RESOULTION;
            }

            devicesInRow1 = 0;
            devicesInRow2 = 0;

            for (String s : value.split(ConfigConstant.DELIM)) {
                try {
                    DeviceConfig cfg = DeviceConfig.valueOf(StringUtils.strip(s));
                    lpdDevice.add(cfg);
                    devicesInRow1++;
                } catch (Exception e) {
                    LOG.log(Level.WARNING, FAILED_TO_PARSE, s);
                }
            }
        }

        value = config.getProperty(ConfigConstant.PIXELINVADERS_ROW2);
        if (StringUtils.isNotBlank(value)) {
            for (String s : value.split(ConfigConstant.DELIM)) {
                try {
                    DeviceConfig cfg = DeviceConfig.valueOf(StringUtils.strip(s));
                    lpdDevice.add(cfg);
                    devicesInRow2++;
                } catch (Exception e) {
                    LOG.log(Level.WARNING, FAILED_TO_PARSE, s);
                }
            }
        }

        String tmp = config.getProperty(ConfigConstant.PIXELINVADERS_NET_IP);
        this.pixelinvadersNetIp = StringUtils.strip(tmp);
        this.pixelinvadersNetPort = parseInt(ConfigConstant.PIXELINVADERS_NET_PORT);

        // colorcorrection, maximal 16 panels
        for (int i = 0; i < 16; i++) {
            String pixColAdjustR = config.getProperty(ConfigConstant.PIXELINVADERS_COLORADJUST_R
                    + i);
            String pixColAdjustG = config.getProperty(ConfigConstant.PIXELINVADERS_COLORADJUST_G
                    + i);
            String pixColAdjustB = config.getProperty(ConfigConstant.PIXELINVADERS_COLORADJUST_B
                    + i);

            if (pixColAdjustR != null && pixColAdjustG != null && pixColAdjustB != null) {
                RGBAdjust adj = new RGBAdjust(parseInt(ConfigConstant.PIXELINVADERS_COLORADJUST_R
                        + i), parseInt(ConfigConstant.PIXELINVADERS_COLORADJUST_G + i),
                        parseInt(ConfigConstant.PIXELINVADERS_COLORADJUST_B + i));
                LOG.log(Level.INFO, "Found PixelInvaders color correction for output " + i + ": "
                        + adj);
                pixelInvadersCorrectionMap.put(i, adj);
            }
        }

        // check if PixelController Net Device is enabled
        if (StringUtils.isNotBlank(pixelinvadersNetIp) && pixelinvadersNetPort > 0) {
            LOG.log(Level.INFO, "Found PixelInvaders Net Config " + pixelinvadersNetIp + ":"
                    + pixelinvadersNetPort);

            // TODO this is quite a hack here
            return 0;
        }

        // get blacklist devices
        String blacklist = config.getProperty(ConfigConstant.PIXELINVADERS_BLACKLIST);
        if (blacklist != null) {
            pixelInvadersBlacklist = new ArrayList<String>();
            for (String s : blacklist.split(",")) {
                pixelInvadersBlacklist.add(StringUtils.strip(s));
            }
        }

        return lpdDevice.size();
    }

    /**
     * 
     * @return
     */
    private int parsePixelInvaderNetConfig() {
        if (StringUtils.isNotBlank(pixelinvadersNetIp) && pixelinvadersNetPort > 0) {
            return lpdDevice.size();
        }
        return 0;
    }

    /**
     * get the size of the software emulated matrix.
     * 
     * @return the size or -1 if nothing was defined
     */
    public int getLedPixelSize() {

        String tmp = config.getProperty(ConfigConstant.CFG_PIXEL_SIZE, ""
                + ConfigDefault.DEFAULT_GUI_PIXELSIZE);
        try {
            return Integer.parseInt(tmp);
        } catch (NumberFormatException e) {
            LOG.log(Level.WARNING, FAILED_TO_PARSE, e);
        }
        return ConfigDefault.DEFAULT_GUI_PIXELSIZE;

    }

    /**
     * Gets the color format from cfg.
     * 
     * @return the color format from cfg
     */
    private int getColorFormatFromCfg() {
        colorFormat = new ArrayList<ColorFormat>();
        String rawConfig = config.getProperty(ConfigConstant.CFG_PANEL_COLOR_ORDER);

        if (StringUtils.isNotBlank(rawConfig)) {
            for (String s : rawConfig.split(ConfigConstant.DELIM)) {
                try {
                    ColorFormat cf = ColorFormat.valueOf(StringUtils.strip(s));
                    colorFormat.add(cf);
                } catch (Exception e) {
                    LOG.log(Level.WARNING, FAILED_TO_PARSE, s);
                }
            }
        }

        return colorFormat.size();
    }

    /**
     * 
     * @return
     */
    private int getPanelOrderFromCfg(int totalDevices) {
        panelOrder = new LinkedList<Integer>();
        String rawConfig = config.getProperty(ConfigConstant.PIXELINVADERS_PANEL_ORDER);

        if (StringUtils.isNotBlank(rawConfig)) {
            for (String s : rawConfig.split(ConfigConstant.DELIM)) {
                try {
                    Integer order = Integer.parseInt(StringUtils.strip(s));

                    // sanity check
                    if (order >= totalDevices) {
                        LOG.log(Level.WARNING, ConfigConstant.PIXELINVADERS_PANEL_ORDER
                                + ": Error parsing, " + "order value " + order
                                + " >= total panels " + totalDevices + ". Settings igored!");
                        panelOrder.clear();
                        return 0;
                    }
                    panelOrder.add(order);
                } catch (Exception e) {
                    LOG.log(Level.WARNING, FAILED_TO_PARSE,
                            ConfigConstant.PIXELINVADERS_PANEL_ORDER);
                }
            }
        }

        return panelOrder.size();
    }

    /**
     * 
     * @return
     */
    private GammaType parseGammaCorrection() {
        GammaType ret = GammaType.NONE;

        String rawConfig = config.getProperty(ConfigConstant.CFG_PANEL_GAMMA_TAB);
        if (StringUtils.isBlank(rawConfig)) {
            return ret;
        }

        try {
            ret = GammaType.valueOf(rawConfig);
        } catch (Exception e) {
            LOG.log(Level.WARNING, FAILED_TO_PARSE, ConfigConstant.CFG_PANEL_GAMMA_TAB);
        }
        return ret;
    }

    /**
     * Parses the startup global output gain
     *
     * @return int gain value from cfg or default
     */
    private int parseStartupOutputGain() {
        String tmp = config.getProperty(ConfigConstant.STARTUP_OUTPUT_GAIN, ""
                + ConfigDefault.DEFAULT_STARTUP_OUTPUT_GAIN);
        try {
            int ti = Integer.parseInt(tmp);
            if (ti < 0 || ti > 100) {
                LOG.log(Level.WARNING, ConfigConstant.STARTUP_OUTPUT_GAIN + ", invalid startup gain value: "
                    + ti);
            } else {
                return ti;
            }
        } catch (NumberFormatException e) {
            LOG.log(Level.WARNING, FAILED_TO_PARSE, e);
        }
        return ConfigDefault.DEFAULT_STARTUP_OUTPUT_GAIN;
    }

    /**
     * Parses the i2c address.
     * 
     * @return the int
     */
    private int parseI2cAddress() {
        i2cAddr = new ArrayList<Integer>();

        String rawConfig = config.getProperty(ConfigConstant.RAINBOWDUINO_V2_ROW1);
        if (StringUtils.isNotBlank(rawConfig)) {
            this.deviceXResolution = 8;
            this.deviceYResolution = 8;

            for (String s : rawConfig.split(ConfigConstant.DELIM)) {
                i2cAddr.add(Integer.decode(StringUtils.strip(s)));
                devicesInRow1++;
            }
        }
        rawConfig = config.getProperty(ConfigConstant.RAINBOWDUINO_V2_ROW2);
        if (StringUtils.isNotBlank(rawConfig)) {
            for (String s : rawConfig.split(ConfigConstant.DELIM)) {
                i2cAddr.add(Integer.decode(StringUtils.strip(s)));
                devicesInRow2++;
            }
        }

        return i2cAddr.size();
    }

    /**
     * 
     * @return
     */
    private int parseRainbowduinoV3Config() {
        this.rainbowduinoV3SerialDevices = new ArrayList<String>();
        String row1String = this.config.getProperty(ConfigConstant.RAINBOWDUINO_V3_ROW1);
        if (StringUtils.isNotBlank(row1String)) {
            this.deviceXResolution = 8;
            this.deviceYResolution = 8;
            for (String string : row1String.split(ConfigConstant.DELIM)) {
                this.rainbowduinoV3SerialDevices.add(StringUtils.strip(string));
                this.devicesInRow1++;
            }
        }
        String row2String = this.config.getProperty(ConfigConstant.RAINBOWDUINO_V3_ROW2);
        if (StringUtils.isNotBlank(row2String)) {
            for (String string : row2String.split(ConfigConstant.DELIM)) {
                this.rainbowduinoV3SerialDevices.add(StringUtils.strip(string));
                this.devicesInRow2++;
            }
        }
        return this.rainbowduinoV3SerialDevices.size();
    }

    /**
     * Parses the null output settings.
     * 
     * @return the int
     */
    private int parseNullOutputAddress() {
        int row1 = parseInt(ConfigConstant.NULLOUTPUT_ROW1);
        int row2 = parseInt(ConfigConstant.NULLOUTPUT_ROW2);
        if (row1 + row2 > 0) {
            devicesInRow1 = row1;
            devicesInRow2 = row2;

            // check for a user specific output size
            this.deviceXResolution = parseOutputXResolution();
            this.deviceYResolution = parseOutputYResolution();

            // fallback
            if (deviceXResolution < 1 || deviceYResolution < 1) {
                this.deviceXResolution = 8;
                this.deviceYResolution = 8;
            }
        }

        return row1 + row2;
    }

    /**
     * get configured udp ip.
     * 
     * @return the udp ip
     */
    public String getUdpIp() {
        return config.getProperty(ConfigConstant.UDP_IP);
    }

    /**
     * get configured udp port.
     * 
     * @return the udp port
     */
    public int getUdpPort() {
        return parseInt(ConfigConstant.UDP_PORT, ConfigDefault.DEFAULT_UDP_PORT);
    }

      /**
     * get configured OPC ip.
     * 
     * @return the tcp ip
     */
    public String getOpcIp() {
        return config.getProperty(ConfigConstant.OPC_IP);
    }

    /**
     * get configured OPC port.
     * 
     * @return the tcp port
     */
    public int getOpcPort() {
        return parseInt(ConfigConstant.OPC_PORT, ConfigDefault.DEFAULT_OPC_PORT);
    }

    /**
     * get configured e131 ip.
     * 
     * @return the e131 controller ip
     */
    public String getE131Ip() {
        return config.getProperty(ConfigConstant.E131_IP);
    }

    /**
     * how many pixels (=3 Channels) per DMX universe
     * 
     * @return
     */
    public int getE131PixelsPerUniverse() {
        int ppU = parseInt(ConfigConstant.E131_PIXELS_PER_UNIVERSE, MAXIMAL_PIXELS_PER_UNIVERSE);
        if (ppU > MAXIMAL_PIXELS_PER_UNIVERSE) {
            LOG.log(Level.WARNING, "Invalid configuration found, "
                    + ConfigConstant.E131_PIXELS_PER_UNIVERSE + "=" + ppU + ". Maximal value is "
                    + MAXIMAL_PIXELS_PER_UNIVERSE + ", I fixed that for you.");
            ppU = MAXIMAL_PIXELS_PER_UNIVERSE;
        }
        return ppU;
    }

    /**
     * get first arnet universe id
     * 
     * @return
     */
    public int getE131StartUniverseId() {
        return parseInt(ConfigConstant.E131_FIRST_UNIVERSE_ID, 0);
    }

    public int getRandomModeLifetime() {
        return parseInt(ConfigConstant.RANDOMMODE_LIFETIME, 0);
    }

    /**
     * get configured artnet ip.
     * 
     * @return the art net ip
     */
    public String getArtNetIp() {
        return config.getProperty(ConfigConstant.ARTNET_IP);
    }

    /**
     * how many pixels (=3 Channels) per DMX universe
     * 
     * @return
     */
    public int getArtNetPixelsPerUniverse() {
        int ppU = parseInt(ConfigConstant.ARTNET_PIXELS_PER_UNIVERSE, MAXIMAL_PIXELS_PER_UNIVERSE);
        if (ppU > MAXIMAL_PIXELS_PER_UNIVERSE) {
            LOG.log(Level.WARNING, "Invalid configuration found, "
                    + ConfigConstant.E131_PIXELS_PER_UNIVERSE + "=" + ppU + ". Maximal value is "
                    + MAXIMAL_PIXELS_PER_UNIVERSE + ", I fixed that for you.");
            ppU = MAXIMAL_PIXELS_PER_UNIVERSE;
        }
        return ppU;
    }

    /**
     * get first arnet universe id
     * 
     * @return
     */
    public int getArtNetStartUniverseId() {
        return parseInt(ConfigConstant.ARTNET_FIRST_UNIVERSE_ID, 0);
    }

    /**
     * 
     * @return
     */
    public String getArtNetBroadcastAddr() {
        return config.getProperty(ConfigConstant.ARTNET_BROADCAST_ADDR, "");
    }

    /**
     * Parses the art net devices.
     * 
     * @return the int
     */
    private int parseArtNetDevices() {
        artNetDevice = new ArrayList<DeviceConfig>();

        // minimal ip length 1.1.1.1
        if (StringUtils.length(getArtNetIp()) > 6 && parseOutputXResolution() > 0
                && parseOutputYResolution() > 0) {
            this.deviceXResolution = parseOutputXResolution();
            this.deviceYResolution = parseOutputYResolution();

            String value = config.getProperty(ConfigConstant.ARTNET_ROW1);
            if (StringUtils.isNotBlank(value)) {

                devicesInRow1 = 0;
                devicesInRow2 = 0;

                for (String s : value.split(ConfigConstant.DELIM)) {
                    try {
                        DeviceConfig cfg = DeviceConfig.valueOf(StringUtils.strip(s));
                        artNetDevice.add(cfg);
                        devicesInRow1++;
                    } catch (Exception e) {
                        LOG.log(Level.WARNING, FAILED_TO_PARSE, s);
                    }
                }
            }

            value = config.getProperty(ConfigConstant.ARTNET_ROW2);
            if (StringUtils.isNotBlank(value)) {
                for (String s : value.split(ConfigConstant.DELIM)) {
                    try {
                        DeviceConfig cfg = DeviceConfig.valueOf(StringUtils.strip(s));
                        artNetDevice.add(cfg);
                        devicesInRow2++;
                    } catch (Exception e) {
                        LOG.log(Level.WARNING, FAILED_TO_PARSE, s);
                    }
                }
            }
        }

        return artNetDevice.size();
    }

    /**
     * 
     * @return
     */
    public List<DeviceConfig> getArtNetDevice() {
        return artNetDevice;
    }

    /**
     * Parses the e131 devices.
     * 
     * @return the int
     */
    private int parseE131Devices() {
        e131Device = new ArrayList<DeviceConfig>();

        if (StringUtils.length(getE131Ip()) > 6 && parseOutputXResolution() > 0
                && parseOutputYResolution() > 0) {

            this.deviceXResolution = parseOutputXResolution();
            this.deviceYResolution = parseOutputYResolution();

            String value = config.getProperty(ConfigConstant.E131_ROW1);
            if (StringUtils.isNotBlank(value)) {

                devicesInRow1 = 0;
                devicesInRow2 = 0;

                for (String s : value.split(ConfigConstant.DELIM)) {
                    try {
                        DeviceConfig cfg = DeviceConfig.valueOf(StringUtils.strip(s));
                        e131Device.add(cfg);
                        devicesInRow1++;
                    } catch (Exception e) {
                        LOG.log(Level.WARNING, FAILED_TO_PARSE, s);
                    }
                }
            }

            value = config.getProperty(ConfigConstant.E131_ROW2);
            if (StringUtils.isNotBlank(value)) {
                for (String s : value.split(ConfigConstant.DELIM)) {
                    try {
                        DeviceConfig cfg = DeviceConfig.valueOf(StringUtils.strip(s));
                        e131Device.add(cfg);
                        devicesInRow2++;
                    } catch (Exception e) {
                        LOG.log(Level.WARNING, FAILED_TO_PARSE, s);
                    }
                }
            }
        }

        return e131Device.size();
    }

    /**
     * 
     * @return
     */
    public List<DeviceConfig> getE131Device() {
        return e131Device;
    }

    /**
     * 
     * @return
     */
    private int parseUdpDevices() {
        if (StringUtils.length(getUdpIp()) > 6 && parseOutputXResolution() > 0
                && parseOutputYResolution() > 0) {
            this.devicesInRow1 = 1;
            this.devicesInRow2 = 0;
            this.deviceXResolution = parseOutputXResolution();
            this.deviceYResolution = parseOutputYResolution();
            return 1;
        }

        return 0;
    }

    public int parseRpi2801Devices() {
        if (getRpiWs2801SpiSpeed() > 1000 && parseOutputXResolution() > 0
                && parseOutputYResolution() > 0) {
            this.devicesInRow1 = 1;
            this.devicesInRow2 = 0;
            this.deviceXResolution = parseOutputXResolution();
            this.deviceYResolution = parseOutputYResolution();
            return 1;
        }

        return 0;
    }

    public int parseOPCDevices() {
        if (StringUtils.length(getOpcIp()) > 6 && parseOutputXResolution() > 0
                && parseOutputYResolution() > 0) {
            this.devicesInRow1 = 1;
            this.devicesInRow2 = 0;
            this.deviceXResolution = parseOutputXResolution();
            this.deviceYResolution = parseOutputYResolution();
            return 1;
        }

        return 0;
    }

    /**
     * Parses the mini dmx devices.
     * 
     * @return the int
     */
    private int parseMiniDmxDevices() {
        if (parseMiniDmxBaudRate() > 100 && parseOutputXResolution() > 0
                && parseOutputYResolution() > 0) {
            this.devicesInRow1 = 1;
            this.devicesInRow2 = 0;
            this.deviceXResolution = parseOutputXResolution();
            this.deviceYResolution = parseOutputYResolution();
            return 1;
        }
        return 0;
    }

    /**
     * Parses tpm2 devices
     * 
     * @return
     */
    private int parseTpm2Devices() {
        if (StringUtils.isNotBlank(getTpm2Device()) && parseOutputXResolution() > 0
                && parseOutputYResolution() > 0) {
            this.devicesInRow1 = 1;
            this.devicesInRow2 = 0;
            this.deviceXResolution = parseOutputXResolution();
            this.deviceYResolution = parseOutputYResolution();
            return 1;
        }
        return 0;
    }

    /**
     * Parses tpm2net devices
     * 
     * @return
     */
    private int parseTpm2NetDevices() {
        tpm2netDevice = new ArrayList<DeviceConfig>();

        if (StringUtils.isNotBlank(getTpm2NetIpAddress()) && parseOutputXResolution() > 0
                && parseOutputYResolution() > 0) {
            this.deviceXResolution = parseOutputXResolution();
            this.deviceYResolution = parseOutputYResolution();

            String value = config.getProperty(ConfigConstant.TPM2NET_ROW1);
            if (StringUtils.isNotBlank(value)) {

                devicesInRow1 = 0;
                devicesInRow2 = 0;

                for (String s : value.split(ConfigConstant.DELIM)) {
                    try {
                        DeviceConfig cfg = DeviceConfig.valueOf(StringUtils.strip(s));
                        tpm2netDevice.add(cfg);
                        devicesInRow1++;
                    } catch (Exception e) {
                        LOG.log(Level.WARNING, FAILED_TO_PARSE, s);
                    }
                }
            }

            value = config.getProperty(ConfigConstant.TPM2NET_ROW2);
            if (StringUtils.isNotBlank(value)) {
                for (String s : value.split(ConfigConstant.DELIM)) {
                    try {
                        DeviceConfig cfg = DeviceConfig.valueOf(StringUtils.strip(s));
                        tpm2netDevice.add(cfg);
                        devicesInRow2++;
                    } catch (Exception e) {
                        LOG.log(Level.WARNING, FAILED_TO_PARSE, s);
                    }
                }
            }
        }
        return tpm2netDevice.size();
    }

    public int getRpiWs2801SpiSpeed() {
        return parseInt(ConfigConstant.RPI_WS2801_SPI_SPEED, 0);
    }

    /**
     * Parses the mini dmx devices x.
     * 
     * @return the int
     */
    public int parseOutputXResolution() {
        return parseInt(ConfigConstant.OUTPUT_DEVICE_RESOLUTION_X, ConfigDefault.DEFAULT_RESOLUTION);
    }

    /**
     * Parses the mini dmx devices y.
     * 
     * @return the int
     */
    public int parseOutputYResolution() {
        return parseInt(ConfigConstant.OUTPUT_DEVICE_RESOLUTION_Y, ConfigDefault.DEFAULT_RESOLUTION);
    }

    /**
     * 
     * @return
     */
    public boolean isOutputSnakeCabeling() {
        return parseBoolean(ConfigConstant.OUTPUT_DEVICE_SNAKE_CABELING);
    }

    /**
     * baudrate of the minidmx device
     * 
     * @return the int
     */
    public int parseMiniDmxBaudRate() {
        return parseInt(ConfigConstant.MINIDMX_BAUDRATE);
    }

    /**
     * 
     * @return
     */
    public int parseTpm2BaudRate() {
        return parseInt(ConfigConstant.TPM2_BAUDRATE);
    }

    /**
     * 
     * @return
     */
    public String getTpm2Device() {
        return config.getProperty(ConfigConstant.TPM2_DEVICE);
    }

    /**
     * overwrite root path of resources
     * 
     * @return
     */
    public String getResourcePath() {
        return config.getProperty(ConfigConstant.RESOURCE_PATH);
    }

    /**
     * baudrate of the minidmx device
     * 
     * @return the int
     */
    public float parseFps() {
        return parseFloat(ConfigConstant.FPS, ConfigDefault.DEFAULT_FPS);
    }

    public float parseRemoteFps() {
        return parseFloat(ConfigConstant.REMOTE_CLIENT_FPS, ConfigDefault.DEFAULT_REMOTE_CLIENT_FPS);
    }

    public boolean parseRemoteConnectionUseCompression() {
        return parseBoolean(ConfigConstant.REMOTE_CLIENT_USE_COMPRESSION);
    }

    /**
     * x and y offset for screen capture
     * 
     * @return
     */
    public int parseScreenCaptureOffset() {
        return parseInt(ConfigConstant.CAPTURE_OFFSET);
    }

    /**
     * the width of the capturing window
     * 
     * @return
     */
    public int parseScreenCaptureWindowSizeX() {
        return parseInt(ConfigConstant.CAPTURE_WINDOW_SIZE_X,
                ConfigDefault.DEFAULT_CAPTURE_WINDOW_SIZE_X);
    }

    /**
     * the height of the capturing window
     * 
     * @return
     */
    public int parseScreenCaptureWindowSizeY() {
        return parseInt(ConfigConstant.CAPTURE_WINDOW_SIZE_Y,
                ConfigDefault.DEFAULT_CAPTURE_WINDOW_SIZE_Y);
    }

    /**
     * 
     * @return
     */
    public int loadPresetOnStart() {
        return parseInt(ConfigConstant.STARTUP_LOAD_PRESET_NR,
                ConfigDefault.DEFAULT_STARTUP_LOAD_PRESET_NR);
    }

    /**
     * Start randommode.
     * 
     * @return true, if successful
     */
    public boolean startRandommode() {
        return parseBoolean(ConfigConstant.STARTUP_IN_RANDOM_MODE);
    }

    public boolean startRandomPresetmode() {
        return parseBoolean(ConfigConstant.STARTUP_IN_RANDOM_PRESET_MODE);
    }

    /**
     * Start randommode.
     * 
     * @return true, if successful
     */
    public boolean isAudioAware() {
        return parseBoolean(ConfigConstant.SOUND_AWARE_GENERATORS);
    }

    /**
     * Gets the nr of screens.
     * 
     * @return the nr of screens
     */
    public int getNrOfScreens() {
        return devicesInRow1 + devicesInRow2;
    }

    /**
     * Parses the mini dmx devices y.
     * 
     * @return the int
     */
    public int getNrOfAdditionalVisuals() {
        return parseInt(ConfigConstant.ADDITIONAL_VISUAL_SCREENS, 0);
    }

    /**
     * 
     * @return
     */
    public int getDebugWindowMaximalXSize() {
        return parseInt(ConfigConstant.DEBUG_WINDOW_MAX_X_SIZE,
                ConfigDefault.DEFAULT_GUI_WINDOW_MAX_X_SIZE);
    }

    /**
     * 
     * @return
     */
    public int getDebugWindowMaximalYSize() {
        return parseInt(ConfigConstant.DEBUG_WINDOW_MAX_Y_SIZE,
                ConfigDefault.DEFAULT_GUI_WINDOW_MAX_Y_SIZE);
    }

    /**
     * Gets the layout.
     * 
     * @return the layout
     */
    public Layout getLayout() {
        if (devicesInRow1 > 0 && devicesInRow2 == 0) {
            return new HorizontalLayout(devicesInRow1);
        }

        if (devicesInRow1 > 0 && devicesInRow2 > 0 && devicesInRow1 == devicesInRow2) {
            return new BoxLayout(devicesInRow1, devicesInRow2);
        }

        throw new IllegalStateException("Illegal device configuration detected!");
    }

    /**
     * Gets the i2c addr.
     * 
     * @return i2c address for rainbowduino devices
     */
    public List<Integer> getI2cAddr() {
        return i2cAddr;
    }

    /**
     * 
     * @return
     */
    public List<String> getRainbowduinoV3SerialDevices() {
        return this.rainbowduinoV3SerialDevices;
    }

    /**
     * Gets the lpd device.
     * 
     * @return options to display lpd6803 displays
     */
    public List<DeviceConfig> getLpdDevice() {
        return lpdDevice;
    }

    /**
     * Gets the tpm2net device.
     * 
     * @return options to display Stealth displays
     */
    public List<DeviceConfig> getTpm2NetDevice() {
        return tpm2netDevice;
    }

    /**
     * Gets the color format.
     * 
     * @return the color format
     */
    public List<ColorFormat> getColorFormat() {
        return colorFormat;
    }

    /**
     * 
     * @param nrOfPanels
     * @return
     */
    public List<Integer> getPanelOrder() {
        return panelOrder;
    }

    /**
     * Gets the output device.
     * 
     * @return the configured output device
     */
    public OutputDeviceEnum getOutputDevice() {
        return this.outputDeviceEnum;
    }

    /**
     * Gets the device x resolution.
     * 
     * @return the device x resolution
     */
    public int getDeviceXResolution() {
        return deviceXResolution;
    }

    /**
     * Gets the device y resolution.
     * 
     * @return the device y resolution
     */
    public int getDeviceYResolution() {
        return deviceYResolution;
    }

    public int[] getOutputMappingValues() {
        String rawConfig = config.getProperty(ConfigConstant.OUTPUT_MAPPING);
        if (rawConfig == null) {
            return new int[0];
        }

        String[] tmp = rawConfig.split(",");
        if (tmp == null || tmp.length == 0) {
            return new int[0];
        }

        int ofs = 0;
        int[] ret = new int[tmp.length];
        for (String s : tmp) {
            try {
                ret[ofs] = Integer.decode(s.trim());
                ofs++;
            } catch (Exception e) {
                LOG.log(Level.WARNING, FAILED_TO_PARSE, s);
            }
        }
        return ret;
    }

    /**
     * @return the pixelInvadersBlacklist
     */
    public List<String> getPixelInvadersBlacklist() {
        return pixelInvadersBlacklist;
    }

    /**
     * get color adjust for one or multiple panels
     * 
     * @return
     */
    public Map<Integer, RGBAdjust> getPixelInvadersCorrectionMap() {
        return pixelInvadersCorrectionMap;
    }

    /**
     * get configured tpm2net ip.
     * 
     * @return the tpm2net ip
     */
    public String getTpm2NetIpAddress() {
        return config.getProperty(ConfigConstant.TPM2NET_IP);
    }

    /**
     * 
     * @return
     */
    public String getPixelinvadersNetIp() {
        return pixelinvadersNetIp;
    }

    /**
     * 
     * @return
     */
    public int getPixelinvadersNetPort() {
        return pixelinvadersNetPort;
    }

    /**
     * return user selected gamma correction
     * 
     * @return
     */
    public GammaType getGammaType() {
        return gammaType;
    }

    /**
     * return user selected startup output gain
     * 
     * @return
     */
    public int getStartupOutputGain() {
        return startupOutputGain;
    }

    /**
     * 
     * @return
     */
    public float getSoundSilenceThreshold() {
        String s = StringUtils.trim(config.getProperty(ConfigConstant.SOUND_SILENCE_THRESHOLD));
        if (StringUtils.isNotBlank(s)) {
            try {
                float f = Float.parseFloat(s);
                if (f >= 0.0f && f <= 1.0f) {
                    return f;
                }
            } catch (Exception e) {
                LOG.log(Level.WARNING, FAILED_TO_PARSE, s);
            }
        }
        return ConfigDefault.DEFAULT_SOUND_THRESHOLD;
    }

    /**
     * 
     * @return
     */
    public int getPresetLoadingFadeTime() {
        return parseInt(ConfigConstant.PRESET_LOADING_FADE_TIME,
                ConfigDefault.DEFAULT_PRESET_LOADING_FADE_TIME);
    }

    /**
     * 
     * @return
     */
    public int getVisualFadeTime() {
        return parseInt(ConfigConstant.VISUAL_FADE_TIME, ConfigDefault.DEFAULT_VISUAL_FADE_TIME);
    }

    public int getOscListeningPort() {
        return parseInt(ConfigConstant.NET_OSC_LISTENING_PORT,
                ConfigDefault.DEFAULT_NET_OSC_LISTENING_PORT);
    }

}
