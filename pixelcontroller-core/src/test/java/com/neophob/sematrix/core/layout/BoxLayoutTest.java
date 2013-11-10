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
package com.neophob.sematrix.core.layout;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.neophob.sematrix.core.fader.IFader;
import com.neophob.sematrix.core.fader.Switch;
import com.neophob.sematrix.core.glue.MatrixData;
import com.neophob.sematrix.core.glue.OutputMapping;
import com.neophob.sematrix.core.layout.BoxLayout;
import com.neophob.sematrix.core.layout.Layout;
import com.neophob.sematrix.core.layout.LayoutModel;

public class BoxLayoutTest {

    @Test
    public void basicTest() throws Exception {
    	final int panels = 2;
    	
    	Layout l = new BoxLayout(panels, panels);
    	assertEquals(panels, l.getRow1Size());
    	assertEquals(panels, l.getRow2Size());    	
    	assertEquals(Layout.LayoutName.BOX, l.getLayoutName());
    	
    	MatrixData matrix = new MatrixData(8, 8);
    	IFader fader = new Switch(matrix, 50);
    	List<OutputMapping> ioMapping = new ArrayList<OutputMapping>();
		for (int n=0; n<panels+panels; n++) {
			ioMapping.add(new OutputMapping(fader, n));			
		}

		//testcase#1: each output has a visual assigned
    	LayoutModel lom0 = l.getDataForScreen(0, ioMapping);
    	assertEquals(0, lom0.getOfsX());
    	assertEquals(0, lom0.getOfsY());
    	assertEquals(1, lom0.getSameFxOnX());
    	assertEquals(1, lom0.getSameFxOnY());
    	assertEquals(0, lom0.getVisualId());
    	assertEquals(true, lom0.screenDoesNotNeedStretching());
    	
		//testcase#2: visual 0 is displayed on ALL outputs    	
    	ioMapping.clear();
		for (int n=0; n<panels+panels; n++) {
			ioMapping.add(new OutputMapping(fader, 0));			
		}
		lom0 = l.getDataForScreen(0, ioMapping);
    	assertEquals(0, lom0.getOfsX());
    	assertEquals(0, lom0.getOfsY());
    	assertEquals(panels, lom0.getSameFxOnX());
    	assertEquals(panels, lom0.getSameFxOnY());
    	assertEquals(0, lom0.getVisualId());
    	assertEquals(false, lom0.screenDoesNotNeedStretching());
    	
    	//testcase#3: mix, split vertically
    	ioMapping.get(1).setVisualId(1);
    	ioMapping.get(3).setVisualId(1);
		lom0 = l.getDataForScreen(0, ioMapping);
    	assertEquals(0, lom0.getOfsX());
    	assertEquals(0, lom0.getOfsY());
    	assertEquals(1, lom0.getSameFxOnX());
    	assertEquals(panels, lom0.getSameFxOnY());
    	assertEquals(0, lom0.getVisualId());
    	assertEquals(false, lom0.screenDoesNotNeedStretching());
    }
    
}
