package com.neophob.sematrix.core.output.serial;

import java.io.OutputStream;

public interface ISerial {

  void openPort(String name, int baud);

  /**
   */
  int available();

  /**
   * clear internal buffer
   */
  void clear();

  /**
   * @param bytes
   *          [] data to write
   */
  void write(byte bytes[]);

  /**
   * stop serial port, release resources
   */
  void closePort();

  /**
   * read string from serial port
   * 
   * @return
   */
  String readString();

  byte[] readBytes();

  /**
   * return serial output stream
   * 
   * @return
   */
  OutputStream getOutputStream();

  /**
   * is serial line open/available
   */
  boolean isConnected();

  String getConnectedPortname();

  /**
   * serial port names are CASE SENSITIVE. this sounds logically on unix platform
   * however com1 will not work on windows, there all names need to be in uppercase (COM1)
   * 
   * see https://github.com/neophob/PixelController/issues/30 for more details
   * 
   * @param configuredName
   * @return
   */
  String getSerialPortName(String configuredName);

  String[] getAllSerialPorts();

}