/**
 * Copyright (C) 2011-2014 Michael Vogt <michu@neophob.com>
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
