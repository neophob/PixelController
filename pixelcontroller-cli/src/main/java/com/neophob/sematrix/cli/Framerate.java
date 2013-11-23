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