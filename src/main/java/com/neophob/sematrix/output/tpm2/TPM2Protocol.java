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

import java.awt.Color;

public abstract class TPM2Protocol {

    private static final byte START_BYTE = (byte) 0xC9;
    private static final byte DATA_FRAME = (byte) 0xDA;
    private static final byte BLOCK_END = (byte) 0x36;

    public static byte[] apply_tpm2_protocol(int[] frame) {
        return do_protocol(frame);
    }

    public static byte[] apply_tpm2_protocol(Color[] frame) {
        int[] int_frame = new int[frame.length];
        for (int i = 0; i < int_frame.length; i++) {
            int_frame[i] = frame[i].getRGB();
        }
        return do_protocol(int_frame);
    }

    private static byte[] do_protocol(int[] frame) {

        byte[] output_buffer = new byte[frame.length * 3 + 5];

        //3 colors per pixel
        int frame_size = frame.length * 3;
        byte frame_size_byte_high;
        byte frame_size_byte_low;
        int index;

        index = 0;

        //Start-Byte
        output_buffer[index] = START_BYTE;
        index++;

        //Ident-Byte
        output_buffer[index] = DATA_FRAME;
        index++;

        //Raw Data Size
        frame_size_byte_high = (byte) (frame_size >> 8 & 0xff);
        frame_size_byte_low = (byte) (frame_size & 0xff);

        output_buffer[index] = frame_size_byte_high;
        index++;

        output_buffer[index] = frame_size_byte_low;
        index++;

        //Raw Data
        for (int i = 0; i < (frame_size); i++) {
            output_buffer[index] = (byte) ((frame[i] >> 16) & 255);
            index++;
            output_buffer[index] = (byte) ((frame[i] >> 8) & 255);
            index++;
            output_buffer[index] = (byte) (frame[i] & 255);
            index++;
        }

        //Block-End-Byte
        output_buffer[index] = BLOCK_END;

        return output_buffer;
    }
}
