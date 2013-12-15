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
package com.neophob.sematrix.core.resize;

import java.awt.image.BufferedImage;

import static org.junit.Assert.*;

import org.junit.Test;

import com.neophob.sematrix.core.resize.IResize;
import com.neophob.sematrix.core.resize.PixelControllerResize;
import com.neophob.sematrix.core.visual.MatrixData;
import com.neophob.sematrix.core.visual.Visual;
import com.neophob.sematrix.core.visual.color.ColorSet;
import com.neophob.sematrix.core.visual.effect.Effect;
import com.neophob.sematrix.core.visual.effect.PassThru;
import com.neophob.sematrix.core.visual.generator.Generator;
import com.neophob.sematrix.core.visual.generator.PassThruGen;
import com.neophob.sematrix.core.visual.mixer.Checkbox;
import com.neophob.sematrix.core.visual.mixer.Mixer;

public class GenerateAllResolutionResizeTest {

    @Test
    public void verifyResizersDoNotCrash() throws Exception {
    	final int maxResolution = 17;
    	
    	for (int x=1; x<maxResolution; x++) {
    		for (int y=1; y<maxResolution; y++) {
    			testWithResolution(x,y);
    			testWithResolution(y,x);
    		}
    	}
    }
    
    private void testWithResolution(int x, int y) {
    	MatrixData matrix = new MatrixData(x,y);
    	PixelControllerResize pcr = new PixelControllerResize();
    	pcr.initAll();

    	Generator g = new PassThruGen(matrix);
    	Effect e = new PassThru(matrix);
    	Mixer m = new Checkbox(matrix);
    	ColorSet c = new ColorSet("test", new int[]{1,2,3});
    	Visual v = new Visual(g,e,m,c);    	

    	for (IResize rsz: pcr.getAllResizers()) {
    		BufferedImage bi = rsz.createImage(v.getBuffer(), matrix.getBufferXSize(), matrix.getBufferYSize());
    		int[] b1 = rsz.getBuffer(bi, matrix.getDeviceXSize(), matrix.getDeviceYSize());
    		int[] b2 = rsz.resizeImage(v.getBuffer(), matrix.getBufferXSize(), matrix.getBufferYSize(), 
    				matrix.getDeviceXSize(), matrix.getDeviceYSize());
    		assertArrayEquals(b1, b2);
    	}
    	
    }
    	
    	

    
    
}
