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
