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
package com.neophob.sematrix.core.effect;

import java.util.Random;

import com.neophob.sematrix.core.glue.Collector;
import com.neophob.sematrix.core.glue.MatrixData;
import com.neophob.sematrix.core.glue.ShufflerOffset;
import com.neophob.sematrix.core.resize.Resize.ResizeName;


/**
 * The Class Tint.
 */
public class TextureDeformation extends Effect {

    /** The m lut. */
    private int[] lookUpTable;

    /** The time displacement. */
    private int timeDisplacement;

    /** The lut. */
    private int selectedLut;

    private int cnt;

    /**
     * Instantiates a new tint.
     *
     * @param controller the controller
     */
    public TextureDeformation(MatrixData matrix) {
        super(matrix, EffectName.TEXTURE_DEFORMATION, ResizeName.QUALITY_RESIZE);

        lookUpTable =  new int[3 * this.internalBufferXSize * this.internalBufferYSize];

        // use higher resolution textures if things get to pixelated
        this.selectedLut=7;
        changeLUT(selectedLut);
    }


    /* (non-Javadoc)
     * @see com.neophob.sematrix.core.effect.Effect#getBuffer(int[])
     */
    public int[] getBuffer(int[] buffer) {
        int[] ret = new int[buffer.length];

        for (int pixelCount = 0; pixelCount < buffer.length; pixelCount++) {
            int o = (pixelCount << 1) + pixelCount;  // equivalent to 3 * pixelCount
            int u = lookUpTable[o+0] + timeDisplacement;    // to look like its animating, add timeDisplacement
            int v = lookUpTable[o+1] + timeDisplacement;
            int adjustBrightness = lookUpTable[o+2];

            // get the R,G,B values from texture
            int sofs = Math.abs(this.internalBufferXSize * v + u);
            int currentPixel = buffer[sofs%(buffer.length-1)];

            // only apply brightness if it was calculated
            if (adjustBrightness != 0) {       
                int b;

                // disassemble pixel using bit mask to remove color components for greater speed
                b = currentPixel & 0xFF;              

                // make darker or brighter
                b += adjustBrightness;

                // constrain RGB to make sure they are within 0-255 color range
                b = constrain(b,0,255);

                // reassemble colors back into pixel
                currentPixel = b;
            }

            // put texture pixel on buffer screen
            ret[pixelCount] = currentPixel;
        }

        return ret;
    }


    /**
     * Change lut.
     *
     * @param lut the lut
     */
    public void changeLUT(int lut) {
        this.selectedLut = lut;
        createLut(lut);
    }

    /**
     * Gets the lut.
     *
     * @return the lut
     */
    public int getLut() {
        return selectedLut;
    }

    @Override
    public void update() {
        cnt++;
        if (cnt%2==1) {
            timeDisplacement++;    
        }        
    }
    
    /* (non-Javadoc)
     * @see com.neophob.sematrix.core.effect.Effect#shuffle()
     */
    @Override
    public void shuffle() {
        if (Collector.getInstance().getShufflerSelect(ShufflerOffset.TEXTURE_DEFORM)) {
            Random rand = new Random();
            this.changeLUT(rand.nextInt(12));
        }
    }


    /**
     * Constrain.
     *
     * @param amt the amt
     * @param low the low
     * @param high the high
     * @return the int
     */
    public static final int constrain(int amt, int low, int high) {
        return (amt < low) ? low : ((amt > high) ? high : amt);
    }

    /**
     * Constrain.
     *
     * @param amt the amt
     * @param low the low
     * @param high the high
     * @return the float
     */
    public static final float constrain(float amt, float low, float high) {
        return (amt < low) ? low : ((amt > high) ? high : amt);
    }

    /**
     * Creates the lut.
     *
     * @param effectStyle the effect style
     */
    private void createLut(int effectStyle){
        // increment placeholder
        int k = 0;

        // u and v are euclidean coordinates  
        float u,v,bright = 0; 

        for (int j=0; j < this.internalBufferYSize; j++ ) {
            float y = -1.00f + 2.00f*(float)j/(float)this.internalBufferYSize;

            for (int i=0; i < this.internalBufferXSize; i++ ) {
                float x = -1.00f + 2.00f*(float)i/(float)this.internalBufferXSize;
                float d = (float)Math.sqrt( x*x + y*y );
                float a = (float)Math.atan2( y, x );
                float r = d;
                switch(effectStyle) {
                    case 1:   // stereographic projection / anamorphosis 
                        u = (float)Math.cos( a )/d;
                        v = (float)Math.sin( a )/d;
                        bright = -10 * (2/(6*r + 3*x));
                        break;
                    case 2:  // hypnotic rainbow spiral
                        v = (float)Math.sin(a+(float)Math.cos(3*r))/(float)(Math.pow(r,.2));
                        u = (float)Math.cos(a+(float)Math.cos(3*r))/(float)(Math.pow(r,.2));
                        bright = 1;
                        break;
                    case 3:  // rotating tunnel of wonder
                        v = 2/(6*r + 3*x);
                        u = a*3/(float)Math.PI;
                        bright = 15 * -v;
                        break;
                    case 4:  // wavy star-burst
                        v = (-0.4f/r)+.1f*(float)Math.sin(8*a);
                        u = .5f + .5f*a/(float)Math.PI;
                        bright=0;
                        break;
                    case 5:  // hyper-space travel
                        u = (0.02f*y+0.03f)*(float)Math.cos(a*3)/r;
                        v = (0.02f*y+0.03f)*(float)Math.sin(a*3)/r;
                        bright=0;
                        break;
                    case 6:  // five point magnetic flare
                        u = 1f/(r+0.5f+0.5f*(float)Math.sin(5*a));
                        v = a*3/(float)Math.PI;
                        bright = 0;
                        break;
                    case 7:  // cloud like dream scroll
                        u = 0.1f*x/(0.11f+r*0.5f);
                        v = 0.1f*y/(0.11f+r*0.5f);
                        bright=0;
                        break;
                    case 8:  // floor and ceiling with fade to dark horizon
                        u = x/(float)Math.abs(y);
                        v = 1/(float)Math.abs(y);
                        bright = 10* -v;
                        break;
                    case 9:  // hot magma liquid swirl
                        u = 0.5f*(a)/(float)Math.PI;
                        v = (float)Math.sin(2*r);
                        bright = 0;
                        break;
                    case 10:  // clockwise flush down the toilet
                        v = (float)Math.pow(r,0.1);
                        u = (1*a/(float)Math.PI)+r;
                        bright=0;
                        break;
                    case 11:  // 3D ball
                        v = x*(3-(float)Math.sqrt(4-5*r*r))/(r*r+1);
                        u = y*(3-(float)Math.sqrt(4-5*r*r))/(r*r+1);
                        bright = 7f * -18.7f*(x+y+r*r-(x+y-1)*(float)Math.sqrt(4-5*r*r)/3)/(r*r+1);
                        break;
                    default:  // show texture with no deformation or lighting
                        u = x;
                        v = y;
                        bright = 0;
                        break;
                }
                lookUpTable[k++] = (int)(this.internalBufferXSize*u) % (this.internalBufferXSize-1);
                lookUpTable[k++] = (int)(this.internalBufferYSize*v) % (this.internalBufferYSize-1);
                lookUpTable[k++] = (int)(bright);
            }
        }
    }

}
