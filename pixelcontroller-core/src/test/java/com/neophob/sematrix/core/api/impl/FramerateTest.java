package com.neophob.sematrix.core.api.impl;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class FramerateTest {

    @Test
    public void testDelay() {
        Framerate f = new Framerate(25f);
        assertEquals(40L, f.getDelay());

        f.setFps(10);
        assertEquals(100L, f.getDelay());

        f.setFps(1);
        assertEquals(1000L, f.getDelay());
    }
}
