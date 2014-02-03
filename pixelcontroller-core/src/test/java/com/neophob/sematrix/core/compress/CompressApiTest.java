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
package com.neophob.sematrix.core.compress;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.neophob.sematrix.core.rmi.compression.CompressApi;
import com.neophob.sematrix.core.rmi.compression.impl.CompressFactory;

public class CompressApiTest {

	@Test
	public void testCompressApi() throws Exception {
		CompressApi compressApi = CompressFactory.getCompressApi();
		String rawData = "hello, please compress this string!                            "; 
		byte[] in = rawData.getBytes();
		byte[] out = compressApi.compress(in);
		assertTrue(out.length < in.length);
		String decompressedData = new String(compressApi.decompress(out, rawData.length()));
		assertEquals(rawData, decompressedData);
	}
}
