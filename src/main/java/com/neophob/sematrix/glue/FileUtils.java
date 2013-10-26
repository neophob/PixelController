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

/**
 * Helper Class to find some files
 * @author michu
 *
 */
public class FileUtils {

	private String rootDirectory;
	
	/**
	 * 
	 * @param rootDirectory
	 */
	public FileUtils(String rootDirectory) {
		this.rootDirectory = rootDirectory;
	}
	
	/**
	 * get files by extension
	 * not recursive
	 * @param path
	 * @param ff
	 * @return
	 */
	private String[] findFiles(String path, FilenameFilter ff) {
		File f = new File(rootDirectory+path);
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
	public String[] findBlinkenFiles() {
		FilenameFilter ff = new BlinkenlightsFilter();
		return findFiles("/data/blinken", ff);
	}
	
	/**
	 * 
	 * @param path
	 * @return
	 */
	public String[] findImagesFiles() {
		FilenameFilter ff = new ImageFilter();
		return findFiles("/data/pics", ff);
	}
	
	/**
	 * 
	 * @return
	 */
	public String getRootDirectory() {
		return rootDirectory;
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
