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
package com.neophob.sematrix.generator;


import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;

import processing.core.PImage;

import com.neophob.sematrix.glue.Collector;
import com.neophob.sematrix.glue.FileUtils;
import com.neophob.sematrix.glue.MatrixData;
import com.neophob.sematrix.glue.ShufflerOffset;
import com.neophob.sematrix.resize.PixelControllerResize;
import com.neophob.sematrix.resize.Resize.ResizeName;

/**
 * display an image.
 *
 * @author mvogt
 */
public class Image extends Generator {

    /** The Constant INITIAL_IMAGE. */
    public static final String INITIAL_IMAGE = "initial.image.simple";
    
	/** The Constant PREFIX. */
    public static final String PREFIX = "pics/";
	
	//list to store movie files used by shuffler
    private List<String> imageFiles;
    
	/** The Constant RESIZE_TYP. */
	private static final ResizeName RESIZE_TYP = ResizeName.PIXEL_RESIZE;	
	
	/** The Constant LOG. */
	private static final Logger LOG = Logger.getLogger(Image.class.getName());
	
	/** The currently loaded file */
	private String filename;
	
	private FileUtils fileUtils;
		
	/**
	 * Instantiates a new image.
	 *
	 * @param controller the controller
	 * @param filename the filename
	 */
	public Image(MatrixData matrix, String filename, FileUtils fu) {
		super(matrix, GeneratorName.IMAGE, RESIZE_TYP);
		this.fileUtils = fu;
		this.loadFile(filename);
		
	    //find image files      
		imageFiles = new ArrayList<String>();
		
		try {
	        for (String s: fu.findImagesFiles()) {
	            imageFiles.add(s);
	        }		    
		} catch (NullPointerException e) {
		    LOG.log(Level.SEVERE, "Failed to search image files, make sure directory 'data/pics' exist!");
		    throw new IllegalArgumentException("Failed to search image files, make sure directory 'data/pics' exist!");
		}
		
        LOG.log(Level.INFO, "Image, found "+imageFiles.size()+" image files");
        
	}
	
	/**
	 * load a new file.
	 *
	 * @param filename the filename
	 */
	public synchronized void loadFile(String filename) {
		if (StringUtils.isBlank(filename)) {
			LOG.log(Level.INFO, "Empty filename provided, call ignored!");
			return;
		}

		//only load if needed
		if (StringUtils.equals(filename, this.filename)) {
			LOG.log(Level.INFO, "new filename does not differ from old: "+Image.PREFIX+filename);
			return;
		}
						
		try {
			String fileToLoad = fileUtils.getRootDirectory()+File.separator+"data"+File.separator+PREFIX+filename;

			LOG.log(Level.INFO, "load image "+fileToLoad);
			//use the ancient MediaTracker to load the image. ImageIO.read(new File(fileToLoad))
			//would me easier, however additional work has to be done (convert to RGB image)
			java.awt.Image awtImage = Toolkit.getDefaultToolkit().createImage(fileToLoad);
			PImage img = loadImageMT(awtImage);								
			if (img==null || img.height<2) {
				LOG.log(Level.WARNING, "Invalid image, image height is < 2!");
				return;
			}
			this.filename = filename;
			
	        LOG.log(Level.INFO, "resize to img "+filename+" "+internalBufferXSize+", "+internalBufferYSize);
	        PixelControllerResize res = Collector.getInstance().getPixelControllerResize();
	        img.loadPixels();
	        this.internalBuffer = res.resizeImage(RESIZE_TYP, img.pixels, 
	                img.width, img.height, internalBufferXSize, internalBufferYSize);
	        img.updatePixels();	        
	       
	        short r,g,b;
	        int rgbColor;

	        //greyscale it
	        for (int i=0; i<internalBuffer.length; i++){
	            rgbColor = internalBuffer[i];
	            r = (short) ((rgbColor>>16) & 255);
	            g = (short) ((rgbColor>>8)  & 255);
	            b = (short) ( rgbColor      & 255);
	            int val = (int)(r*0.3f+g*0.59f+b*0.11f);
	            internalBuffer[i]=val;
	        }
	        
		} catch (Exception e) {			
			LOG.log(Level.WARNING, "Failed to load image "+Image.PREFIX+filename, e);
		}
	}
	
	
	 /**
	  * Ripped from Processing
	  * Load an AWT image synchronously by setting up a MediaTracker for
	  * a single image, and blocking until it has loaded.
	  */
	private PImage loadImageMT(java.awt.Image awtImage) {
		//TODO remove dependency to Collector/PApplet
		MediaTracker tracker = new MediaTracker(Collector.getInstance().getPapplet());
		tracker.addImage(awtImage, 0);
		try {
			tracker.waitForAll();
		} catch (Exception e) {
			//e.printStackTrace();  // non-fatal, right?
		}

		PImage image = new PImage(awtImage);
	    //image.parent = this;
	    return image;
	}


	
	/* (non-Javadoc)
	 * @see com.neophob.sematrix.generator.Generator#update()
	 */
	@Override
	public void update() {

	}

	/* (non-Javadoc)
	 * @see com.neophob.sematrix.generator.Generator#shuffle()
	 */
	@Override
	public void shuffle() {
		if (Collector.getInstance().getShufflerSelect(ShufflerOffset.IMAGE)) {
			int nr = new Random().nextInt(imageFiles.size());
			loadFile(imageFiles.get(nr));		
		}
	}
	
	/**
	 * Gets the filename.
	 *
	 * @return the filename
	 */
	public String getFilename() {
		return filename;
	}

}
