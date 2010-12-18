package com.neophob.sematrix.mixer;

import com.neophob.sematrix.glue.Collector;
import com.neophob.sematrix.glue.Visual;

public abstract class Mixer {

	public enum MixerName {
		PASSTHRU(0),
		ADDSAT(1),		
		MULTIPLY(2),
		MIX(3),
		NEGATIVE_MULTIPLY(4),
		CHECKBOX(5),
		VOLUMINIZER(6),
		XOR(7);
		
		private int id;
		
		MixerName(int id) {
			this.id = id;
		}
		
		public int getId() {
			return id;
		}
	}
	
	private MixerName mixerName;
	
	public Mixer(MixerName mixerName) {
		this.mixerName = mixerName;
		Collector.getInstance().addMixer(this);
	}
	
	public abstract int[] getBuffer(Visual visual);
	
	public int getId() {
		return this.mixerName.getId();
	}
}
