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

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang.StringUtils;

import com.neophob.sematrix.glue.Collector;
import com.neophob.sematrix.glue.PresentSettings;
import com.neophob.sematrix.layout.BoxLayout;
import com.neophob.sematrix.layout.HorizontalLayout;
import com.neophob.sematrix.layout.Layout;
import com.neophob.sematrix.output.OutputDeviceEnum;

/**
 * load and save properties files.
 *
 * @author michu
 */
public class PropertiesHelper {

    /** The log. */
    private static final Logger LOG = Logger.getLogger(PropertiesHelper.class.getName());

    private static final int DEFAULT_RESOLUTION = 8;
    
    /** The Constant PRESENTS_FILENAME. */
    private static final String PRESENTS_FILENAME = "data/presents.led";

    /** The Constant ERROR_MULTIPLE_DEVICES_CONFIGURATED. */
    private static final String ERROR_MULTIPLE_DEVICES_CONFIGURATED = 
    		"Multiple devices configured, illegal configuration!";

    private static final String ERROR_MULTIPLE_CABLING_METHOD_CONFIGURATED = 
    		"Multiple cabling options configured, illegal configuration!";

    private static final String ERROR_INVALID_OUTPUT_MAPPING = 
    		"Invalid output mapping entries, output.mapping != output.resolution.x*output.resolution.y";

    /** The Constant FAILED_TO_PARSE. */
    private static final String FAILED_TO_PARSE = "Failed to parse {0}";

    /** The config. */
    protected Properties config=null;

    /** The output device enum. */
    private OutputDeviceEnum outputDeviceEnum = null;

    //output specific settings
    /** The i2c addr. */
    private List<Integer> i2cAddr=null;

    /** The lpd device. */
    private List<DeviceConfig> lpdDevice=null;

    /** The color format. */
    private List<ColorFormat> colorFormat=null;

    //how many output screens are used? needed to define layouts
    /** The devices in row1. */
    private int devicesInRow1 = 0;

    /** The devices in row2. */
    private int devicesInRow2 = 0;

    /** The output device x resolution. */
    private int deviceXResolution;

    /** The output device y resolution. */
    private int deviceYResolution;

    /**
     * Instantiates a new properties helper.
     *
     * @param input the input
     */
    public PropertiesHelper(Properties config) {
        this.config = config;
        
        int rainbowduinoDevices = parseI2cAddress();
        int pixelInvadersDevices = parsePixelInvaderConfig();        
        int artnetDevices = parseArtNetDevices();
        int miniDmxDevices = parseMiniDmxDevices();
        int nullDevices = parseNullOutputAddress();
        int adalightDevices = parseAdavisionDevices();
        
        //track how many output systems are enabled
        int enabledOutputs = 0;

        //track how many ouput devices are configured
        int totalDevices = 0;

        if (rainbowduinoDevices > 0) {
            enabledOutputs++;
            totalDevices = rainbowduinoDevices;
            LOG.log(Level.INFO, "found Rainbowduino device: "+totalDevices);
            this.outputDeviceEnum = OutputDeviceEnum.RAINBOWDUINO;
        }  
        if (pixelInvadersDevices > 0) {
            enabledOutputs++;
            totalDevices = pixelInvadersDevices;
            LOG.log(Level.INFO, "found PixelInvaders device: "+totalDevices);
            this.outputDeviceEnum = OutputDeviceEnum.PIXELINVADERS;
        }
        if (artnetDevices > 0) {
            enabledOutputs++;
            totalDevices = artnetDevices;
            LOG.log(Level.INFO, "found Artnet device: "+totalDevices);
            this.outputDeviceEnum = OutputDeviceEnum.ARTNET;
        }
        if (miniDmxDevices > 0) {
            enabledOutputs++;
            totalDevices = miniDmxDevices;
            LOG.log(Level.INFO, "found miniDMX device: "+totalDevices);
            this.outputDeviceEnum = OutputDeviceEnum.MINIDMX;
        } 
        if (nullDevices > 0) {
            enabledOutputs++;
            totalDevices = nullDevices;
            LOG.log(Level.INFO, "found Null device: "+totalDevices);
            this.outputDeviceEnum = OutputDeviceEnum.NULL;
        } 
        if (adalightDevices > 0) {
            enabledOutputs++;
            totalDevices = adalightDevices;
            LOG.log(Level.INFO, "found Adalight device: "+totalDevices);
            this.outputDeviceEnum = OutputDeviceEnum.ADAVISION;
        } 


        if (enabledOutputs>1) {
            LOG.log(Level.SEVERE, ERROR_MULTIPLE_DEVICES_CONFIGURATED+": "+enabledOutputs);
            throw new IllegalArgumentException(ERROR_MULTIPLE_DEVICES_CONFIGURATED);
        }
        
        int outputMappingSize = getOutputMappingValues().length;
        if (isOutputSnakeCabeling() && outputMappingSize>0) {
            LOG.log(Level.SEVERE, ERROR_MULTIPLE_CABLING_METHOD_CONFIGURATED);
            throw new IllegalArgumentException(ERROR_MULTIPLE_CABLING_METHOD_CONFIGURATED);        	
        }

        int entries = this.deviceXResolution * this.deviceYResolution;
        if (outputMappingSize>0 && outputMappingSize!=entries) {
        	String s = " ("+entries+"!="+outputMappingSize+")";
            LOG.log(Level.SEVERE, ERROR_INVALID_OUTPUT_MAPPING+s);
            throw new IllegalArgumentException(ERROR_INVALID_OUTPUT_MAPPING+s);        	
        }

        if (enabledOutputs==0 || totalDevices==0) {
            enabledOutputs=1;
            totalDevices = 1;
            devicesInRow1 = 1;
            LOG.log(Level.INFO, "no output device defined, use NULL output");
            this.outputDeviceEnum = OutputDeviceEnum.NULL;
        }

        //add default color format RGB is nothing is configured
        int nrOfColorFormat = getColorFormatFromCfg();
        if (nrOfColorFormat==0) {
            for (int i=0; i<totalDevices; i++) {
                colorFormat.add(ColorFormat.RBG);
            }
        }
    }


    /**
     * Parses the boolean.
     *
     * @param property the property
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
     * @param property the property
     * @return the int
     */
    private int parseInt(String property, int defaultValue) {
        String rawConfig = config.getProperty(property);
        if (StringUtils.isNotBlank(rawConfig)) {
            try {
                return Integer.parseInt(rawConfig);
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
     * @param key the key
     * @return the property
     */
    public String getProperty(String key) {
        return config.getProperty(key);
    }

    /**
     * Gets the property.
     *
     * @param key the key
     * @param defaultValue the default value
     * @return the property
     */
    public String getProperty(String key, String defaultValue) {
        return config.getProperty(key, defaultValue);
    }


    /**
     * Load presents.
     */
    public void loadPresents() {
        Properties props = new Properties();
        try {
            InputStream input = Collector.getInstance().getPapplet().createInput(PRESENTS_FILENAME);
            List<PresentSettings> presents = Collector.getInstance().getPresent();
            props.load(input);
            String s;
            int count=0;
            for (int i=0; i<Collector.NR_OF_PRESENT_SLOTS; i++) {
                s=props.getProperty(""+i);
                if (StringUtils.isNotBlank(s)) {
                    presents.get(i).setPresent(s.split(";"));
                    count++;
                }
            }
            LOG.log(Level.INFO,
                    "Loaded {0} presents from file {1}"
                    , new Object[] { count, PRESENTS_FILENAME });
        } catch (Exception e) {
            LOG.log(Level.WARNING,
                    "Failed to load {0}, Error: {1}"
                    , new Object[] { PRESENTS_FILENAME, e });
        }
    }

    /**
     * Save presents.
     */
    public void savePresents() {
        Properties props = new Properties();
        List<PresentSettings> presents = Collector.getInstance().getPresent();
        int idx=0;
        for (PresentSettings p: presents) {
            props.setProperty( ""+idx, p.getSettingsAsString() );
            idx++;
        }

        try {
            OutputStream output = Collector.getInstance().getPapplet().createOutput(PRESENTS_FILENAME);
            props.store(output, "Visual Daemon presents file");
            LOG.log(Level.INFO, "Presents saved as {0}", PRESENTS_FILENAME );
        } catch (Exception e) {
            LOG.log(Level.WARNING, "Failed to save {0}, Error: {1}"
                    , new Object[] { PRESENTS_FILENAME, e });
        }
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
            this.deviceXResolution = 8;
            this.deviceYResolution = 8;
            for (String s: value.split(ConfigConstant.DELIM)) {
                try {
                    DeviceConfig cfg = DeviceConfig.valueOf(s);
                    lpdDevice.add(cfg);
                    devicesInRow1++;
                } catch (Exception e) {
                    LOG.log(Level.WARNING, FAILED_TO_PARSE, s);
                }
            }
        }

        value = config.getProperty(ConfigConstant.PIXELINVADERS_ROW2);
        if (StringUtils.isNotBlank(value)) {
            for (String s: value.split(ConfigConstant.DELIM)) {
                try {
                    DeviceConfig cfg = DeviceConfig.valueOf(s);
                    lpdDevice.add(cfg);
                    devicesInRow2++;				
                } catch (Exception e) {
                    LOG.log(Level.WARNING, FAILED_TO_PARSE, s);
                }
            }
        }

        return lpdDevice.size();
    }

    /**
     * get the size of the software emulated matrix.
     *
     * @return the size or -1 if nothing was defined
     */
    public int getLedPixelSize() {
        int ret=-1;

        String tmp = config.getProperty(ConfigConstant.CFG_PIXEL_SIZE);
        try {
            ret = Integer.parseInt(tmp);
        } catch (NumberFormatException e) {
            LOG.log(Level.WARNING, FAILED_TO_PARSE, e);
        }
        return ret;

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
            for (String s: rawConfig.split(ConfigConstant.DELIM)) {
                try {
                    ColorFormat cf = ColorFormat.valueOf(s);
                    colorFormat.add(cf);					
                } catch (Exception e) {
                    LOG.log(Level.WARNING, FAILED_TO_PARSE, s);
                }
            }			
        }

        return colorFormat.size();
    }

    /**
     * Parses the i2c address.
     *
     * @return the int
     */
    private int parseI2cAddress() {
        i2cAddr = new ArrayList<Integer>();

        String rawConfig = config.getProperty(ConfigConstant.RAINBOWDUINO_ROW1);
        if (StringUtils.isNotBlank(rawConfig)) {
            this.deviceXResolution = 8;
            this.deviceYResolution = 8;

            for (String s: rawConfig.split(ConfigConstant.DELIM)) {
                i2cAddr.add( Integer.decode(s));
                devicesInRow1++;
            }
        }
        rawConfig = config.getProperty(ConfigConstant.RAINBOWDUINO_ROW2);
        if (StringUtils.isNotBlank(rawConfig)) {
            for (String s: rawConfig.split(ConfigConstant.DELIM)) {
                i2cAddr.add( Integer.decode(s));
                devicesInRow2++;
            }
        }

        return i2cAddr.size();
    }


    /**
     * Parses the null output settings.
     *
     * @return the int
     */
    private int parseNullOutputAddress() {
        int row1=parseInt(ConfigConstant.NULLOUTPUT_ROW1);
        int row2=parseInt(ConfigConstant.NULLOUTPUT_ROW2);
        if (row1+row2>0) {
            devicesInRow1 = row1;
            devicesInRow2 = row2;
            this.deviceXResolution = 8;
            this.deviceYResolution = 8;
        }

        return row1+row2;
    }	

    /**
     * 
     * @return
     */
    private int parseAdavisionDevices() {
    	if (parseBoolean(ConfigConstant.ADAVISION_DEVICE) &&
    			parseInt(ConfigConstant.OUTPUT_DEVICE_RESOLUTION_X)>0 && 
    			parseInt(ConfigConstant.OUTPUT_DEVICE_RESOLUTION_Y)>0) {
            this.devicesInRow1=1;
            this.devicesInRow2=0;
            this.deviceXResolution = parseOutputXResolution();
            this.deviceYResolution = parseOutputYResolution();
    		return 1;
    	}
        return 0;
    }	

    /**
     * get configured artnet ip.
     *
     * @return the art net ip
     */
    public String getAdavisionSerialPort() {
        return config.getProperty(ConfigConstant.ADAVISION_SERIAL_PORT);
    }

    /**
     * get serial baudspeed for the adavision output
     *
     * @return bps for adavision device or 0 if not defined
     */
    public int getAdavisionSerialPortSpeed() {
    	return parseInt(ConfigConstant.ADAVISION_SERIAL_SPEED, 0);
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
     * @return
     */
    public int getArtNetPixelsPerUniverse() {
        return parseInt(ConfigConstant.ARTNET_PIXELS_PER_UNIVERSE, 170);
    }

    /**
     * get first arnet universe id
     * @return
     */
    public int getArtNetStartUniverseId() {
        return parseInt(ConfigConstant.ARTNET_FIRST_UNIVERSE_ID, 0);
    }

    

    /**
     * Parses the art net devices.
     *
     * @return the int
     */
    private int parseArtNetDevices() {
        //minimal ip length 1.1.1.1
        if (StringUtils.length(getArtNetIp())>6 && parseOutputXResolution()>0 && parseOutputYResolution()>0) {
            this.devicesInRow1=1;
            this.devicesInRow2=0;
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
        if (parseMiniDmxBaudRate()>100 && parseOutputXResolution()>0 && parseOutputYResolution()>0) {
            this.devicesInRow1=1;
            this.devicesInRow2=0;
            this.deviceXResolution = parseOutputXResolution();            
            this.deviceYResolution = parseOutputYResolution();
            return 1;
        }
        return 0;
    }

    /**
     * Parses the mini dmx devices x.
     *
     * @return the int
     */
    public int parseOutputXResolution() {
        return parseInt(ConfigConstant.OUTPUT_DEVICE_RESOLUTION_X, DEFAULT_RESOLUTION);
    }

    /**
     * Parses the mini dmx devices y.
     *
     * @return the int
     */
    public int parseOutputYResolution() {
        return parseInt(ConfigConstant.OUTPUT_DEVICE_RESOLUTION_Y, DEFAULT_RESOLUTION);
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
     * baudrate of the minidmx device
     * 
     * @return the int
     */
    public int parseFps() {
        return parseInt(ConfigConstant.FPS, 20);        
    }

    /**
     * x and y offset for screen capture
     * @return
     */
    public int parseScreenCaptureOffset() {
        return parseInt(ConfigConstant.CAPTURE_OFFSET);        
    }

    /**
     * the width of the capturing window
     * @return
     */
    public int parseScreenCaptureWindowSizeX() {
        return parseInt(ConfigConstant.CAPTURE_WINDOW_SIZE_X, 64);        
    }

    /**
     * the height of the capturing window
     * @return
     */
    public int parseScreenCaptureWindowSizeY() {
        return parseInt(ConfigConstant.CAPTURE_WINDOW_SIZE_Y, 64);        
    }

    /**
     * 
     * @return
     */
    public int loadPresetOnStart() {
        int val = parseInt(ConfigConstant.STARTUP_LOAD_PRESET_NR, -1);
        if (val < Collector.NR_OF_PRESENT_SLOTS) {
            return val;
        }
        return -1;
    }
    
    /**
     * Start randommode.
     *
     * @return true, if successful
     */
    public boolean startRandommode() {
        return parseBoolean(ConfigConstant.STARTUP_IN_RANDOM_MODE);
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
        return devicesInRow1+devicesInRow2;
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
        return parseInt(ConfigConstant.DEBUG_WINDOW_MAX_X_SIZE, 1024);
    }

    /**
     * Gets the layout.
     *
     * @return the layout
     */
    public Layout getLayout() {
        if (devicesInRow1>0 && devicesInRow2==0) {
            return new HorizontalLayout(devicesInRow1, devicesInRow2);
        }

        if (devicesInRow1>0 && devicesInRow2>0 && devicesInRow1==devicesInRow2) {
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
     * Gets the lpd device.
     *
     * @return options to display lpd6803 displays
     */
    public List<DeviceConfig> getLpdDevice() {
        return lpdDevice;
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

    /**
     * 
     * @return
     */
    private List<Integer> parseRgbColors(String cfg) {
    	String rawConfig = config.getProperty(cfg);
    	if (rawConfig==null) {
    		return new ArrayList<Integer>();
    	}

    	String[] tmp = rawConfig.split(",");
    	if (tmp==null || tmp.length==0) {
    		return new ArrayList<Integer>();
    	}
    	
    	List<Integer> list = new ArrayList<Integer>();
    	for (String s: tmp) {
    		try {
    			list.add( Integer.decode(s.trim()) );
    		} catch (Exception e) {
    			LOG.log(Level.WARNING, FAILED_TO_PARSE, s);
			}	
    	}
    	
    	return list;
    }

    /**
     * 
     * @return
     */
    public List<Integer> getColorScrollValues() {
    	return parseRgbColors(ConfigConstant.COLORSCROLL_RGBCOLOR);
    }

    /**
     * 
     * @return a set, so no duplicate entires are allowed
     */
    public int[] getOutputMappingValues() {    	
    	String rawConfig = config.getProperty(ConfigConstant.OUTPUT_MAPPING);
    	if (rawConfig==null) {
    		return new int[0];
    	}

    	String[] tmp = rawConfig.split(",");
    	if (tmp==null || tmp.length==0) {
    		return new int[0];
    	}
    	
    	int ofs=0;
    	int[] ret = new int[tmp.length];
    	for (String s: tmp) {
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
     * 
     * @return
     */
    public List<Integer> getColorFadeValues() {
    	return parseRgbColors(ConfigConstant.COLORFADE_RGBCOLOR);
    }

    /**
     * 
     * @return
     */
    public List<Integer> getPlasmaColorValues() {
        return parseRgbColors(ConfigConstant.PLASMA_RGBCOLOR);
    }

}
