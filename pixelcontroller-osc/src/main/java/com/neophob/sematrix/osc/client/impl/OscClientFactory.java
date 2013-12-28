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
package com.neophob.sematrix.osc.client.impl;

import com.neophob.sematrix.osc.client.OscClientException;
import com.neophob.sematrix.osc.client.PixOscClient;

/**
 * OSC Client Factory, send OSC Message
 * 
 * @author michu
 * 
 */
public final class OscClientFactory {

    private OscClientFactory() {
        // no instance
    }

    public static PixOscClient createClientTcp(String targetIp, int targetPort, int sourcePort,
            int bufferSize) throws OscClientException {
        return new OscClientImpl(true, targetIp, targetPort, sourcePort, bufferSize);
    }

    public static PixOscClient createClientUdp(String targetIp, int targetPort, int sourcePort,
            int bufferSize) throws OscClientException {
        return new OscClientImpl(false, targetIp, targetPort, sourcePort, bufferSize);
    }

}
