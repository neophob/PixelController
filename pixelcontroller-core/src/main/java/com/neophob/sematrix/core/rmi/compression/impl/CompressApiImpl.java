/**
 * Copyright (C) 2011-2014 Michael Vogt <michu@neophob.com>
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
package com.neophob.sematrix.core.rmi.compression.impl;

import net.jpountz.lz4.LZ4Compressor;
import net.jpountz.lz4.LZ4Factory;
import net.jpountz.lz4.LZ4SafeDecompressor;

import com.neophob.sematrix.core.rmi.compression.CompressApi;
import com.neophob.sematrix.core.rmi.compression.DecompressException;

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
	public byte[] decompress(byte[] in, int buffersize) throws DecompressException {
		byte[] decompressedData = new byte[buffersize];	
		try {
			decompressor.decompress(in, decompressedData);			
			return decompressedData;			
		} catch (Exception e) {
			throw new DecompressException(e);
		}
	}

}
