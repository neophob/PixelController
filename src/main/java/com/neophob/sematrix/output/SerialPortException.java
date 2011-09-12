package com.neophob.sematrix.output;

/**
 * If the library is unable to find a serial port, this Exception will be thrown
 * <br><br>
 * part of the neorainbowduino library
 * 
 * @author Michael Vogt / neophob.com
 *
 */
public class SerialPortException extends Exception {


	/**
	 * 
	 */
	private static final long serialVersionUID = 3951181732955456485L;

	/**
	 * 
	 * @param s
	 */
	public SerialPortException(String s) {
		super(s);
	}
}
