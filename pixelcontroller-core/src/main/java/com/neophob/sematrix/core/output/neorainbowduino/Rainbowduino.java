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
/*
 A nice wrapper class to control the Rainbowduino 

 (c) copyright 2009 by rngtng - Tobias Bielohlawek
 (c) copyright 2010 by Michael Vogt/neophob.com 
 http://code.google.com/p/rainbowduino-firmware/wiki/FirmwareFunctionsReference

 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.

 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General
 Public License along with this library; if not, write to the
 Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 Boston, MA  02111-1307  USA
 */

package com.neophob.sematrix.core.output.neorainbowduino;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.Adler32;

import com.neophob.sematrix.core.output.NoSerialPortFoundException;
import com.neophob.sematrix.core.output.SerialPortException;
import com.neophob.sematrix.core.output.transport.serial.ISerial;

/**
 * library to communicate with an arduino via serial port<br>
 * the arduino control up to n rainbowduinos using the i2c protocol <br>
 * <br>
 * part of the neorainbowduino library
 * 
 * TODO: add blacklist for serial port detection!
 * 
 * @author Michael Vogt / neophob.com
 * 
 */
public class Rainbowduino {

    private static Logger LOG = Logger.getLogger(Rainbowduino.class.getName());

    /**
     * number of leds horizontal<br>
     * TODO: should be dynamic, someday
     */
    public static final int NR_OF_LED_HORIZONTAL = 8;

    /**
     * number of leds vertical<br>
     * TODO: should be dynamic, someday
     */
    public static final int NR_OF_LED_VERTICAL = NR_OF_LED_HORIZONTAL;

    /**
     * internal lib version
     */
    public static final String VERSION = "1.9";

    private static final byte START_OF_CMD = 0x01;
    private static final byte CMD_SENDFRAME = 0x03;
    private static final byte CMD_PING = 0x04;
    private static final byte CMD_INIT_RAINBOWDUINO = 0x05;
    private static final byte CMD_SCAN_I2C_BUS = 0x06;

    private static final byte START_OF_DATA = 0x10;
    private static final byte END_OF_DATA = 0x20;

    private static Adler32 adler = new Adler32();

    private int baud = 115200;
    private ISerial serialPort;

    private long arduinoHeartbeat;
    private long ackErrors = 0;
    private int arduinoBufferSize;

    // logical errors reported by arduino, TODO: rename to lastErrorCode
    private int arduinoErrorCounter;

    /**
     * result of i2c bus scan
     */
    private List<Integer> scannedI2cDevices;

    /**
     * map to store checksum of image
     */
    private Map<Byte, Long> lastDataMap;

    /**
     * Create a new instance to communicate with the rainbowduino.
     * 
     * @param app
     * @param rainbowduinoAddr
     * @throws NoSerialPortFoundException
     */
    public Rainbowduino(List<Integer> rainbowduinoAddr, ISerial serialPort)
            throws NoSerialPortFoundException {
        this(null, 0, rainbowduinoAddr, serialPort);
    }

    /**
     * Create a new instance to communicate with the rainbowduino.
     * 
     * @param _app
     * @param portName
     * @param baud
     * @param rainbowduinoAddr
     * @throws NoSerialPortFoundException
     */
    public Rainbowduino(String portName, int baud, List<Integer> rainbowduinoAddr,
            ISerial serialPort) throws NoSerialPortFoundException {

        LOG.log(Level.INFO, "Initialize neorainbowduino lib v{0}", VERSION);
        this.serialPort = serialPort;
        this.scannedI2cDevices = new ArrayList<Integer>();
        this.lastDataMap = new HashMap<Byte, Long>();

        String serialPortName = "";
        if (baud > 0) {
            this.baud = baud;
        }

        if (portName != null && !portName.trim().isEmpty()) {
            // open specific port
            LOG.log(Level.INFO, "open port: {0}", portName);
            serialPortName = portName;
            openPort(portName, rainbowduinoAddr);
        } else {
            // try to find the port
            String[] ports = serialPort.getAllSerialPorts();
            for (int i = 0; !serialPort.isConnected() && i < ports.length; i++) {
                LOG.log(Level.INFO, "open port: {0}", ports[i]);
                try {
                    serialPortName = ports[i];
                    openPort(ports[i], rainbowduinoAddr);
                    // catch all, there are multiple exception to catch
                    // (NoSerialPortFoundException,
                    // PortInUseException...)
                } catch (Exception e) {
                    // search next port...
                }
            }
        }

        if (!serialPort.isConnected()) {
            throw new NoSerialPortFoundException("Error: no serial port found!");
        }
        LOG.log(Level.INFO, "found serial port: " + serialPortName);
    }

    /**
     * clean up library
     */
    public void dispose() {
        serialPort.closePort();
    }

    /**
     * return the version of the library.
     * 
     * @return String version number
     */
    public String version() {
        return VERSION;
    }

    /**
     * return connection state of lib
     * 
     * @return wheter rainbowudino is connected
     */
    public boolean connected() {
        return serialPort.isConnected();
    }

    /**
     * 
     * Open serial port with given name. Send ping to check if port is working.
     * If not port is closed and set back to null
     * 
     * @param portName
     */
    private void openPort(String portName, List<Integer> rainbowduinoAddr)
            throws NoSerialPortFoundException {
        if (portName == null) {
            return;
        }

        try {
            serialPort.openPort(portName, this.baud);
            sleep(1500); // give it time to initialize
            if (ping()) {

                // send initial image to rainbowduinos
                for (int i : rainbowduinoAddr) {
                    this.initRainbowduino((byte) i);
                }

                return;
            }
            LOG.log(Level.WARNING, "No response from port {0}", portName);
            serialPort.closePort();

            throw new NoSerialPortFoundException("No response from port " + portName);
        } catch (Exception e) {
            LOG.log(Level.WARNING, "Failed to open port {0}: {1}", new Object[] { portName, e });
            serialPort.closePort();
            throw new NoSerialPortFoundException("Failed to open port " + portName + ": " + e);
        }
    }

    /**
     * send a serial ping command to the arduino board.
     * 
     * @return wheter ping was successfull (arduino reachable) or not
     */
    public boolean ping() {
        /*
         * 0 <startbyte> 1 <i2c_addr> 2 <num_bytes_to_send> 3 command type, was
         * <num_bytes_to_receive> 4 data marker 5 ... data n end of data
         */
        byte[] cmdfull = new byte[7];
        cmdfull[0] = START_OF_CMD;
        cmdfull[1] = 0; // unused here!
        cmdfull[2] = 0x01;
        cmdfull[3] = CMD_PING;
        cmdfull[4] = START_OF_DATA;
        cmdfull[5] = 0x02;
        cmdfull[6] = END_OF_DATA;

        try {
            writeSerialData(cmdfull);
            return waitForAck();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Initiate a I2C bus scan<br>
     * The result will be stored in the scannedI2cDevices list.<br>
     * Hint: it takes some time for the scan to finish - wait 1-2s before you
     * check the result.
     */
    private boolean i2cBusScan() {
        byte[] cmdfull = new byte[7];
        cmdfull[0] = START_OF_CMD;
        cmdfull[1] = 0;
        cmdfull[2] = 1;
        cmdfull[3] = CMD_SCAN_I2C_BUS;
        cmdfull[4] = START_OF_DATA;
        cmdfull[5] = 0;
        cmdfull[6] = END_OF_DATA;

        try {
            writeSerialData(cmdfull);
            return waitForI2cResultAndAck();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * wrapper class to send a RGB image to the rainbowduino. the rgb image gets
     * converted to the rainbowduino compatible "image format"
     * 
     * @param addr
     *            the i2c address of the device
     * @param data
     *            rgb data (int[64], each int contains one RGB pixel)
     * @return true if send was successful
     */
    public boolean sendRgbFrame(byte addr, int[] data) {
        return sendFrame(addr, RainbowduinoHelper.convertRgbToRainbowduino(data));
    }

    /**
     * get md5 hash out of an image. used to check if the image changed
     * 
     * @param addr
     * @param data
     * @return true if send was successful
     */
    private boolean didFrameChange(byte ofs, byte[] data) {
        adler.reset();
        adler.update(data);
        long l = adler.getValue();

        if (!lastDataMap.containsKey(ofs)) {
            // first run
            lastDataMap.put(ofs, l);
            return true;
        }

        if (lastDataMap.get(ofs) == l) {
            // last frame was equal current frame, do not send it!
            // LOG.log(Level.INFO, "do not send frame to {0}", addr);
            return false;
        }
        // update new hash
        lastDataMap.put(ofs, l);
        return true;
    }

    /**
     * send a frame to the active rainbowduino the data needs to be in this
     * format: buffer[3][8][4], The array to be sent formatted as
     * [color][row][dots]
     * 
     * @param addr
     *            the i2c address of the device
     * @param data
     *            byte[3*8*4]
     * @return true if send was successful
     */
    public boolean sendFrame(byte addr, byte[] data) {
        // TODO stop if connection counter > n
        // if (connectionErrorCounter>10000) {}

        if (!didFrameChange(addr, data)) {
            return false;
        }

        // LOG.log(Level.INFO, "Send data to device {0}", addr);

        byte[] cmdfull = new byte[6 + data.length];
        cmdfull[0] = START_OF_CMD;
        cmdfull[1] = addr;
        cmdfull[2] = (byte) data.length;
        cmdfull[3] = CMD_SENDFRAME;
        cmdfull[4] = START_OF_DATA;

        System.arraycopy(data, 0, cmdfull, 5, data.length);
        // for (int i=0; i<data.length; i++) {
        // cmdfull[5+i] = data[i];
        // }
        cmdfull[data.length + 5] = END_OF_DATA;

        try {
            writeSerialData(cmdfull);
            if (waitForAck()) {
                // frame was send successful
                return true;
            }
        } catch (Exception e) {
            LOG.log(Level.WARNING, "sending serial data failed: {0}", e);
        }

        // an error occoured sending the frame, make sure we resend next time
        lastDataMap.put(addr, 0L);
        return false;
    }

    /**
     * initialize an rainbowduino device - send the initial image to the
     * rainbowduino. check arduinoErrorCounter for any errors.
     * 
     * @param addr
     *            the i2c slave address of the rainbowduino
     * @return true if send was successful
     */
    public boolean initRainbowduino(byte addr) {
        // TODO stop if connection counter > n
        // if (connectionErrorCounter>10000) {}

        byte[] cmdfull = new byte[7];
        cmdfull[0] = START_OF_CMD;
        cmdfull[1] = addr;
        cmdfull[2] = 1;
        cmdfull[3] = CMD_INIT_RAINBOWDUINO;
        cmdfull[4] = START_OF_DATA;
        cmdfull[5] = 0;
        cmdfull[6] = END_OF_DATA;

        try {
            writeSerialData(cmdfull);
            return waitForAck();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * get last error code from arduino if the errorcode is between 100..109 -
     * serial connection issue (pc-arduino issue) if the errorcode is < 100 it's
     * a i2c lib error code (arduino-rainbowduino error) check
     * http://arduino.cc/en/Reference/WireEndTransmission for more information
     * 
     * @return last error code from arduino
     */
    public int getArduinoErrorCounter() {
        return arduinoErrorCounter;
    }

    /**
     * return the serial buffer size of the arduino
     * 
     * the buffer is by default 128 bytes - if the buffer is most of the time
     * almost full (>110 bytes) you probabely send too much serial data
     * 
     * @return arduino filled serial buffer size
     */
    public int getArduinoBufferSize() {
        return arduinoBufferSize;
    }

    /**
     * per default arduino update this library each 3s with statistic
     * information this value save the timestamp of the last message.
     * 
     * @return timestamp when the last heartbeat receieved. should be updated
     *         each 3s.
     */
    public long getArduinoHeartbeat() {
        return arduinoHeartbeat;
    }

    /**
     * how may times the serial response was missing / invalid
     * 
     * @return
     */
    public synchronized long getAckErrors() {
        return ackErrors;
    }

    /**
     * send the data to the serial port
     * 
     * @param cmdfull
     */
    private synchronized void writeSerialData(byte[] cmdfull) throws SerialPortException {
        if (!serialPort.isConnected()) {
            throw new SerialPortException("port is not ready!");
        }

        try {
            serialPort.getOutputStream().write(cmdfull);
            // DO NOT flush the buffer
        } catch (Exception e) {
            LOG.log(Level.WARNING, "Error sending serial data!", e);
            throw new SerialPortException("cannot send serial data: " + e);
        }
    }

    /**
     * read data from serial port, wait for ACK
     * 
     * @return true if ack received, false if not
     */
    private synchronized boolean waitForAck() {
        long start = System.currentTimeMillis();
        long waitTime = start + 50; // wait up to 50ms
        while (waitTime > System.currentTimeMillis() && serialPort.available() < 3) {
            sleep(1);
        }

        if (serialPort.available() < 3) {
            LOG.log(Level.INFO, "No serial reply, duration: {0}ms", System.currentTimeMillis()
                    - start);
            ackErrors++;
            return false;
        }
        byte[] msg = serialPort.readBytes();
        // LOG.log(Level.INFO, "data length: {0}", msg.length);

        // INFO: MEEE [0, 0, 65, 67, 75, 0, 0]
        for (int i = 0; i < msg.length - 1; i++) {
            if (msg[i] == 'A' && msg[i + 1] == 'K') {
                try {
                    this.arduinoBufferSize = msg[i + 2];
                    this.arduinoErrorCounter = msg[i + 3];
                } catch (Exception e) {
                    // we failed to update statistics...
                }
                this.arduinoHeartbeat = System.currentTimeMillis();
                if (this.arduinoErrorCounter == 0) {
                    return true;
                }
                ackErrors++;
                return false;
            }
        }

        String s = "";
        for (byte b : msg) {
            s += (char) b;
        }
        LOG.log(Level.INFO, "Invalid serial data {0}, duration: {1}ms", new String[] { s,
                "" + (System.currentTimeMillis() - start) });
        ackErrors++;
        return false;
    }

    /**
     * read data from serial port, wait for ACK
     * 
     * @return true if ack received, false if not
     */
    private synchronized boolean waitForI2cResultAndAck() {
        // wait for ack/nack
        // TODO make this configurabe
        long waitTime = System.currentTimeMillis() + 100; // wait up to 100ms
        while (waitTime > System.currentTimeMillis() && serialPort.available() < 15) {
            sleep(1);
        }

        byte[] msg = serialPort.readBytes();
        if (serialPort.available() < 15) {
            LOG.log(Level.INFO, "Invalid serial data {0}", Arrays.toString(msg));
            return false;
        }

        // LOG.log(Level.INFO, "get serialdata {1} bytes: {0}", new Object[] {
        // Arrays.toString(msg),
        // msg.length });

        for (int i = 0; i < msg.length - 2; i++) {
            if (msg[i] == START_OF_CMD && msg[i + 1] == CMD_SCAN_I2C_BUS) {
                // process i2c scanning result
                for (int x = i + 2; x < msg.length; x++) {
                    int n;
                    try {
                        n = Integer.parseInt("" + msg[x]);
                        if (n == 255 || n < 1) {
                            break;
                        }
                        LOG.log(Level.INFO, "Reply from I2C device: #{0}", n);
                        scannedI2cDevices.add(n);
                    } catch (Exception e) {
                    }
                }
            }
        }

        // LOG.log(Level.INFO, "Invalid serial data {0}", Arrays.toString(msg));
        return true;
    }

    /**
     * Sleep wrapper
     * 
     * @param ms
     */
    private void sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
        }
    }

    /**
     * Scan I2C bus on a connected rainbowduino device
     * 
     * @return List of found i2c devices
     */
    public List<Integer> scanI2cBus() {
        try {
            this.i2cBusScan();
        } catch (Exception e) {
            LOG.log(Level.WARNING, "I2C scanner failed: {0}", e);
        }

        return scannedI2cDevices;
    }

}
