package com.neophob.sematrix.core.visual.color;

public interface IColorSet extends Comparable<IColorSet> {

    /**
     * get ColorSet name
     * 
     * @return
     */
    public abstract String getName();

    /**
     * return a color defined in this color set
     * 
     * @param pos
     * @return
     */
    public abstract int getSmoothColor(int pos);

    /**
     * colorize an image buffer
     * 
     * @param buffer
     *            8bpp image
     * @param cs
     *            ColorSet to apply
     * @return 24 bpp image
     */
    public abstract int[] convertToColorSetImage(int[] buffer);

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public abstract int compareTo(IColorSet otherColorSet);

}