package com.neophob.sematrix.gui;


import static org.junit.Assert.*;

import org.junit.Test;


public class MessagesTest {

	@Test
	public void messageTest() {
		assertEquals("", Messages.getString(null));
		assertEquals("!not-exist!", Messages.getString("not-exist"));
		assertEquals("Freeze Update", Messages.getString("GeneratorGui.GUI_TOGGLE_FREEZE"));		
	}
}
