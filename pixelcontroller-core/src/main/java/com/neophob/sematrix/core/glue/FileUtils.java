package com.neophob.sematrix.core.glue;

import java.io.Serializable;

public interface FileUtils extends Serializable{

	/**
	 * 
	 * @param path
	 * @return
	 */
	String[] findBlinkenFiles();

	/**
	 * 
	 * @param path
	 * @return
	 */
	String[] findImagesFiles();

	/**
	 * 
	 * @return
	 */
	String getRootDirectory();

	/**
	 * @return the dataDir
	 */
	String getDataDir();

	/**
	 * @return the bmlDir
	 */
	String getBmlDir();

	/**
	 * @return the imageDir
	 */
	String getImageDir();

}