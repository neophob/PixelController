package com.neophob.sematrix.osc.client;

/**
 * Used if the OSC client cannot send a message
 * 
 * @author michu
 *
 */
public class OscClientException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3310423091524466829L;

	public OscClientException(String message, Throwable cause) {
		super(message, cause);
	}

	public OscClientException(String message) {
		super(message);
	}

	public OscClientException(Throwable cause) {
		super(cause);
	}

}
