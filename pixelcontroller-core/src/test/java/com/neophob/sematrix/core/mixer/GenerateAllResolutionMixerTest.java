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
package com.neophob.sematrix.core.mixer;

import org.junit.Test;

import com.neophob.sematrix.core.sound.ISound;
import com.neophob.sematrix.core.sound.SoundDummy;
import com.neophob.sematrix.core.visual.MatrixData;
import com.neophob.sematrix.core.visual.Visual;
import com.neophob.sematrix.core.visual.color.ColorSet;
import com.neophob.sematrix.core.visual.color.IColorSet;
import com.neophob.sematrix.core.visual.effect.Effect;
import com.neophob.sematrix.core.visual.effect.PassThru;
import com.neophob.sematrix.core.visual.generator.Generator;
import com.neophob.sematrix.core.visual.generator.PassThruGen;
import com.neophob.sematrix.core.visual.mixer.Checkbox;
import com.neophob.sematrix.core.visual.mixer.Mixer;
import com.neophob.sematrix.core.visual.mixer.PixelControllerMixer;

public class GenerateAllResolutionMixerTest {

	private ISound sound;
	
    @Test
    public void verifyMixersDoNotCrash() throws Exception {
    	final int maxResolution = 17;
    	sound = new SoundDummy();
    	
    	for (int x=1; x<maxResolution; x++) {
    		for (int y=1; y<maxResolution; y++) {
    			testWithResolution(x,y);
    			testWithResolution(y,x);
    		}
    	}
    }
    
    private void testWithResolution(int x, int y) {
    	MatrixData matrix = new MatrixData(x,y);
    	PixelControllerMixer pcm = new PixelControllerMixer(matrix, sound);
    	pcm.initAll();

    	Generator g = new PassThruGen(matrix);
    	Effect e = new PassThru(matrix);
    	Mixer m = new Checkbox(matrix);
    	IColorSet c = new ColorSet("test", new int[]{1,2,3});
    	Visual v = new Visual(g,e,m,c);    	

    	for (Mixer mix: pcm.getAllMixer()) {
    		//System.out.println(mix);
    		mix.getBuffer(v);
    	}
    	
    }
    	
    	

    
    
}
