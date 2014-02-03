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
package com.neophob.sematrix.core.output;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class BufferCacheTest {

    @Test
    public void testBufferCache() {
        BufferCache cache = new BufferCache();
        assertFalse(cache.didFrameChange(null));

        assertTrue(cache.didFrameChange(new byte[] { 1, 2, 3 }));
        assertFalse(cache.didFrameChange(new byte[] { 1, 2, 3 }));

        assertTrue(cache.didFrameChange(new byte[] { 1, 2, 3, 4 }));

        assertTrue(cache.didFrameChange((byte) 2, new byte[] { 1, 2, 3, 4 }));
        assertFalse(cache.didFrameChange((byte) 2, new byte[] { 1, 2, 3, 4 }));
        cache.resetHash((byte) 2);
        assertTrue(cache.didFrameChange((byte) 2, new byte[] { 1, 2, 3, 4 }));
    }
}
