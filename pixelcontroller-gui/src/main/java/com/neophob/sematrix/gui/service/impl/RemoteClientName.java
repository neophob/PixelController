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
package com.neophob.sematrix.gui.service.impl;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.neophob.sematrix.core.api.CallbackMessageInterface;
import com.neophob.sematrix.mdns.client.MDnsClientException;
import com.neophob.sematrix.mdns.client.PixMDnsClient;
import com.neophob.sematrix.mdns.client.impl.MDnsClientFactory;
import com.neophob.sematrix.mdns.server.PixMDnsServer;

public class RemoteClientName {

    private static final Logger LOG = Logger.getLogger(RemoteClientName.class.getName());

    private static final String CFG_REMOTE_HOST = "remote.client.host";
    private static final String CFG_REMOTE_PORT = "remote.client.port";

    public static final String DEFAULT_TARGET_HOST = "pixelcontroller.local";
    private static final int DEFAULT_REMOTE_OSC_SERVER_PORT = 9876;
    private static final int BONJOUR_DETECTION_TIMEOUT = 6000;

    private int targetPort;
    private String targetHost;
    private Properties clientProperties;
    private CallbackMessageInterface<String> setupFeedback;

    public RemoteClientName(CallbackMessageInterface<String> setupFeedback,
            Properties localClientProperties) {
        this.setupFeedback = setupFeedback;
        this.targetHost = DEFAULT_TARGET_HOST;
        this.targetPort = DEFAULT_REMOTE_OSC_SERVER_PORT;
        this.clientProperties = localClientProperties;
    }

    public void queryRemoteName() {
        // check if the user defined the remote ip/port in the config file
        if (this.clientProperties != null) {
            boolean validClientConfig = false;

            String s = this.clientProperties.getProperty(CFG_REMOTE_HOST);
            if (s != null && !s.trim().isEmpty()) {
                this.targetHost = s;
                LOG.log(Level.INFO, "Manual remote host found: {0}", this.targetHost);
                validClientConfig = true;
            }

            s = this.clientProperties.getProperty(CFG_REMOTE_PORT);
            if (s != null && !s.trim().isEmpty()) {
                try {
                    this.targetPort = Integer.parseInt(s);
                    LOG.log(Level.INFO, "Manual remote port found: {0}", this.targetPort);
                    validClientConfig = true;
                } catch (Exception e) {
                    // ignored
                }
            }

            if (validClientConfig) {
                return;
            }
        }
        LOG.log(Level.INFO, "No manual config file found...");

        // if not, try to find pixelcontroller by bonjour
        try {
            PixMDnsClient client = MDnsClientFactory.queryService(PixMDnsServer.REMOTE_TYPE_UDP,
                    BONJOUR_DETECTION_TIMEOUT);
            client.start();
            if (client.mdnsServerFound()) {
                targetPort = client.getPort();
                setupFeedback.handleMessage("... found on port " + targetPort + ", ip: "
                        + client.getFirstIp());
                targetHost = client.getFirstIp();
            } else {
                setupFeedback.handleMessage("... not found, use default port "
                        + DEFAULT_REMOTE_OSC_SERVER_PORT);
                targetPort = DEFAULT_REMOTE_OSC_SERVER_PORT;
            }
        } catch (MDnsClientException e) {
            LOG.log(Level.WARNING, "Service discover failed.", e);
            targetPort = DEFAULT_REMOTE_OSC_SERVER_PORT;
            setupFeedback.handleMessage("... not found, use default port "
                    + DEFAULT_REMOTE_OSC_SERVER_PORT);
        }
    }

    public String getTargetHost() {
        return targetHost;
    }

    public int getTargetPort() {
        return targetPort;
    }

}
