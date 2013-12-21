package com.neophob.sematrix.core.glue;

import java.io.Serializable;

public interface FileUtils extends Serializable{

	/**
	 * 
	 * @param path
	 * @return
	 */
	public abstract String[] findBlinkenFiles();

	/**
	 * 
	 * @param path
	 * @return
	 */
	public abstract String[] findImagesFiles();

	/**
	 * 
	 * @return
	 */
	public abstract String getRootDirectory();

	/**
	 * @return the dataDir
	 */
	public abstract String getDataDir();

	/**
	 * @return the bmlDir
	 */
	public abstract String getBmlDir();

	/**
	 * @return the imageDir
	 */
	public abstract String getImageDir();

}