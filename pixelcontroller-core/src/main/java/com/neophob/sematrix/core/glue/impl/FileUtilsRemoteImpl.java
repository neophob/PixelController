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
