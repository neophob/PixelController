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

import java.util.logging.Level;
import java.util.logging.Logger;

import com.neophob.sematrix.glue.Collector;

import ddf.minim.AudioInput;
import ddf.minim.Minim;
import ddf.minim.analysis.BeatDetect;
import ddf.minim.analysis.FFT;

/**
 * The Class SoundMinim.
 */
public final class SoundMinim implements SeSound, Runnable {

	//samples per 1/4s
	/** The Constant SOUND_BUFFER_RESOLUTION. */
	private static final int SOUND_BUFFER_RESOLUTION = 8;

	/** The log. */
	private static final Logger LOG = Logger.getLogger(SoundMinim.class.getName());

	/** The minim. */
	private Minim minim;
	
	/** The in. */
	private AudioInput in;
	
	/** The beat. */
	private BeatDetect beat;
	
	/** The bl. */
	@SuppressWarnings("unused")
	private BeatListener bl;

	/** The fft. */
	private FFT fft;

	/* thread to collect volume information */
	/** The runner. */
	private Thread runner;

	/** The snd volume max. */
	private float sndVolumeMax=0;

	/**
	 * Instantiates a new sound minim.
	 */
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
		beat.detectMode(BeatDetect.FREQ_ENERGY);

		bl = new BeatListener(beat, in);		 

		fft = new FFT(in.bufferSize(), in.sampleRate());
		fft.window(FFT.HAMMING);
		fft.logAverages(120,4); // 32 bands

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

	/* (non-Javadoc)
	 * @see com.neophob.sematrix.input.SeSound#getVolumeNormalized()
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

	/* (non-Javadoc)
	 * @see com.neophob.sematrix.input.SeSound#isKick()
	 */
	public boolean isKick() {
		return beat.isKick();
	}

	/* (non-Javadoc)
	 * @see com.neophob.sematrix.input.SeSound#isSnare()
	 */
	public boolean isSnare() {
		return beat.isSnare();
	}

	/* (non-Javadoc)
	 * @see com.neophob.sematrix.input.SeSound#isHat()
	 */
	public boolean isHat() {
		return beat.isHat();
	}

	/* (non-Javadoc)
	 * @see com.neophob.sematrix.input.SeSound#isPang()
	 */
	public boolean isPang() {
		return beat.isHat() || beat.isKick() || beat.isSnare();
	}

	/**
	 * Returns the number of averages currently being calculated.
	 *
	 * @return the fft avg
	 */
	public int getFftAvg() {
		// perform a forward FFT on the samples 
		fft.forward(in.mix);

		return fft.avgSize();
	}
	
	/**
	 * Gets the value of the ith average.
	 *
	 * @param i the i
	 * @return the fft avg
	 */
	public float getFftAvg(int i) {
		return fft.getAvg(i);
	}

	
	/* (non-Javadoc)
	 * @see com.neophob.sematrix.input.SeSound#shutdown()
	 */
	public void shutdown() {
		in.close();
		minim.stop();
	}

	/**
	 * Dispose.
	 */
	public void dispose() {		
		runner = null;
		//XXX this.shutdown();
	}

	/**
	 * the thread runner.
	 */
	public void run() {
		long sleep = (int)(250/SOUND_BUFFER_RESOLUTION);
		LOG.log(Level.INFO,	"Sound thread started...");
		int loop=0;
		while (Thread.currentThread() == runner) {
			try {
				Thread.sleep(sleep);
			} catch (InterruptedException e) {}

			// perform a forward FFT on the samples 
//			fft.forward(in.mix);
			
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


	/**
	 * Gets the snd volume max.
	 *
	 * @return the snd volume max
	 */
	public synchronized float getSndVolumeMax() {
		return sndVolumeMax;
	}

}
