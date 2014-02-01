package com.neophob.sematrix.core.output.transport.ethernet;

import java.io.IOException;

public interface IEthernetUdp {

    /**
     * init
     * 
     * @param target
     *            target ip or port
     * @param port
     * @return
     */
    boolean initializeEthernet(String target, int port);

    /**
     * send data to target
     */
    void sendData(byte[] data) throws IOException;

    /**
     * update target, use 4 byte parameter
     */
    void setTargetIp(byte[] ipAddress);

    /**
     * stop ethernet port, release resources
     */
    void closePort();

}
