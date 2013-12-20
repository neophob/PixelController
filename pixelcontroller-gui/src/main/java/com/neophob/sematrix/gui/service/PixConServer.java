package com.neophob.sematrix.gui.service;

import java.util.List;
import java.util.Observer;

import com.neophob.sematrix.core.glue.FileUtils;
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

	void start();
	boolean isInitialized();
	String getVersion();
	
	ApplicationConfigurationHelper getConfig();
	List<ColorSet> getColorSets();
		
	int[] getVisualBuffer(int nr);
	int[] getOutputBuffer(int nr);
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
	
	PresetSettings getCurrentPresetSettings();
	
	FileUtils getFileUtils();
		
	void updateNeededTimeForMatrixEmulator(long t);
	void updateNeededTimeForInternalWindow(long t);
	
	void sendMessage(String[] msg);	
	
	void refreshGuiState();
	void observeVisualState(Observer o);
	float getSetupSteps();
}
