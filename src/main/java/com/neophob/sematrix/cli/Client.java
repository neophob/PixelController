package com.neophob.sematrix.cli;

import jargs.gnu.CmdLineParser;

import java.text.ParseException;

import com.neophob.sematrix.listener.MessageProcessor.ValidCommands;

/**
 * The Class Client.
 */
public class Client {

	/** The Constant DEFAULT_PORT. */
	private static final String DEFAULT_PORT = "3448";
	
	/** The Constant DEFAULT_HOST. */
	private static final String DEFAULT_HOST = "127.0.0.1";
	
	private static final String PARAM_COMMAND = "command";
	private static final String PARAM_PORT = "port";
	private static final String PARAM_HOST = "hostname";
	
	/**
	 * Usage.
	 *
	 * @param options the options
	 */
	private static void usage() {
		System.out.println("Usage: Client [-h hostname] [-p port] -c ValidCommand");
		System.exit(1);		
	}
	
	
	/**
	 * Parses the argument.
	 *
	 * @param args the args
	 * @return the parsed argument
	 */
	private static ParsedArgument parseArgument(String[] args) {
		
		CmdLineParser parser = new CmdLineParser();
        CmdLineParser.Option host = parser.addStringOption('h', PARAM_HOST);
        CmdLineParser.Option port = parser.addIntegerOption('p', PARAM_PORT);
        CmdLineParser.Option command = parser.addStringOption('c', PARAM_COMMAND);

        try {
            parser.parse(args);
            String pHost = (String)parser.getOptionValue(host, DEFAULT_HOST);
            int pPort = (Integer)parser.getOptionValue(port, DEFAULT_PORT);
            String pCmd = (String)parser.getOptionValue(command);            
            
            ValidCommands parsedCommand = ValidCommands.valueOf(pCmd);
                        
            String[] otherArgs = parser.getRemainingArgs();
            String param = "";
            for (String s: otherArgs) {
            	System.out.println("PARAM2: "+s);
            	param += s+" ";
            }

    		return new ParsedArgument(pHost, pPort, parsedCommand, param.trim());

        } catch ( CmdLineParser.OptionException e ) {
            System.err.println(e.getMessage());
            usage();
            System.exit(2);
        }
        
        return null;
	}
	
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 * @throws ParseException the parse exception
	 */
	public static void main(String[] args) throws Exception {
		System.out.println("PixelController Client v0.1");
		String[] param = new String[6];
		param[0] = "-c";
		param[1] = "CHANGE_GENERATOR_A";
		param[2] = "ddh";
		param[3] = "192.111.1.1";
		param[4] = "-p";
		param[5] = "44";
		ParsedArgument cmd = parseArgument(param);
		
		System.out.println(cmd);
		
	}
}
