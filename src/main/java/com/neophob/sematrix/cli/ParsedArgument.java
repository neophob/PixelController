package com.neophob.sematrix.cli;

import com.neophob.sematrix.listener.MessageProcessor.ValidCommands;

/**
 * The Class ParsedArgument.
 */
public class ParsedArgument {
	
	/** The hostname. */
	private String hostname;
	
	/** The port. */
	private int port;
	
	/** The command. */
	private ValidCommands command;
	
	/** The parameter. */
	private String parameter;

	/**
	 * Instantiates a new parsed argument.
	 *
	 * @param hostname the hostname
	 * @param port the port
	 * @param command the command
	 * @param parameter the parameter
	 */
	public ParsedArgument(String hostname, int port, ValidCommands command, String parameter) {
		this.hostname = hostname;
		this.port = port;
		this.command = command;
		this.parameter = parameter;
	}

	/**
	 * Gets the hostname.
	 *
	 * @return the hostname
	 */
	public String getHostname() {
		return hostname;
	}

	/**
	 * Gets the port.
	 *
	 * @return the port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * Gets the command.
	 *
	 * @return the command
	 */
	public ValidCommands getCommand() {
		return command;
	}

	/**
	 * Gets the parameter.
	 *
	 * @return the parameter
	 */
	public String getParameter() {
		return parameter;
	}

	public String toString() {
		return hostname+":"+port+" -> command: "+command+" '"+parameter+"'";
	}
	
}
