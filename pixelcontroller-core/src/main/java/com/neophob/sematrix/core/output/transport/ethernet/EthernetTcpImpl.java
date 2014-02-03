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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EthernetTcpImpl implements IEthernetTcp {

    private static final transient Logger LOG = Logger.getLogger(EthernetTcpImpl.class.getName());

    private transient Socket socket;
    private int targetPort;

    @Override
    public boolean initializeEthernet(String targetHost, int port) {
        InetAddress address;
        this.targetPort = port;
        try {
            address = InetAddress.getByName(targetHost);
        } catch (UnknownHostException e) {
            LOG.log(Level.SEVERE, "Failed to resolve <" + targetHost + ">", e);
            return false;
        }

        try {
            socket = new Socket(address, port);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Failed to create socket.", e);
            return false;
        }

        return true;
    }

    @Override
    public void sendData(byte[] data) throws IOException {
        socket.getOutputStream().write(data);
        socket.getOutputStream().flush();
    }

    @Override
    public void setTargetIp(byte[] ipAddress) {
        InetAddress iaddr;
        try {
            iaddr = InetAddress.getByAddress(ipAddress);
            SocketAddress sa = new InetSocketAddress(iaddr, targetPort);
            socket.connect(sa);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Failed to update socket.", e);
        }
    }

    @Override
    public void closePort() {
        try {
            socket.close();
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Failed to close socket.", e);
        }
    }

    @Override
    public boolean isConnected() {
        return socket.isConnected();
    }

    @Override
    public byte[] readBytes() {
        // non blocking read
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] data = new byte[2048];
            int length = 0;
            while (socket.getInputStream().available() > 0
                    && (length = socket.getInputStream().read(data)) != -1) {
                out.write(data, 0, length);
            }
            return out.toByteArray();
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Failed to read bytes from socket.", e);
        }
        return new byte[0];
    }

    @Override
    public int available() {
        try {
            return socket.getInputStream().available();
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Failed to read remaining bytes from socket.", e);
        }
        return 0;
    }
}
