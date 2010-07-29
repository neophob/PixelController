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
