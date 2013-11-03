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
package com.neophob.sematrix.input;

import ddf.minim.AudioInput;
import ddf.minim.AudioListener;
import ddf.minim.analysis.BeatDetect;

/**
 * The listener interface for receiving beat events.
 * The class that is interested in processing a beat
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's <code>addBeatListener<code> method. When
 * the beat event occurs, that object's appropriate
 * method is invoked.
 *
 * @see BeatEvent
 */
public class BeatListener implements AudioListener {
	  
  	/** The beat. */
  	private BeatDetect beat;
	  
  	/** The source. */
  	private AudioInput source;
	  
	  /**
  	 * Instantiates a new beat listener.
  	 *
  	 * @param beat the beat
  	 * @param source the source
  	 */
  	BeatListener(BeatDetect beat, AudioInput source) {
	    this.source = source;
	    this.source.addListener(this);
	    this.beat = beat;
	  }
	  
	  /* (non-Javadoc)
  	 * @see ddf.minim.AudioListener#samples(float[])
  	 */
  	public void samples(float[] samps) {
	    beat.detect(source.mix);
	  }
	  
	  /* (non-Javadoc)
  	 * @see ddf.minim.AudioListener#samples(float[], float[])
  	 */
  	public void samples(float[] sampsL, float[] sampsR) {
	    beat.detect(source.mix);
	  }

}
