package com.neophob.sematrix.core.output;

import com.neophob.sematrix.core.visual.color.IColorSet;

public class JunitColorSet implements IColorSet {

    @Override
    public String getName() {
        return "JunitColorSet";
    }

    @Override
    public int getSmoothColor(int pos) {
        return pos;
    }

    @Override
    public int[] convertToColorSetImage(int[] buffer) {
        return buffer;
    }

    @Override
    public int compareTo(IColorSet otherColorSet) {
        return 0;
    }

}
