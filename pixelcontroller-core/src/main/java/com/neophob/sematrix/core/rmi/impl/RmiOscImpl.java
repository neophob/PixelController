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
package com.neophob.sematrix.core.rmi.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.neophob.sematrix.core.jmx.PacketAndBytesStatictics;
import com.neophob.sematrix.core.properties.Command;
import com.neophob.sematrix.core.rmi.RmiApi;
import com.neophob.sematrix.core.rmi.compression.CompressApi;
import com.neophob.sematrix.core.rmi.compression.DecompressException;
import com.neophob.sematrix.core.rmi.compression.impl.CompressFactory;
import com.neophob.sematrix.osc.client.OscClientException;
import com.neophob.sematrix.osc.client.PixOscClient;
import com.neophob.sematrix.osc.client.impl.OscClientFactory;
import com.neophob.sematrix.osc.model.OscMessage;
import com.neophob.sematrix.osc.server.OscServerException;
import com.neophob.sematrix.osc.server.PixOscServer;
import com.neophob.sematrix.osc.server.impl.OscServerFactory;

class RmiOscImpl implements RmiApi, PacketAndBytesStatictics {

    private static final Logger LOG = Logger.getLogger(RmiOscImpl.class.getName());

    private PixOscServer oscServer;
    private PixOscClient oscClient;
    private int clientTargetPort = 0;
    private String clientTargetIp = "";

    private CompressApi compressor;
    private boolean useCompression;
    private int bufferSize;

    private int packetCount;
    private long packetBytesRecieved;

    public RmiOscImpl(boolean useCompression, int bufferSize) {
        this.compressor = CompressFactory.getCompressApi();
        this.useCompression = useCompression;
        this.bufferSize = bufferSize;
        LOG.log(Level.INFO, "Start new OSC RMI Object, use compression: " + useCompression);
    }

    @Override
    public void startServer(Protocol protocol, Observer handler, int port)
            throws OscServerException {
        if (protocol == Protocol.TCP) {
            this.oscServer = OscServerFactory.createServerTcp(handler, port, bufferSize);
        } else {
            this.oscServer = OscServerFactory.createServerUdp(handler, port, bufferSize);
        }
        this.oscServer.startServer();
    }

    @Override
    public void startClient(Protocol protocol, String targetIp, int targetPort, int sourcePort)
            throws OscClientException {

        if (this.oscClient != null && oscClient.isConnected()) {
            LOG.log(Level.INFO, "Disconnect current client: " + oscClient);
            this.oscClient.disconnect();
        }
        if (protocol == Protocol.TCP) {
            this.oscClient = OscClientFactory.createClientTcp(targetIp, targetPort, sourcePort,
                    bufferSize);
        } else {
            this.oscClient = OscClientFactory.createClientUdp(targetIp, targetPort, sourcePort,
                    bufferSize);
        }
        this.clientTargetIp = targetIp;
        this.clientTargetPort = targetPort;
    }

    @Override
    public void shutdown() {
        if (oscServer != null) {
            oscServer.stopServer();
        }
        if (oscClient != null) {
            try {
                oscClient.disconnect();
            } catch (OscClientException e) {
                // ignored
            }
        }
    }

    @Override
    public void sendPayload(Command cmd, Serializable data) throws OscClientException {
        if (this.oscClient == null) {
            throw new OscClientException("client not initialized");
        }
        OscMessage reply = new OscMessage(cmd.getValidCommand().toString(), cmd.getParameter(),
                convertFromObject(data));
        // LOG.log(Level.INFO,
        // "send "+cmd.getValidCommand()+" reply size: "+reply.getMessageSize());
        packetCount++;
        packetBytesRecieved += reply.getMessageSize();

        if (packetCount % 1000 == 0) {
            LOG.log(Level.INFO, "OSC Send statistics, sent packages: " + packetCount + " ("
                    + packetBytesRecieved / 1024 + "kb)");
        }

        this.oscClient.sendMessage(reply);
    }

    @Override
    public <T> T reassembleObject(byte[] data, Class<T> type) {
        try {
            return convertToObject(data, type);
        } catch (Exception e) {
            LOG.log(Level.WARNING, "Failed to convert object", e);
        }
        return null;
    }

    private byte[] convertFromObject(Serializable s) {
        if (s == null) {
            return null;
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out = null;
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(s);
            if (!useCompression) {
                return bos.toByteArray();
            }

            return compressor.compress(bos.toByteArray());

        } catch (IOException e) {
            LOG.log(Level.WARNING, "Failed to serializable object", e);
            return new byte[0];
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException ex) {
                // ignore close exception
            }
            try {
                bos.close();
            } catch (IOException ex) {
                // ignore close exception
            }
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T convertToObject(byte[] input, Class<T> type) throws IOException,
            ClassNotFoundException {

        ByteArrayInputStream bis;
        if (!useCompression) {
            bis = new ByteArrayInputStream(input);
        } else {
            try {
                bis = new ByteArrayInputStream(compressor.decompress(input, bufferSize));
            } catch (DecompressException e) {
                LOG.log(Level.INFO, "Failed to decompress data, disable compression");
                useCompression = false;
                bis = new ByteArrayInputStream(input);
            }
        }
        ObjectInput in = null;
        try {
            in = new ObjectInputStream(bis);
            return (T) in.readObject();
        } finally {
            try {
                bis.close();
            } catch (IOException ex) {
                // ignore close exception
            }
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                // ignore close exception
            }
        }
    }

    @Override
    public String getClientTargetIp() {
        return clientTargetIp;
    }

    @Override
    public int getClientTargetPort() {
        return clientTargetPort;
    }

    @Override
    public int getPacketCounter() {
        return packetCount;
    }

    @Override
    public long getBytesRecieved() {
        return packetBytesRecieved;
    }
}
