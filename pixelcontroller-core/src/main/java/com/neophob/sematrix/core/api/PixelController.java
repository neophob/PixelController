package com.neophob.sematrix.core.api;

import com.neophob.sematrix.core.output.Output;
import com.neophob.sematrix.core.properties.ApplicationConfigurationHelper;

public interface PixelController {

	void start();
	
	void stop();
	
	boolean isInitialized();
	
	float getFps();
	
	ApplicationConfigurationHelper getConfig();
	
	Output getOutput();
	
	String getVersion();
}
