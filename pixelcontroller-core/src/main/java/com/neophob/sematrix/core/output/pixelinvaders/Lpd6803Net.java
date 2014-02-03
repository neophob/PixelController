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
package com.neophob.sematrix.core.output.pixelinvaders;

import java.net.SocketException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.neophob.sematrix.core.output.gamma.RGBAdjust;
import com.neophob.sematrix.core.output.transport.ethernet.IEthernetTcp;

/**
 * http://blog.mafr.de/2010/03/14/tcp-for-low-latency-applications/ -use apache
 * mina (http://mina.apache.org/) as tcp connection pool -disable Nagle’s
 * Algorithm (Socket.setNoDelay(true)) Here's a little hack to disable the nagle
 * algo. Field f = clientConnection.getClass().getDeclaredField("socket");
 * f.setAccessible(true); Socket s = (Socket)f.get(clientConnection);
 * s.setTcpNoDelay(true); -> I didn't see any improvements in my setup, (wlan,
 * 9.3ms avg of 15'000 connections)
 * 
 * PIXELCONTROLLER--TCP--RPI(SER2NET)--USB--TEENSY--SPI--PIXELMODULE communicate
 * with a arduino via tcp port/ser2net<b <br>
 * <br>
 * 
 * @author Michael Vogt / neophob.com
 */
public class Lpd6803Net extends Lpd6803Common {

    /** The log. */
    private static final transient Logger LOG = Logger.getLogger(Lpd6803Net.class.getName());

    /** internal lib version. */
    public static final transient String VERSION = "1.4";

    // maximal network latency
    public static final transient int MAX_ACK_WAIT = 80;
    public static final transient int WAIT_PER_LOOP = 6;

    private String destIp;
    private int destPort;

    private transient IEthernetTcp tcpImpl;

    /**
     * Create a new instance to communicate with the lpd6803 device.
     * 
     * @param _app
     *            the _app
     * @param portName
     *            the port name
     * @param baud
     *            the baud
     * @throws Exception
     *             the no serial port found exception
     */
    public Lpd6803Net(IEthernetTcp tcpImpl, String destIp, int destPort,
            Map<Integer, RGBAdjust> correctionMap, int panelSize) throws Exception {
        super(panelSize, panelSize);
        LOG.log(Level.INFO, "Initialize LPD6803 net lib v{0}", VERSION);

        this.tcpImpl = tcpImpl;
        this.destIp = destIp;
        this.destPort = destPort;
        this.correctionMap = correctionMap;

        // output connection
        LOG.log(Level.INFO, "Connect to target " + destIp + ":" + destPort);
        if (this.tcpImpl.initializeEthernet(destIp, destPort)) {
            this.initialized = this.ping();
            LOG.log(Level.INFO, "initialized: " + this.initialized);
        } else {
            LOG.log(Level.INFO,
                    "Failed to initialize LPD6803 net lib, verify the destination settings");
        }
    }

    /**
     * clean up library.
     */
    public void dispose() {
        tcpImpl.closePort();
    }

    /**
     * return the version of the library.
     * 
     * @return String version number
     */
    public String version() {
        return VERSION;
    }

    /**
     * how may times the serial response was missing / invalid.
     * 
     * @return the ack errors
     */
    public long getAckErrors() {
        return ackErrors;
    }

    /**
     * send the data to the serial port.
     * 
     * @param cmdfull
     *            the cmdfull
     * @throws WriteDataException
     *             the serial port exception
     */
    protected synchronized void writeData(byte[] cmdfull) throws WriteDataException {
        try {
            tcpImpl.sendData(cmdfull);
            initialized = true;
        } catch (SocketException se) {
            if (connectionErrorCounter % 10 == 9) {
                // try to reconnect
                LOG.log(Level.INFO, "Reinit TCP Client");
                tcpImpl.initializeEthernet(destIp, destPort);
                return;
            }
            // LOG.log(Level.INFO, "Error sending network data!", se);
            connectionErrorCounter++;
            initialized = false;
            throw new WriteDataException("cannot send serial data, errorNr: "
                    + connectionErrorCounter, se);
        } catch (Exception e) {
            connectionErrorCounter++;
            initialized = false;
            throw new WriteDataException("cannot send serial data, errorNr: "
                    + connectionErrorCounter, e);
        }
    }

    /**
     * read data from network, wait for ACK.
     * 
     * @return true if ack received, false if not
     */
    protected synchronized boolean waitForAck() {
        LOG.log(Level.INFO, "Wait for ACK, max " + (MAX_ACK_WAIT * WAIT_PER_LOOP) + "ms");
        int currentDelay = 0;
        byte[] msg = null;

        // wait maximal MAX_ACK_WAIT ms until we get a reply
        while (currentDelay < MAX_ACK_WAIT && (msg == null || msg.length < 4)) {
            sleep(WAIT_PER_LOOP);
            currentDelay += WAIT_PER_LOOP;
            msg = tcpImpl.readBytes();
            LOG.log(Level.INFO, "got reply: " + msg.length + " bytes: " + new String(msg));
        }

        if (msg == null) {
            LOG.log(Level.WARNING,
                    "No reply recieved, verify that ser2net is started on the target machine!");
            ackErrors++;
            return false;
        }

        String reply = new String(msg);
        LOG.log(Level.INFO, "got ACK: {0}", reply);

        if (reply.contains("AK")) {
            return true;
        }
        return false;
    }

    /**
     * @return the destination ip
     */
    public String getDestIp() {
        return destIp;
    }

    /**
     * 
     * @return the destination port
     */
    public int getDestPort() {
        return destPort;
    }

    @Override
    protected byte[] getReplyFromController() {
        return tcpImpl.readBytes();
    }
}
