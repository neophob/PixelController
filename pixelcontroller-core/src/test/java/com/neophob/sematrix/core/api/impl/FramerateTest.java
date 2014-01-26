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
