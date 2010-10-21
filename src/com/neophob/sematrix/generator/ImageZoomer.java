package com.neophob.sematrix.generator;

import java.security.InvalidParameterException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import processing.core.PApplet;
import processing.core.PImage;

import com.neophob.sematrix.glue.Collector;


public class ImageZoomer extends Generator {

	private static Logger log = Logger.getLogger(ImageZoomer.class.getName());

	public static final String PREFIX = "pics/";

	private PImage a,p;  // Declare variable "a" of type PImage 
	private PApplet parent;
	
	private int updown = 0;
	private int leftright = 0;
	private float zoom = 1;
	private float endZoom = 1;
	private float distZoom, beginZoom;

	private float beginX = 0.0f;  // Initial x-coordinate
	private float beginY = 0.0f;  // Initial y-coordinate
	private float endX = 0.0f;   // Final x-coordinate
	private float endY = 0.0f;   // Final y-coordinate
	private float distX;          // X-axis distance to move
	private float distY;          // Y-axis distance to move
	private float exponent = 22;   // Determines the curve
	private float x = 0.0f;        // Current x-coordinate
	private float y = 0.0f;        // Current y-coordinate
	private float step = 0.005f;    // Size of each step along the path
	private float pct = 1.0f;      // Percentage traveled (0.0 to 1.0)

	public ImageZoomer(String filename) {
		super(GeneratorName.IMAGE_ZOOMER);
		this.loadImage(filename); 
		parent = Collector.getInstance().getPapplet();
		p = parent.createImage(internalBufferXSize, internalBufferYSize, PApplet.RGB); 
		log.log(Level.INFO, "IMAGE SIZE: "+a.width+" "+internalBufferXSize+", "+internalBufferYSize);
	}

	/**
	 * load a new file
	 * @param filename
	 */
	public void loadImage(String filename) {
		try {
			a = Collector.getInstance().getPapplet().loadImage(PREFIX+filename);
			if (a==null || a.height<2) {
				throw new InvalidParameterException("invalid data");
			}
			log.log(Level.INFO, "resize to img "+filename+" "+internalBufferXSize+", "+internalBufferYSize);
			
		} catch (Exception e) {
			log.log(Level.WARNING,
					"Failed to load image {0}!", new Object[] { filename });
		}	
	}

	@Override
	public void update() {
		if (a==null) {
			log.log(Level.WARNING, "image is null!");
			return;
		}
		
		doTheMove();
		
		parent.pushMatrix();
		parent.scale(zoom);
		parent.popMatrix();
		
		a.loadPixels();
		p.copy(a, (int)x, (int)y, internalBufferXSize, internalBufferYSize, 0, 0, internalBufferXSize, internalBufferYSize);
		a.updatePixels();
		
		p.loadPixels();
		System.arraycopy(p.pixels, 0, this.internalBuffer, 0, internalBufferXSize*internalBufferYSize);		
		p.updatePixels();
		
/*		parent.pushMatrix();
		parent.scale(zoom);
		parent.translate (leftright, updown);
*/
	}

	/**
	 * 
	 */
	private void updateTarget() {
		pct = 0.0f;
		beginX = x;
		beginY = y;

		endX = (float)(Math.random())*(a.width-internalBufferXSize);
		endY = (float)Math.random()*(a.height-internalBufferYSize);
		distX = endX - beginX;
		distY = endY - beginY;

		beginZoom = zoom;
		endZoom = 1.0f+(float)Math.random()*1.5f;
		distZoom = endZoom-beginZoom;
	}

	/**
	 * 
	 */
	private void doTheMove() {
		pct += step;
		if (pct > 1.0) {
			updateTarget();
		}

		x = beginX + (pct * distX);
		y = beginY + ((float)Math.pow(pct, exponent) * distY);
		zoom = beginZoom + (pct * distZoom);
		leftright = -(int)x;
		updown = -(int)y;
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
	}



}
