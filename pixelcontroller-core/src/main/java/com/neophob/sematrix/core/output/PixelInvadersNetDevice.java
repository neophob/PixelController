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

import com.neophob.sematrix.core.output.pixelinvaders.Lpd6803Net;
import com.neophob.sematrix.core.output.transport.ethernet.IEthernetTcp;
import com.neophob.sematrix.core.properties.Configuration;
import com.neophob.sematrix.core.resize.PixelControllerResize;
import com.neophob.sematrix.core.visual.MatrixData;

/**
 * Send data to the PixelInvaders Device. A Pixelinvaders Panel is always 8x8
 * but supports multiple panels
 * 
 * @author michu
 */
public class PixelInvadersNetDevice extends PixelInvadersDevice {

    /** The log. */
    private static final transient Logger LOG = Logger.getLogger(PixelInvadersNetDevice.class
            .getName());

    /** The lpd6803. */
    private transient Lpd6803Net lpd6803 = null;

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
    public PixelInvadersNetDevice(MatrixData matrixData, PixelControllerResize resizeHelper,
            Configuration ph, IEthernetTcp tcpImpl) {
        super(matrixData, resizeHelper, OutputDeviceEnum.PIXELINVADERS_NET, ph, 5, ph
                .getNrOfScreens());

        String ip = ph.getPixelinvadersNetIp();
        int port = ph.getPixelinvadersNetPort();
        try {
            lpd6803 = new Lpd6803Net(tcpImpl, ip, port, ph.getPixelInvadersCorrectionMap(),
                    ph.getDeviceXResolution());
            this.initialized = lpd6803.connected();
            super.setLpd6803(lpd6803);
            LOG.log(Level.INFO, "\nPING result: " + this.initialized + "\n\n");
        } catch (Exception e) {
            LOG.log(Level.WARNING, "failed to conect to pixelcontroller network device!", e);
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
            return "Connected to " + lpd6803.getDestIp() + ":" + lpd6803.getDestPort();
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
        return lpd6803.connected();
    }

}
