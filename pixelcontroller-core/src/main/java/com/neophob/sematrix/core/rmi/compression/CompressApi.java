package com.neophob.sematrix.core.rmi.compression;

public interface CompressApi {

	/**
	 * compress data
	 * @param in
	 * @return
	 */
	byte[] compress(byte[] in);

	/**
	 * decompress data
	 * @param in, data to decompress
	 * @param bufferSize, should have enough space to keep the uncompressed data
	 * @return
	 */
	byte[] decompress(byte[] in, int bufferSize) throws DecompressException;
	
}
