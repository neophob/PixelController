package com.neophob.sematrix.core.glue.impl;

import com.neophob.sematrix.core.glue.FileUtils;

public class FileUtilsRemoteImpl implements FileUtils {

	private String[] blinkenFiles;
	private String[] imageFiles;
	
	public FileUtilsRemoteImpl(String[] blinkenFiles, String[] imageFiles) {
		this.blinkenFiles = blinkenFiles;
		this.imageFiles = imageFiles;
	}

	@Override
	public String[] findBlinkenFiles() {
		return blinkenFiles;
	}

	@Override
	public String[] findImagesFiles() {
		return imageFiles;
	}

	@Override
	public String getRootDirectory() {
		return "";
	}

	@Override
	public String getDataDir() {
		return "";
	}

	@Override
	public String getBmlDir() {
		return "";
	}

	@Override
	public String getImageDir() {
		return "";
	}

}
