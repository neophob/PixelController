/**
 * Copyright (C) 2011-2014 Michael Vogt <michu@neophob.com>
 *
 * This file is part of PixelController.
 *
 * PixelController is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PixelController is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with PixelController.  If not, see <http://www.gnu.org/licenses/>.
 */
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
import com.neophob.sematrix.core.visual.color.IColorSet;

/**
 * server interface, provide data from PixelController server
 * 
 * @author michu
 * 
 */
public interface PixConServer {

    void start();

    boolean isInitialized();

    String getVersion();

    ApplicationConfigurationHelper getConfig();

    List<IColorSet> getColorSets();

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
