package com.neophob.sematrix.osc.server;

/**
 * Used if the OSC server cannot start or fail during operation
 * 
 * @author michu
 *
 */
public class OscServerException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1536439654275608922L;

	public OscServerException(String message, Throwable cause) {
		super(message, cause);
	}

	public OscServerException(String message) {
		super(message);
	}

	public OscServerException(Throwable cause) {
		super(cause);
	}

}
