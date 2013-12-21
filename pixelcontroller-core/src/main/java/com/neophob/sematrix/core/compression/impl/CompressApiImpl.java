package com.neophob.sematrix.core.compression.impl;

import net.jpountz.lz4.LZ4Compressor;
import net.jpountz.lz4.LZ4Factory;
import net.jpountz.lz4.LZ4SafeDecompressor;

import com.neophob.sematrix.core.compression.CompressApi;

class CompressApiImpl implements CompressApi {

	private LZ4SafeDecompressor decompressor; 
	private LZ4Compressor compressor; 

	public CompressApiImpl() {
		this.compressor = LZ4Factory.fastestJavaInstance().fastCompressor();
		this.decompressor = LZ4Factory.fastestJavaInstance().safeDecompressor();
	}
	
	@Override
	public byte[] compress(byte[] in) {
		return compressor.compress(in);
	}

	@Override
	public byte[] decompress(byte[] in, int buffersize) {
		byte decompressedData[] = new byte[buffersize];		
		int decompressedLength = decompressor.decompress(in, decompressedData);			
		return decompressedData;
	}

}
