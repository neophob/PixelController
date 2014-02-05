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
package com.neophob.sematrix.cli;

import java.util.Observable;
import java.util.Observer;

import com.neophob.sematrix.core.jmx.PixelControllerStatusMBean;
import com.neophob.sematrix.core.properties.Configuration;
import com.neophob.sematrix.core.properties.ValidCommand;
import com.neophob.sematrix.core.rmi.RmiApi;
import com.neophob.sematrix.osc.model.OscMessage;

/**
 * wait for an answer of the pixecontroller server and reassemble message
 * 
 * @author michu
 * 
 */
public class OscAnswerHandler implements Observer {

    private boolean answerRecieved = false;
    private RmiApi rmi;

    public OscAnswerHandler(RmiApi rmi) {
        this.rmi = rmi;
    }

    public void handleOscMessage(OscMessage msg) {
        System.out.println("got: " + msg);

        ValidCommand command;
        try {
            command = ValidCommand.valueOf(msg.getPattern());
        } catch (Exception e) {
            System.out.println("Unknown message: " + msg.getPattern());
            return;
        }

        switch (command) {
            case GET_VERSION:
                String version = rmi.reassembleObject(msg.getBlob(), String.class);
                System.out.println("PixelController Version: " + version);
                break;

            case GET_JMXSTATISTICS:
                PixelControllerStatusMBean jmxStatistics = rmi.reassembleObject(msg.getBlob(),
                        PixelControllerStatusMBean.class);
                System.out.println(jmxStatistics);
                break;

            case GET_CONFIGURATION:
                Configuration config = rmi.reassembleObject(msg.getBlob(),
                        Configuration.class);
                System.out.println(config);
                break;

            default:
                System.out.println("Unsupported answer: " + command);
                break;
        }

        this.answerRecieved = true;
    }

    @Override
    public void update(Observable o, Object arg) {
        if (arg instanceof OscMessage) {
            OscMessage msg = (OscMessage) arg;
            handleOscMessage(msg);
        } else {
            System.out.println("Ignored notification of unknown type: " + arg);
        }
    }

    public boolean isAnswerRecieved() {
        return answerRecieved;
    }

}
