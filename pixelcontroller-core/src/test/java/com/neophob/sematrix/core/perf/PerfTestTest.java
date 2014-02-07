package com.neophob.sematrix.core.perf;

import org.junit.Test;

import com.neophob.sematrix.core.perf.PerfTests;

public class PerfTestTest {

    @Test
    public void perfTestTest() {
        PerfTests test = new PerfTests(2, 8);
        test.startPerfTest();

    }
}
