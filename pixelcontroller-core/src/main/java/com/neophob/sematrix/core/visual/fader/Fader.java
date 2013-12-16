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
package com.neophob.sematrix.core.visual.fader;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.neophob.sematrix.core.visual.MatrixData;


/**
 * the fader class differs from the other classes (effect, generators...).
 * the fader class gets used once and then will never be reused! 
 * 
 * reason: the timer - after the time is over we switch the mapping in the collector class
 * 
 * Use 24bpp buffer
 * 
 * @author mvogt
 *
 */
public abstract class Fader implements IFader, Serializable {

	/** The log. */
	private static transient final Logger LOG = Logger.getLogger(Fader.class.getName());

	protected static transient final int DEFAULT_FADER_DURATION = 1500;
	
	/**
	 * The Enum FaderName.
	 */
	public enum FaderName {
		
		/** The SWITCH. */
		SWITCH(0),
		
		/** The CROSSFADE. */
		CROSSFADE(1),
		
		/** The SLIDE upside down. */
		SLIDE_UPSIDE_DOWN(2),
		
		/** The SLIDE left right. */
		SLIDE_LEFT_RIGHT(3);
		
		/** The id. */
		private int id;
		
		/**
		 * Instantiates a new fader name.
		 *
		 * @param id the id
		 */
		FaderName(int id) {
			this.id = id;
		}
		
		/**
		 * Gets the id.
		 *
		 * @return the id
		 */
		public int getId() {
			return id;
		}
	}
	
	/** The fader name. */
	private FaderName faderName;
	
	/** fade time in ms. */
	protected int fadeTime;
	
	/** The new visual. */
	protected int newVisual;
	
	/** The screen output. */
	protected int screenOutput;
	
	/** The steps. */
	protected int steps;
	
	/** The current step. */
	protected int currentStep;
	
	/** The internal buffer x size. */
	protected int internalBufferXSize;
	
	/** The internal buffer y size. */
	protected int internalBufferYSize;

	protected transient int[] oldBuffer;
	
	//the preset fader do not animate but fade from a static buffer to the new visual
	protected boolean presetFader;
	
	/** The started. */
	private boolean started;
	
	/**
	 * Instantiates a new fader.
	 *
	 * @param faderName the fader name
	 * @param fadeTime the fade time
	 */
	public Fader(MatrixData matrix, FaderName faderName, int fadeTime, int fps) {
		this.faderName = faderName;
		this.fadeTime = fadeTime;
		
		//example: duration=2000, FPS=10 -> 20000frames 1000/10=100ms / frame
		//example: duration=200,  FPS=50 -> 10000frames 1000/50=20ms / frame
		if (fps<1) {
			LOG.log(Level.WARNING, "Invalid FPS detected {0}, use default of 50", fps);
			fps = 50;
		}
		int timePerFrame = (int)(1000.0f / (float)fps);
		
		//just if a crazy guy defines more than 1000 fps...
		if (timePerFrame<1) {
			timePerFrame = 1;
		}
		if (fadeTime < timePerFrame) {			
			LOG.log(Level.WARNING, "Invalid fadeTime {0} fixed to {1}", new Object[] { fadeTime, timePerFrame });
			this.fadeTime = timePerFrame;
		}
		
		this.internalBufferXSize = matrix.getBufferXSize();
		this.internalBufferYSize = matrix.getBufferYSize();

		steps = (int)(fadeTime/timePerFrame);
		started=false;
	}

	/**
	 * Gets the buffer.
	 *
	 * @param buffer the buffer
	 * @return the buffer
	 */
	public abstract int[] getBuffer(int[] visual1Buffer, int[] visual2Buffer);
	
	/**
	 * Start to fade, visual to visual fader
	 *
	 * @param newVisual - fade to which visual nr
	 * @param screenNr - if the fading is finished, switch to this output
	 */
	public void startFade(int newVisual, int screenNr) {
		this.newVisual = newVisual;
		this.screenOutput = screenNr;

		currentStep = 0;
		started = true;
		presetFader = false;
		
		LOG.log(Level.INFO, "Started fader {0}, duration {1}, steps {2}, new visual {3}, output screen {4}", 
				new Object[] { faderName.toString(), fadeTime, steps, newVisual, screenNr });
	}

	/**
	 * Start to fade, static image to visual
	 * 
	 * @param newVisual - fade to which visual nr
	 * @param bfr static buffer input, fade source
	 */
	public void startFade(int newVisual, int[] bfr) {
		this.newVisual = newVisual;
		this.screenOutput = -1;
		oldBuffer = bfr;

		currentStep = 0;
		started = true;
		presetFader = true;
		
		LOG.log(Level.INFO, "Started preset fader {0}, duration {1}, steps {2}, new visual {3}", 
				new Object[] { faderName.toString(), fadeTime, steps, newVisual });	
	}
	
	/**
	 * switch the output and stop the fading.
	 */
	public void cleanUp() {
		if (!started) {
			return;
		}
		
		started=false;		
	}
	
	/**
	 * is fading still running.
	 *
	 * @return true, if is done
	 */
	public boolean isDone() {
		if (currentStep>=steps) {
			return true;
		}
		return false;
	}
	
	/**
	 * Sets the done.
	 */
	public void setDone() {
		currentStep=steps;
	}


	/**
	 * Gets the current step.
	 *
	 * @return the current step
	 */
	protected float getCurrentStep() {
		return currentStep/(float)steps;
	}
	
	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public int getId() {
		return this.faderName.getId();
	}

	/**
	 * Checks if is started.
	 *
	 * @return true, if is started
	 */
	public boolean isStarted() {
		return started;
	}
	

	public int getNewVisual() {
		return newVisual;
	}

	
	public int getScreenOutput() {
		return screenOutput;
	}
	
	public String getFaderName() {
		return faderName.name();
	}

	@Override
	public String toString() {
		return String
				.format("Fader [faderName=%s, fadeTime=%s, newVisual=%s, screenOutput=%s, steps=%s, currentStep=%s, internalBufferXSize=%s, internalBufferYSize=%s, presetFader=%s, started=%s]",
						faderName, fadeTime, newVisual, screenOutput, steps,
						currentStep, internalBufferXSize, internalBufferYSize,
						presetFader, started);
	}
	

}
