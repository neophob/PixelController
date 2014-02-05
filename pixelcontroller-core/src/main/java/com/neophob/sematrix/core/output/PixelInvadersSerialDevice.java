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

import java.util.logging.Level;
import java.util.logging.Logger;

import com.neophob.sematrix.core.output.pixelinvaders.Lpd6803Serial;
import com.neophob.sematrix.core.output.transport.serial.ISerial;
import com.neophob.sematrix.core.properties.Configuration;
import com.neophob.sematrix.core.resize.PixelControllerResize;
import com.neophob.sematrix.core.visual.MatrixData;

/**
 * Send data to the PixelInvaders Device. A Pixelinvaders Panel is always 8x8
 * but supports multiple panels
 * 
 * @author michu
 */
public class PixelInvadersSerialDevice extends PixelInvadersDevice {

    /** The log. */
    private static final transient Logger LOG = Logger.getLogger(PixelInvadersSerialDevice.class
            .getName());

    /** The lpd6803. */
    private Lpd6803Serial lpd6803 = null;

    /**
     * init the lpd6803 devices.
     * 
     * @param controller
     *            the controller
     * @param displayOptions
     *            the display options
     * @param colorFormat
     *            the color format
     */
    public PixelInvadersSerialDevice(MatrixData matrixData, PixelControllerResize resizeHelper,
            Configuration ph, ISerial serial) {
        super(matrixData, resizeHelper, OutputDeviceEnum.PIXELINVADERS, ph, 5, ph.getNrOfScreens());

        try {
            lpd6803 = new Lpd6803Serial(serial, ph.getPixelInvadersBlacklist(),
                    ph.getPixelInvadersCorrectionMap(), ph.getDeviceXResolution());
            this.initialized = lpd6803.isInitialized();
            super.setLpd6803(lpd6803);
            LOG.log(Level.INFO, "\nPING result: " + this.initialized + "\n\n");
        } catch (NoSerialPortFoundException e) {
            LOG.log(Level.WARNING, "failed to initialize serial port!");
        } catch (Throwable e) {
            // catch really ALL excetions here!
            LOG.log(Level.SEVERE, "\n\n\n\nSERIOUS ERROR, check your RXTX installation!", e);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e1) {
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.neophob.sematrix.core.output.Output#update()
     */
    public void update() {
        if (initialized) {
            sendPayload();
        }
    }

    @Override
    public String getConnectionStatus() {
        if (initialized) {
            return "Connected on port " + lpd6803.getSerialPortName();
        }
        return "Not connected!";
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.neophob.sematrix.core.output.Output#close()
     */
    @Override
    public void close() {
        if (initialized) {
            lpd6803.dispose();
        }
    }

    @Override
    public boolean isSupportConnectionState() {
        return true;
    }

    @Override
    public boolean isConnected() {
        if (initialized) {
            return lpd6803.connected();
        }
        return false;
    }

}
