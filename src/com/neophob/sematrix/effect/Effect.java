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
