package com.neophob.sematrix.core.visual.color;

import java.util.List;
import java.util.Properties;

import org.junit.Assert;
import org.junit.Test;

public class ColorSetTest {

    @Test
    public void testLoadColorSet() {
        Properties palette = new Properties();
        List<IColorSet> loadedColorSets = ColorSet.loadAllEntries(palette);
        Assert.assertEquals(0, loadedColorSets.size());

        // test order
        palette.put("BB", "0,200,500");
        palette.put("AA", "10000");
        loadedColorSets = ColorSet.loadAllEntries(palette);
        Assert.assertEquals(2, loadedColorSets.size());
        Assert.assertEquals("AA", loadedColorSets.get(0).getName());

        // test equals
        Assert.assertEquals(loadedColorSets.get(0), new ColorSet("AA", new int[] { 10000 }));
    }
}
