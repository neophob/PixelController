package com.neophob.sematrix.core.compress;

import static org.junit.Assert.*;

import org.junit.Test;

import com.neophob.sematrix.core.compression.CompressApi;
import com.neophob.sematrix.core.compression.impl.CompressFactory;

public class CompressApiTest {

	@Test
	public void testCompressApi() throws Exception {
		CompressApi compressApi = CompressFactory.getCompressApi();
		String rawData = "hello, please compress this string!                            "; 
		byte[] in = rawData.getBytes();
		byte[] out = compressApi.compress(in);
		System.out.println(in.length);
		System.out.println(out.length);
		assertTrue(out.length < in.length);
		String decompressedData = new String(compressApi.decompress(out, rawData.length()));
		assertEquals(rawData, decompressedData);
	}
}
