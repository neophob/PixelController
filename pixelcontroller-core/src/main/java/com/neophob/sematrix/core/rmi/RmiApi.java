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
package com.neophob.sematrix.core.rmi;

import java.io.Serializable;
import java.util.Observer;

import com.neophob.sematrix.core.properties.Command;
import com.neophob.sematrix.osc.client.OscClientException;
import com.neophob.sematrix.osc.server.OscServerException;

/**
 * simple api to abstract the transport of data/objects between a server and a
 * client
 * 
 * @author michu
 * 
 */
public interface RmiApi {

    public enum Protocol {
        TCP, UDP
    }

    /**
     * starts a RMI server
     * 
     * @param handler
     *            notification if a client send data
     * @param port
     * @param bufferSize
     * @throws OscServerException
     */
    void startServer(Protocol protocol, Observer handler, int port) throws OscServerException;

    /**
     * starts a RMI client that connect to an RMI server
     * 
     * @param targetIp
     * @param targetPort
     * @param bufferSize
     * @throws OscClientException
     */
    void startClient(Protocol protocol, String targetIp, int targetPort, int sourcePort)
            throws OscClientException;

    /**
     * return the server ip this client use
     */
    String getClientTargetIp();

    /**
     * return the server port this client use
     */
    int getClientTargetPort();

    /**
     * shutdown
     */
    void shutdown();

    /**
     * send data to server
     * 
     * @param socket
     *            , optional target address, if null last connection will be
     *            reused
     * @param cmd
     *            the command, what to execute
     * @param data
     *            optional parameter to send an object
     * @throws OscClientException
     */
    void sendPayload(Command cmd, Serializable data) throws OscClientException;

    /**
     * recreate an object from binary data
     * 
     * @param data
     * @param type
     * @return
     */
    <T> T reassembleObject(byte[] data, Class<T> type);
}
