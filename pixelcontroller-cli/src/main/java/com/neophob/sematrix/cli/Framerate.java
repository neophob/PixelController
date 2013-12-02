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
package com.neophob.sematrix.cli;

import java.util.logging.Logger;

/**
 * 
 * @author michu
 *
 */
public class Framerate {

	private static final Logger LOG = Logger.getLogger(Framerate.class.getName());
	
	private long nextRepaintDue = 0;
	private long startTime;
	private long delay;
	private int targetFps;

	public Framerate(int targetFps) {
		LOG.info("Target fps: "+targetFps);
		this.delay = 1000/targetFps;		
		this.startTime = System.currentTimeMillis();
		this.targetFps = targetFps;
	}

	public void waitForFps(long cnt) {
		long now = System.currentTimeMillis();
		if (nextRepaintDue > now) {
			// too soon to repaint, wait...
			try {
				Thread.sleep(nextRepaintDue - now);
			} catch (InterruptedException e) {
				//ignore it
			}
		}
		nextRepaintDue = System.currentTimeMillis() + delay;
		
		if (cnt % (targetFps*5) == 0) {
			long tdiff = (System.currentTimeMillis() - startTime) / 1000;
			if (tdiff>0) {
				LOG.info("FPS: "+ (cnt/tdiff));						
			}
		}
		
	}
}