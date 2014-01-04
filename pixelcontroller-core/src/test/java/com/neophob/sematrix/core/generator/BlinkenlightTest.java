package com.neophob.sematrix.core.generator;

import org.junit.Test;

import com.neophob.sematrix.core.glue.FileUtils;
import com.neophob.sematrix.core.glue.FileUtilsJunit;
import com.neophob.sematrix.core.visual.generator.blinken.BlinkenLibrary;

public class BlinkenlightTest {

    @Test
    public void loadBlinkenFile() {
        BlinkenLibrary blinken = new BlinkenLibrary();
        FileUtils fu = new FileUtilsJunit();
        // load 4bpp file
        blinken.loadFile(fu.getBmlDir() + "torus2.bml.gz");

        // load 8bpp file
        blinken.loadFile(fu.getBmlDir() + "xflame.bml.gz");
    }

}
