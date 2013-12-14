package com.neophob.sematrix;

import com.neophob.sematrix.core.output.IOutput;
import com.neophob.sematrix.core.preset.PresetSettings;
import com.neophob.sematrix.core.properties.ApplicationConfigurationHelper;
import com.neophob.sematrix.core.visual.MatrixData;

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
	
	/**
	 * 
	 * @return true if pixelcontroller is started, false if pixelcontroller is starting up
	 */
	boolean isInitialized();
	
	int[] getVisualBuffer(int nr);
	IOutput getOutput();
	float getCurrentFps();
	long getServerStartTime();
	long getRecievedOscPackets();
	long getRecievedOscBytes();

	MatrixData getMatrixData();
	
	int getCurrentPreset();
	PresetSettings getCurrentPresetSettings();
		
	void updateNeededTimeForMatrixEmulator(long t);
	void updateNeededTimeForInternalWindow(long t);
	
	void sendMessage(String[] msg);	
}
