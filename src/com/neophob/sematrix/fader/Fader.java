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

package com.neophob.sematrix.fader;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.neophob.sematrix.glue.Collector;
import com.neophob.sematrix.glue.MatrixData;
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
	
	protected int internalBufferXSize;
	protected int internalBufferYSize;

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
		
		MatrixData matrix = Collector.getInstance().getMatrix();
		this.internalBufferXSize = matrix.getBufferXSize();
		this.internalBufferYSize = matrix.getBufferYSize();

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
