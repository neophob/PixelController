package com.neophob.sematrix.core.output.spi;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.pi4j.wiringpi.Spi;

public class SpiRaspberryPi implements ISpi {

    private static final transient Logger LOG = Logger.getLogger(SpiRaspberryPi.class.getName());
    private int spiChannel = Spi.CHANNEL_0;

    @Override
    public boolean initializeSpi(int channelNr, int speed) {
        int fd = Spi.wiringPiSPISetup(channelNr, speed);
        this.spiChannel = channelNr;
        if (fd < 0) {
            LOG.log(Level.SEVERE, "SPI init failed, RC=" + fd);
            return false;
        }
        return true;
    }

    @Override
    public boolean writeSpiData(byte[] buffer) {
        int rc = Spi.wiringPiSPIDataRW(spiChannel, buffer, buffer.length);
        if (rc != buffer.length) {
            return false;
        }
        return true;
    }

    @Override
    public int getSpiChannel() {
        return spiChannel;
    }

}
