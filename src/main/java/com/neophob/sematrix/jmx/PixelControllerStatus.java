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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.management.JMException;
import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.apache.commons.collections.buffer.CircularFifoBuffer;

import com.neophob.sematrix.output.Output;
import com.neophob.sematrix.output.OutputDeviceEnum;

/**
 * The Class PixelControllerStatus.
 *
 * @author michu
 */
public class PixelControllerStatus implements PixelControllerStatusMBean {

	/** The log. */
	private static final Logger LOG = Logger.getLogger(PixelControllerStatus.class.getName());
	
	/** The Constant JMX_BEAN_NAME. */
	public static final String JMX_BEAN_NAME = PixelControllerStatus.class.getCanonicalName()+":type=PixelControllerStatusMBean";
	
	/** The Constant VERSION. */
	private static final float VERSION = 1.1f;
	
	/** The Constant SECONDS. */
	private static final int SECONDS = 10;
		
	/** The Constant COOL_DOWN_MILLISECONDS. */
	private static final int COOL_DOWN_MILLISECONDS = 3000;
	
	private static long coolDownTimestamp = System.currentTimeMillis();

	/** The configured fps. */
	private int configuredFps;
	
	/** The current fps. */
	private float currentFps;
	
	/** The frame count. */
	private long frameCount;
	
	/** The start time. */
	private long startTime;
	
	/** The buffers. */
	private Map<TimeMeasure, CircularFifoBuffer> buffers;
	
	/** The output buffers. */
	private Map<Output, Map<OutputValueEnum, CircularFifoBuffer>> outputBuffers;
	
	/** The output list. */
	private List<Output> outputList;
	
	/**
	 * Register the JMX Bean.
	 *
	 * @param configuredFps the configured fps
	 */
	public PixelControllerStatus(int configuredFps) {
		LOG.log(Level.INFO, "Initialize the PixelControllerStatus JMX Bean");
		
		this.configuredFps = configuredFps;
		
		// initialize all buffers 
		this.buffers = new HashMap<TimeMeasure, CircularFifoBuffer>();
		for (TimeMeasure valueEnum : TimeMeasure.values()) {
			this.buffers.put(valueEnum, new CircularFifoBuffer(this.configuredFps * SECONDS));
		}
		this.outputBuffers = new HashMap<Output, Map<OutputValueEnum, CircularFifoBuffer>>();
		this.outputList = new ArrayList<Output>();

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
	
	/* (non-Javadoc)
	 * @see com.neophob.sematrix.jmx.PixelControllerStatusMBean#getVersion()
	 */
	@Override
	public float getVersion() {
		return VERSION;
	}

	/* (non-Javadoc)
	 * @see com.neophob.sematrix.jmx.PixelControllerStatusMBean#getCurrentFps()
	 */
	@Override
	public float getCurrentFps() {
		return currentFps;
	}

	/* (non-Javadoc)
	 * @see com.neophob.sematrix.jmx.PixelControllerStatusMBean#getConfiguredFps()
	 */
	@Override
	public float getConfiguredFps() {
		return this.configuredFps;
	}
	
	/* (non-Javadoc)
	 * @see com.neophob.sematrix.jmx.PixelControllerStatusMBean#getRecordedMilliSeconds()
	 */
	@Override
	public long getRecordedMilliSeconds() {
		return Float.valueOf(((this.configuredFps * SECONDS) / this.currentFps) * 1000).longValue();
	}

	/* (non-Javadoc)
	 * @see com.neophob.sematrix.jmx.PixelControllerStatusMBean#getAverageTime(com.neophob.sematrix.jmx.ValueEnum)
	 */
	@Override
	public float getAverageTime(TimeMeasure valueEnum) {
		return this.getBufferValue(this.buffers.get(valueEnum));
	}
	
	/* (non-Javadoc)
	 * @see com.neophob.sematrix.jmx.PixelControllerStatusMBean#getOutputAverageTime(int, com.neophob.sematrix.jmx.OutputValueEnum)
	 */
	@Override
	public float getOutputAverageTime(int output, OutputValueEnum outputValueEnum) {
		return this.getBufferValue(this.outputBuffers.get(this.outputList.get(output)).get(outputValueEnum));
	}

	/* (non-Javadoc)
	 * @see com.neophob.sematrix.jmx.PixelControllerStatusMBean#getFrameCount()
	 */
	@Override
	public long getFrameCount() {
		return frameCount;
	}

	/* (non-Javadoc)
	 * @see com.neophob.sematrix.jmx.PixelControllerStatusMBean#getStartTime()
	 */
	@Override
	public long getStartTime() {
		return startTime;
	}
	
	/**
	 * Sets the current fps.
	 *
	 * @param currentFps the new current fps
	 */
	public void setCurrentFps(float currentFps) {
		this.currentFps = currentFps;
	}

	/**
	 * Sets the frame count.
	 *
	 * @param frameCount the new frame count
	 */
	public void setFrameCount(long frameCount) {
		this.frameCount = frameCount;
	}

	/**
	 * Track time.
	 *
	 * @param valueEnum the value enum
	 * @param time the time
	 */
	public void trackTime(TimeMeasure valueEnum, long time) {
		if (this.ignoreValue()) {
			return;
		}
		this.buffers.get(valueEnum).add(time);
	}
	
	/**
	 * Track output time.
	 *
	 * @param output the output
	 * @param outputValueEnum the output value enum
	 * @param time the time
	 */
	public void trackOutputTime(Output output, OutputValueEnum outputValueEnum, long time) {
		if (this.ignoreValue()) {
			return;
		}
		// make sure the output instance is known inside the outputBuffers instance
		if (!this.outputBuffers.containsKey(output)) {
			this.outputBuffers.put(output, new HashMap<OutputValueEnum, CircularFifoBuffer>());
			this.outputList.add(output);
		}
		// make sure a circularFifoBuffer instance was construct for the given outputValueEnum
		if (!this.outputBuffers.get(output).containsKey(outputValueEnum)) {
			this.outputBuffers.get(output).put(outputValueEnum, new CircularFifoBuffer(this.configuredFps * SECONDS));
		}
		// add time to internal buffer instance
		this.outputBuffers.get(output).get(outputValueEnum).add(time);
	}
	
	/**
	 * Ignore value.
	 *
	 * @return true, if successful
	 */
	private boolean ignoreValue() {
		if (coolDownTimestamp != -1) {
			if (System.currentTimeMillis() - coolDownTimestamp > COOL_DOWN_MILLISECONDS) {
				coolDownTimestamp = -1;
			} else {
				return true;
			}
		}
		return false;
	}

	/**
	 * Gets the buffer value.
	 *
	 * @param circularFifoBuffer the circular fifo buffer
	 * @return returns average value of all buffer entries
	 */
	private float getBufferValue(CircularFifoBuffer circularFifoBuffer) {
		// handle null instance
		if (circularFifoBuffer == null) {
			return 0f;
		}
		// calculate sum of all buffer values
		float bufferSum = 0f;
		@SuppressWarnings("rawtypes")
		Iterator iterator = circularFifoBuffer.iterator();
		while (iterator.hasNext()) {
			bufferSum += (Long) iterator.next();
		}
		// return average value
		float result = bufferSum / circularFifoBuffer.size();
		if (Float.isNaN(result)) {
			result = 0f;
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see com.neophob.sematrix.jmx.PixelControllerStatusMBean#getNumberOfOutputs()
	 */
	@Override
	public int getNumberOfOutputs() {
		return this.outputList.size();
	}

	/* (non-Javadoc)
	 * @see com.neophob.sematrix.jmx.PixelControllerStatusMBean#getOutputType(int)
	 */
	@Override
	public OutputDeviceEnum getOutputType(int output) {
		return this.outputList.get(output).getType();
	}
}
