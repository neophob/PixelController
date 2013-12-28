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
package com.neophob.sematrix.cli;

import jargs.gnu.CmdLineParser;
import jargs.gnu.CmdLineParser.IllegalOptionValueException;
import jargs.gnu.CmdLineParser.UnknownOptionException;

import java.security.InvalidParameterException;
import java.text.ParseException;

import org.apache.commons.lang3.ArrayUtils;

import com.neophob.sematrix.core.osc.PixelControllerOscServer;
import com.neophob.sematrix.core.properties.Command;
import com.neophob.sematrix.core.properties.CommandGroup;
import com.neophob.sematrix.core.properties.ValidCommand;
import com.neophob.sematrix.core.rmi.RmiApi;
import com.neophob.sematrix.core.rmi.RmiApi.Protocol;
import com.neophob.sematrix.core.rmi.impl.RmiFactory;
import com.neophob.sematrix.osc.client.OscClientException;
import com.neophob.sematrix.osc.server.OscServerException;

/**
 * PixelController OSC Client
 * 
 * @author michu
 * 
 */
public class PixConClient {

    private static final int SLEEP_INTERVAL = 200;

    private static final float VERSION = 0.8f;

    /** The Constant DEFAULT_PORT. */
    private static final int DEFAULT_PORT = 9876;

    private static final int LOCAL_SERVER_PORT = 10009;

    /** The Constant DEFAULT_HOST. */
    private static final String DEFAULT_HOST = "127.0.0.1";

    private static final String PARAM_COMMAND = "command";
    private static final String PARAM_PORT = "port";
    private static final String PARAM_HOST = "hostname";

    private OscAnswerHandler oap;
    private RmiApi rmi;

    protected PixConClient(ParsedArgument cmd) throws OscServerException, OscClientException,
            InterruptedException {
        // send osc payload

        rmi = RmiFactory.getRmiApi(true, PixelControllerOscServer.REPLY_PACKET_BUFFERSIZE);

        System.out.println(cmd.getPayload());
        if (cmd.getCommand().isExpectAnswer()) {
            oap = new OscAnswerHandler(rmi);
            rmi.startServer(Protocol.TCP, oap, LOCAL_SERVER_PORT);
        }

        rmi.startClient(Protocol.UDP, cmd.getHostname(), cmd.getPort(), LOCAL_SERVER_PORT);
        Command cmdAndParameter = new Command(cmd.getCommand(), new String[] { cmd.getParameter() });
        rmi.sendPayload(cmdAndParameter, null);

        if (cmd.getCommand().isExpectAnswer()) {
            System.out.println("Wait for answer...");
            long maxWait = 5000;
            while (!oap.isAnswerRecieved()) {
                if (maxWait < 0) {
                    System.out.println("No reply recieved, Bye!");
                    return;
                }
                Thread.sleep(SLEEP_INTERVAL);
                maxWait -= SLEEP_INTERVAL;
            }
        }
        System.out.println("Close connection, Bye!");
    }

    /**
     * Usage.
     * 
     * @param options
     *            the options
     */
    private static void usage() {
        System.out.println("Usage: Client [-h hostname] [-p port] -c ValidCommand");
        System.out.println("Valid commands:");

        for (CommandGroup cg : CommandGroup.values()) {
            if (cg.name().equals(CommandGroup.INTERNAL)) {
                continue;
            }
            for (ValidCommand vc : ValidCommand.getCommandsByGroup(cg)) {
                System.out.println("\t" + ClientHelper.pretifyString(vc.toString(), 28)
                        + ClientHelper.pretifyString("# of parameters: " + vc.getNrOfParams(), 23)
                        + vc.getDesc());
            }
            System.out.println();
        }
    }

    /**
     * Parses the argument.
     * 
     * @param args
     *            the args
     * @return the parsed argument
     * @throws InvalidParameterException
     */
    protected static ParsedArgument parseArgument(String[] args) throws InvalidParameterException {

        if (args.length < 2) {
            System.out.println("No arguments specified!\n");
            throw new InvalidParameterException("No arguments specified!");
        }

        CmdLineParser parser = new CmdLineParser();
        CmdLineParser.Option host = parser.addStringOption('h', PARAM_HOST);
        CmdLineParser.Option port = parser.addIntegerOption('p', PARAM_PORT);
        CmdLineParser.Option command = parser.addStringOption('c', PARAM_COMMAND);

        String pCmd = "undefined";
        try {
            parser.parse(args);
            String pHost = (String) parser.getOptionValue(host, DEFAULT_HOST);
            int pPort = (Integer) parser.getOptionValue(port, DEFAULT_PORT);
            pCmd = (String) parser.getOptionValue(command);
            if (pCmd == null) {
                System.out.println("Unknown Command: " + ArrayUtils.toString(args));
                System.out.println("Exit now");
                throw new IllegalArgumentException("no ValidCommand specified!");
            }

            ValidCommand parsedCommand = ValidCommand.valueOf(pCmd.toUpperCase());
            String[] otherArgs = parser.getRemainingArgs();

            if (parsedCommand.getNrOfParams() < otherArgs.length) {
                String err = "Invalid parameter count, expected: " + parsedCommand.getNrOfParams()
                        + ", provided: " + otherArgs.length;
                System.out.println(err);
                throw new InvalidParameterException(err);
            }

            String param = "";
            for (String s : otherArgs) {
                param += s + " ";
            }

            return new ParsedArgument(pHost, pPort, parsedCommand, param.trim());
        } catch (UnknownOptionException e) {
            System.out.println("Invalid option defined: " + e.getMessage());
            System.out.println();
        } catch (IllegalOptionValueException e) {
            System.out.println("Invalid option value defined: " + e.getMessage());
            System.out.println();
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid command defined <" + pCmd + ">: " + e.getMessage());
            System.out.println();
        }

        throw new InvalidParameterException("Something went wrong..");
    }

    /**
     * The main method.
     * 
     * @param args
     *            the arguments
     * @throws ParseException
     *             the parse exception
     */
    public static void main(String[] args) throws Exception {
        System.out.println("PixelController Client v" + VERSION + "\n");

        ParsedArgument cmd = null;
        try {
            cmd = parseArgument(args);
        } catch (Exception e) {
            usage();
            System.exit(1);
        }

        System.out.println(cmd);
        new PixConClient(cmd);
    }

}