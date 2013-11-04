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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

/**
 * test internal buffer size
 * 
 * @author michu
 *
 */
public class FileUtilsTest {
    
    @Test
    public void findFilesTest() throws Exception {
    	FileUtils fu = new FileUtils();
    	
    	String[] bmlFiles = fu.findBlinkenFiles();
    	assertNotNull(bmlFiles);
    	assertTrue(bmlFiles.length>2);

    	String[] imgFiles = fu.findImagesFiles();
    	assertNotNull(imgFiles);
    	assertTrue(imgFiles.length>2);
    	
    	assertFalse(fu.getRootDirectory().isEmpty());

    	List<PresetSettings> presets = fu.loadPresents(128);
    	assertNotNull(presets);
    	assertTrue(presets.size()>2);
    	
    	fu.savePresents(presets);
    	
    }


}
