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
package com.neophob.sematrix.core.output;

import com.neophob.sematrix.core.properties.ApplicationConfigurationHelper;
import com.neophob.sematrix.core.resize.PixelControllerResize;
import com.neophob.sematrix.core.visual.MatrixData;

/**
 * The Class ArduinoOutput.
 * 
 * @author michu
 */
public abstract class ArduinoOutput extends Output {

    /** The initialized. */
    protected boolean initialized;

    /** The need update. */
    protected long needUpdate;

    /** The no update. */
    protected long noUpdate;

    /**
     * Instantiates a new arduino output.
     * 
     * @param outputDeviceEnum
     *            the outputDeviceEnum
     * @param ph
     *            the ph
     * @param controller
     *            the controller
     */
    public ArduinoOutput(MatrixData matrixData, PixelControllerResize resizeHelper,
            OutputDeviceEnum outputDeviceEnum, ApplicationConfigurationHelper ph, int bpp) {
        super(matrixData, resizeHelper, outputDeviceEnum, ph, bpp);
        this.supportConnectionState = true;
    }

    /**
     * Gets the arduino error counter.
     * 
     * @return the arduino error counter
     */
    public abstract long getArduinoErrorCounter();

    /**
     * Gets the arduino buffer size.
     * 
     * @return the arduino buffer size
     */
    public abstract int getArduinoBufferSize();

    /**
     * Gets the latest heartbeat.
     * 
     * @return the latest heartbeat
     */
    public abstract long getLatestHeartbeat();

    @Override
    public boolean isConnected() {
        return this.initialized;
    }

    @Override
    public long getErrorCounter() {
        return getArduinoErrorCounter();
    }

}
