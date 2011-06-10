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

package com.neophob.sematrix.mixer;

import com.neophob.sematrix.glue.Collector;
import com.neophob.sematrix.glue.Visual;
import com.neophob.sematrix.resize.Resize.ResizeName;

/**
 * mix two buffers together
 * 
 * @author michu
 *
 */
public abstract class Mixer {

	public enum MixerName {
		PASSTHRU(0),
		ADDSAT(1),		
		MULTIPLY(2),
		MIX(3),
		NEGATIVE_MULTIPLY(4),
		CHECKBOX(5),
		VOLUMINIZER(6),
		XOR(7),
		MINUS_HALF(8),
		EITHER(9);
		
		private int id;
		
		MixerName(int id) {
			this.id = id;
		}
		
		public int getId() {
			return id;
		}
	}
	
	private MixerName mixerName;
	private ResizeName resizeOption;
	
	public Mixer(MixerName mixerName, ResizeName resizeOption) {
		this.mixerName = mixerName;
		this.resizeOption = resizeOption;
		Collector.getInstance().addMixer(this);
	}
	
	public abstract int[] getBuffer(Visual visual);
	
	public ResizeName getResizeOption() {
		return resizeOption;
	}
	
	public int getId() {
		return this.mixerName.getId();
	}
}
