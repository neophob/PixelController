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
package com.neophob.sematrix.core.output;

import org.junit.Test;

/**
 * verify universe fliping
 * @author michu
 *
 */
public class ArtnetDeviceTest {
	
    @Test
    public void testArtnetUniverse() {
    	
    	int pixelsPerUniverse = 8;//170;
    	int bfrsze = 8*2;
    	int nrOfUniverse=1;
    	
	    int bufferSize=bfrsze;
	    if (bufferSize > pixelsPerUniverse) {
	    	while (bufferSize > pixelsPerUniverse) {
	    		nrOfUniverse++;
	    		bufferSize -= pixelsPerUniverse;
	    	}
	    }
	    //System.out.println("nrOfUniverse: "+nrOfUniverse);	    
		int remainingInt = bfrsze;
		
		
		int ofs=0;
		for (int i=0; i<nrOfUniverse; i++) { //nr of universe
			int tmp=pixelsPerUniverse;
			if (remainingInt<pixelsPerUniverse) {
				tmp = remainingInt;
			}
			remainingInt-=tmp;
			ofs+=tmp;
			//System.out.println(i+": ofs:"+ofs+", remainingInt:"+remainingInt);
		}

    }
    
}
