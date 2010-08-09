package com.neophob.sematrix.generator;

import java.util.Random;
import java.util.logging.Logger;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PImage;
import processing.lib.blinken.BlinkenLibrary;

import com.neophob.sematrix.glue.Collector;

/**
 * @author mvogt
 *
 */
public class Blinkenlights extends Generator implements PConstants {

	static Logger log = Logger.getLogger(Blinkenlights.class.getName());

	private BlinkenLibrary blinken;
	private PImage tmp;
	private boolean random;
	private Random rand = new Random();

	public Blinkenlights(String filename) {
		super(GeneratorName.BLINKENLIGHTS);
		PApplet parent = Collector.getInstance().getPapplet();
		tmp=parent.createImage( internalBufferXSize, internalBufferYSize, RGB);
		random=false;
		blinken = new BlinkenLibrary(parent, filename);
		blinken.setIgnoreFileDelay(true);
		blinken.loop();
	}

	/**
	 * load a new file
	 * @param file
	 */
	public void loadFile(String file) {
		blinken.loadFile(file);
		blinken.setIgnoreFileDelay(true);
	}
	
	@Override
	public void update() {
		if (random) {
			blinken.jump(
					rand.nextInt(blinken.getNrOfFrames())
			);
		}
		tmp.loadPixels();
		tmp.copy(blinken, 0, 0, blinken.width, blinken.height, 0, 0, internalBufferXSize, internalBufferYSize);
		System.arraycopy(tmp.pixels, 0, this.internalBuffer, 0, tmp.pixels.length);
		tmp.updatePixels();		
	}
	
	public boolean isRandom() {
		return random;
	}

	public void setRandom(boolean random) {
		this.random = random;
		if (random) {
			blinken.noLoop();
			blinken.stop();
		} else {
			blinken.loop();
		}
	}

	@Override
	public void close() {
		blinken.dispose();
	}
}
