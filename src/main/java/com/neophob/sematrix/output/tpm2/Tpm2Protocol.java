/*
 * Copyright (C) 2011 McGyver, michuNeo, Pepe_1981
 * http://www.ledstyles.de/ftopic18969.html
 * 
 * https://raw.github.com/McGyver666/jTPM2/master/src/main/java/com/ledstyles/jtpm2/TPM2Protocol.java
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.neophob.sematrix.output.tpm2;

import com.neophob.sematrix.properties.ColorFormat;


public abstract class Tpm2Protocol {

	private static final int HEADER_SIZE = 5;
	
    private static final byte START_BYTE = (byte) 0xC9;
    private static final byte DATA_FRAME = (byte) 0xDA;
    private static final byte BLOCK_END = (byte) 0x36;

    /**
     * Create TPM2 Protocol
     * 
     * @param frame
     * @return
     */
    public static byte[] doProtocol(int[] frame, ColorFormat colorFormat) {
        //3 colors per pixel
        int index = 0;
        byte[] outputBuffer = new byte[frame.length*3 + HEADER_SIZE];

        //Start-Byte
        outputBuffer[index++] = START_BYTE;

        //Ident-Byte
        outputBuffer[index++] = DATA_FRAME;

        //Raw Data Size
        byte frameSizeByteHigh = (byte) (frame.length >> 8 & 0xff);
        byte frameSizeByteLow = (byte) (frame.length & 0xff);
        outputBuffer[index++] = frameSizeByteHigh;
        outputBuffer[index++] = frameSizeByteLow;

        //Raw Data
        //TODO respect color order
        for (int i = 0; i < frame.length; i++) {
            outputBuffer[index++] = (byte) ((frame[i] >> 16) & 255);
            outputBuffer[index++] = (byte) ((frame[i] >> 8) & 255);
            outputBuffer[index++] = (byte) (frame[i] & 255);
        }

        //Block-End-Byte
        outputBuffer[index] = BLOCK_END;

        return outputBuffer;
    }
}
