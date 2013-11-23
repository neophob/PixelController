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
package com.neophob.sematrix.core.output.tpm2;


/**
 * TPM2Net protocol converter
 * 
 * TPM2 use UDP as transport layer, Port 65506
 * 
 * see http://www.ledstyles.de/ftopic18969.html for more details
 * 
 * Protocol:
 * Blockstart-Byte:        0x9C
 * 
 * Block-Art:              0xDA = Datenframe (Data) *oder*
 *                         0xC0 = Befehl (Command) *oder*
 *                         0xAA = Angeforderte Antwort (vom Datenempfänger an den Sender)
 * 
 * Framegrösse in 16 Bit:  High-Byte zuerst, dann
 *                         Low-Byte
 * 
 * Paketnummer:            0-255
 * 
 * Anzahl Pakete:          1-255
 *
 * Nutzdaten:              1 - 65.535 Bytes Daten oder Befehle mit Parametern
 * 
 * Blockende-Byte:         0x36
 * 
 * @author michu
 *
 */
public abstract class Tpm2NetProtocol {

	public static final int TPM2_NET_PORT = 65506;
	
	private static final int TPM2_NET_HEADER_SIZE = 6;		
    private static final byte START_BYTE = (byte) 0x9C;
    private static final byte DATA_FRAME = (byte) 0xDA;
    private static final byte CMD_FRAME = (byte) 0xc0;
    private static final byte BLOCK_END = (byte) 0x36;
    
    /**
     * Create a TPM2.Net payload. Hint: this is the 2nd release of the protocol, added totalPackets
     * 
     * @param frame
     * @return
     */
    public static byte[] createImagePayload(int packetNumber, int totalPackets, byte[] data) {
    	int frameSize = data.length;
        byte[] outputBuffer = new byte[frameSize + TPM2_NET_HEADER_SIZE + 1];
        
    	outputBuffer[0] = ((byte)(START_BYTE&0xff));
    	outputBuffer[1] = ((byte)(DATA_FRAME&0xff));
    	outputBuffer[2] = ((byte)(frameSize >> 8 & 0xFF));
    	outputBuffer[3] = ((byte)(frameSize & 0xFF));
    	outputBuffer[4] = ((byte)packetNumber);
    	outputBuffer[5] = ((byte)totalPackets);
    	
		//write footer
		outputBuffer[TPM2_NET_HEADER_SIZE + frameSize] = BLOCK_END;		
		
		//copy payload
		System.arraycopy(data, 0, outputBuffer, TPM2_NET_HEADER_SIZE, frameSize);		
		return outputBuffer;
    }
    
    /**
     * send a cmd data packet, used as PING command
     * 
     * @param data
     * @return
     */
    public static byte[] createCmdPayload(byte[] data) {
    	int frameSize = data.length;
        byte[] outputBuffer = new byte[frameSize + TPM2_NET_HEADER_SIZE + 1];
        
    	outputBuffer[0] = ((byte)(START_BYTE&0xff));
    	outputBuffer[1] = ((byte)(CMD_FRAME&0xff));
    	outputBuffer[2] = ((byte)(frameSize >> 8 & 0xFF));
    	outputBuffer[3] = ((byte)(frameSize & 0xFF));
    	outputBuffer[4] = ((byte)0);
    	outputBuffer[5] = ((byte)0);

		//write footer
		outputBuffer[TPM2_NET_HEADER_SIZE + frameSize] = BLOCK_END;		

		//copy payload
		System.arraycopy(data, 0, outputBuffer, TPM2_NET_HEADER_SIZE, frameSize);
    	return outputBuffer;
    }
}
