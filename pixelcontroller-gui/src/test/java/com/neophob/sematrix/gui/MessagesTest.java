package com.neophob.sematrix.gui;


import static org.junit.Assert.*;

import org.junit.Test;


public class MessagesTest {

	@Test
	public void messageTest() {
		Messages m = new Messages();
		assertEquals("", m.getString(null));
		assertEquals("!not-exist!", m.getString("not-exist"));
		assertEquals("Freeze Update", m.getString("GeneratorGui.GUI_TOGGLE_FREEZE"));		
	}
}
