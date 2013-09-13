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

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * test internal buffer size
 * 
 * @author michu
 *
 */
public class MatrixDataTest {
    
    @Test
    public void processMessages() throws Exception {
    	//verify the buffer get multiplied with 8
    	MatrixData matrix = new MatrixData(8,8);
    	assertEquals(64, matrix.getBufferXSize());
    	assertEquals(64, matrix.getBufferYSize());
    	
    	//verify the buffer get multiplied with 8
    	matrix = new MatrixData(16,16);
    	assertEquals(128, matrix.getBufferXSize());
    	
    	//verify the buffer get multiplied with 4
    	matrix = new MatrixData(32,32);
    	assertEquals(128, matrix.getBufferXSize());
    	
    	//verify the buffer get multiplied with 2
    	matrix = new MatrixData(64,64);
    	assertEquals(128, matrix.getBufferXSize());

    	//verify the buffer get multiplied with 1
    	matrix = new MatrixData(512, 512);
    	assertEquals(512, matrix.getBufferXSize());
    	
    	matrix = new MatrixData(24,18);
    }


}
