package com.neophob.sematrix.core.api.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Observable;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.neophob.sematrix.core.api.CallbackMessageInterface;
import com.neophob.sematrix.core.api.PixelController;
import com.neophob.sematrix.core.properties.ApplicationConfigurationHelper;
import com.neophob.sematrix.core.visual.color.ColorSet;
import com.neophob.sematrix.core.visual.color.IColorSet;

/**
 * abstract class, implements observer class
 * 
 * @author michu
 * 
 */
public abstract class PixelControllerServer extends Observable implements PixelController, Runnable {

    private static final Logger LOG = Logger.getLogger(PixelControllerServer.class.getName());

    private static final String APPLICATION_CONFIG_FILENAME = "config.properties";
    private static final String PALETTE_CONFIG_FILENAME = "palette.properties";

    /**
     * 
     * @param handler
     */
    public PixelControllerServer(CallbackMessageInterface<String> handler) {
        // register the caller as observer
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
    static ApplicationConfigurationHelper loadConfiguration(String dataDir)
            throws IllegalArgumentException {
        Properties config = new Properties();
        InputStream is = null;
        String fileToLoad = dataDir + File.separator + APPLICATION_CONFIG_FILENAME;
        try {
            is = new FileInputStream(fileToLoad);
            config.load(is);
            LOG.log(Level.INFO, "Config loaded, {0} entries", config.size());
        } catch (Exception e) {
            String error = "Failed to open the configfile " + fileToLoad;
            LOG.log(Level.SEVERE, error, e);
            throw new IllegalArgumentException(error);
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (Exception e) {
                // ignored
            }
        }

        try {
            return new ApplicationConfigurationHelper(config);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Configuration Error: ", e);
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * 
     * @param dataDir
     * @return
     * @throws IllegalArgumentException
     */
    static List<IColorSet> loadColorPalettes(String dataDir) throws IllegalArgumentException {
        // load palette
        Properties palette = new Properties();
        String filename = dataDir + File.separator + PALETTE_CONFIG_FILENAME;
        InputStream is = null;
        try {
            is = new FileInputStream(filename);
            palette.load(is);
            List<IColorSet> colorSets = ColorSet.loadAllEntries(palette);

            LOG.log(Level.INFO, "ColorSets loaded, {0} entries", colorSets.size());
            return colorSets;
        } catch (Exception e) {
            String error = "Failed to load the palette config file " + filename;
            LOG.log(Level.SEVERE, error, e);
            throw new IllegalArgumentException(error, e);
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (Exception e) {
                // ignored
            }
        }

    }
}
