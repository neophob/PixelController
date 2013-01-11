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
package com.neophob.sematrix.glue;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;

/**
 * The Enum ShufflerOffset.
 *
 * @author michu
 */
public enum ShufflerOffset {
	
	/** The GENERATOR a. */
	GENERATOR_A(0),
	
	/** The GENERATOR b. */
	GENERATOR_B(1),
	
	/** The EFFECT a. */
	EFFECT_A(2),
	
	/** The EFFECT b. */
	EFFECT_B(3),
	
	/** The MIXER. */
	MIXER(4),
	
	/** The MIXER output. */
	MIXER_OUTPUT(5),
	
	/** The FADER output. */
	FADER_OUTPUT(6),
	
	/** The OUTPUT. */
	OUTPUT(7),
	
	/** The BLINKEN. */
	BLINKEN(8),
	
	/** The IMAGE. */
	IMAGE(9),
	
	/** The TEXTURE deformation. */
	TEXTURE_DEFORM(10),
	
	/** The THRESHOLD value. */
	THRESHOLD_VALUE(11),
	
	/** The ROTOZOOMER. */
	ROTOZOOMER(12),
	
	/** COLOR_SCROLL */
	COLOR_SCROLL(13),
	
	COLORSET(14)
	;
	
	
	/** The ofs. */
	private int ofs;
	
	/**
	 * Instantiates a new shuffler offset.
	 *
	 * @param ofs the ofs
	 */
	ShufflerOffset(int ofs) {
		this.ofs = ofs;
	}
	
	/**
	 * Gets the offset.
	 *
	 * @return the offset
	 */
	int getOffset() {
		return ofs;
	}
	
	/**
	 * 
	 * @return
	 */
	public String guiText() {
		return WordUtils.capitalizeFully(StringUtils.replace(this.name(), "_", " "));		
	}
}
