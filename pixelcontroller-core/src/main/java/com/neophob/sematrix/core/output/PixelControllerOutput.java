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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.neophob.sematrix.core.PixelControllerElement;
import com.neophob.sematrix.core.jmx.PixelControllerStatusMBean;
import com.neophob.sematrix.core.jmx.TimeMeasureItemGlobal;
import com.neophob.sematrix.core.jmx.TimeMeasureItemOutput;
import com.neophob.sematrix.core.output.transport.OutputTransportFactory;
import com.neophob.sematrix.core.output.transport.serial.ISerial;
import com.neophob.sematrix.core.properties.ApplicationConfigurationHelper;
import com.neophob.sematrix.core.resize.PixelControllerResize;
import com.neophob.sematrix.core.visual.MatrixData;
import com.neophob.sematrix.core.visual.VisualState;

/**
 * The Class PixelControllerOutput.
 */
public class PixelControllerOutput implements PixelControllerElement {

    /** The Constant LOG. */
    private static final Logger LOG = Logger.getLogger(PixelControllerOutput.class.getName());

    /** The all outputs. */
    private List<IOutput> allOutputs;

    /** The executor service. */
    private ExecutorService executorService;

    /** The update end gate. */
    private CountDownLatch updateEndGate;

    /** The prepare end gate. */
    private CountDownLatch prepareEndGate;

    private PixelControllerStatusMBean statistic;

    /**
     * 
     * @param applicationConfig
     * @throws IllegalArgumentException
     */
    public static IOutput getOutputDevice(MatrixData matrixData,
            PixelControllerResize resizeHelper, ApplicationConfigurationHelper applicationConfig)
            throws IllegalArgumentException {
        OutputDeviceEnum outputDeviceEnum = applicationConfig.getOutputDevice();
        IOutput output = null;

        // create concrete serial implementation
        ISerial serialPort = OutputTransportFactory.getSerialImpl();

        try {
            switch (outputDeviceEnum) {
                case PIXELINVADERS:
                    output = new PixelInvadersSerialDevice(matrixData, resizeHelper,
                            applicationConfig, serialPort);
                    break;
                case PIXELINVADERS_NET:
                    output = new PixelInvadersNetDevice(matrixData, resizeHelper, applicationConfig);
                    break;
                case RAINBOWDUINO_V2:
                    output = new RainbowduinoV2Device(matrixData, resizeHelper, applicationConfig,
                            serialPort);
                    break;
                case RAINBOWDUINO_V3:
                    output = new RainbowduinoV3Device(matrixData, resizeHelper, applicationConfig,
                            serialPort);
                    break;
                case ARTNET:
                    output = new ArtnetDevice(matrixData, resizeHelper, applicationConfig);
                    break;
                case E1_31:
                    output = new E1_31Device(matrixData, resizeHelper, applicationConfig,
                            OutputTransportFactory.getUdpImpl());
                    break;
                case MINIDMX:
                    output = new MiniDmxDevice(matrixData, resizeHelper, applicationConfig,
                            serialPort);
                    break;
                case NULL:
                    output = new NullDevice(matrixData, resizeHelper, applicationConfig);
                    break;
                case UDP:
                    output = new UdpDevice(matrixData, resizeHelper, applicationConfig,
                            OutputTransportFactory.getUdpImpl());
                    break;
                case TPM2:
                    output = new Tpm2(matrixData, resizeHelper, applicationConfig, serialPort);
                    break;
                case TPM2NET:
                    output = new Tpm2Net(matrixData, resizeHelper, applicationConfig,
                            OutputTransportFactory.getUdpImpl());
                    break;
                case RPI_2801:
                    output = new RaspberrySpi2801(matrixData, resizeHelper, applicationConfig,
                            OutputTransportFactory.getRaspberryPiSpiImpl());
                    break;
                default:
                    throw new IllegalArgumentException(
                            "Unable to initialize unknown output device: " + outputDeviceEnum);
            }

        } catch (Exception e) {
            LOG.log(Level.SEVERE, "\n\nERROR: Unable to initialize output device: "
                    + outputDeviceEnum, e);
        }

        return output;
    }

    /**
     * Instantiates a new pixel controller output.
     */
    public PixelControllerOutput(PixelControllerStatusMBean statistic) {
        this.allOutputs = new CopyOnWriteArrayList<IOutput>();
        this.statistic = statistic;
        this.executorService = Executors.newCachedThreadPool();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.neophob.sematrix.core.glue.PixelControllerElement#initAll()
     */
    public void initAll() {
        // nothing to init here
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.neophob.sematrix.core.glue.PixelControllerElement#getCurrentState()
     */
    public List<String> getCurrentState() {
        // no status to store
        return new ArrayList<String>();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.neophob.sematrix.core.glue.PixelControllerElement#update()
     */
    @Override
    public void update() {
        // check if this is the first call of this method
        if (this.prepareEndGate == null && this.updateEndGate == null) {
            LOG.log(Level.INFO, "Init output");
            // we have to prepare the int[] buffers manually the first time. to
            // not mess up this method even more the prepare() methods will be
            // called directly without any additional threading overhead. for
            // the first frame it shouldn't really matter that the outputs have
            // to wait until the int[] buffers preparation is done.
            for (IOutput output : this.allOutputs) {
                output.prepareOutputBuffer(VisualState.getInstance());
            }
        }

        // wait for the outputs to finish their prepare() methods from the
        // previous call of this method
        long startTime = System.currentTimeMillis();
        if (this.prepareEndGate != null) {
            try {
                this.prepareEndGate.await();
            } catch (InterruptedException e) {
                LOG.log(Level.SEVERE,
                        "waiting for all outputs to finish their prepare() method got interrupted!",
                        e);
            }
        }

        statistic.trackTime(TimeMeasureItemGlobal.OUTPUT_PREPARE_WAIT, System.currentTimeMillis()
                - startTime);

        // wait for the outputs to finish their update() methods from the
        // previous call of this method
        startTime = System.currentTimeMillis();
        if (this.updateEndGate != null) {
            try {
                this.updateEndGate.await();
            } catch (InterruptedException e) {
                LOG.log(Level.SEVERE,
                        "waiting for all outputs to finish their update() method got interrupted!",
                        e);
            }
        }
        statistic.trackTime(TimeMeasureItemGlobal.OUTPUT_UPDATE_WAIT, System.currentTimeMillis()
                - startTime);

        // after the prepare() and update() methods call of all outputs are done
        // we have in every output the currentBufferMap instance that contains
        // all int[] buffer that just have been written to the output instances
        // and can therefore be cleaned. also we have the preparedBufferMap
        // instance containing the new set of int[] buffers to be written to the
        // output. therefore we have switch both map instances to be ready for
        // the next call of this method
        for (IOutput output : this.allOutputs) {
            output.switchBuffers();
        }

        // create countDownLatches used to call all update() and prepare()
        // methods simultaneously and to block until all calls have been
        // finished via the end gate instances
        final CountDownLatch updateStartGate = new CountDownLatch(1);
        this.updateEndGate = new CountDownLatch(this.getNumberOfPhysicalOutputs());
        final CountDownLatch prepareStartGate = new CountDownLatch(1);
        this.prepareEndGate = new CountDownLatch(this.allOutputs.size());

        // construct two runnable instance for each output and schedule them
        for (final IOutput output : this.allOutputs) {
            // create runnable instance for preparing an output instance
            Runnable prepareRunnable = new Runnable() {
                @Override
                public void run() {
                    try {
                        prepareStartGate.await();
                        try {
                            long startTime = System.currentTimeMillis();
                            output.prepareOutputBuffer(VisualState.getInstance());
                            statistic.trackOutputTime(output, TimeMeasureItemOutput.PREPARE,
                                    System.currentTimeMillis() - startTime);
                        } finally {
                            prepareEndGate.countDown();
                        }
                    } catch (InterruptedException e) {
                        LOG.log(Level.SEVERE, "waiting for start gate of output: "
                                + output.getClass().getSimpleName() + " got interrupted!", e);
                    }
                }
            };
            this.executorService.execute(prepareRunnable);
            // skip update method call for non-physical outputs
            if (!output.getType().isPhysical()) {
                continue;
            }
            // create runnable instance for updating an output instance
            Runnable updateRunnable = new Runnable() {
                @Override
                public void run() {
                    try {
                        updateStartGate.await();
                        try {
                            long startTime = System.currentTimeMillis();
                            output.update();
                            statistic.trackOutputTime(output, TimeMeasureItemOutput.UPDATE,
                                    System.currentTimeMillis() - startTime);
                        } finally {
                            updateEndGate.countDown();
                        }
                    } catch (InterruptedException e) {
                        LOG.log(Level.SEVERE, "waiting for start gate of output: "
                                + output.getClass().getSimpleName() + " got interrupted!", e);
                    }
                }
            };
            this.executorService.execute(updateRunnable);
        }

        // trigger output update() methods and write the
        // current int[] buffers to the output instances
        updateStartGate.countDown();

        // trigger output prepare() methods to be ready for
        // the next run in parallel to the running update() methods
        prepareStartGate.countDown();
    }

    /**
     * Gets the all outputs.
     * 
     * @return the all outputs
     */
    public List<IOutput> getAllOutputs() {
        return allOutputs;
    }

    /**
     * Adds the output.
     * 
     * @param output
     *            the output
     */
    public void addOutput(IOutput output) {
        allOutputs.add(output);
    }

    /**
     * Gets the number of physical outputs.
     * 
     * @return the number of physical outputs
     */
    private int getNumberOfPhysicalOutputs() {
        int outputs = 0;
        for (IOutput output : this.allOutputs) {
            if (output.getType().isPhysical()) {
                outputs++;
            }
        }
        return outputs;
    }
}
