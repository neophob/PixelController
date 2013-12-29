package com.neophob.sematrix.core.output;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.neophob.sematrix.core.properties.ApplicationConfigurationHelper;
import com.pi4j.wiringpi.Spi;

public class RaspberrySpi2801 extends OnePanelResolutionAwareOutput {

    private static final transient Logger LOG = Logger.getLogger(RaspberrySpi2801.class.getName());

    private static final transient int LATCH_TIME_IN_US = 500;

    private boolean connected = false;
    private int spiChannel;

    public RaspberrySpi2801(ApplicationConfigurationHelper ph) {
        super(OutputDeviceEnum.RASPBERRYPI_SPI_WS2801, ph, 8);

        LOG.log(Level.INFO, "Initialize RPi SPI channel, speed: " + ph.getRpiWs2801SpiSpeed());
        spiChannel = Spi.CHANNEL_0;
        int fd = Spi.wiringPiSPISetup(spiChannel, ph.getRpiWs2801SpiSpeed());
        if (fd < 0) {
            LOG.log(Level.SEVERE,
                    "Failed to initialize SPI, error: "
                            + fd
                            + ".\nMake sure the pi user has access to it (sudo chown `id -u`.`id -g` /dev/spidev0.*)\n");
            return;
        }
        this.connected = true;
    }

    @Override
    public void update() {
        if (!this.connected) {
            return;
        }

        byte[] rgbBuffer = OutputHelper.convertBufferTo24bit(getTransformedBuffer(), colorFormat);
        Spi.wiringPiSPIDataRW(0, rgbBuffer, rgbBuffer.length);
        try {
            Thread.sleep(0, LATCH_TIME_IN_US);
        } catch (InterruptedException e) {
            // ignored
        }
    }

    @Override
    public void close() {
        if (!this.connected) {
            return;
        }
    }

    @Override
    public String getConnectionStatus() {
        if (this.connected) {
            return "Connected on SPI channel " + spiChannel;
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
