package com.neophob.sematrix.core.api;

import java.util.List;
import java.util.Observer;

import com.neophob.sematrix.core.glue.FileUtils;
import com.neophob.sematrix.core.jmx.PixelControllerStatusMBean;
import com.neophob.sematrix.core.output.IOutput;
import com.neophob.sematrix.core.preset.PresetService;
import com.neophob.sematrix.core.properties.ApplicationConfigurationHelper;
import com.neophob.sematrix.core.sound.ISound;
import com.neophob.sematrix.core.visual.MatrixData;
import com.neophob.sematrix.core.visual.OutputMapping;
import com.neophob.sematrix.core.visual.VisualState;
import com.neophob.sematrix.core.visual.color.ColorSet;

/**
 * the pixelcontroller API
 * 
 * @author michu
 *
 */
public interface PixelController {

	static final String ZEROCONF_NAME = "PixelController";

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

	long getProcessedFrames();

	/**
	 * 
	 * @return configuration of pixelcontroller
	 */
	ApplicationConfigurationHelper getConfig();
	
	List<ColorSet> getColorSets();
	
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
	List<OutputMapping> getAllOutputMappings();
	
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
	
	FileUtils getFileUtils();
	
	/**
	 * return internal and device size
	 * @return
	 */
	MatrixData getMatrix();
	VisualState getVisualState();
	
	ISound getSoundImplementation();
	
	List<String> getGuiState();
	
	/**
	 * request a gui refresh, the whole visual state will be sent
	 */
	void refreshGuiState();
	
	/**
	 * register observer, get informed if the visual state changes
	 * @param o
	 */
	void observeVisualState(Observer o);
	void stopObserveVisualState(Observer o);

}
