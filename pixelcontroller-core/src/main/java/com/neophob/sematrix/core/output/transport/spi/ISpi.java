package com.neophob.sematrix.core.output.transport.spi;

public interface ISpi {

    /**
     * 
     * @param channel
     * @param speed
     * @return true if initialize was successful
     */
    boolean initializeSpi(int channelNr, int speed);

    /**
     * 
     * @param buffer
     * @return true if all data was sent
     */
    boolean writeSpiData(byte[] buffer);

    int getSpiChannel();

}
