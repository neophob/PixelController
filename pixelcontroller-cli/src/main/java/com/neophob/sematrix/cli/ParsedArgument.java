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

import com.neophob.sematrix.core.properties.ValidCommands;
import com.neophob.sematrix.osc.model.OscMessage;

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

	/**
	 * Gets the target.
	 *
	 * @return the target
	 */
	public String getTarget() {
	    return hostname+":"+port;
	}
	
	/**
	 * Gets the payload.
	 *
	 * @param eol the eol
	 * @return the payload
	 */
	public OscMessage getPayload() {
		return new OscMessage("/"+command, parameter);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return getTarget()+" -> command: "+command+" <"+parameter+">";
	}
	
}