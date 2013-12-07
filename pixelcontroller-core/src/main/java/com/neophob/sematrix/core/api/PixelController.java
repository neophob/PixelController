package com.neophob.sematrix.core.api;

import com.neophob.sematrix.core.output.Output;
import com.neophob.sematrix.core.properties.ApplicationConfigurationHelper;

/**
 * the pixelcontroller API
 * 
 * @author michu
 *
 */
public interface PixelController {

	/**
	 * start pixelcontroller, initialize application and start thread
	 */
	void start();
	
	/**
	 * shutdown pixelcontroller
	 */
	void stop();
	
	/**
	 * 
	 * @return true if pixelcontroller is initialized and running in the mainloop
	 */
	boolean isInitialized();
	
	/**
	 * 
	 * @return current framerate
	 */
	float getFps();
	
	/**
	 * 
	 * @return configuration of pixelcontroller
	 */
	ApplicationConfigurationHelper getConfig();
	
	/**
	 * 
	 * @return selected output
	 */
	Output getOutput();
	
	/**
	 * 
	 * @return pixelcontroller version
	 */
	String getVersion();
}
