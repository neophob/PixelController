final byte START_BYTE = (byte) 0x9c;
final byte DATA_FRAME = (byte) 0xDA;
final byte BLOCK_END = (byte) 0x36;
final int HEADER_SIZE = 7;

/**
 * Create TPM2 Protocol
 * 
 * @param frame
 * @return
 */
public byte[] doProtocol(int[] frame, int currentPacket, int totalPacket) {
  //3 colors per pixel
  int index = 0;
  int frameSize = frame.length*3;
  byte[] outputBuffer = new byte[frameSize + HEADER_SIZE];

  //Start-Byte
  outputBuffer[index++] = START_BYTE;

  //Ident-Byte
  outputBuffer[index++] = DATA_FRAME;

  //Raw Data Size
  byte frameSizeByteHigh = (byte) (frameSize >> 8 & 0xff);
  byte frameSizeByteLow = (byte) (frameSize & 0xff);
  outputBuffer[index++] = frameSizeByteHigh;
  outputBuffer[index++] = frameSizeByteLow;
  
  outputBuffer[index++] = (byte)currentPacket;
  outputBuffer[index++] = (byte)totalPacket;

  //Raw Data
  for (int i = 0; i < frame.length; i++) {
    outputBuffer[index++] = (byte) ((frame[i] >> 16) & 255);
    outputBuffer[index++] = (byte) ((frame[i] >> 8) & 255);
    outputBuffer[index++] = (byte) (frame[i] & 255);
  }

  //Block-End-Byte
  outputBuffer[index] = BLOCK_END;

  return outputBuffer;
}

