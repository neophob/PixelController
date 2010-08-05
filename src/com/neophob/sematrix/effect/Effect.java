package com.neophob.sematrix.effect;

import com.neophob.sematrix.glue.Collector;
import com.neophob.sematrix.glue.MatrixData;

public abstract class Effect {
	

	public enum EffectName {
		PASSTHRU(0),
		INVERTER(1),
		ROTOZOOM(2),
		BEAT_HORIZONTAL_SHIFT(3),
		BEAT_VERTICAL_SHIFT(4),
		VOLUMINIZE(5),
		TINT(6);
		
		private int id;
		
		EffectName(int id) {
			this.id = id;
		}
		
		public int getId() {
			return id;
		}
	}
	
	private EffectName effectName;
	protected int internalBufferXSize;
	protected int internalBufferYSize;

	
	public Effect(EffectName effectName) {
		this.effectName = effectName;
		MatrixData matrix = Collector.getInstance().getMatrix();
		this.internalBufferXSize = matrix.getBufferXSize();
		this.internalBufferYSize = matrix.getBufferYSize();
		Collector.getInstance().addEffect(this);
	}
	
	/**
	 * return the image buffer
	 * 
	 * @return the buffer
	 */
	public abstract int[] getBuffer(int[] buffer);
	
	public int getId() {
		return this.effectName.getId();
	}
}
