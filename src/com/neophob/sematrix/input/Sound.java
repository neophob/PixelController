package com.neophob.sematrix.input;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.collections.buffer.CircularFifoBuffer;

import com.neophob.sematrix.glue.Collector;

import ddf.minim.AudioInput;
import ddf.minim.Minim;
import ddf.minim.analysis.BeatDetect;

public class Sound implements Runnable {

	private static Logger log = Logger.getLogger(Sound.class.getName());

	private static final int SOUND_BUFFER_RESOLUTION = 64;
	private static Sound instance = new Sound();

	private Minim minim;
	private AudioInput in;
	private BeatDetect beat;
	private BeatListener bl;
	
	/* thread to collect volume information */
	private Thread runner;
	private CircularFifoBuffer volumeBuffer;
	private float sndVolumeMax=1;
	private float sndVolumeMin=0;

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
		
		Collector.getInstance().getPapplet().registerDispose(this);
		this.runner = new Thread(this);
		this.runner.start();

		volumeBuffer = new CircularFifoBuffer(SOUND_BUFFER_RESOLUTION);
	}

	public static Sound getInstance() {
		return instance;
	}

	/**
	 * get current volume
	 * @return
	 */
	public float getVolume() {
		float f,m = 0;
		for(int i = 0; i < in.bufferSize() - 1; i++) {
			f = Math.abs(in.mix.get(i));
			if ( f > m ) {
				m = f;
			}
		}
		return m;
	}

	public float getVolumeNormalized() {
		float f = getVolume();
		f=(1/sndVolumeMax-sndVolumeMin)*f;
		if (f>1f) f=1f;
		return f;
	}
	
	/**
	 * 
	 * @param count: slit up result in n pieces
	 * @return
	 */
	public float[] getVolume(int count) {
		float m = 0;
		float result[] = new float[count];
		int sequenze = in.bufferSize()/count;
		
		int ofs=0;
		for (int s=0; s<count; s++) {
			for (int i=0; i<sequenze; i++) {
				if ( Math.abs(in.mix.get(i+ofs)) > m ) {
					m = Math.abs(in.mix.get(i));
				}		
			}
			result[s] = m;
			ofs+=sequenze;
		}
		return result;
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
	}

	/**
	 * the thread runner
	 */
	public void run() {
		int sleep = (1000 / SOUND_BUFFER_RESOLUTION/3);
		int cnt=0;
		log.log(Level.INFO,	"Sound thread started...");
		while (Thread.currentThread() == runner) {
			try {
				Thread.sleep(sleep);
			} catch (InterruptedException e) {}

			float f = this.getVolume();
			volumeBuffer.add(f);
			
			if (cnt%(SOUND_BUFFER_RESOLUTION)==0) {
				float max=0;
				float min=1;
				for (Object o: volumeBuffer) {
					max = Math.max(max, (Float)o);
					min = Math.min(min, (Float)o);
				}
				sndVolumeMax=max;
				sndVolumeMin=min;
				//System.out.println("MAX: "+sndVolumeMax+", MIN: "+sndVolumeMin+", DIFF: "+(sndVolumeMax-sndVolumeMin));
			}
			
			cnt++;
		}
	}
}
