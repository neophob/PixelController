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
