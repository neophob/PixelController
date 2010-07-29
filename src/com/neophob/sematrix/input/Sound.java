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
		in = minim.getLineIn( Minim.MONO, 512 );
		beat = new BeatDetect(in.bufferSize(),in.sampleRate());
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

	public void shutdown() {
		in.close();
		minim.stop();
	}
}
