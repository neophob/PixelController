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
package com.neophob.sematrix.mixer;

import org.junit.Test;

import com.neophob.sematrix.color.ColorSet;
import com.neophob.sematrix.effect.Effect;
import com.neophob.sematrix.effect.PassThru;
import com.neophob.sematrix.generator.Generator;
import com.neophob.sematrix.generator.PassThruGen;
import com.neophob.sematrix.glue.MatrixData;
import com.neophob.sematrix.glue.Visual;

public class CheckboxTest {

    @Test
    public void mixTest() throws Exception {
    	
    	for (int x=2; x<50; x++) {
    		for (int y=2; y<50; y++) {
    	    	MatrixData matrix = new MatrixData(x,y);
    	    	Generator g = new PassThruGen(matrix);
    	    	Effect e = new PassThru(matrix);
    	    	Mixer m = new Checkbox(matrix);
    	    	ColorSet c = new ColorSet("test", new int[]{1,2,3});
    	    	Visual v = new Visual(g,e,m,c);    	
    	    	
    	    	m.getBuffer(v);    			
    		}
    	}

    }

    
    
}
