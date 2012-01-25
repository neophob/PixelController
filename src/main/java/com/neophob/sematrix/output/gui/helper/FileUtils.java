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
package com.neophob.sematrix.output.gui.helper;

import java.io.File;
import java.io.FilenameFilter;

/**
 * Helper Class to find some files
 * @author michu
 *
 */
public class FileUtils {

	/**
	 * get files by extension
	 * not recursive
	 * @param path
	 * @param ff
	 * @return
	 */
	private static String[] findFiles(String path, FilenameFilter ff) {
		File f = new File(path);
		if (!f.isDirectory()) {
			return null;
		}
		
		return f.list(ff);
	}
	
	/**
	 * 
	 * @param path
	 * @return
	 */
	public static String[] findBlinkenFiles(String path) {
		FilenameFilter ff = new BlinkenlightsFilter();
		return findFiles(path+"/blinken", ff);
	}
	
	/**
	 * 
	 * @param path
	 * @return
	 */
	public static String[] findImagesFiles(String path) {
		FilenameFilter ff = new ImageFilter();
		return findFiles(path+"/pics", ff);
	}
	
	/**
	 * 
	 * @author michu
	 *
	 */
	static class BlinkenlightsFilter implements FilenameFilter {		
		public boolean accept(File dir, String name) {
	        return (name.endsWith(".bml"));
	    }
	}

	/**
	 * 
	 * @author michu
	 *
	 */
	static class ImageFilter implements FilenameFilter {		
	    public boolean accept(File dir, String name) {
	        return (name.endsWith(".jpg") || name.endsWith(".gif"));
	    }
	}
	

}
