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
package com.neophob.sematrix.core.glue.impl;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.neophob.sematrix.core.glue.FileUtils;

/**
 * Helper Class to find some files
 * @author michu
 *
 */
public class FileUtilsLocalImpl implements FileUtils {

	private static transient final Logger LOG = Logger.getLogger(FileUtils.class.getName());

	private static transient final String DATA_DIR = File.separator+"data";

	private static transient final String BML_DIR = DATA_DIR+File.separator+"blinken"+File.separator;
	private static transient final String IMAGE_DIR = DATA_DIR+File.separator+"pics";
	
	private String rootDirectory;
	
	/**
	 * 
	 * @param rootDirectory
	 */
	public FileUtilsLocalImpl() {
		this.rootDirectory = System.getProperty("user.dir");
		
		if (!new File(getDataDir()).isDirectory()) {						
			this.rootDirectory = System.getProperty("user.dir")+"/../pixelcontroller-distribution/src/main/resources";//
			LOG.log(Level.INFO, "Try root directory: {0}", rootDirectory);
			
			if (!new File(getDataDir()).isDirectory()) {
				LOG.log(Level.INFO, "Root directory {0} is not correct!", rootDirectory);
			}
		} else {
			LOG.log(Level.INFO, "Use root directory: {0}", rootDirectory);	
		}
				
	}

	protected FileUtilsLocalImpl(String rootDirectory) {
		this.rootDirectory = rootDirectory;
		LOG.log(Level.INFO, "Root directory: {0}", rootDirectory);
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
			return new String[0];
		}
		
		List<String> tmp = Arrays.asList(f.list(ff));
		Collections.sort(tmp, new SortIgnoreCase());
		return tmp.toArray(new String[tmp.size()]);
	}
	
	/* (non-Javadoc)
	 * @see com.neophob.sematrix.core.glue.FileUtils#findBlinkenFiles()
	 */
	@Override
	public String[] findBlinkenFiles() {
		FilenameFilter ff = new BlinkenlightsFilter();
		return findFiles(this.rootDirectory+BML_DIR, ff);
	}
	
	/* (non-Javadoc)
	 * @see com.neophob.sematrix.core.glue.FileUtils#findImagesFiles()
	 */
	@Override
	public String[] findImagesFiles() {
		FilenameFilter ff = new ImageFilter();
		return findFiles(this.rootDirectory+IMAGE_DIR, ff);
	}
	
	/* (non-Javadoc)
	 * @see com.neophob.sematrix.core.glue.FileUtils#getRootDirectory()
	 */
	@Override
	public String getRootDirectory() {
		return rootDirectory;
	}
	
	
	/* (non-Javadoc)
	 * @see com.neophob.sematrix.core.glue.FileUtils#getDataDir()
	 */
	@Override
	public String getDataDir() {
		return rootDirectory+DATA_DIR;
	}

	/* (non-Javadoc)
	 * @see com.neophob.sematrix.core.glue.FileUtils#getBmlDir()
	 */
	@Override
	public String getBmlDir() {
		return rootDirectory+BML_DIR;
	}

	/* (non-Javadoc)
	 * @see com.neophob.sematrix.core.glue.FileUtils#getImageDir()
	 */
	@Override
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
	        return name.toLowerCase().endsWith(".bml") 
	        		|| name.toLowerCase().endsWith(".bml.gz");
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
	
	/**
	 * 
	 * @author michu
	 *
	 */
	class SortIgnoreCase implements Comparator<String> {
	    public int compare(String o1, String o2) {
	        String s1 = (String) o1;
	        String s2 = (String) o2;
	        return s1.compareToIgnoreCase(s2);
	    }
	}
}
