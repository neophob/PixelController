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
package com.neophob.sematrix.core.output;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.neophob.sematrix.core.output.transport.spi.ISpi;
import com.neophob.sematrix.core.properties.Configuration;
import com.neophob.sematrix.core.resize.PixelControllerResize;
import com.neophob.sematrix.core.visual.MatrixData;

public class RaspberrySpi2801 extends OnePanelResolutionAwareOutput {

    private static final transient Logger LOG = Logger.getLogger(RaspberrySpi2801.class.getName());

    private static final transient int SPI_CHANNEL = 0;
    private static final transient int LATCH_TIME_IN_US = 500;
    private static final transient int MAXIMAL_SPI_DATA_SIZE = 1024;

    private transient BufferCache bufferCache;

    private boolean connected = false;
    private transient ISpi spi;

    public RaspberrySpi2801(MatrixData matrixData, PixelControllerResize resizeHelper,
            Configuration ph, ISpi spi) {
        super(matrixData, resizeHelper, OutputDeviceEnum.RPI_2801, ph, 8);

        LOG.log(Level.INFO, "Initialize RPi SPI channel, speed: " + ph.getRpiWs2801SpiSpeed());
        this.spi = spi;
        if (!spi.initializeSpi(SPI_CHANNEL, ph.getRpiWs2801SpiSpeed())) {
            LOG.log(Level.SEVERE, "Failed to initialize SPI interface!"
                    + ".\nHint: Verify the SPI module is loaded and not blacklisted. "
                    + "You need to run PixelController as root user to use the SPI device.\n");
            return;
        }
        this.connected = true;
        bufferCache = new BufferCache();
    }

    @Override
    public void update() {
        if (!this.connected) {
            return;
        }

        byte[] rgbBuffer = OutputHelper.convertBufferTo24bit(getTransformedBuffer(), colorFormat);

        if (rgbBuffer.length >= MAXIMAL_SPI_DATA_SIZE) {
            LOG.log(Level.SEVERE,
                    "Maximal SPI sending size is {0}, you tried to send {1}. Reduce matrix size. Disable output.",
                    new Object[] { MAXIMAL_SPI_DATA_SIZE, rgbBuffer.length });
            this.connected = false;
            return;
        } else if (bufferCache.didFrameChange(rgbBuffer)) {

            if (!spi.writeSpiData(rgbBuffer)) {
                LOG.log(Level.WARNING, "Failed to send {0} bytes via SPI!",
                        new Object[] { rgbBuffer.length });
            }
        }
        try {
            Thread.sleep(0, LATCH_TIME_IN_US);
        } catch (InterruptedException e) {
            // ignored
        }
    }

    @Override
    public void close() {
        // nothing todo
    }

    @Override
    public String getConnectionStatus() {
        if (this.connected) {
            return "Connected on SPI channel " + spi.getSpiChannel();
        }
        return "Not connected!";
    }

    @Override
    public boolean isSupportConnectionState() {
        return true;
    }

    @Override
    public boolean isConnected() {
        return this.connected;
    }

}
