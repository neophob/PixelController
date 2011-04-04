package com.neophob.sematrix.generator;

import java.security.InvalidParameterException;
import java.util.logging.Level;
import java.util.logging.Logger;

import processing.core.PImage;

import com.neophob.sematrix.glue.Collector;
import com.neophob.sematrix.resize.Resize.ResizeName;

/**
 * @author mvogt
 *
 */
public class Image extends Generator {

	public static final String PREFIX = "pics/";
	private static Logger log = Logger.getLogger(Image.class.getName());
	
	private PImage pimage;
	
	/**
	 * 
	 * @param filename
	 */
	public Image(String filename) {
		super(GeneratorName.IMAGE, ResizeName.PIXEL_RESIZE);
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
		try {
			PImage tmp = Collector.getInstance().getPapplet().loadImage(Image.PREFIX+filename);
			if (tmp==null || tmp.height<2) {
				throw new InvalidParameterException("invalid data");
			}
			pimage = tmp;
			log.log(Level.INFO, "resize to img "+filename+" "+internalBufferXSize+", "+internalBufferYSize);
			//TODO still buggy!
			pimage.resize(internalBufferXSize, internalBufferYSize);
		} catch (Exception e) {
			log.log(Level.WARNING,
					"Failed to load image {0}!", new Object[] { filename });
		}
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
