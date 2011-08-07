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

package com.neophob.sematrix.input;

import ddf.minim.AudioInput;
import ddf.minim.AudioListener;
import ddf.minim.analysis.BeatDetect;

public class BeatListener implements AudioListener {
	  private BeatDetect beat;
	  private AudioInput source;
	  
	  BeatListener(BeatDetect beat, AudioInput source) {
	    this.source = source;
	    this.source.addListener(this);
	    this.beat = beat;
	  }
	  
	  public void samples(float[] samps) {
	    beat.detect(source.mix);
	  }
	  
	  public void samples(float[] sampsL, float[] sampsR) {
	    beat.detect(source.mix);
	  }

}
