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
package com.neophob.sematrix.core.api;

import java.util.List;
import java.util.Observer;

import com.neophob.sematrix.core.glue.FileUtils;
import com.neophob.sematrix.core.jmx.PixelControllerStatusMBean;
import com.neophob.sematrix.core.output.IOutput;
import com.neophob.sematrix.core.preset.PresetService;
import com.neophob.sematrix.core.properties.Configuration;
import com.neophob.sematrix.core.sound.ISound;
import com.neophob.sematrix.core.visual.MatrixData;
import com.neophob.sematrix.core.visual.OutputMapping;
import com.neophob.sematrix.core.visual.VisualState;
import com.neophob.sematrix.core.visual.color.IColorSet;

/**
 * the pixelcontroller API
 * 
 * @author michu
 * 
 */
public interface PixelController {

    String ZEROCONF_NAME = "PixelController";

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
     * @return true if pixelcontroller is initialized and running in the
     *         mainloop
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
    Configuration getConfig();

    List<IColorSet> getColorSets();

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
     * 
     * @return
     */
    PresetService getPresetService();

    FileUtils getFileUtils();

    /**
     * return internal and device size
     * 
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
     * 
     * @param o
     */
    void observeVisualState(Observer o);

    void stopObserveVisualState(Observer o);

}
