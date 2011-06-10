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

package com.neophob.sematrix.glue;

public enum ShufflerOffset {
	GENERATOR_A(0),
	GENERATOR_B(1),
	EFFECT_A(2),
	EFFECT_B(3),
	MIXER(4),
	MIXER_OUTPUT(5),
	FADER_OUTPUT(6),
	OUTPUT(7),
	BLINKEN(8),
	IMAGE(9),
	TINT(10),
	TEXTURE_DEFORMATION(11),
	THRESHOLD_VALUE(12),
	ROTOZOOMER(13);
	
	
	
	int ofs;
	ShufflerOffset(int ofs) {
		this.ofs = ofs;
	}
	
	int getOffset() {
		return ofs;
	}
}
