/**
 * Copyright (C) 2011 Michael Vogt <michu@neophob.com>
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

package com.neophob.sematrix.output.gamma;

/**
 * create gamma tab for color correction
 * code ripped from https://github.com/scottjgibson/lightingPi/blob/master/lightingPi.py
 * 
 * @author michu
 *
 */
public abstract class Gammatab {

	private static int[] lpd6803GammaTab = generateLpd6803GammaTab();
	private static int[] ws2801GammaTab = generateWs2801GammaTab();
	

	/**
	 * create gammatab for lpd6803 based led strips
	 * 
	 * @return
	 */
	private static int[] generateLpd6803GammaTab() {
		int[] ret = new int[256];

		for (int i=0; i<256; i++) {
			ret[i] = (int)(Math.pow ((float)(i)/255.0f, 2.0f)*255.0f+0.5f);
		}

		return ret;
	}

	/**
	 * create gammatab for ws2801 based led strips
	 * 
	 * @return
	 */
	private static int[] generateWs2801GammaTab() {
		int[] ret = new int[256];

		for (int i=0; i<256; i++) {
			ret[i] = (int)(Math.pow ((float)(i)/255.0f, 2.5f)*255.0f+0.5f);
		}

		return ret;
	}
	
	
    /**
     * apply brightness level
     * @param buffer
     * @param brightness
     * @return
     */
    public static int[] applyBrightnessAndGammaTab(int[] buffer, GammaType type, float brightness) {
    	int ret[] = new int[buffer.length];
    	int ofs=0;
    	int r,g,b;
    	
    	for (int n=0; n<buffer.length; n++) {
    		int tmp = buffer[ofs];	
            r = (int) ((tmp>>16) & 255);
            g = (int) ((tmp>>8)  & 255);
            b = (int) ( tmp      & 255);                       

            //apply brightness
            r = (int)(r*brightness);
            g = (int)(g*brightness);
            b = (int)(b*brightness);
            
            //apply gamma
    		switch (type) {
    		case LPD_6803:
    			r = lpd6803GammaTab[r];
    			g = lpd6803GammaTab[g];
    			b = lpd6803GammaTab[b];
    			break;

    		case WS_2801:
    			r = ws2801GammaTab[r];
    			g = ws2801GammaTab[g];
    			b = ws2801GammaTab[b];

    		case NONE:
    			break;

    		default:
    			break;
    		}

    		
            ret[ofs++] = (r<<16)|(g<<8)|b;
    	}
    	
    	return ret;
    }



}
