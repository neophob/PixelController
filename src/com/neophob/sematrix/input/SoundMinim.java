package com.neophob.sematrix.input;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.neophob.sematrix.glue.Collector;

import ddf.minim.AudioInput;
import ddf.minim.Minim;
import ddf.minim.analysis.BeatDetect;

public class SoundMinim implements SeSound, Runnable {

	//samples per 1/4s
	private static final int SOUND_BUFFER_RESOLUTION = 8;
	
	private static Logger log = Logger.getLogger(SoundMinim.class.getName());
	
	private Minim minim;
	private AudioInput in;
	private BeatDetect beat;
	@SuppressWarnings("unused")
	private BeatListener bl;
	
	/* thread to collect volume information */
	private Thread runner;

	private float sndVolumeMax=0;

	public SoundMinim() {
		minim = new Minim(Collector.getInstance().getPapplet());
		in = minim.getLineIn( Minim.STEREO, 512 );
		//in = minim.getLineIn( Minim.MONO, 1024 );
		
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
		
		Collector.getInstance().getPapplet().registerDispose(this);
		this.runner = new Thread(this);
		this.runner.setName("ZZ Sound stuff");
		this.runner.start();
	}
	
	
	/**
	 * Gets the current level of the buffer. It is calculated as 
	 * the root-mean-squared of all the samples in the buffer.
	 * @return the RMS amplitude of the buffer
	 */
	public float getVolume() {
		return getVolumeNormalized();
	}

	/**
	 * 
	 */
	public float getVolumeNormalized() {
		float f = in.mix.level();
		float max = getSndVolumeMax();
		float norm=(1.0f/max)*f;	
		//System.out.println("max: "+(int)(max*10000)+", val: "+(int)(f*10000)+"->"+norm);

		//im a bad coder! limit it!
		if (norm>1f) {
			norm=1f;		
		}
		
		//if the sound volume is very low, limit the normalized volume
		if (max<0.004f) {
			norm/=2;
		}
		return norm;
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
	
	public void dispose() {		
		runner = null;
		//XXX this.shutdown();
	}

	/**
	 * the thread runner
	 */
	public void run() {
		long sleep = (int)(250/SOUND_BUFFER_RESOLUTION);
		log.log(Level.INFO,	"Sound thread started...");
		int loop=0;
		while (Thread.currentThread() == runner) {
			try {
				Thread.sleep(sleep);
			} catch (InterruptedException e) {}
			
			//decrement max volume after 1/4s
			if (loop>SOUND_BUFFER_RESOLUTION) {
				sndVolumeMax*=.93f;
			}

			float f = in.mix.level();
			if (f>sndVolumeMax) {
				sndVolumeMax=f;
				loop=0;
			}
						
			loop++;
		}
	}


	public synchronized float getSndVolumeMax() {
		return sndVolumeMax;
	}
	
}
