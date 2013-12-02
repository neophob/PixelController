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
package com.neophob.sematrix.gui.callback;

import static org.junit.Assert.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class GuiStateTest {

	@Test
	public void testGuiState() {
		GuiState gs = new GuiState();
		
		List<String> o = new LinkedList<String>();
		o.add("CHANGE_GENERATOR_A 1");
		o.add("CHANGE_GENERATOR_B 0");
		o.add("BLINKEN bnf_auge.bml");
		gs.updateState(o);		
		Map<String,String> result = gs.getState();
		assertEquals(3, result.size());
		assertEquals("1", result.get("CHANGE_GENERATOR_A"));
		assertEquals("0", result.get("CHANGE_GENERATOR_B"));
		assertEquals("bnf_auge.bml", result.get("BLINKEN"));
		
		o.clear();
		o.add("CHANGE_GENERATOR_B 3");
		o.add("CHANGE_EFFECT_B 1");		
		gs.updateState(o);
		result = gs.getState();
		assertEquals(4, result.size());
		assertEquals("1", result.get("CHANGE_GENERATOR_A"));
		assertEquals("3", result.get("CHANGE_GENERATOR_B"));
		assertEquals("1", result.get("CHANGE_EFFECT_B"));
		assertEquals("bnf_auge.bml", result.get("BLINKEN"));
		
		Map<String,String> diff = gs.getDiff();
		assertEquals(2, diff.size());
		assertEquals("3", result.get("CHANGE_GENERATOR_B"));
		assertEquals("1", result.get("CHANGE_EFFECT_B"));
	}

}
