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

import com.neophob.sematrix.core.visual.MatrixData;
import com.neophob.sematrix.core.visual.OutputMapping;
import com.neophob.sematrix.core.visual.fader.IFader;
import com.neophob.sematrix.core.visual.fader.Switch;
import com.neophob.sematrix.core.visual.layout.HorizontalLayout;
import com.neophob.sematrix.core.visual.layout.Layout;
import com.neophob.sematrix.core.visual.layout.LayoutModel;

public class HorizontalLayoutTest {

    @Test
    public void basicTest() throws Exception {
    	final int panels = 3;
    	
    	Layout l = new HorizontalLayout(panels);
    	assertEquals(panels, l.getRow1Size());
    	assertEquals(0, l.getRow2Size());    	
    	assertEquals(Layout.LayoutName.HORIZONTAL, l.getLayoutName());
    	
    	MatrixData matrix = new MatrixData(8, 8);
    	IFader f = new Switch(matrix, 50);
    	List<OutputMapping> ioMapping = new ArrayList<OutputMapping>();
    	
    	//testcase#1: each output has a visual assigned
		for (int n=0; n<panels; n++) {
			ioMapping.add(new OutputMapping(f, n));			
		}
    	LayoutModel lom0 = l.getDataForScreen(0, ioMapping);
    	assertEquals(0, lom0.getOfsX());
    	assertEquals(0, lom0.getOfsY());
    	assertEquals(1, lom0.getSameFxOnX());
    	assertEquals(1, lom0.getSameFxOnY());
    	assertEquals(0, lom0.getVisualId());
    	assertEquals(true, lom0.screenDoesNotNeedStretching());
    	
    	//testcase#2: output 0 and 2 shot visual 0, output 1 show visual 1
    	ioMapping.get(0).setVisualId(0);
    	ioMapping.get(2).setVisualId(0);
    	ioMapping.get(1).setVisualId(1);
    	lom0 = l.getDataForScreen(0, ioMapping);
    	assertEquals(0, lom0.getOfsX());
    	assertEquals(0, lom0.getOfsY());
    	assertEquals(2, lom0.getSameFxOnX());
    	assertEquals(1, lom0.getSameFxOnY());
    	assertEquals(0, lom0.getVisualId());
    	assertEquals(false, lom0.screenDoesNotNeedStretching());

    	LayoutModel lom1 = l.getDataForScreen(1, ioMapping);
    	assertEquals(0, lom1.getOfsX());
    	assertEquals(0, lom1.getOfsY());
    	assertEquals(1, lom1.getSameFxOnX());
    	assertEquals(1, lom1.getSameFxOnY());
    	assertEquals(1, lom1.getVisualId());
    	assertEquals(true, lom1.screenDoesNotNeedStretching());
    }
    
}
