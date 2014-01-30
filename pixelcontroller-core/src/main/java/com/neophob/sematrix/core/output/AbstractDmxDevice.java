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
package com.neophob.sematrix.core.output;

import java.net.InetAddress;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.neophob.sematrix.core.properties.ApplicationConfigurationHelper;
import com.neophob.sematrix.core.properties.ColorFormat;
import com.neophob.sematrix.core.properties.DeviceConfig;
import com.neophob.sematrix.core.resize.PixelControllerResize;
import com.neophob.sematrix.core.visual.MatrixData;

/**
 * 
 * @author michu
 * 
 */
public abstract class AbstractDmxDevice extends Output {

    private static final transient Logger LOG = Logger.getLogger(AbstractDmxDevice.class.getName());

    /** The display options, does the buffer needs to be flipped? rotated? */
    protected List<DeviceConfig> displayOptions;

    /** The output color format. */
    private List<ColorFormat> colorFormat;

    /** define how the panels are arranged */
    private List<Integer> panelOrder;

    /** The x size. */
    private int xResolution;

    /** The y size. */
    private int yResolution;

    // dmx specific settings
    protected int sequenceID;
    protected int pixelsPerUniverse;
    protected int nrOfUniverse;
    protected int firstUniverseId;
    protected InetAddress targetAdress;

    /** The initialized. */
    protected boolean initialized;

    /** flip each 2nd scanline? */
    protected boolean snakeCabeling;

    protected int[] mapping;

    private int nrOfScreens;

    /**
     * 
     * @param outputDeviceEnum
     * @param ph
     * @param controller
     * @param bpp
     */
    public AbstractDmxDevice(MatrixData matrixData, PixelControllerResize resizeHelper,
            OutputDeviceEnum outputDeviceEnum, ApplicationConfigurationHelper ph, int bpp,
            int nrOfScreens) {
        super(matrixData, resizeHelper, outputDeviceEnum, ph, bpp);

        this.nrOfScreens = nrOfScreens;
        this.colorFormat = ph.getColorFormat();
        this.panelOrder = ph.getPanelOrder();
        this.xResolution = ph.parseOutputXResolution();
        this.yResolution = ph.parseOutputYResolution();
        this.snakeCabeling = ph.isOutputSnakeCabeling();
        this.mapping = ph.getOutputMappingValues();

        this.initialized = false;
    }

    /**
     * concrete classes need to implement this
     * 
     * @param universeId
     * @param buffer
     */
    protected abstract void sendBufferToReceiver(int universeId, byte[] buffer);

    /**
	 * 
	 */
    protected void calculateNrOfUniverse() {
        // check how many universe we need
        this.nrOfUniverse = 1;
        int bufferSize = xResolution * yResolution;
        if (bufferSize > pixelsPerUniverse) {
            while (bufferSize > pixelsPerUniverse) {
                this.nrOfUniverse++;
                bufferSize -= pixelsPerUniverse;
            }
        }

        LOG.log(Level.INFO, "\tPixels per universe: " + pixelsPerUniverse);
        LOG.log(Level.INFO, "\tFirst universe ID: " + firstUniverseId);
        LOG.log(Level.INFO, "\t# of universe: " + nrOfUniverse * nrOfScreens);
        LOG.log(Level.INFO, "\tOutput Mapping entry size: " + this.mapping.length);
        LOG.log(Level.INFO, "\tTarget address: " + targetAdress);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.neophob.sematrix.core.output.Output#update()
     */
    @Override
    public void update() {
        int universeOfs = 0;

        if (initialized) {
            for (int nr = 0; nr < nrOfScreens; nr++) {
                // get the effective panel buffer
                int panelNr = this.panelOrder.get(nr);

                // get buffer data
                int[] transformedBuffer = RotateBuffer.transformImage(super.getBufferForScreen(nr),
                        displayOptions.get(panelNr), this.matrixData.getDeviceXSize(),
                        this.matrixData.getDeviceYSize());

                if (this.snakeCabeling) {
                    // flip each 2nd scanline
                    transformedBuffer = OutputHelper.flipSecondScanline(transformedBuffer,
                            this.matrixData.getDeviceXSize(), this.matrixData.getDeviceYSize());
                } else if (this.mapping.length > 0) {
                    // do manual mapping
                    transformedBuffer = OutputHelper.manualMapping(transformedBuffer, mapping,
                            xResolution, yResolution);
                }

                byte[] rgbBuffer = OutputHelper.convertBufferTo24bit(transformedBuffer,
                        colorFormat.get(panelNr));

                // send out
                int remainingBytes = rgbBuffer.length;// 510
                int ofs = 0;
                for (int i = 0; i < this.nrOfUniverse; i++) {
                    int tmp = pixelsPerUniverse * 3;// tmp=510
                    if (remainingBytes <= pixelsPerUniverse * 3) {
                        tmp = remainingBytes;
                    }
                    byte[] buffer = new byte[tmp];
                    System.arraycopy(rgbBuffer, ofs, buffer, 0, tmp);
                    remainingBytes -= tmp;
                    ofs += tmp;
                    sendBufferToReceiver(this.firstUniverseId + universeOfs, buffer);

                    universeOfs++;
                }

            }
        }

    }

    @Override
    public String getConnectionStatus() {
        if (initialized) {
            return "Target IP: " + targetAdress + ", # of universe: " + nrOfUniverse * nrOfScreens;
        }
        return "Not connected!";
    }

    @Override
    public boolean isSupportConnectionState() {
        return true;
    }

    @Override
    public boolean isConnected() {
        return initialized;
    }

    public int getPixelsPerUniverse() {
        return pixelsPerUniverse;
    }

    public int getNrOfUniverse() {
        return nrOfUniverse;
    }

    public int getFirstUniverseId() {
        return firstUniverseId;
    }

}
