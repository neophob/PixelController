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
package com.neophob.sematrix.core.glue.helper;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.neophob.sematrix.core.glue.FileUtils;
import com.neophob.sematrix.core.visual.color.ColorSet;

/**
 * helper class to initialize color palettes
 * @author michu
 *
 */
public abstract class InitHelper {

    private static final Logger LOG = Logger.getLogger(InitHelper.class.getName());

    private static final String PALETTE_CONFIG_FILENAME = "palette.properties";

    /**
     * 
     * @return
     * @throws IllegalArgumentException
     */
    public static List<ColorSet> getColorPalettes(FileUtils fu) throws IllegalArgumentException {
        //load palette
        Properties palette = new Properties();
        String filename = fu.getDataDir()+File.separator+PALETTE_CONFIG_FILENAME;
        InputStream is = null;
        try {
        	is = new FileInputStream(filename);
            palette.load(is);
            List<ColorSet> colorSets = ColorSet.loadAllEntries(palette);

            LOG.log(Level.INFO, "ColorSets loaded, {0} entries", colorSets.size());
            return colorSets;
        } catch (Exception e) {
            String error = "Failed to load the palette config file "+filename;
            LOG.log(Level.SEVERE, error, e);
            throw new IllegalArgumentException(error, e);
        } finally {
        	try {
        		if (is!=null) {
        			is.close();        	
        		}
        	} catch (Exception e) {
        		//ignored
        	}
        }

    }

}
