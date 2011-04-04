package com.neophob.sematrix.effect;

import com.neophob.sematrix.glue.Collector;
import com.neophob.sematrix.glue.MatrixData;
import com.neophob.sematrix.resize.Resize.ResizeName;

public abstract class Effect {
	
	/**
	 * 
	 * @author michu
	 *
	 */
	public enum EffectName {
		PASSTHRU(0),
		INVERTER(1),
		ROTOZOOM(2),
		BEAT_HORIZONTAL_SHIFT(3),
		BEAT_VERTICAL_SHIFT(4),
		VOLUMINIZE(5),
		TINT(6),
		THRESHOLD(7),
		EMBOSS(8);
		
		private int id;
		
		EffectName(int id) {
			this.id = id;
		}
		
		public int getId() {
			return id;
		}
	}
	
	private EffectName effectName;
	private ResizeName resizeOption;
	
	protected int internalBufferXSize;
	protected int internalBufferYSize;

	
	public Effect(EffectName effectName, ResizeName resizeOption) {
		this.effectName = effectName;
		this.resizeOption = resizeOption;
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
	
	public ResizeName getResizeOption() {
		return resizeOption;
	}
	
	/**
	 * update an effect 
	 */
	public void update() {
		//overwrite me if needed
	}
	
	public int getId() {
		return this.effectName.getId();
	}
}
