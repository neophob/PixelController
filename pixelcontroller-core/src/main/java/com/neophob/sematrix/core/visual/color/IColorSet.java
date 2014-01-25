package com.neophob.sematrix.core.visual.color;

public interface IColorSet extends Comparable<IColorSet> {

    /**
     * get ColorSet name
     * 
     * @return
     */
    String getName();

    /**
     * return a color defined in this color set
     * 
     * @param pos
     * @return
     */
    int getSmoothColor(int pos);

    /**
     * colorize an image buffer
     * 
     * @param buffer
     *            8bpp image
     * @param cs
     *            ColorSet to apply
     * @return 24 bpp image
     */
    int[] convertToColorSetImage(int[] buffer);

}