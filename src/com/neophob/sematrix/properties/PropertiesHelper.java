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

/**
 * load and save properties files
 * 
 * @author michu
 *
 */
public class PropertiesHelper {

	private static final String PRESENTS_FILENAME = "data/presents.led";
	private static final String CONFIG_FILENAME = "data/config.properties";
	
	private static Properties config=null;
	
	static Logger log = Logger.getLogger(PropertiesHelper.class.getName());
	
	private PropertiesHelper() {
		//no instance
	}

	/**
	 * load config file
	 * @return
	 */
	public static Properties loadConfig() {
		//cache config
		if (config!=null) {
			return config;
		}
		
		config = new Properties();		
		try {
			InputStream input = Collector.getInstance().getPapplet().createInput(CONFIG_FILENAME);
			config.load(input);
			log.log(Level.INFO, "Config loaded");
		} catch (Exception e) {
			log.log(Level.WARNING,
					"Failed to load Config", e );
		}
		
		return config;
	}
	
	/**
	 * 
	 */
	public static void loadPresents() {
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
					"Presents loaded {0} presents from file {1}"
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
	public static void savePresents() {
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
	public static List<Integer> getAllI2cAddress() {
		loadConfig();
		List<Integer> adr = new ArrayList<Integer>();
		String rawConfig = config.getProperty("layout.row1.i2c.addr");
		if (StringUtils.isNotBlank(rawConfig)) {
			for (String s: rawConfig.split(",")) {
				adr.add( Integer.parseInt(s));
			}
		}
		rawConfig = config.getProperty("layout.row2.i2c.addr");
		if (StringUtils.isNotBlank(rawConfig)) {
			for (String s: rawConfig.split(",")) {
				adr.add( Integer.parseInt(s));
			}
		}
		return adr;
	}

}
