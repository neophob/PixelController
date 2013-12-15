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
package com.neophob.sematrix.osc.model;

import java.net.SocketAddress;
import java.util.Arrays;

public class OscMessage {

	private final String pattern; 
	private final String[] args;
	private final byte[] blob;
	private SocketAddress socketAddress;
	
	public OscMessage(String pattern) {
		this.pattern = pattern;
		this.args = null;		
		this.blob = null;
	}

	public OscMessage(String[] msg) {
		if (msg == null || msg.length<1) {
			throw new IllegalArgumentException("parameter null or empty");
		}
		this.pattern = msg[0];		
		this.args = Arrays.copyOfRange(msg, 1, msg.length);
		this.blob = null;
	}

	public OscMessage(String pattern, String parameter) {
		this.pattern = pattern;
		this.args = new String[] {parameter};
		this.blob = null;
	}

	public OscMessage(String pattern, String[] args, byte[] blob) {
		this.pattern = pattern;
		this.args = args;
		this.blob = blob;
	}

	public OscMessage(String pattern, byte[] blob) {
		this.pattern = pattern;
		this.args = null;
		this.blob = blob;
	}

	/**
	 * @return the pattern
	 */
	public String getPattern() {
		if (pattern==null) {
			return null;
		}
		
		String ret = pattern.trim().toUpperCase();
		//remove beginning "/"
		if (ret.startsWith("/")) {
			ret = ret.substring(1, pattern.length());
		}
		return ret;
	}

	/**
	 * 
	 * @return message, make sure message starts with / 
	 */
	public String getOscPattern() {
		if (pattern.startsWith("/")) {
			return pattern;
		}
		return "/"+pattern;
	}
	
	
	/**
	 * @return the args
	 */
	public String[] getArgs() {
		return args;
	}

	/**
	 * @return the blob
	 */
	public byte[] getBlob() {
		return blob;
	}

	public long getMessageSize() {
		long l = pattern.length();
		l += blob == null ? 0 : blob.length;
		l += args == null ? 0 : args.length;
		return l;
	}
	
	/**
	 * @return the socketAddress
	 */
	public SocketAddress getSocketAddress() {
		return socketAddress;
	}

	/**
	 * @param socketAddress the socketAddress to set
	 */
	public void setSocketAddress(SocketAddress socketAddress) {
		this.socketAddress = socketAddress;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format(
				"OscMessage [pattern=%s, args=%s, socketAddress=%s]", pattern,
				Arrays.toString(args), socketAddress);
	}


	
}
