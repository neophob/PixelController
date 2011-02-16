package com.neophob.sematrix.fader;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.neophob.sematrix.glue.Collector;
import com.neophob.sematrix.glue.OutputMapping;


/**
 * the fader class differs from the other classes (effect, generators...).
 * the fader class gets used once and then will never be reused! 
 * 
 * reason: the timer - after the time is over we switch the mapping in the collector class
 * 
 * @author mvogt
 *
 */
public abstract class Fader {

	private static Logger log = Logger.getLogger(Fader.class.getName());

	public enum FaderName {
		SWITCH(0),
		CROSSFADE(1),
		SLIDE_UPSIDE_DOWN(2),
		SLIDE_LEFT_RIGHT(3);
		
		private int id;
		
		FaderName(int id) {
			this.id = id;
		}
		
		public int getId() {
			return id;
		}
	}
	
	private FaderName faderName;
	private OutputMapping outputMapping;
	
	/** fade time in ms */
	protected int fadeTime;
	
	protected int newVisual;
	protected int screenOutput;
	
	protected int steps;
	protected int currentStep;
	private boolean started;

	/**
	 * 
	 * @param faderName
	 * @param fadeTime
	 */
	public Fader(FaderName faderName, int fadeTime) {
		this.faderName = faderName;
		this.fadeTime = fadeTime;
		
		//example: duration=2000, FPS=10 -> 20000frames 1000/10=100ms / frame
		//example: duration=200,  FPS=50 -> 10000frames 1000/50=20ms / frame
		int fps = Collector.getInstance().getFps();
		int timePerFrame = (int)(1000.0f / (float)fps);
		if (fadeTime < timePerFrame) {			
			log.log(Level.WARNING, "Invalid fadeTime {0} fixed to {1}", new Object[] { fadeTime, timePerFrame });
			this.fadeTime = timePerFrame;
		}
		
		steps = (int)(fadeTime/timePerFrame);
		started=false;
	}

	public abstract int[] getBuffer(int[] buffer);
	
	public void startFade(int newVisual, int screenNr) {
		this.newVisual = newVisual;
		this.screenOutput = screenNr;
		this.outputMapping = Collector.getInstance().getOutputMappings(screenNr);

		currentStep = 0;
		started = true;
		
		log.log(Level.INFO, "Started fader {0}, duration {1}, steps {2}", 
				new Object[] { faderName.toString(), fadeTime, steps });
	}
	
	/**
	 * switch the output and stop the fading
	 */
	public void cleanUp() {
		if (!started) {
			return;
		}
		
		started=false;
		Collector.getInstance().mapInputToScreen(screenOutput, newVisual);
		log.log(Level.INFO, "Cleanup {0}, new visual: {1}", 
				new Object[] { faderName.toString(), newVisual });
	}
	
	/**
	 * is fading still running
	 * @return
	 */
	public boolean isDone() {
		if (currentStep>=steps) {
			return true;
		}
		return false;
	}
	
	public void setDone() {
		currentStep=steps;
	}

	protected int[] getNewBuffer() {
		int[] buffer = Collector.getInstance().getVisual(newVisual).getBuffer();
		return outputMapping.getEffect().getBuffer(buffer);
	}

	protected float getCurrentStep() {
		return currentStep/(float)steps;
	}
	
	public int getId() {
		return this.faderName.getId();
	}

	public boolean isStarted() {
		return started;
	}
	

}
