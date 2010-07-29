package com.neophob.sematrix.effect;

import com.neophob.sematrix.generator.Generator;
import com.neophob.sematrix.glue.Collector;

public abstract class Effect {
	
	public enum EffectName {
		PASSTHRU(0),
		INVERTER(1),
		ROTOZOOM(2),
		RND_HORIZONTAL_SHIFT(3),
		RND_VERTICAL_SHIFT(4);
		
		private int id;
		
		EffectName(int id) {
			this.id = id;
		}
		
		public int getId() {
			return id;
		}
	}
	
	private EffectName effectName;
	
	public Effect(EffectName effectName) {
		this.effectName = effectName;
		Collector.getInstance().addEffect(this);
	}
	
	/**
	 * return the RESIZED buffer
	 * 
	 * @return the buffer
	 */
	public abstract int[] getBuffer(Generator generator);
	
	public int getId() {
		return this.effectName.getId();
	}
}
