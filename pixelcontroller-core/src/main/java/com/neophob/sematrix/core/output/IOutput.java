/**
 * Copyright (C) 2011-2013 Michael Vogt <michu@neophob.com>
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
package com.neophob.sematrix.core.output;

import java.io.Serializable;

import com.neophob.sematrix.core.output.gamma.GammaType;
import com.neophob.sematrix.core.resize.Resize.ResizeName;
import com.neophob.sematrix.core.visual.VisualState;

/**
 * Output device interface
 * 
 * @author michu
 * 
 */
public interface IOutput extends Serializable {

    /**
     * 
     * @return Output type
     */
    OutputDeviceEnum getType();

    /**
     * connection oriented device?
     * 
     * @return
     */
    boolean isSupportConnectionState();

    /**
     * 
     * @return connection state
     */
    boolean isConnected();

    /**
     * if device supports a connection status, overwrite me. examples: connected
     * to /dev/aaa or IP Adress: 1.2.3.4
     */
    String getConnectionStatus();

    /**
     * configured gamma type
     * 
     * @return
     */
    GammaType getGammaType();

    /**
     * 
     * @return color resolution
     */
    int getBpp();

    /**
     * @return how many errors occurred (if supported)
     */
    long getErrorCounter();

    /**
     * get buffer for a output, this method respect the mapping and brightness
     * 
     * @param screenNr
     *            the screen nr
     * @return the buffer for screen
     */
    int[] getBufferForScreen(int screenNr, boolean applyGamma);

    /**
     * Update the output device
     */
    void update();

    /**
     * fill the the preparedBufferMap instance with int[] buffers for all
     * screens
     */
    void prepareOutputBuffer(VisualState vs);

    void switchBuffers();

    int[] resizeBufferForDevice(int[] buffer, ResizeName resizeName, int deviceXSize,
            int deviceYSize);

    void close();
}
