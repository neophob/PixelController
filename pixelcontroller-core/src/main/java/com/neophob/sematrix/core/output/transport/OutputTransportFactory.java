package com.neophob.sematrix.core.output.transport;

import com.neophob.sematrix.core.output.transport.serial.ISerial;
import com.neophob.sematrix.core.output.transport.serial.SerialImpl;
import com.neophob.sematrix.core.output.transport.spi.ISpi;
import com.neophob.sematrix.core.output.transport.spi.SpiRaspberryPi;

public final class OutputTransportFactory {

    private OutputTransportFactory() {
        // no instance allowed
    }

    public static ISerial getSerialImpl() {
        return new SerialImpl();
    }

    public static ISpi getRaspberryPiSpiImpl() {
        return new SpiRaspberryPi();
    }

}
