package com.neophob.sematrix.core.compression.impl;

import com.neophob.sematrix.core.compression.CompressApi;

public final class CompressFactory {

	private CompressFactory() {
		//no instance
	}
	
	public static CompressApi getCompressApi() {
		return new CompressApiImpl();
	}
}
