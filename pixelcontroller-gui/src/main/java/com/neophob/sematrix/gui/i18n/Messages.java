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
package com.neophob.sematrix.gui.i18n;

import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * load gui text files from properties file
 * @author mvogt
 *
 */
public class Messages {
	
	private static final Logger LOG = Logger.getLogger(Messages.class.getName());
	
    private static final String BUNDLE_NAME = "messages"; //$NON-NLS-1$

    private ResourceBundle bundle;    

    public Messages() {
    	try {
    		bundle = ResourceBundle.getBundle(BUNDLE_NAME);
        } catch (MissingResourceException e) {
            LOG.log(Level.SEVERE, "Failed to load resource bundle!", e);
        }
    		
    }

    public String getString(String key) {
    	if (key==null) {
    		return "";
    	}
    	
    	if (bundle==null) {
    		return key;
    	}
    	
        try {
            return bundle.getString(key);
        } catch (MissingResourceException e) {
            return '!' + key + '!';
        }
    }
}
