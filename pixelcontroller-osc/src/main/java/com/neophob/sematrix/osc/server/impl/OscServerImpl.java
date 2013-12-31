/**
 * Copyright (C) 2011-2013 Michael Vogt <michu@neophob.com>
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
package com.neophob.sematrix.osc.server.impl;

import java.io.IOException;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.neophob.sematrix.osc.model.OscMessage;
import com.neophob.sematrix.osc.server.OscServerException;

import de.sciss.net.OSCListener;
import de.sciss.net.OSCMessage;
import de.sciss.net.OSCServer;

/**
 * concrete osc server implementation
 * 
 * @author michu
 * 
 */
class OscServerImpl extends AbstractOscServer implements OSCListener {

    private static final transient Logger LOG = Logger.getLogger(AbstractOscServer.class.getName());

    private transient OSCServer oscServer;

    /**
     * 
     * @param useTcp
     * @param handler
     * @param host
     * @param port
     * @param bufferSize
     * @throws OscServerException
     */
    public OscServerImpl(boolean useTcp, Observer handler, String host, int port, int bufferSize)
            throws OscServerException {
        super(handler, host, port, bufferSize);
        long t1 = System.currentTimeMillis();
        try {
            if (useTcp) {
                oscServer = OSCServer.newUsing(OSCServer.TCP, port);
            } else {
                oscServer = OSCServer.newUsing(OSCServer.UDP, port);
            }
            oscServer.addOSCListener(this);
            oscServer.setBufferSize(bufferSize);
            LOG.log(Level.INFO, "OSC Server initialized on port " + port + " (buffersize: "
                    + bufferSize + " bytes) in " + (System.currentTimeMillis() - t1) + "ms");
        } catch (Exception e) {
            throw new OscServerException("Failed to start OSC Server", e);
        }
    }

    @Override
    public void startServer() {
        try {
            oscServer.start();
            LOG.log(Level.INFO, "OSC Server started");
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Failed to start OSC Server!", e);
        }
    }

    @Override
    public void stopServer() {
        try {
            oscServer.stop();
            oscServer.dispose();
            LOG.log(Level.INFO, "OSC Server stopped");
        } catch (Exception e) {
            // LOG.log(Level.SEVERE, "Failed to stop OSC Server!", e);
        }
    }

    @Override
    public void messageReceived(OSCMessage m, SocketAddress addr, long time) {
        String[] args = null;
        byte[] blob = null;
        if (m.getArgCount() > 0) {
            List<String> tmp = new ArrayList<String>();
            for (int i = 0; i < m.getArgCount(); i++) {
                Object o = m.getArg(i);

                if (o instanceof Integer || o instanceof String || o instanceof Long
                        || o instanceof Float) {
                    tmp.add("" + o);
                } else if (o instanceof byte[]) {
                    blob = (byte[]) o;
                }
            }
            args = new String[tmp.size()];
            args = tmp.toArray(args);

        }

        OscMessage msg = new OscMessage(m.getName(), args, blob);
        msg.setSocketAddress(addr);
        this.notifyOscClients(msg);
    }
}
