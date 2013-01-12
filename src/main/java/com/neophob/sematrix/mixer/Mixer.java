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
package com.neophob.sematrix.mixer;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;

import com.neophob.sematrix.glue.Visual;
import com.neophob.sematrix.resize.Resize.ResizeName;

/**
 * mix two buffers together.
 *
 * @author michu
 */
public abstract class Mixer {

	/**
	 * The Enum MixerName.
	 */
	public enum MixerName {
		
		/** The PASSTHRU. */
		PASSTHRU(0),
		
		/** The ADDSAT. */
		ADDSAT(1),		
		
		/** The MULTIPLY. */
		MULTIPLY(2),
		
		/** The MIX. */
		MIX(3),
		
		/** The NEGATIV e_ multiply. */
		NEGATIVE_MULTIPLY(4),
		
		/** The CHECKBOX. */
		CHECKBOX(5),
		
		/** The VOLUMINIZER. */
		VOLUMINIZER(6),
		
		/** The EITHER. */
		EITHER(7),
		
		SUBSAT(8),

		HALFHALF(9),
		
		HALFHALFVERTICAL(10),
		;
		
		/** The id. */
		private int id;
		
		/**
		 * Instantiates a new mixer name.
		 *
		 * @param id the id
		 */
		MixerName(int id) {
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
		
		/**
		 * 
		 * @return
		 */
		public String guiText() {
			return WordUtils.capitalizeFully(StringUtils.replace(this.name(), "_", " "));		
		}
	}
	
	/** The mixer name. */
	private MixerName mixerName;
	
	/** The resize option. */
	private ResizeName resizeOption;
	
	/**
	 * Instantiates a new mixer.
	 *
	 * @param controller the controller
	 * @param mixerName the mixer name
	 * @param resizeOption the resize option
	 */
	public Mixer(PixelControllerMixer controller, MixerName mixerName, ResizeName resizeOption) {
		this.mixerName = mixerName;
		this.resizeOption = resizeOption;
		controller.addMixer(this);
	}
	
	/**
	 * Gets the buffer.
	 *
	 * @param visual the visual
	 * @return the buffer
	 */
	public abstract int[] getBuffer(Visual visual);
	
	/**
	 * Gets the resize option.
	 *
	 * @return the resize option
	 */
	public ResizeName getResizeOption() {
		return resizeOption;
	}
	
	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public int getId() {
		return this.mixerName.getId();
	}
	
}
