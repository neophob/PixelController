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
	
	private Map<ValueEnum, CircularFifoBuffer> buffers;
	private Map<Output, Map<OutputValueEnum, CircularFifoBuffer>> outputBuffers;
	private List<Output> outputList;
	
	/**
	 * Register the JMX Bean
	 */
	public PixelControllerStatus(int configuredFps) {
		LOG.log(Level.INFO, "Initialize the PixelControllerStatus JMX Bean");
		
		this.configuredFps = configuredFps;
		
		// initialize all buffers 
		this.buffers = new HashMap<ValueEnum, CircularFifoBuffer>();
		for (ValueEnum valueEnum : ValueEnum.values()) {
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
	public float getAverageTime(ValueEnum valueEnum) {
		return this.getBufferValue(this.buffers.get(valueEnum));
	}
	
	@Override
	public float getOutputAverageTime(int output, OutputValueEnum outputValueEnum) {
		return this.getBufferValue(this.outputBuffers.get(this.outputList.get(output)).get(outputValueEnum));
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

	public void trackTime(ValueEnum valueEnum, long time) {
		if (this.ignoreValue()) {
			return;
		}
		this.buffers.get(valueEnum).add(time);
	}
	
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

	/**
	 * @param valueEnum the buffer for that you want to get the average value
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

	@Override
	public int getNumberOfOutputs() {
		return this.outputList.size();
	}

	@Override
	public OutputDeviceEnum getOutputType(int output) {
		return this.outputList.get(output).getType();
	}
}
