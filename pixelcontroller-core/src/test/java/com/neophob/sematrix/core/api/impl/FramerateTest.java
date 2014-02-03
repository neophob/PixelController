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
package com.neophob.sematrix.core.api.impl;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class FramerateTest {

    @Test
    public void testSleep() {
        Framerate f = new Framerate(25f);
        assertEquals(40L, f.getFrameDelay());
        assertEquals(2, f.getFrameCount());

        f.setFps(10);
        assertEquals(100L, f.getFrameDelay());
        assertEquals(3, f.getFrameCount());

        f.setFps(1);
        assertEquals(1000L, f.getFrameDelay());
        assertEquals(4, f.getFrameCount());

        f.setFps(0);
        assertEquals(f.getConfiguredFps(), Framerate.MINIMAL_FPS, 0.001);
    }

}
