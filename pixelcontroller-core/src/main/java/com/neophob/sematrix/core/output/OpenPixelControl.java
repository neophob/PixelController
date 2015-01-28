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

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.neophob.sematrix.core.output.transport.ethernet.IEthernetTcp;
import com.neophob.sematrix.core.properties.Configuration;
import com.neophob.sematrix.core.resize.PixelControllerResize;
import com.neophob.sematrix.core.visual.MatrixData;

import java.net.SocketException;

/**
 * Send frames out via TCP
 *
 * @author rj
 *
 */
public class OpenPixelControl extends OnePanelResolutionAwareOutput {

    private static final transient Logger LOG = Logger.getLogger(OpenPixelControl.class.getName());

    private transient IEthernetTcp tcpImpl;
    private String targetHost;
    private int targetPort;
    private int connectionErrorCounter;
    protected boolean initialized;

    public OpenPixelControl(MatrixData matrixData, PixelControllerResize resizeHelper, Configuration ph,
            IEthernetTcp tcpImpl) {
        super(matrixData, resizeHelper, OutputDeviceEnum.OPEN_PIXEL_CONTROL, ph, 8);

        targetHost = ph.getOpcIp();
        targetPort = ph.getOpcPort();
        this.tcpImpl = tcpImpl;

        LOG.log(Level.INFO, "Connect to target " + targetHost + ":" + targetPort);
        if (this.tcpImpl.initializeEthernet(targetHost, targetPort)) {
            initialized = true;
            LOG.log(Level.INFO, "initialized: " + this.initialized);
        } else {
            LOG.log(Level.INFO,
                    "Failed to initialize OPC target, verify the destination settings");
        }
    }

    @Override
    public void update() {
        if (initialized) {
            byte[] buffer = OutputHelper.convertBufferTo24bit(getTransformedBuffer(), colorFormat, 4);

            //Add the OPC header
            buffer[0] = (byte) 0 & 0xFF; // address
            buffer[1] = (byte) 0 & 0xFF; // command 0x00 (draw pixels)
            buffer[2] = (byte) (((buffer.length-4) >> 8) & 0xFF); //high byte data length
            buffer[3] = (byte) ((buffer.length-4) & 0xFF); //low byte data length

            writeData(buffer);
        }
        else {
            // try to reconnect
            if (this.tcpImpl.initializeEthernet(targetHost, targetPort)) {
                initialized = true;
                LOG.log(Level.INFO, "Reinitialized TCP Socket");
            } else {
                LOG.log(Level.INFO,
                "Failed to reinitialize OPC target, verify the destination settings");
            }
        }
    }

    protected synchronized void writeData(byte[] cmdfull) {
        try {
            tcpImpl.sendData(cmdfull);
            initialized = true;
        } catch (SocketException se) {
            LOG.log(Level.INFO, "Error sending network data!", se);
            connectionErrorCounter++;
            initialized = false;
        } catch (Exception e) {
            connectionErrorCounter++;
            initialized = false;
            LOG.log(Level.INFO, "Connection error!", e);
        }
    }

    @Override
    public boolean isSupportConnectionState() {
        return true;
    }

    @Override
    public boolean isConnected() {
        return initialized;
    }

    @Override
    public String getConnectionStatus() {
        if (initialized) {
            return "Target IP " + targetHost + ":" + targetPort;
        }
        return "Not connected!";
    }

    @Override
    public void close() {
        tcpImpl.closePort();
    }

    @Override
    public long getErrorCounter() {
        return connectionErrorCounter;
    }

}
