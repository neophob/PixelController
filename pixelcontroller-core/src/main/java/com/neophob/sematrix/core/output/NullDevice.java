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

import com.neophob.sematrix.core.properties.Configuration;
import com.neophob.sematrix.core.resize.PixelControllerResize;
import com.neophob.sematrix.core.visual.MatrixData;

/**
 * does nothin.
 * 
 * @author michu
 */
public class NullDevice extends Output {

    /**
     * init the null devices.
     * 
     * @param controller
     *            the controller
     */
    public NullDevice(MatrixData matrixData, PixelControllerResize resizeHelper,
            Configuration ph) {
        super(matrixData, resizeHelper, OutputDeviceEnum.NULL, ph, 8);
        this.supportConnectionState = true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.neophob.sematrix.core.output.Output#update()
     */
    public void update() {
        // nothing todo
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.neophob.sematrix.core.output.Output#close()
     */
    @Override
    public void close() {
        // nothing todo
    }

    @Override
    public boolean isConnected() {
        return true;
    }

    @Override
    public String getConnectionStatus() {
        return "Connected on port NULL";
    }

}
