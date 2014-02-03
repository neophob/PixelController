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
package com.neophob.sematrix.core.output.transport.spi;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.neophob.sematrix.core.output.transport.spi.ISpi;
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
