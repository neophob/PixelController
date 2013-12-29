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
