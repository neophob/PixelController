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
package com.neophob.sematrix.osc.server;

import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.neophob.sematrix.osc.model.OscMessage;

/**
 * observer pattern implementation for client callback
 * 
 * 
 * @author michu
 *
 */
public abstract class OscMessageHandler implements Observer {

	private static final Logger LOG = Logger.getLogger(OscMessageHandler.class.getName());
	
	public abstract void handleOscMessage(OscMessage msg);
	
	@Override
	public void update(Observable o, Object arg) {
		if (arg instanceof OscMessage) {
			OscMessage msg = (OscMessage) arg;
			handleOscMessage(msg);
        } else {
        	LOG.log(Level.WARNING, "Ignored notification of unknown type: "+arg);
        }
	}

}
