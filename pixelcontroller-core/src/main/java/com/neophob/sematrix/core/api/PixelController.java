package com.neophob.sematrix.core.api;

import com.neophob.sematrix.core.jmx.PixelControllerStatusMBean;
import com.neophob.sematrix.core.output.IOutput;
import com.neophob.sematrix.core.preset.PresetService;
import com.neophob.sematrix.core.properties.ApplicationConfigurationHelper;
import com.neophob.sematrix.core.visual.MatrixData;

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
	 * @return pixelcontroller jmx statistics
	 */
	PixelControllerStatusMBean getPixConStat();
	
	/**
	 * 
	 * @return selected output
	 */
	IOutput getOutput();
	
	/**
	 * 
	 * @return pixelcontroller version
	 */
	String getVersion();
	
	/**
	 * preset service
	 * @return
	 */
	PresetService getPresetService();
	
	/**
	 * return internal and device size
	 * @return
	 */
	MatrixData getMatrix();
}
