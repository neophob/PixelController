/**
 * Copyright (C) 2011-2014 Michael Vogt <michu@neophob.com>
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
package com.neophob.sematrix.core.output.transport.serial;

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
     *            [] data to write
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
     * serial port names are CASE SENSITIVE. this sounds logically on unix
     * platform however com1 will not work on windows, there all names need to
     * be in uppercase (COM1)
     * 
     * see https://github.com/neophob/PixelController/issues/30 for more details
     * 
     * @param configuredName
     * @return
     */
    String getSerialPortName(String configuredName);

    String[] getAllSerialPorts();

}