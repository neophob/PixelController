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

package com.neophob.sematrix.generator;

import java.util.logging.Logger;

import com.neophob.sematrix.resize.Resize.ResizeName;

/**
 * The Class PassThruGen.
 *
 * @author mvogt
 */
public class PassThruGen extends Generator {

	/** The log. */
	static Logger log = Logger.getLogger(PassThruGen.class.getName());
	
	/**
	 * Instantiates a new pass thru gen.
	 *
	 * @param controller the controller
	 */
	public PassThruGen(PixelControllerGenerator controller) {
		super(controller, GeneratorName.PASSTHRU, ResizeName.QUALITY_RESIZE);
	}
	
	/* (non-Javadoc)
	 * @see com.neophob.sematrix.generator.Generator#update()
	 */
	@Override
	public void update() {	
	}

}
