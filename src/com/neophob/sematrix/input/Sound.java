package com.neophob.sematrix.input;

import com.neophob.sematrix.glue.Collector;

import ddf.minim.AudioInput;
import ddf.minim.Minim;
import ddf.minim.analysis.BeatDetect;

public class Sound {

	private static Sound instance = new Sound();

	private Minim minim;
	private AudioInput in;
	private BeatDetect beat;
	private BeatListener bl;

	public Sound() {
		minim = new Minim(Collector.getInstance().getPapplet());
		in = minim.getLineIn( Minim.STEREO, 1024 );
		
		// a beat detection object that is FREQ_ENERGY mode that 
		// expects buffers the length of song's buffer size
		// and samples captured at songs's sample rate
		beat = new BeatDetect(in.bufferSize(), in.sampleRate());
		
		// set the sensitivity to 300 milliseconds
		// After a beat has been detected, the algorithm will wait for 300 milliseconds 
		// before allowing another beat to be reported. You can use this to dampen the 
		// algorithm if it is giving too many false-positives. The default value is 10, 
		// which is essentially no damping. If you try to set the sensitivity to a negative value, 
		// an error will be reported and it will be set to 10 instead. 
		beat.setSensitivity(300); 
		bl = new BeatListener(beat, in); 
	}

	public static Sound getInstance() {
		return instance;
	}

	public float getVolume() {
		float m = 0;
		for(int i = 0; i < in.bufferSize() - 1; i++) {
			if ( Math.abs(in.mix.get(i)) > m ) {
				m = Math.abs(in.mix.get(i));
			}
		}
		return m;
	}
	
	public boolean isKick() {
		return beat.isKick();
	}

	public boolean isSnare() {
		return beat.isSnare();
	}

	public boolean isHat() {
		return beat.isHat();
	}
	
	public boolean isPang() {
		return beat.isHat() || beat.isKick() || beat.isSnare();
	}
	
	public void shutdown() {
		in.close();
		minim.stop();
	}
}
