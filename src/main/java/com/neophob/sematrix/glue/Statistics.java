package com.neophob.sematrix.glue;


public class Statistics {
	private boolean enabled;
	
	private Statistics() {
		this.enabled = false;
	}
	
	public synchronized void enable() {
		if (this.enabled) {
			throw new IllegalStateException("Statistics class can only be enabled once!");
		}
		this.enabled = true;
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

	public void sendGeneratorsUpdateTime(long time) {
		if (!this.enabled) {
			return;
			// TODO implement statistics collection
		}
	}

	public void sendEffectsUpdateTime(long time) {
		if (!this.enabled) {
			return;
		}
		// TODO implement statistics collection
	}

	public void sendOutputsUpdateTime(long time) {
		if (!this.enabled) {
			return;
		}
		// TODO implement statistics collection
	}
}
