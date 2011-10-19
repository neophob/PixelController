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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
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
	private static final int SECONDS = 10;
	private static long COOL_DOWN_TIMESTAMP = System.currentTimeMillis();
	private static final int COOL_DOWN_MILLISECONDS = 3000;
	
	private int configuredFps;
	private float currentFps;
	private long frameCount;
	private long startTime;
	
	private Map<BufferEnum, CircularFifoBuffer> buffers;
	
	private enum BufferEnum {
		GENERATOR,
		EFFECT,
		OUTPUT,
		FADER,
		INTERNAL_WINDOW;
	}
	
	/**
	 * Register the JMX Bean
	 */
	public PixelControllerStatus(int configuredFps) {
		LOG.log(Level.INFO, "Initialize the PixelControllerStatus JMX Bean");
		
		this.configuredFps = configuredFps;
		
		// initialize all buffers 
		this.buffers = new HashMap<BufferEnum, CircularFifoBuffer>();
		for (BufferEnum bufferenum : BufferEnum.values()) {
			this.buffers.put(bufferenum, new CircularFifoBuffer(this.configuredFps * SECONDS));
		}
		startTime = System.currentTimeMillis();
		
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

	@Override
	public float getConfiguredFps() {
		return this.configuredFps;
	}
	
	@Override
	public long getRecordedMilliSeconds() {
		return Float.valueOf(((this.configuredFps * SECONDS) / this.currentFps) * 1000).longValue();
	}

	@Override
	public float getGeneratorUpdateTime() {
		return this.getBufferValue(BufferEnum.GENERATOR);
	}

	@Override
	public float getEffectUpdateTime() {
		return this.getBufferValue(BufferEnum.EFFECT);
	}

	@Override
	public float getOutputUpdateTime() {
		return this.getBufferValue(BufferEnum.OUTPUT);
	}

	@Override
	public float getFaderUpdateTime() {
		return this.getBufferValue(BufferEnum.FADER);
	}

	@Override
	public float getInternalWindowUpdateTime() {
		return this.getBufferValue(BufferEnum.INTERNAL_WINDOW);
	}

	@Override
	public long getFrameCount() {
		return frameCount;
	}

	@Override
	public long getStartTime() {
		return startTime;
	}
	
	public void setCurrentFps(float currentFps) {
		this.currentFps = currentFps;
	}

	public void setFrameCount(long frameCount) {
		this.frameCount = frameCount;
	}

	public void addGeneratorUpdateTime(long generatorUpdateTime) {
		if (!ignoreValue()) {
			this.buffers.get(BufferEnum.GENERATOR).add(generatorUpdateTime);
		}
	}

	public void addEffectUpdateTime(long effectUpdateTime) {
		if (!ignoreValue()) {
			this.buffers.get(BufferEnum.EFFECT).add(effectUpdateTime);
		}
	}

	public void addOutputUpdateTime(long outputUpdateTime) {
		if (!ignoreValue()) {
			this.buffers.get(BufferEnum.OUTPUT).add(outputUpdateTime);
		}
	}

	public void addFaderUpdateTime(long faderUpdateTime) {
		if (!ignoreValue()) {
			this.buffers.get(BufferEnum.FADER).add(faderUpdateTime);
		}
	}

	public void addInternalWindowUpdateTime(long udateTime) {
		if (!ignoreValue()) {
			this.buffers.get(BufferEnum.INTERNAL_WINDOW).add(udateTime);
		}
	}

	/**
	 * @param bufferEnum the buffer for that you want to get the average value
	 * @return returns average value of all buffer entries
	 */
	private float getBufferValue(BufferEnum bufferEnum) {
		CircularFifoBuffer buffer = this.buffers.get(bufferEnum);
		// calculate sum of all buffer values
		float bufferSum = 0f;
		@SuppressWarnings("rawtypes")
		Iterator iterator = buffer.iterator();
		while (iterator.hasNext()) {
			bufferSum += (Long) iterator.next();
		}
		// return average value
		float result = bufferSum / buffer.size();
		if (Float.isNaN(result)) {
			result = 0f;
		}
		return result;
	}

	private boolean ignoreValue() {
		if (COOL_DOWN_TIMESTAMP != -1) {
			if (System.currentTimeMillis() - COOL_DOWN_TIMESTAMP > (COOL_DOWN_MILLISECONDS)) {
				COOL_DOWN_TIMESTAMP = -1;
			} else {
				return true;
			}
		}
		return false;
	}
}
