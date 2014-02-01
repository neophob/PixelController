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
