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

	static Logger log = Logger.getLogger(Image.class.getName());
	
	private PImage pimage;
	
	/**
	 * 
	 * @param filename
	 */
	public Image(String filename) {
		super(GeneratorName.IMAGE);
		PApplet parent = Collector.getInstance().getPapplet();
		pimage = parent.loadImage(filename);
		log.log(Level.INFO, "resize to img "+filename+" "+this.getInternalBufferXSize()+", "+this.getInternalBufferYSize());
		pimage.resize(this.getInternalBufferXSize(), this.getInternalBufferYSize());
	}
	
	/**
	 * load a new file
	 * @param filename
	 */
	public void loadFile(String filename) {
		PApplet parent = Collector.getInstance().getPapplet();
		
		pimage = parent.loadImage(filename);
		log.log(Level.INFO, "resize to img "+filename+" "+this.getInternalBufferXSize()+", "+this.getInternalBufferYSize());
		pimage.resize(this.getInternalBufferXSize(), this.getInternalBufferYSize());
	}

	
	@Override
	public void update() {
		System.arraycopy(pimage.pixels, 0, this.internalBuffer, 0, this.getInternalBufferXSize()*this.getInternalBufferYSize());
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}
}
