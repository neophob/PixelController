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
package com.neophob.sematrix.generator;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.junit.Test;

import com.neophob.sematrix.color.ColorSet;
import com.neophob.sematrix.effect.Effect;
import com.neophob.sematrix.effect.PassThru;
import com.neophob.sematrix.generator.Generator.GeneratorName;
import com.neophob.sematrix.glue.Collector;
import com.neophob.sematrix.glue.FileUtils;
import com.neophob.sematrix.glue.MatrixData;
import com.neophob.sematrix.glue.Visual;
import com.neophob.sematrix.input.ISound;
import com.neophob.sematrix.input.SoundDummy;
import com.neophob.sematrix.mixer.Mixer;
import com.neophob.sematrix.mixer.PassThruMixer;
import com.neophob.sematrix.properties.ApplicationConfigurationHelper;
import com.neophob.sematrix.resize.IResize;
import com.neophob.sematrix.resize.PixelResize;

public class GenerateAllResolutionTest {

	private FileUtils fileUtils;
	private ISound sound;
	private IResize resize;
	private ColorSet col;
	private ApplicationConfigurationHelper ph;
	private int fps = 50;
	
    @Test
    public void verifyGeneratorsDoNotCrash() {
    	final int maxResolution = 17;
    	
    	System.setProperty("java.awt.headless", "true");
    	
		String rootDir = System.getProperty("buildDirectory");
		if (rootDir == null) {
			//if unit test is run in eclipse
			rootDir = "."+File.separatorChar;
		}
    	ph = new ApplicationConfigurationHelper(new Properties());
    	fileUtils = new FileUtils();
    	sound = new SoundDummy();
    	resize = new PixelResize();
    	col = new ColorSet("test", new int[]{1,2,3});
    	
    	for (int x=1; x<maxResolution; x++) {
    		for (int y=1; y<maxResolution; y++) {
    			testWithResolution(x,y);
    			testWithResolution(y,x);
    		}
    	}

    }

    /**
     * 
     * @param x
     * @param y
     */
    private void testWithResolution(int x, int y) {
    	MatrixData matrix = new MatrixData(x,y);
    	
    	List<Visual> vlist = new ArrayList<Visual>();
    	vlist.add(createVisual(matrix, col));
    	Collector.getInstance().setAllVisuals(vlist);
    	
    	PixelControllerGenerator pcGen = new PixelControllerGenerator(ph, fileUtils, matrix, fps, sound, resize);
    	pcGen.initAll();
    	for (Generator gen: pcGen.getAllGenerators()) {
    		gen.update();
    		
    		if (gen.getId() == GeneratorName.COLOR_SCROLL.getId()) {
    			for (int i=0; i<16; i++) {
    				ColorScroll cs = (ColorScroll)gen;
    				cs.setScrollMode(i);
    				cs.update();
    			}
    		}
    	}
    	
    }
    
    /**
     * create a dummy visual
     * @param matrix
     * @param col
     * @return
     */
    private Visual createVisual(MatrixData matrix, ColorSet col) {
    	Generator g = new PassThruGen(matrix);
    	Effect e = new PassThru(matrix);
    	Mixer m = new PassThruMixer();
    	return new Visual(g,e,m,col);
    }
    
    
}
