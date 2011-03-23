package com.neophob.sematrix.generator;

import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.lib.blinken.BlinkenLibrary;

import com.neophob.sematrix.glue.Collector;

/**
 * @author mvogt
 *
 */
public class Blinkenlights extends Generator implements PConstants {

	private static final String PREFIX = "blinken/";
	static Logger log = Logger.getLogger(Blinkenlights.class.getName());

	private BlinkenLibrary blinken;
	private boolean random;
	private Random rand = new Random();
	private int frames;
	private int movieFrames;

	public Blinkenlights(String filename) {
		super(GeneratorName.BLINKENLIGHTS);
		PApplet parent = Collector.getInstance().getPapplet();
		random=false;
		blinken = new BlinkenLibrary(parent, PREFIX+filename);
		blinkenSettings();
	}

	/**
	 * load a new file
	 * @param file
	 */
	public void loadFile(String file) {
		blinken.loadFile(PREFIX+file);
		blinkenSettings();
	}
	
	private void blinkenSettings() {
		blinken.setIgnoreFileDelay(true);
		blinken.noLoop();
		blinken.stop();
		movieFrames = blinken.getNrOfFrames();
		//frames=0;
	}
	
	@Override
	public void update() {
		if (random) {
			blinken.jump(
					rand.nextInt(blinken.getNrOfFrames())
			);
		} else {
			try {
				blinken.jump(frames%movieFrames);
				frames++;				
			} catch (Exception e) {
				//a npe exception might happen if a new file is loaded!
			}
		}

		//resize image to 128x128
		int ofs, dst=0, xofs, yofs=0;		
		float xSrc,ySrc=0;
		float xDiff = internalBufferXSize/(float)blinken.width;
		float yDiff = internalBufferYSize/(float)blinken.height;
		
		try {
			for (int y=0; y<internalBufferYSize; y++) {
				if (ySrc>yDiff) {
					if (yofs<blinken.height) yofs++;				
					ySrc-=yDiff;
				}
				xofs=0;
				xSrc=0;
				for (int x=0; x<internalBufferXSize; x++) {
					if (xSrc>xDiff) {
						if (xofs<blinken.width)xofs++;
						xSrc-=xDiff;
					}				
					ofs=xofs+yofs*blinken.width;
					this.internalBuffer[dst++]=blinken.pixels[ofs];
					xSrc++;
				}
				ySrc++;
			}			
		} catch (ArrayIndexOutOfBoundsException e) {
			log.log(Level.SEVERE, "Failed to update internal buffer", e);
		}
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
