/**
 * Copyright (C) 2011-2014 Michael Vogt <michu@neophob.com>
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
package com.neophob.sematrix.core;

import java.util.List;

/**
 * 
 * interface for all collectors. used to initialize all elements during startup,
 * get the current state from all elements (used to store presents) and update 
 * the elements. 
 * 
 * @author michu
 *
 */
public interface PixelControllerElement {

	/**
	 * initialize all elements.
	 */
	void initAll();
	
	/**
	 * get current status of all childs.
	 *
	 * @return the current state
	 */
	List<String> getCurrentState();

	/**
	 * update the element if needed.
	 */
	void update();
}
