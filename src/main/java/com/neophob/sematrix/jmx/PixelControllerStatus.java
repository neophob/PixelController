/**
 * Copyright (C) 2011 Michael Vogt <michu@neophob.com>
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
package com.neophob.sematrix.jmx;

import java.lang.management.ManagementFactory;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.management.JMException;
import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.apache.commons.collections.buffer.CircularFifoBuffer;

/**
 * 
 * @author michu
 *
 */
public class PixelControllerStatus implements PixelControllerStatusMBean {

	/** The log. */
	private static final Logger LOG = Logger.getLogger(PixelControllerStatus.class.getName());

	public static final String JMX_BEAN_NAME = PixelControllerStatus.class.getCanonicalName()+":type=PixelControllerStatusMBean";
	
	private static final float VERSION = 1.04f;
	
	private int configuredFps;
	private float currentFps;
	private long frameCount;
	
	private CircularFifoBuffer generatorUpdateTime;
	private CircularFifoBuffer effectUpdateTime;
	private CircularFifoBuffer outputUpdateTime;
	private CircularFifoBuffer faderUpdateTime;
		
	
	/**
	 * Register the JMX Bean
	 */
	public PixelControllerStatus(int fps) {
		LOG.log(Level.INFO, "Initialize the PixelControllerStatus JMX Bean");
		
		this.configuredFps = fps;
		generatorUpdateTime = new CircularFifoBuffer(fps);
		effectUpdateTime = new CircularFifoBuffer(fps);
		outputUpdateTime = new CircularFifoBuffer(fps);
		faderUpdateTime = new CircularFifoBuffer(fps);
		
		// register MBean
		try {
			MBeanServer server = ManagementFactory.getPlatformMBeanServer();
			ObjectName name = new ObjectName(JMX_BEAN_NAME);
			// check if the MBean is already registered
			if (!server.isRegistered(name)) {
				server.registerMBean(this, name);
			}
		} catch (JMException ex) {
			LOG.log(Level.WARNING, "Error while registering class as JMX Bean.", ex);
		}
	}
	
	@Override
	public float getVersion() {
		return VERSION;
	}

	@Override
	public float getCurrentFps() {
		return currentFps;
	}

	/**
	 * sum up all existing entries (May represent the last hour)
	 * @param it
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	private float sumUp(Iterator it) {
		float sum = 0;
		while (it.hasNext()) {
			Float entry = (Float) it.next();
			sum += entry;
		}
		return sum;
	}


	@Override
	public float getGeneratorUpdateTime() {
		float total = sumUp(generatorUpdateTime.iterator());
		return total/configuredFps;
	}

	@Override
	public float getEffectUpdateTime() {
		float total = sumUp(effectUpdateTime.iterator());
		return total/configuredFps;
	}

	@Override
	public float getOutputUpdateTime() {
		float total = sumUp(outputUpdateTime.iterator());
		return total/configuredFps;
	}

	@Override
	public float getFaderUpdateTime() {
		float total = sumUp(faderUpdateTime.iterator());
		return total/configuredFps;
	}

	@Override
	public long getFrameCount() {
		return frameCount;
	}


	public void setCurrentFps(float currentFps) {
		this.currentFps = currentFps;
	}

	public void setFrameCount(long frameCount) {
		this.frameCount = frameCount;
	}

	public void addGeneratorUpdateTime(float generatorUpdateTime) {
		this.generatorUpdateTime.add(generatorUpdateTime);
	}

	public void addEffectUpdateTime(float effectUpdateTime) {
		this.effectUpdateTime.add(effectUpdateTime);
	}

	public void addOutputUpdateTime(float outputUpdateTime) {
		this.outputUpdateTime.add(outputUpdateTime);
	}

	public void addFaderUpdateTime(float faderUpdateTime) {
		this.faderUpdateTime.add(faderUpdateTime);
	}
	
	
}
