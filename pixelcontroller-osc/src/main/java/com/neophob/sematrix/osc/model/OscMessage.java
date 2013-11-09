package com.neophob.sematrix.osc.model;

import java.util.Arrays;

public class OscMessage {

	private final String pattern; 
	private final String[] args;
	private final byte[] blob;
	
	public OscMessage(String pattern) {
		this.pattern = pattern;
		this.args = null;		
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
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format("OscMessage [pattern=%s, args=%s]", pattern,
				Arrays.toString(args));
	}

	
}
