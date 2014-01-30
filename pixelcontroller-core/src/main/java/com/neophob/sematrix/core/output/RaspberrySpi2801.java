package com.neophob.sematrix.core.output;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.neophob.sematrix.core.properties.ApplicationConfigurationHelper;
import com.neophob.sematrix.core.resize.PixelControllerResize;
import com.neophob.sematrix.core.visual.MatrixData;
import com.pi4j.wiringpi.Spi;

public class RaspberrySpi2801 extends OnePanelResolutionAwareOutput {

    private static final transient Logger LOG = Logger.getLogger(RaspberrySpi2801.class.getName());

    private static final transient int LATCH_TIME_IN_US = 500;
    private static final transient int MAXIMAL_SPI_DATA_SIZE = 1024;

    private transient BufferCache bufferCache;

    private boolean connected = false;
    private int spiChannel;

    public RaspberrySpi2801(MatrixData matrixData, PixelControllerResize resizeHelper,
            ApplicationConfigurationHelper ph) {
        super(matrixData, resizeHelper, OutputDeviceEnum.RPI_2801, ph, 8);

        LOG.log(Level.INFO, "Initialize RPi SPI channel, speed: " + ph.getRpiWs2801SpiSpeed());
        spiChannel = Spi.CHANNEL_0;
        int fd = Spi.wiringPiSPISetup(spiChannel, ph.getRpiWs2801SpiSpeed());
        if (fd < 0) {
            LOG.log(Level.SEVERE, "Failed to initialize SPI, error: " + fd
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
            int rc = Spi.wiringPiSPIDataRW(spiChannel, rgbBuffer, rgbBuffer.length);
            if (rc != rgbBuffer.length) {
                LOG.log(Level.WARNING, "Failed to send data, send {0} bytes, rc: {1}.",
                        new Object[] { rgbBuffer.length, rc });
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
