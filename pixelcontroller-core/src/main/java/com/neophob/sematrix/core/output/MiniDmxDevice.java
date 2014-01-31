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

import java.util.logging.Level;
import java.util.logging.Logger;

import com.neophob.sematrix.core.output.minidmx.MiniDmxSerial;
import com.neophob.sematrix.core.output.transport.serial.ISerial;
import com.neophob.sematrix.core.properties.ApplicationConfigurationHelper;
import com.neophob.sematrix.core.resize.PixelControllerResize;
import com.neophob.sematrix.core.visual.MatrixData;

/**
 * Send data to a miniDMX Device via serial line
 * 
 * There is only ONE Matrix supported per output.
 * 
 * @author michu
 */
public class MiniDmxDevice extends OnePanelResolutionAwareOutput {

    /** The log. */
    private static final transient Logger LOG = Logger.getLogger(MiniDmxDevice.class.getName());

    /** The mini dmx. */
    private transient MiniDmxSerial miniDmx;

    /**
     * init the mini dmx devices.
     * 
     * @param controller
     *            the controller
     */
    public MiniDmxDevice(MatrixData matrixData, PixelControllerResize resizeHelper,
            ApplicationConfigurationHelper ph, ISerial serialPort) {
        super(matrixData, resizeHelper, OutputDeviceEnum.MINIDMX, ph, 8);

        int baud = ph.parseMiniDmxBaudRate();
        if (baud == 0) {
            // set default
            baud = 115200;
        }
        this.supportConnectionState = true;
        this.initialized = false;
        try {
            miniDmx = new MiniDmxSerial(null, matrixData.getDeviceXSize()
                    * matrixData.getDeviceYSize() * 3, baud, serialPort);
            this.initialized = miniDmx.ping();
            LOG.log(Level.INFO, "ping result: " + this.initialized);
        } catch (NoSerialPortFoundException e) {
            LOG.log(Level.WARNING, "failed to initialize serial port!");
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.neophob.sematrix.core.output.Output#update()
     */
    public void update() {
        if (initialized) {
            miniDmx.sendRgbFrame(getTransformedBuffer(), colorFormat);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.neophob.sematrix.core.output.Output#close()
     */
    @Override
    public void close() {
        if (initialized) {
            miniDmx.dispose();
        }
    }

    @Override
    public boolean isConnected() {
        return this.initialized;
    }

    @Override
    public boolean isSupportConnectionState() {
        return true;
    }

    @Override
    public String getConnectionStatus() {
        if (initialized) {
            return "Connected on port " + miniDmx.getSerialPortName();
        }
        return "Not connected!";
    }

}
