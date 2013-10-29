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

import org.junit.Test;

import com.neophob.sematrix.glue.MatrixData;

public class PixelImageTest {

    @Test
    public void pixelImageTest() throws Exception {

    	for (int x=2; x<99; x++) {
    		for (int y=2; y<99; y++) {
    	    	MatrixData matrix = new MatrixData(x,y);
    	    	Generator g = new PixelImage(matrix, 10);
    	    	g.update();
    		}
    	}

    }

    
    
}
