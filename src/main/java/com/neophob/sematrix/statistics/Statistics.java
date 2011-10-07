package com.neophob.sematrix.statistics;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang.time.DurationFormatUtils;


public class Statistics {
	private static final Logger LOG = Logger.getLogger(Statistics.class.getName());
	private static final int LOGGING_INTERVAL = 60;
	
	private boolean enabled;
	private Map<ValueEnum, List<Long>> values;
	private int frameCount;
	private int referenceFrameCount;
	private ScheduledExecutorService scheduledExecutorService;
	
	private Statistics() {
		this.enabled = false;
	}
	
	public synchronized void enable() {
		if (this.enabled) {
			throw new IllegalStateException("Statistics class can only be enabled once!");
		}
		// initialize class variables
		this.enabled = true;
		this.values = new ConcurrentHashMap<ValueEnum, List<Long>>();
		for (ValueEnum valueEnum : ValueEnum.values()) {
			this.values.put(valueEnum, new CopyOnWriteArrayList<Long>());
		}
		this.frameCount = 0;
		this.referenceFrameCount = 0;
		this.scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
		// construct runnable instance
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				// calculate average values of all ValueEnum instances
				long[] averageValues = new long[ValueEnum.values().length];
				long[] totalValues = new long[ValueEnum.values().length];
				for (ValueEnum valueEnum : ValueEnum.values()) {
					averageValues[valueEnum.ordinal()] = 0;
					for (Long value : values.get(valueEnum)) {
						totalValues[valueEnum.ordinal()] += value; 
					}
					averageValues[valueEnum.ordinal()] = totalValues[valueEnum.ordinal()] / values.get(valueEnum).size();
					values.get(valueEnum).clear();
				}
				// calculate average fps per minute
				float averageFPS = (frameCount - referenceFrameCount) / (float) LOGGING_INTERVAL;
				referenceFrameCount = frameCount;
				// log average values
				StringBuffer stringBuffer = new StringBuffer();
				for (ValueEnum valueEnum : ValueEnum.values()) {
					// add average values
					stringBuffer.append(valueEnum.getDescription());
					stringBuffer.append(": ");
					stringBuffer.append(DurationFormatUtils.formatDuration(
							TimeUnit.MILLISECONDS.convert(averageValues[valueEnum.ordinal()], TimeUnit.NANOSECONDS),
							"ss.SSS"
					));
					stringBuffer.append("\n");
					// add total values
					stringBuffer.append(valueEnum.getDescription());
					stringBuffer.append(" [total]");
					stringBuffer.append(": ");
					stringBuffer.append(DurationFormatUtils.formatDuration(
							TimeUnit.MILLISECONDS.convert(totalValues[valueEnum.ordinal()], TimeUnit.NANOSECONDS),
							"ss.SSS"
					));
					stringBuffer.append("\n");
				}
				stringBuffer.append("average fps per minute: ");
				stringBuffer.append(averageFPS);
				LOG.log(Level.INFO, "##### statistic values of the last minute:\n" + stringBuffer.toString());
			}
		};
		// schedule runnable
		this.scheduledExecutorService.scheduleAtFixedRate(runnable, LOGGING_INTERVAL, LOGGING_INTERVAL, TimeUnit.SECONDS);
	}
	
	public boolean isEnabled() {
		return this.enabled;
	}

	private final static class StatisticsSingletonHolder {
		private final static Statistics INSTANCE = new Statistics();
	}

	public final static Statistics getInstance() {
		return StatisticsSingletonHolder.INSTANCE;
	}

	public void trackGeneratorsUpdateTime(long init, long start, long end) {
		if (!this.enabled) {
			return;
		}
		this.values.get(ValueEnum.GENERATORS_UPDATE).add(end - init);
		this.values.get(ValueEnum.GENERATORS_UPDATE_EFFECTIVE).add(end - start);
	}

	public void trackEffectsUpdateTime(long init, long start, long end) {
		if (!this.enabled) {
			return;
		}
		this.values.get(ValueEnum.EFFECTS_UPDATE).add(end - init);
		this.values.get(ValueEnum.EFFECTS_UPDATE_EFFECTIVE).add(end - start);
	}

	public void trackOutputsUpdateTime(long init, long start, long end) {
		if (!this.enabled) {
			return;
		}
		this.values.get(ValueEnum.OUTPUTS_UPDATE).add(end - init);
		this.values.get(ValueEnum.OUTPUTS_UPDATE_EFFECTIVE).add(end - start);
	}

	public void trackFPS(int frameCount) {
		if (!this.enabled) {
			return;
		}
		this.frameCount = frameCount;
	}
}
