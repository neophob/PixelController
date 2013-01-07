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
package com.neophob.sematrix.output;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.neophob.sematrix.properties.ColorFormat;



/**
 * Output Helper
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
	public static int[] flipSecondScanline(int buffer[], int xResolution, int yResolution) {
		int bufferTwo[] = buffer.clone();

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
	public static int[] manualMapping(int src[], int mapping[], int xResolution, int yResolution) {
		int bufferTwo[] = src.clone();
		int lenght = src.length;
		int ofs=0;
		for (int i: mapping) {
			if (i+1>lenght) {
				LOG.log(Level.SEVERE, "Your manual mapping is wrong,the first index is 0! Invalid entry index: {0}", i);
			} else {
				bufferTwo[ofs++] = src[i]; 				
			}
		}
		return bufferTwo;
	}



	/**
	 * 
	 * @param frameBuf
	 * @return
	 */
	public static byte[] convertIntToByteBuffer(int[] frameBuf) {
		byte[] buffer = new byte[frameBuf.length*3];
		int ofs;
		for (int i = 0; i < frameBuf.length; i++) {
			ofs = i*3;
			buffer[ofs++] = (byte) ((frameBuf[i]>>16) & 0xff);
			buffer[ofs++] = (byte) ((frameBuf[i]>>8)  & 0xff);
			buffer[ofs  ] = (byte) ( frameBuf[i]      & 0xff);
		}

		return buffer;
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
    

}
