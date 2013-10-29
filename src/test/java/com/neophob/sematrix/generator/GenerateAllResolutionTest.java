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

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.junit.Test;

import com.neophob.sematrix.color.ColorSet;
import com.neophob.sematrix.effect.Effect;
import com.neophob.sematrix.effect.PassThru;
import com.neophob.sematrix.glue.Collector;
import com.neophob.sematrix.glue.FileUtils;
import com.neophob.sematrix.glue.MatrixData;
import com.neophob.sematrix.glue.Visual;
import com.neophob.sematrix.input.SeSound;
import com.neophob.sematrix.input.SoundDummy;
import com.neophob.sematrix.mixer.Mixer;
import com.neophob.sematrix.mixer.PassThruMixer;
import com.neophob.sematrix.properties.ApplicationConfigurationHelper;
import com.neophob.sematrix.resize.IResize;
import com.neophob.sematrix.resize.PixelResize;

public class GenerateAllResolutionTest {

	private FileUtils fileUtils;
	private SeSound sound;
	private IResize resize;
	private ColorSet col;
	private ApplicationConfigurationHelper ph;
	private int fps = 50;
	
    @Test
    public void verifyGeneratorsDoNotCrash() {
    	final int maxResolution = 17;
    	
		String rootDir = System.getProperty("buildDirectory");
		if (rootDir == null) {
			//TODO fixme
			rootDir = "/Users/michu/_code/workspace/PixelController.github/PixelController/";
		}
    	ph = new ApplicationConfigurationHelper(new Properties());
    	fileUtils = new FileUtils(rootDir);
    	sound = new SoundDummy();
    	resize = new PixelResize();
    	col = new ColorSet("test", new int[]{1,2,3});
    	
    	int i=0;
    	for (int x=1; x<maxResolution; x++) {
    		for (int y=1; y<maxResolution; y++) {
    			System.out.println(i+" test: "+x+"/"+y);
    			testWithResolution(x,y);
    			testWithResolution(y,x);
        		i++;    	
    		}
    	}

    }

    
    private void testWithResolution(int x, int y) {
    	MatrixData matrix = new MatrixData(x,y);
    	
    	List<Visual> vlist = new ArrayList<Visual>();
    	vlist.add(createVisual(matrix, col));
    	Collector.getInstance().setAllVisuals(vlist);
    	
    	PixelControllerGenerator pcGen = new PixelControllerGenerator(ph, fileUtils, matrix, fps, sound, resize);
    	pcGen.initAll();
    	for (Generator gen: pcGen.getAllGenerators()) {
    		//System.out.println(gen);
    		gen.update();
    	}
    	
    }
    
    
    private Visual createVisual(MatrixData matrix, ColorSet col) {
    	Generator g = new PassThruGen(matrix);
    	Effect e = new PassThru(matrix);
    	Mixer m = new PassThruMixer();
    	return new Visual(g,e,m,col);
    }
    
    
}
