package com.neophob.sematrix.core.output.transport;

import com.neophob.sematrix.core.output.transport.ethernet.EthernetTcpImpl;
import com.neophob.sematrix.core.output.transport.ethernet.EthernetUdpImpl;
import com.neophob.sematrix.core.output.transport.ethernet.IEthernetTcp;
import com.neophob.sematrix.core.output.transport.ethernet.IEthernetUdp;
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

    public static IEthernetUdp getUdpImpl() {
        return new EthernetUdpImpl();
    }

    public static IEthernetTcp getTcpImpl() {
        return new EthernetTcpImpl();
    }

}
