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
package com.neophob.sematrix.fader;

/**
 * a fader is a transition between two buffers. the target buffer is always a visual, the source
 * can be a static imag (preset fader) or another visual.
 * a fader has a defined time to run
 * @author michu
 *
 */
public interface IFader {

	/**
	 * return the faded buffer
	 * 
	 * @param buffer
	 * @return
	 */
	int[] getBuffer(int[] visual1Buffer, int[] visual2Buffer);
	
	/**
	 * start visual to visual buffer
	 * 
	 * @param newVisual
	 * @param screenNr
	 */
	void startFade(int newVisual, int screenNr);
	
	/**
	 * start static image (preset) to visual buffer
	 * 
	 * @param newVisual
	 * @param bfr
	 */
	void startFade(int newVisual, int[] bfr);
	
	/**
	 * switch the output and stop the fading.
	 */
	void cleanUp();
	
	/**
	 * is fading still running.
	 *
	 * @return true, if is done
	 */
	boolean isDone();
	
	/**
	 * Gets the fader id.
	 *
	 * @return the id
	 */
	int getId();

	/**
	 * get fadername
	 * @return
	 */
	String getFaderName();
	
	/**
	 * Checks if fader is started.
	 *
	 * @return true, if is started
	 */
	 boolean isStarted();

	 /**
	  * get the id of the new visual
	  * @return
	  */
	 int getNewVisual();
	 
	 /**
	  * switch to this output if fader is finished
	  * @return
	  */
	 int getScreenOutput();
}
