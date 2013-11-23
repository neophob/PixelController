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
package com.neophob.sematrix.core.fader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.neophob.sematrix.core.fader.Crossfader;
import com.neophob.sematrix.core.fader.Fader;
import com.neophob.sematrix.core.fader.IFader;
import com.neophob.sematrix.core.glue.MatrixData;

public class CrossfaderTest {

    @Test
    public void presetFadeTest() throws Exception {
    	final int fps = 50;
    	    	
    	MatrixData matrix = new MatrixData(8, 8);
    	IFader switchFader = new Crossfader(matrix, fps);
    	assertEquals(Fader.FaderName.CROSSFADE.getId(), switchFader.getId());

    	assertFalse(switchFader.isDone());
    	assertFalse(switchFader.isStarted());
    	
    	switchFader.startFade(99, new int[55]);
    	assertTrue(switchFader.isStarted());
    	
    	switchFader.getBuffer(new int[55], new int[55]);
    	switchFader.cleanUp();
    }
    
    @Test
    public void visualFadeTest() throws Exception {
    	final int fps = 50;
    	    	
    	MatrixData matrix = new MatrixData(8, 8);
    	IFader switchFader = new Crossfader(matrix, 20, fps);
    	assertEquals(Fader.FaderName.CROSSFADE.getId(), switchFader.getId());

    	assertFalse(switchFader.isDone());
    	assertFalse(switchFader.isStarted());
    	
    	switchFader.startFade(99, 1);
    	assertTrue(switchFader.isStarted());
    	
    	switchFader.getBuffer(new int[55], new int[55]);
    	switchFader.getBuffer(new int[55], new int[55]);
    	assertTrue(switchFader.isDone());
    	switchFader.cleanUp();
    }    
}
