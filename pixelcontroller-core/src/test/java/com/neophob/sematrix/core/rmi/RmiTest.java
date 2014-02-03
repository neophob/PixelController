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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Observable;
import java.util.Observer;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;

import com.neophob.sematrix.core.properties.ApplicationConfigurationHelper;
import com.neophob.sematrix.core.properties.Command;
import com.neophob.sematrix.core.properties.ConfigConstant;
import com.neophob.sematrix.core.properties.ValidCommand;
import com.neophob.sematrix.core.rmi.RmiApi.Protocol;
import com.neophob.sematrix.core.rmi.impl.RmiFactory;
import com.neophob.sematrix.osc.model.OscMessage;

public class RmiTest implements Observer {

    private static final int BUFFERSIZE = 1024 * 60;
    private static final int PORT = 12346;
    private static final int CLIENTPORT = 0;

    private OscMessage m;

    @Before
    public void setUp() {
        m = null;
    }

    @Test
    public void RmiTestSimpleCompressed() throws Exception {
        RmiApi remoteServer = RmiFactory.getRmiApi(true, BUFFERSIZE);
        remoteServer.startServer(Protocol.UDP, this, PORT);
        remoteServer.startClient(Protocol.UDP, "localhost", PORT, CLIENTPORT);
        remoteServer.sendPayload(new Command(ValidCommand.CHANGE_GENERATOR_A), null);
        Thread.sleep(200);
        assertNotNull(m);
        assertEquals(ValidCommand.CHANGE_GENERATOR_A.toString(), m.getPattern());
        remoteServer.shutdown();
    }

    @Test
    public void RmiTestSimpleUnCompressed() throws Exception {
        RmiApi remoteServer = RmiFactory.getRmiApi(false, BUFFERSIZE);
        remoteServer.startServer(Protocol.UDP, this, PORT);
        remoteServer.startClient(Protocol.UDP, "localhost", PORT, CLIENTPORT);
        remoteServer.sendPayload(new Command(ValidCommand.CHANGE_GENERATOR_A), null);
        Thread.sleep(200);
        assertNotNull(m);
        assertEquals(ValidCommand.CHANGE_GENERATOR_A.toString(), m.getPattern());
        remoteServer.shutdown();
    }

    @Test
    public void RmiTestAdjustCompressionSetting() throws Exception {
        Properties p = new Properties();
        p.put(ConfigConstant.FPS, 234);
        ApplicationConfigurationHelper ach = new ApplicationConfigurationHelper(p);

        RmiApi remoteServer = RmiFactory.getRmiApi(true, BUFFERSIZE);
        remoteServer.startServer(Protocol.UDP, this, PORT);

        RmiApi remoteClient = RmiFactory.getRmiApi(false, BUFFERSIZE);
        remoteClient.startClient(Protocol.UDP, "localhost", PORT, CLIENTPORT);
        remoteClient.sendPayload(new Command(ValidCommand.GET_CONFIGURATION), ach);

        Thread.sleep(200);

        assertNotNull(m);
        assertEquals(ValidCommand.GET_CONFIGURATION.toString(), m.getPattern());

        ApplicationConfigurationHelper ach2 = remoteServer.reassembleObject(m.getBlob(),
                ApplicationConfigurationHelper.class);
        assertEquals(ach.parseFps(), ach2.parseFps(), 0.0001);
        remoteServer.shutdown();
        remoteClient.shutdown();
    }

    @Test
    public void rmiTestClientRestart() throws Exception {
        RmiApi remoteServer = RmiFactory.getRmiApi(false, BUFFERSIZE);
        remoteServer.startServer(Protocol.TCP, this, PORT);

        RmiApi remoteClient = RmiFactory.getRmiApi(false, BUFFERSIZE);
        remoteClient.startClient(Protocol.TCP, "localhost", PORT, CLIENTPORT);
        remoteClient.sendPayload(new Command(ValidCommand.CHANGE_GENERATOR_A), null);
        Thread.sleep(200);
        assertEquals(ValidCommand.CHANGE_GENERATOR_A.toString(), m.getPattern());

        remoteServer.shutdown();
        remoteServer.startServer(Protocol.TCP, this, PORT);
        Thread.sleep(200);

        remoteClient.sendPayload(new Command(ValidCommand.CHANGE_GENERATOR_B), null);
        remoteClient.sendPayload(new Command(ValidCommand.CHANGE_GENERATOR_B), null);
        remoteClient.sendPayload(new Command(ValidCommand.CHANGE_GENERATOR_B), null);
        remoteClient.sendPayload(new Command(ValidCommand.CHANGE_GENERATOR_B), null);
        Thread.sleep(200);
        assertEquals(ValidCommand.CHANGE_GENERATOR_B.toString(), m.getPattern());

        remoteServer.shutdown();
        remoteClient.shutdown();
    }

    @Override
    public void update(Observable o, Object arg) {
        if (arg instanceof OscMessage) {
            m = (OscMessage) arg;
        }
    }
}
