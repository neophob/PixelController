package com.neophob.sematrix.core.api.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Observable;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.neophob.sematrix.core.api.CallbackMessageInterface;
import com.neophob.sematrix.core.api.PixelController;
import com.neophob.sematrix.core.glue.FileUtils;
import com.neophob.sematrix.core.properties.ApplicationConfigurationHelper;

/**
 * abstract class, implements observer class
 * 
 * @author michu
 *
 */
public abstract class PixelControllerServer extends Observable implements PixelController, Runnable {

	private static final Logger LOG = Logger.getLogger(PixelControllerServer.class.getName());

	private static final String APPLICATION_CONFIG_FILENAME = "config.properties";

	/**
	 * 
	 * @param handler
	 */
	public PixelControllerServer(CallbackMessageInterface<String> handler) {
		//register the caller as observer
		addObserver(handler);		
	}

	protected synchronized void clientNotification(final String msg) {
		setChanged();
        notifyObservers(msg);
	}
	
	/**
	 * load and parse configuration file
	 * 
	 * @param papplet
	 * @return
	 * @throws IllegalArgumentException
	 */
	static ApplicationConfigurationHelper loadConfiguration(FileUtils fileUtils) throws IllegalArgumentException {
		Properties config = new Properties();
		InputStream is = null;
		String fileToLoad = fileUtils.getDataDir()+File.separator+APPLICATION_CONFIG_FILENAME;
		try {
			is = new FileInputStream(fileToLoad);
			config.load(is);            
			LOG.log(Level.INFO, "Config loaded, {0} entries", config.size());
		} catch (Exception e) {
			String error = "Failed to open the configfile "+fileToLoad;
			LOG.log(Level.SEVERE, error, e);
			throw new IllegalArgumentException(error);
		} finally {
			try {
				if (is!=null) {
					is.close();        	
				}
			} catch (Exception e) {
				//ignored
			}
		}

		try {
			return new ApplicationConfigurationHelper(config);
		} catch (Exception e) {
			LOG.log(Level.SEVERE, "Configuration Error: ", e);
			throw new IllegalArgumentException(e);
		}
	}
}
