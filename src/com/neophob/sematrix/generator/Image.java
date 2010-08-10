package com.neophob.sematrix.generator;

import java.util.logging.Level;
import java.util.logging.Logger;

import processing.core.PApplet;
import processing.core.PImage;

import com.neophob.sematrix.glue.Collector;

/**
 * TODO: multiple palettes
 * 		 various sizes
 * @author mvogt
 *
 */
public class Image extends Generator {

	private static final String PREFIX = "pics/";
	private static Logger log = Logger.getLogger(Image.class.getName());
	
	private PImage pimage;
	
	/**
	 * 
	 * @param filename
	 */
	public Image(String filename) {
		super(GeneratorName.IMAGE);
/*		PApplet parent = Collector.getInstance().getPapplet();
		pimage = parent.loadImage(filename);
		log.log(Level.INFO, "resize to img "+filename+" "+internalBufferXSize+", "+internalBufferYSize);
		pimage.resize(internalBufferXSize, internalBufferYSize);*/
		this.loadFile(filename);
	}
	
	/**
	 * load a new file
	 * @param filename
	 */
	public void loadFile(String filename) {
		PApplet parent = Collector.getInstance().getPapplet();
		pimage = parent.loadImage(PREFIX+filename);
		log.log(Level.INFO, "resize to img "+filename+" "+internalBufferXSize+", "+internalBufferYSize);
		//TODO still buggy!
		pimage.resize(internalBufferXSize, internalBufferYSize);
	}

	
	@Override
	public void update() {
		pimage.loadPixels();
		System.arraycopy(pimage.pixels, 0, this.internalBuffer, 0, internalBufferXSize*internalBufferYSize);
		pimage.updatePixels();
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}
}
