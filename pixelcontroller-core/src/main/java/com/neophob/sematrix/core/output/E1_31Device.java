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
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;

import com.neophob.sematrix.core.output.e131.E1_31DataPacket;
import com.neophob.sematrix.core.output.transport.ethernet.IEthernetUdp;
import com.neophob.sematrix.core.properties.Configuration;
import com.neophob.sematrix.core.resize.PixelControllerResize;
import com.neophob.sematrix.core.visual.MatrixData;

/**
 * Basic E1.31 device support. Can be tested with
 * http://sourceforge.net/projects/sacnview/
 * 
 * @author michu
 * 
 */
public class E1_31Device extends AbstractDmxDevice {

    private static final transient Logger LOG = Logger.getLogger(E1_31Device.class.getName());
    private static final transient String MULTICAST_START = "239.255.";

    private transient E1_31DataPacket dataPacket = new E1_31DataPacket();
    private transient IEthernetUdp udpImpl;

    // multicast or unicast?
    private boolean sendMulticast = false;

    private int errorCounter = 0;

    /**
     * 
     * @param controller
     */
    public E1_31Device(MatrixData matrixData, PixelControllerResize resizeHelper,
            Configuration ph, IEthernetUdp udpImpl) {
        super(matrixData, resizeHelper, OutputDeviceEnum.E1_31, ph, 8, ph.getNrOfScreens());
        this.displayOptions = ph.getE131Device();
        this.udpImpl = udpImpl;

        // Get dmx specific config
        this.pixelsPerUniverse = ph.getE131PixelsPerUniverse();

        String ip = ph.getE131Ip();
        String sendMode = "Unicast";
        if (StringUtils.startsWith(ip, MULTICAST_START)) {
            this.sendMulticast = true;
            sendMode = "Multicast";
        }
        try {
            this.targetAdress = InetAddress.getByName(ip);
        } catch (UnknownHostException e) {
            // ignored
        }
        this.firstUniverseId = ph.getE131StartUniverseId();
        calculateNrOfUniverse();

        if (udpImpl.initializeEthernet(ip, E1_31DataPacket.E131_PORT)) {
            this.initialized = true;
            LOG.log(Level.INFO, "E1.31 device initialized, send mode: " + sendMode + ", use "
                    + this.displayOptions.size() + " panels");
        } else {
            LOG.log(Level.WARNING, "failed to initialize E1.31 device");
        }
    }

    @Override
    public void close() {
        this.udpImpl.closePort();
    }

    @Override
    protected void sendBufferToReceiver(int universeId, byte[] buffer) {
        if (this.initialized) {

            if (this.sendMulticast) {
                // multicast - universe number must be in lower 2 bytes
                byte[] addr = new byte[4];
                addr[0] = (byte) 239;
                addr[1] = (byte) 255;
                addr[2] = (byte) (universeId >> 8);
                addr[3] = (byte) (universeId & 255);
                this.udpImpl.setTargetIp(addr);
            }

            try {
                byte[] data = dataPacket.assembleNewE131Packet(this.sequenceID++, universeId,
                        buffer);
                this.udpImpl.sendData(data);
            } catch (IOException e) {
                errorCounter++;
                LOG.log(Level.WARNING, "failed to send E1.31 data.", e);
            }
        }
    }

    @Override
    public long getErrorCounter() {
        return errorCounter;
    }

    /**
     * unicast or multicast mode?
     * 
     * @return
     */
    public boolean isSendMulticast() {
        return sendMulticast;
    }

}
