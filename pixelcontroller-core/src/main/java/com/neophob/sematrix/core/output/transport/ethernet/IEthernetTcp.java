package com.neophob.sematrix.core.output.transport.ethernet;

public interface IEthernetTcp extends IEthernetUdp {

    /**
     * is tcp connection established?
     * 
     * @return
     */
    boolean isConnected();

    /**
     * how many bytes are in the incoming buffer
     * 
     * @return
     */
    int available();

    /**
     * read bytes
     * 
     * @return
     */
    byte[] readBytes();

}
