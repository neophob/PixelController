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
package com.neophob.sematrix.effect;

import com.neophob.sematrix.glue.Collector;
import com.neophob.sematrix.glue.MatrixData;
import com.neophob.sematrix.glue.RandomizeState;
import com.neophob.sematrix.resize.Resize.ResizeName;

/**
 * The Class Effect.
 */
public abstract class Effect implements RandomizeState {
	
	/**
	 * The Enum EffectName.
	 *
	 * @author michu
	 */
	public enum EffectName {
		
		/** The PASSTHRU. */
		PASSTHRU(0),
		
		/** The INVERTER. */
		INVERTER(1),
		
		/** The ROTOZOOM. */
		ROTOZOOM(2),
		
		/** The BEAT horizontal shift. */
		BEAT_HORIZONTAL_SHIFT(3),
		
		/** The BEAT vertical shift. */
		BEAT_VERTICAL_SHIFT(4),
		
		/** The VOLUMINIZE. */
		VOLUMINIZE(5),
		
		/** The THRESHOLD. */
		THRESHOLD(6),
				
		TEXTURE_DEFORMATION(7),
		
		/** The ZOOM. */
		ZOOM(8),
		
		FLIP_X(9),
		
		FLIP_Y(10),		
		
		STROBO(11),		
		;

		/** The id. */
		private int id;
		
		/**
		 * Instantiates a new effect name.
		 *
		 * @param id the id
		 */
		EffectName(int id) {
			this.id = id;
		}
		
		/**
		 * Gets the id.
		 *
		 * @return the id
		 */
		public int getId() {
			return id;
		}
	}
	
	/** The effect name. */
	private EffectName effectName;
	
	/** The resize option. */
	private ResizeName resizeOption;
	
	/** The internal buffer x size. */
	protected int internalBufferXSize;
	
	/** The internal buffer y size. */
	protected int internalBufferYSize;

	
	/**
	 * Instantiates a new effect.
	 *
	 * @param controller the controller
	 * @param effectName the effect name
	 * @param resizeOption the resize option
	 */
	public Effect(PixelControllerEffect controller, EffectName effectName, ResizeName resizeOption) {
		this.effectName = effectName;
		this.resizeOption = resizeOption;
		MatrixData matrix = Collector.getInstance().getMatrix();
		this.internalBufferXSize = matrix.getBufferXSize();
		this.internalBufferYSize = matrix.getBufferYSize();
		controller.addEffect(this);
	}
	
	/**
	 * return the image buffer.
	 *
	 * @param buffer the buffer
	 * @return the buffer
	 */
	public abstract int[] getBuffer(int[] buffer);
	
	/**
	 * Gets the resize option.
	 *
	 * @return the resize option
	 */
	public ResizeName getResizeOption() {
		return resizeOption;
	}
	
	/**
	 * update an effect.
	 */
	public void update() {
		//overwrite me if needed
	}
	
	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public int getId() {
		return this.effectName.getId();
	}
		
	/* (non-Javadoc)
	 * @see com.neophob.sematrix.glue.RandomizeState#shuffle()
	 */
	public void shuffle() {
		//default shuffle method - do nothing	
	}
	
}
