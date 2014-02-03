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
