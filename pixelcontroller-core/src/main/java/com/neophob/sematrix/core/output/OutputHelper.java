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
package com.neophob.sematrix.core.output;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;

import com.neophob.sematrix.core.output.gamma.RGBAdjust;
import com.neophob.sematrix.core.properties.ColorFormat;

/**
 * Output Helper Class
 * Contains some common helper methods used by the output devices 
 *
 * @author michu
 */
public class OutputHelper {

	/** The log. */
	private static final Logger LOG = Logger.getLogger(OutputHelper.class.getName());

	private OutputHelper() {
		//no instance allowed
	}

	/**
	 * this function feed the framebufferdata (32 pixels a 2bytes (aka 16bit)
	 * to the send array. each second scanline gets inverteds
	 *
	 * @param cmdfull the cmdfull
	 * @param frameData the frame data
	 */
	public static int[] flipSecondScanline(int[] buffer, int xResolution, int yResolution) {
		int[] bufferTwo = buffer.clone();

		for (int y=0; y<yResolution; y++) {
			if (y%2==1) {
				int ofs = y*xResolution;
				for (int x=0; x<xResolution; x++) {
					bufferTwo[ofs+x] = buffer[xResolution+ofs-x-1];
				}
			}
		}		
		return bufferTwo;
	}

	/**
	 * do manual mapping, this is used to support a more exotic device configuration
	 * 
	 * @param buffer
	 * @param xResolution
	 * @param yResolution
	 * @return
	 */
	public static int[] manualMapping(int[] src, int[] mapping, int xResolution, int yResolution) {
		int[] bufferTwo = new int[mapping.length];
		int length = src.length;
		int ofs=0;
		for (int i: mapping) {
			if (i+1>length) {
				LOG.log(Level.SEVERE, "Your manual mapping is wrong,the first index is 0! Invalid entry index: {0}", i);
			} else {
				bufferTwo[ofs++] = src[i]; 				
			}
		}
		return bufferTwo;
	}

	
	/**
	 * Convert buffer to15bit.
	 *
	 * @param data the data
	 * @param colorFormat the color format
	 * @return the byte[]
	 * @throws IllegalArgumentException the illegal argument exception
	 */
	public static byte[] convertBufferTo15bit(int[] data, ColorFormat colorFormat) throws IllegalArgumentException {
	    int targetBuffersize = data.length;
	    
		int[] r = new int[targetBuffersize];
		int[] g = new int[targetBuffersize];
		int[] b = new int[targetBuffersize];

		splitUpBuffers(targetBuffersize, data, colorFormat, r, g, b);

        int ofs=0;
        byte[] converted = new byte[targetBuffersize*2];
        //convert to 24bpp to 15(16)bpp output format: RRRRRGGG GGGBBBBB (64x)
        for (int i=0; i<targetBuffersize;i++) {
            byte b1 = (byte)(r[i]>>3);
            byte b2 = (byte)(g[i]>>3);
            byte b3 = (byte)(b[i]>>3);

            converted[ofs++] = (byte)((b1<<2) | (b2>>3));
            converted[ofs++] = (byte)(((b2&7)<<5) | b3);
        }

        return converted;		
	}

	/**
	 * Convert buffer to15bit and apply color correction
	 *
	 * @param data the data
	 * @param colorFormat the color format
	 * @return the byte[]
	 * @throws IllegalArgumentException the illegal argument exception
	 */
	public static byte[] convertBufferTo15bit(int[] data, ColorFormat colorFormat, RGBAdjust correction) throws IllegalArgumentException {
	    int targetBuffersize = data.length;
	    
		int[] r = new int[targetBuffersize];
		int[] g = new int[targetBuffersize];
		int[] b = new int[targetBuffersize];

		splitUpBuffers(targetBuffersize, data, colorFormat, r, g, b);

        int ofs=0;
        int ri,gi,bi;
        byte[] converted = new byte[targetBuffersize*2];
        //convert to 24bpp to 15(16)bpp output format: RRRRRGGG GGGBBBBB (64x)
        for (int i=0; i<targetBuffersize;i++) {
        	ri = r[i];
        	gi = g[i];
        	bi = b[i];
        	
        	//color correct
        	if (correction.getR()>0) {
        		if (ri>correction.getR()) {
        			ri -= correction.getR();
        		} else {
        			ri = 0;
        		}
        	}
        	if (correction.getG()>0) {
        		if (gi>correction.getG()) {
        			gi -= correction.getG();
        		} else {
        			gi = 0;
        		}
        	}
        	if (correction.getB()>0) {
        		if (bi>correction.getB()) {
        			bi -= correction.getB();
        		} else {
        			bi = 0;
        		}
        	}
        	
            byte b1 = (byte)(ri>>3);
            byte b2 = (byte)(gi>>3);
            byte b3 = (byte)(bi>>3);

            converted[ofs++] = (byte)((b1<<2) | (b2>>3));
            converted[ofs++] = (byte)(((b2&7)<<5) | b3);
        }

        return converted;		
	}
	
	

	/**
	 * Convert internal buffer to 24bit byte buffer, using colorformat.
	 *
	 * @param data the data
	 * @param colorFormat the color format
	 * @return the byte[]
	 * @throws IllegalArgumentException the illegal argument exception
	 */
	public static byte[] convertBufferTo24bit(int[] data, ColorFormat colorFormat) throws IllegalArgumentException {
		int targetBuffersize = data.length;

		int[] r = new int[targetBuffersize];
		int[] g = new int[targetBuffersize];
		int[] b = new int[targetBuffersize];

		splitUpBuffers(targetBuffersize, data, colorFormat, r, g, b);

        int ofs=0;
		byte[] buffer = new byte[targetBuffersize*3];
		for (int i=0; i<targetBuffersize; i++) {
			buffer[ofs++] = (byte)r[i];
			buffer[ofs++] = (byte)g[i];
			buffer[ofs++] = (byte)b[i];
		}

		return buffer;
	}


    /**
     * convert the int buffer in byte buffers, respecting the color order
     * 
     * @param targetBuffersize
     * @param data
     * @param colorFormat
     * @param r
     * @param g
     * @param b
     */
    private static void splitUpBuffers(int targetBuffersize, int[] data, ColorFormat colorFormat, int[] r, int[] g, int[] b) {
        int ofs = 0;
        int tmp;
        for (int n=0; n<targetBuffersize; n++) {
            //one int contains the rgb color
            tmp = data[ofs];

            switch (colorFormat) {
            case RGB:
                r[ofs] = (int) ((tmp>>16) & 255);
                g[ofs] = (int) ((tmp>>8)  & 255);
                b[ofs] = (int) ( tmp      & 255);                       
                break;
            case RBG:
                r[ofs] = (int) ((tmp>>16) & 255);
                b[ofs] = (int) ((tmp>>8)  & 255);
                g[ofs] = (int) ( tmp      & 255);                       
                break;
            case BRG:
                b[ofs] = (int) ((tmp>>16) & 255);
                r[ofs] = (int) ((tmp>>8)  & 255);
                g[ofs] = (int) ( tmp      & 255);
                break;
            case BGR:
                b[ofs] = (int) ((tmp>>16) & 255);
                g[ofs] = (int) ((tmp>>8)  & 255);
                r[ofs] = (int) ( tmp      & 255);
                break;
            case GBR:
                g[ofs] = (int) ((tmp>>16) & 255);
                b[ofs] = (int) ((tmp>>8)  & 255);
                r[ofs] = (int) ( tmp      & 255);
                break;
            case GRB:
                g[ofs] = (int) ((tmp>>16) & 255);
                r[ofs] = (int) ((tmp>>8)  & 255);
                b[ofs] = (int) ( tmp      & 255);
                break;              
            }
            ofs++;
        }       
    }
    
    /**
     * serial port names are CASE SENSITIVE. this sounds logically on unix platform
     * however com1 will not work on windows, there all names need to be in uppercase (COM1)
     * 
     * see https://github.com/neophob/PixelController/issues/30 for more details
     * @param configuredName
     * @return
     */
    public static String getSerialPortName(String configuredName) {
    	for (String portName: Serial.list()) {
    		if (StringUtils.equalsIgnoreCase(portName, configuredName)) {
    			return portName;
    		}
    	}
    	
    	//we didn't found the port, hope that the provided name will work...
    	return configuredName;
    }

}
