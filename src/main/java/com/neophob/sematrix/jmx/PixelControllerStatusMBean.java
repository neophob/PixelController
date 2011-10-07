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
package com.neophob.sematrix.jmx;

/**
 * JMX Interface to provide basic statistics
 * @author michu
 *
 */
public interface PixelControllerStatusMBean {

	/**
	 * 
	 * @return version of the application
	 */
	float getVersion();
	
	/**
	 * 
	 * @return current frames per second
	 */
	float getCurrentFps();

	/**
	 * how long does it take to update all generators?
	 * @return time in ms
	 */
	float getGeneratorUpdateTime();
	
	/**
	 * how long does it take to update all effects?
	 * @return time in ms
	 */
	float getEffectUpdateTime();

	/**
	 * how long does it take to update all output devices?
	 * @return time in ms
	 */
	float getOutputUpdateTime();

}
