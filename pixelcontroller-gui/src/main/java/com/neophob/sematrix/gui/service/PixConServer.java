package com.neophob.sematrix.gui.service;

import java.util.List;
import java.util.Observer;

import com.neophob.sematrix.core.output.IOutput;
import com.neophob.sematrix.core.preset.PresetSettings;
import com.neophob.sematrix.core.properties.ApplicationConfigurationHelper;
import com.neophob.sematrix.core.sound.ISound;
import com.neophob.sematrix.core.visual.MatrixData;
import com.neophob.sematrix.core.visual.OutputMapping;
import com.neophob.sematrix.core.visual.color.ColorSet;

/**
 * server interface, provide data from PixelController server 
 * @author michu
 *
 */
public interface PixConServer {

	/**
	 * start pixelcontroller core
	 * 
	 * @param handler
	 */
	void startCore();
	
	/**
	 * 
	 * @return pixelcontroller core version
	 */
	String getVersion();
	
	/**
	 * 
	 * @return PixelController configuration
	 */
	ApplicationConfigurationHelper getConfig();
	List<ColorSet> getColorSets();
	
	/**
	 * 
	 * @return true if pixelcontroller is started, false if pixelcontroller is starting up
	 */
	boolean isInitialized();
	
	int[] getVisualBuffer(int nr);
	IOutput getOutput();
	List<OutputMapping> getAllOutputMappings();
	
	float getCurrentFps();
	long getFrameCount();
	long getServerStartTime();
	long getRecievedOscPackets();
	long getRecievedOscBytes();

	ISound getSoundImplementation();
	MatrixData getMatrixData();
	
	int getNrOfVisuals();
	
	int getCurrentPreset();
	PresetSettings getCurrentPresetSettings();
		
	void updateNeededTimeForMatrixEmulator(long t);
	void updateNeededTimeForInternalWindow(long t);
	
	void sendMessage(String[] msg);	
	
	void refreshGuiState();
	void registerObserver(Observer o);
}
