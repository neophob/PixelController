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

/**
 * load and save properties files
 * 
 * @author michu
 *
 */
public class PropertiesHelper {

	private static Logger log = Logger.getLogger(PropertiesHelper.class.getName());
	
	private static PropertiesHelper instance = new PropertiesHelper();

	private static final String PRESENTS_FILENAME = "data/presents.led";
	private static final String CONFIG_FILENAME = "data/config.properties";
	
	private Properties config=null;
	
	private List<Integer> i2cAddr=null;
	private List<DeviceConfig> lpdDevice=null;
	
	private int devicesInRow1 = 0;
	private int devicesInRow2 = 0;
	

	/**
	 * 
	 */
	private PropertiesHelper() {
		config = new Properties();		
		try {
			InputStream input = Collector.getInstance().getPapplet().createInput(CONFIG_FILENAME);
			config.load(input);
						
			log.log(Level.INFO, "Config loaded");
		} catch (Exception e) {
			log.log(Level.SEVERE, "Failed to load Config", e);
			throw new IllegalArgumentException("Configuration error!", e);
		}

		int rainbowduinoDevices = parseI2cAddress();
		int lpdDevices = parseLpdAddress();

		if (rainbowduinoDevices>0 && lpdDevices>0) {
			log.log(Level.SEVERE, "Multiple devices configured, illegal configuration!");
			throw new IllegalArgumentException("Multiple devices configured, illegal configuration!");
		}

		if (devicesInRow1==0 && devicesInRow2==0) {
			log.log(Level.SEVERE, "No devices configured, illegal configuration!");
			throw new IllegalArgumentException("No devices configured, illegal configuration!");
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public static PropertiesHelper getInstance() {
		return instance;
	}

	
	/**
	 * 
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
			log.log(Level.INFO,
					"Loaded {0} presents from file {1}"
					, new Object[] { count, PRESENTS_FILENAME });
		} catch (Exception e) {
			log.log(Level.WARNING,
					"Failed to load {0}, Error: {1}"
					, new Object[] { PRESENTS_FILENAME, e });
		}
	}
		
	/**
	 * 
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
			log.log(Level.INFO,
					"Presents saved as {0}"
					, new Object[] { PRESENTS_FILENAME });
		} catch (Exception e) {
			log.log(Level.WARNING,
					"Failed to save {0}, Error: {1}"
					, new Object[] { PRESENTS_FILENAME, e });
		}
	}

	/**
	 * 
	 * @return
	 */
	private int parseLpdAddress() {
		int found = 0;
		lpdDevice = new ArrayList<DeviceConfig>();
		
		String value = config.getProperty("layout.row1");
		if (StringUtils.isNotBlank(value)) {
			for (String s: value.split(",")) {
				try {
					DeviceConfig cfg = DeviceConfig.valueOf(s);
					lpdDevice.add(cfg);
					devicesInRow1++;
					found++;					
				} catch (Exception e) {
					log.log(Level.WARNING,
							"Failed to parse {0}", s);

				}
			}
		}

		value = config.getProperty("layout.row2");
		if (StringUtils.isNotBlank(value)) {
			for (String s: value.split(",")) {
				try {
					DeviceConfig cfg = DeviceConfig.valueOf(s);
					lpdDevice.add(cfg);
					devicesInRow2++;
					found++;					
				} catch (Exception e) {
					log.log(Level.WARNING,
							"Failed to parse {0}", s);

				}
			}
		}

		return found;
	}
	
	/**
	 * 
	 * @return
	 */
	private int parseI2cAddress() {
		int found = 0;

		i2cAddr = new ArrayList<Integer>();
		String rawConfig = config.getProperty("layout.row1.i2c.addr");
		if (StringUtils.isNotBlank(rawConfig)) {
			for (String s: rawConfig.split(",")) {
				i2cAddr.add( Integer.parseInt(s));
				devicesInRow1++;
				found++;
			}
		}
		rawConfig = config.getProperty("layout.row2.i2c.addr");
		if (StringUtils.isNotBlank(rawConfig)) {
			for (String s: rawConfig.split(",")) {
				i2cAddr.add( Integer.parseInt(s));
				devicesInRow2++;
				found++;
			}
		}
		
		return found;
	}

	/**
	 * 
	 * @return
	 */
	public int getNrOfScreens() {
		return devicesInRow1+devicesInRow2;
	}
	
	/**
	 * 
	 * @param key
	 * @return
	 */
	public String getProperty(String key) {
		return config.getProperty(key);
	}

	/**
	 * 
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public String getProperty(String key, String defaultValue) {
		return config.getProperty(key, defaultValue);
	}
	
	/**
	 * 
	 * @return
	 */
	public Layout getLayout() {
		if (devicesInRow2>0) {
			return new BoxLayout(devicesInRow1, devicesInRow2);
		}
	
		return new HorizontalLayout(devicesInRow1, devicesInRow2);
	}

	/**
	 * 
	 * @return i2c address for rainbowduino devices
	 */
	public List<Integer> getI2cAddr() {
		return i2cAddr;
	}

	/**
	 * 
	 * @return options to display lpd6803 displays
	 */
	public List<DeviceConfig> getLpdDevice() {
		return lpdDevice;
	}
	
	
	

}
