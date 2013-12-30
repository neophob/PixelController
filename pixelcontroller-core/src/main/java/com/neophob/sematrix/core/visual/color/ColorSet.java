/**
 * Copyright (C) 2011-2013 Michael Vogt <michu@neophob.com>
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class defines a color set
 * 
 * @author michu
 * 
 */
public class ColorSet implements Serializable, IColorSet {

    private static final transient Logger LOG = Logger.getLogger(ColorSet.class.getName());

    private transient int[] precalc;

    private String name;

    private int[] colors;

    /**
     * 
     * @param name
     * @param colors
     */
    public ColorSet(String name, int[] colors) {
        this.name = name;
        this.colors = colors.clone();
        float boarderCount = 255f / (float) colors.length;

        // precalc colorset to save to cpu cycles
        precalc = new int[256];
        for (int i = 0; i < 256; i++) {
            int ofs = 0;

            int pos = i;
            while (pos > boarderCount) {
                pos -= boarderCount;
                ofs++;
            }

            int targetOfs = ofs + 1;

            precalc[i] = calcSmoothColor(colors[targetOfs % colors.length], colors[ofs
                    % colors.length], pos);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.neophob.sematrix.core.visual.color.IColorSet#getName()
     */
    @Override
    public String getName() {
        return name;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.neophob.sematrix.core.visual.color.IColorSet#getSmoothColor(int)
     */
    @Override
    public int getSmoothColor(int pos) {
        return precalc[pos];
    }

    /**
     * 
     * @param col1
     * @param col2
     * @param pos
     *            which position (which color offset)
     * @return
     */
    private int calcSmoothColor(int col1, int col2, int pos) {
        int b = col1 & 255;
        int g = (col1 >> 8) & 255;
        int r = (col1 >> 16) & 255;
        int b2 = col2 & 255;
        int g2 = (col2 >> 8) & 255;
        int r2 = (col2 >> 16) & 255;

        int mul = pos * colors.length;
        int oppositeColor = 255 - mul;

        r = (r * mul + r2 * oppositeColor) >> 8;
        g = (g * mul + g2 * oppositeColor) >> 8;
        b = (b * mul + b2 * oppositeColor) >> 8;

        return (r << 16) | (g << 8) | (b);
    }

    /**
     * convert entries from the properties file into colorset objects
     * 
     * @param palette
     * @return
     */
    public static List<IColorSet> loadAllEntries(Properties palette) {
        List<IColorSet> ret = new ArrayList<IColorSet>();

        for (Entry<Object, Object> entry : palette.entrySet()) {
            try {
                String setName = (String) entry.getKey();
                String setColors = (String) entry.getValue();
                String[] colorsAsString = setColors.split(",");

                // convert hex string into int
                int[] colorsAsInt = new int[colorsAsString.length];
                int ofs = 0;
                for (String s : colorsAsString) {
                    colorsAsInt[ofs++] = Integer.decode(s.trim());
                }
                IColorSet cs = new ColorSet(setName, colorsAsInt);
                ret.add(cs);
            } catch (Exception e) {
                LOG.log(Level.SEVERE, "Failed to load Palette entry: " + entry.getKey(), e);
            }
        }

        // sorty by name
        Collections.sort(ret);

        return ret;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.neophob.sematrix.core.visual.color.IColorSet#convertToColorSetImage
     * (int[])
     */
    @Override
    public int[] convertToColorSetImage(int[] buffer) {

        int[] ret = new int[buffer.length];

        for (int i = 0; i < ret.length; i++) {
            // use only 8bpp here!
            ret[i] = precalc[buffer[i] & 255];
        }

        return ret;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    /*
     * (non-Javadoc)
     * 
     * @see
     * com.neophob.sematrix.core.visual.color.IColorSet#compareTo(com.neophob
     * .sematrix.core.visual.color.ColorSet)
     */
    @Override
    public int compareTo(IColorSet otherColorSet) {
        if (otherColorSet.getName() == null && this.getName() == null) {
            return 0;
        }
        if (this.getName() == null) {
            return 1;
        }
        if (otherColorSet.getName() == null) {
            return -1;
        }
        return this.getName().compareTo(otherColorSet.getName());
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(colors);
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + Arrays.hashCode(precalc);
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ColorSet other = (ColorSet) obj;
        if (!Arrays.equals(colors, other.colors))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (!Arrays.equals(precalc, other.precalc))
            return false;
        return true;
    }
}
