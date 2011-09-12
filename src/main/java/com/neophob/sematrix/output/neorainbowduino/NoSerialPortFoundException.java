package com.neophob.sematrix.output.neorainbowduino;

/**
 * If the library is unable to find a serial port, this Exception will be thrown
 * <br><br>
 * part of the neorainbowduino library
 * 
 * @author Michael Vogt / neophob.com
 *
 */
public class NoSerialPortFoundException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6432383124399209942L;

	/**
	 * 
	 * @param s
	 */
	public NoSerialPortFoundException(String s) {
		super(s);
	}
}
