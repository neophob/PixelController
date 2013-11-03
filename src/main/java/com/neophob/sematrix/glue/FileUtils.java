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
package com.neophob.sematrix.glue;

import java.io.File;
import java.io.FilenameFilter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Helper Class to find some files
 * @author michu
 *
 */
public class FileUtils {

	private static final Logger LOG = Logger.getLogger(FileUtils.class.getName());

	private static final String DATA_DIR = File.separator+"data";

	private static final String BML_DIR = DATA_DIR+File.separator+"blinken"+File.separator;
	private static final String IMAGE_DIR = DATA_DIR+File.separator+"pics";
	
	private String rootDirectory;
	
	/**
	 * 
	 * @param rootDirectory
	 */
	public FileUtils() {
		this.rootDirectory = System.getProperty("user.dir");
	}
	
	/**
	 * get files by extension
	 * not recursive
	 * @param path
	 * @param ff
	 * @return
	 */
	private String[] findFiles(String path, FilenameFilter ff) {
		File f = new File(path);
		if (!f.isDirectory()) {
			LOG.log(Level.WARNING, f+" is not a directoy");
			return null;
		}
		
		return f.list(ff);
	}
	
	/**
	 * 
	 * @param path
	 * @return
	 */
	public String[] findBlinkenFiles() {
		FilenameFilter ff = new BlinkenlightsFilter();
		return findFiles(this.rootDirectory+BML_DIR, ff);
	}
	
	/**
	 * 
	 * @param path
	 * @return
	 */
	public String[] findImagesFiles() {
		FilenameFilter ff = new ImageFilter();
		return findFiles(this.rootDirectory+IMAGE_DIR, ff);
	}
	
	/**
	 * 
	 * @return
	 */
	public String getRootDirectory() {
		return rootDirectory;
	}
	
	
	/**
	 * @return the dataDir
	 */
	public String getDataDir() {
		return rootDirectory+DATA_DIR;
	}

	/**
	 * @return the bmlDir
	 */
	public String getBmlDir() {
		return rootDirectory+BML_DIR;
	}

	/**
	 * @return the imageDir
	 */
	public String getImageDir() {
		return rootDirectory+IMAGE_DIR;
	}


	/**
	 * 
	 * @author michu
	 *
	 */
	private class BlinkenlightsFilter implements FilenameFilter {		
		public boolean accept(File dir, String name) {
	        return (name.toLowerCase().endsWith(".bml"));
	    }
	}

	/**
	 * 
	 * @author michu
	 *
	 */
	private class ImageFilter implements FilenameFilter {		
	    public boolean accept(File dir, String name) {
	        return (name.toLowerCase().endsWith(".jpg") 
	        		|| name.toLowerCase().endsWith(".gif")
	        		|| name.toLowerCase().endsWith(".png")
	        		|| name.toLowerCase().endsWith(".jpeg"));
	    }
	}
	

}
