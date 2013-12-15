package com.neophob.sematrix.osc.model;

import org.junit.Test;

public class OscMessageTest {

	@Test
	public void testConstructor1() {
		new OscMessage("");
	}

	@Test
	public void testConstructor2() {
		new OscMessage(new String[] {"HI"});
		new OscMessage(new String[] {"HI", "HO"});
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConstructor2Exception() {
		new OscMessage(new String[] {});
	}

	@Test
	public void testConstructor4() {
		String s = null;
		new OscMessage(s,s);
	}

	@Test
	public void testConstructor5() {
		byte[] b = null;
		new OscMessage("a", b);
	}

}
