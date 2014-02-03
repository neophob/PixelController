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
package com.neophob.sematrix.core.output.transport.ethernet;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EthernetUdpImpl implements IEthernetUdp {

    private static final transient Logger LOG = Logger.getLogger(EthernetUdpImpl.class.getName());

    private transient DatagramPacket packet;
    private transient DatagramSocket dsocket;
    private boolean initialized = false;

    @Override
    public boolean initializeEthernet(String targetHost, int targetPort) {
        InetAddress address;
        try {
            address = InetAddress.getByName(targetHost);
        } catch (UnknownHostException e) {
            LOG.log(Level.SEVERE, "Failed to resolve <" + targetHost + ">", e);
            return false;
        }
        packet = new DatagramPacket(new byte[0], 0, address, targetPort);
        try {
            dsocket = new DatagramSocket();
        } catch (SocketException e) {
            LOG.log(Level.SEVERE, "Failed to create socket", e);
            return false;
        }

        this.initialized = true;
        return true;
    }

    @Override
    public void sendData(byte[] data) throws IOException {
        if (!this.initialized) {
            return;
        }
        packet.setData(data);
        packet.setLength(data.length);
        dsocket.send(packet);
    }

    @Override
    public void closePort() {
        if (this.initialized) {
            dsocket.close();
        }
    }

    @Override
    public void setTargetIp(byte[] ipAddress) {
        try {
            InetAddress iaddr = InetAddress.getByAddress(ipAddress);
            packet.setAddress(iaddr);
        } catch (UnknownHostException e) {
            LOG.log(Level.WARNING, "Failed to set target address!", e);
        }
    }

}
