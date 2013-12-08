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
package com.neophob.sematrix.core.jmx;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.management.JMException;
import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.apache.commons.collections.buffer.CircularFifoBuffer;

import com.neophob.sematrix.core.output.IOutput;
import com.neophob.sematrix.core.output.OutputDeviceEnum;

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
	private static final float VERSION = 1.2f;
	
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
	
	/** The global time measure value. */
	private Map<TimeMeasureItemGlobal, CircularFifoBuffer> timeMeasureMapGlobal;
	
	/** The output dependent measure values */
	private Map<IOutput, Map<TimeMeasureItemOutput, CircularFifoBuffer>> timeMeasureMapOutput;
	
	/** The output list. */
	private List<IOutput> outputList;
	
	private PacketAndBytesStatictics oscServerStatistics;
	
	/**
	 * Register the JMX Bean.
	 *
	 * @param configuredFps the configured fps
	 */
	public PixelControllerStatus(int configuredFps) {
		LOG.log(Level.INFO, "Initialize the PixelControllerStatus JMX Bean");
		
		this.configuredFps = configuredFps;
		
		// initialize all buffers 
		this.timeMeasureMapGlobal = new ConcurrentHashMap<TimeMeasureItemGlobal, CircularFifoBuffer>();
		for (TimeMeasureItemGlobal timeMeasureItem : TimeMeasureItemGlobal.values()) {
			this.timeMeasureMapGlobal.put(timeMeasureItem, new CircularFifoBuffer(this.configuredFps * SECONDS));
		}
		this.timeMeasureMapOutput = new ConcurrentHashMap<IOutput, Map<TimeMeasureItemOutput, CircularFifoBuffer>>();
		this.outputList = new ArrayList<IOutput>();

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
	 * @see com.neophob.sematrix.core.jmx.PixelControllerStatusMBean#getVersion()
	 */
	@Override
	public float getVersion() {
		return VERSION;
	}

	/* (non-Javadoc)
	 * @see com.neophob.sematrix.core.jmx.PixelControllerStatusMBean#getCurrentFps()
	 */
	@Override
	public float getCurrentFps() {
		return currentFps;
	}

	/* (non-Javadoc)
	 * @see com.neophob.sematrix.core.jmx.PixelControllerStatusMBean#getConfiguredFps()
	 */
	@Override
	public float getConfiguredFps() {
		return this.configuredFps;
	}
	
	/* (non-Javadoc)
	 * @see com.neophob.sematrix.core.jmx.PixelControllerStatusMBean#getRecordedMilliSeconds()
	 */
	@Override
	public long getRecordedMilliSeconds() {
		return Float.valueOf(((this.configuredFps * SECONDS) / this.currentFps) * 1000).longValue();
	}

	/* (non-Javadoc)
	 * @see com.neophob.sematrix.core.jmx.PixelControllerStatusMBean#getAverageTime(com.neophob.sematrix.core.jmx.ValueEnum)
	 */
	@Override
	public float getAverageTime(TimeMeasureItemGlobal timeMeasure) {
		return getAverageBufferValue(this.timeMeasureMapGlobal.get(timeMeasure));
	}
	
	/* (non-Javadoc)
	 * @see com.neophob.sematrix.core.jmx.PixelControllerStatusMBean#getOutputAverageTime(int, com.neophob.sematrix.core.jmx.OutputValueEnum)
	 */
	@Override
	public float getOutputAverageTime(int output, TimeMeasureItemOutput timeMeasureItem) {
		return getAverageBufferValue(this.timeMeasureMapOutput.get(this.outputList.get(output)).get(timeMeasureItem));
	}

	/* (non-Javadoc)
	 * @see com.neophob.sematrix.core.jmx.PixelControllerStatusMBean#getFrameCount()
	 */
	@Override
	public long getFrameCount() {
		return frameCount;
	}

	/* (non-Javadoc)
	 * @see com.neophob.sematrix.core.jmx.PixelControllerStatusMBean#getStartTime()
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
	public void trackTime(TimeMeasureItemGlobal valueEnum, long time) {
		if (this.ignoreValue()) {
			return;
		}
		this.timeMeasureMapGlobal.get(valueEnum).add(time);
	}
	
	/**
	 * Track output time.
	 *
	 * @param output the output
	 * @param outputValueEnum the output value enum
	 * @param time the time
	 */
	public void trackOutputTime(IOutput output, TimeMeasureItemOutput timeMeasureItem, long time) {
		if (this.ignoreValue()) {
			return;
		}
		// make sure the output instance is known inside the outputBuffers instance
		if (!this.timeMeasureMapOutput.containsKey(output)) {
			this.timeMeasureMapOutput.put(output, new HashMap<TimeMeasureItemOutput, CircularFifoBuffer>());
			this.outputList.add(output);
		}
		// make sure a circularFifoBuffer instance was construct for the given outputValueEnum
		if (!this.timeMeasureMapOutput.get(output).containsKey(timeMeasureItem)) {
			this.timeMeasureMapOutput.get(output).put(timeMeasureItem, new CircularFifoBuffer(this.configuredFps * SECONDS));
		}
		// add time to internal buffer instance
		this.timeMeasureMapOutput.get(output).get(timeMeasureItem).add(time);
	}
	
	/**
	 * Ignore value if running time < 3 seconds
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
	 * Gets the average buffer value.
	 *
	 * @param circularFifoBuffer the circular fifo buffer
	 * @return returns average value of all buffer entries
	 */
	private static float getAverageBufferValue(CircularFifoBuffer circularFifoBuffer) {
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
	 * @see com.neophob.sematrix.core.jmx.PixelControllerStatusMBean#getNumberOfOutputs()
	 */
	@Override
	public int getNumberOfOutputs() {
		return this.outputList.size();
	}

	/* (non-Javadoc)
	 * @see com.neophob.sematrix.core.jmx.PixelControllerStatusMBean#getOutputType(int)
	 */
	@Override
	public OutputDeviceEnum getOutputType(int output) {
		return this.outputList.get(output).getType();
	}

    /**
     * @return the oscServerStatistics
     */
    public PacketAndBytesStatictics getOscServerStatistics() {
        return oscServerStatistics;
    }

    /**
     * @param oscServerStatistics the oscServerStatistics to set
     */
    public void setOscServerStatistics(PacketAndBytesStatictics oscServerStatistics) {
        this.oscServerStatistics = oscServerStatistics;
    }

    /* (non-Javadoc)
     * @see com.neophob.sematrix.core.jmx.PixelControllerStatusMBean#getRecievedOscPakets()
     */
    @Override
    public int getRecievedOscPakets() {
        if (this.oscServerStatistics==null) {
            return 0;            
        }
        return this.oscServerStatistics.getPacketCounter();
    }

    /* (non-Javadoc)
     * @see com.neophob.sematrix.core.jmx.PixelControllerStatusMBean#getRecievedOscBytes()
     */
    @Override
    public long getRecievedOscBytes() {
        if (this.oscServerStatistics==null) {
            return 0;            
        }
        return this.oscServerStatistics.getBytesRecieved();
    }	
	
}
