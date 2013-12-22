package com.neophob.sematrix.core.rmi.impl;

import com.neophob.sematrix.core.rmi.RmiApi;

public final class RmiFactory {

	private RmiFactory() {
		//no instance
	}
	
	public static RmiApi getRmiApi(boolean useCompression, int bufferSize) {
		return new RmiOscImpl(useCompression, bufferSize);
	}
}
