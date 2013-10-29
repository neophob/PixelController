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

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;

import processing.core.PConstants;
import processing.core.PImage;

import com.neophob.sematrix.generator.blinken.BlinkenLibrary;
import com.neophob.sematrix.glue.Collector;
import com.neophob.sematrix.glue.FileUtils;
import com.neophob.sematrix.glue.MatrixData;
import com.neophob.sematrix.glue.ShufflerOffset;
import com.neophob.sematrix.resize.Resize.ResizeName;

/**
 * The Blinkenlights Class
 * 
 * TODO: respect frame delay
 *
 * @author mvogt
 */
public class Blinkenlights extends Generator implements PConstants {

    public static final String INITIAL_FILENAME = "initial.blinken";

    /** The Constant PREFIX. */
    private static final String PREFIX = "blinken/";

    //list to store movie files used by shuffler
    private List<String> movieFiles;

    /** The log. */
    private static final Logger LOG = Logger.getLogger(Blinkenlights.class.getName());

    /** The blinken. */
    private BlinkenLibrary blinken;

    /** The random. */
    private boolean random;

    /** The rand. */
    private Random rand = new Random();

    /** The filename. */
    private String filename="";

    private PImage img;

    private int currentFrame;
    
    private int frameNr;
    
    private FileUtils fileUtils;

    /**
     * Instantiates a new blinkenlights.
     *
     * @param controller the controller
     * @param filename the filename
     */
    public Blinkenlights(MatrixData matrix, String filename, FileUtils fu) {
        super(matrix, GeneratorName.BLINKENLIGHTS, ResizeName.QUALITY_RESIZE);
        this.filename = null;
        this.fileUtils = fu;
        this.random=false;

        //find movie files		
        movieFiles = new ArrayList<String>();

        try {
            for (String s: fu.findBlinkenFiles()) {
                movieFiles.add(s);
            }
        } catch (NullPointerException e) {
            LOG.log(Level.SEVERE, "Failed to search blinken files, make sure directory '"+fu.getRootDirectory()+"data/blinken' exist!");
            throw new IllegalArgumentException("Failed to search blinken files, make sure directory '"+fu.getRootDirectory()+"data/blinken' exist!");
        }

        LOG.log(Level.INFO, "Blinkenlights, found "+movieFiles.size()+" movie files");

        blinken = new BlinkenLibrary();
        this.loadFile(filename);
    }

    /**
     * load a new file.
     *
     * @param file the file
     */
    public synchronized void loadFile(String file) {
        if (StringUtils.isBlank(file)) {
            LOG.log(Level.INFO, "Empty filename provided, call ignored!");
            return;
        }

        //only load if needed
        if (!StringUtils.equals(file, this.filename)) {
        	String fileToLoad = fileUtils.getRootDirectory()+File.separator+"data"+File.separator+PREFIX+file;
            LOG.log(Level.INFO, "Load blinkenlights file {0}.", fileToLoad);
            if (blinken.loadFile(fileToLoad)) {
                this.filename = file;
                LOG.log(Level.INFO, "DONE");
                currentFrame=0;            	
            } else {
            	LOG.log(Level.INFO, "NOT DONE");
            }
        }
    }


    /* (non-Javadoc)
     * @see com.neophob.sematrix.generator.Generator#update()
     */
    @Override
    public void update() {
        if (random) {
            img = blinken.getFrame(rand.nextInt(blinken.getFrameCount()));
        } else {
        	if (frameNr%2==0) {
        		currentFrame++;
        	}
            img = blinken.getFrame(currentFrame);

            if (currentFrame>blinken.getFrameCount()) {
                currentFrame=0;
            }
        }

        img.loadPixels();
        this.internalBuffer = Collector.getInstance().getPixelControllerResize().resizeImage(ResizeName.PIXEL_RESIZE, img.pixels, 
                img.width, img.height, internalBufferXSize, internalBufferYSize);
        img.updatePixels();	
        
        frameNr++;
    }

    /**
     * Checks if is random.
     *
     * @return true, if is random
     */
    public boolean isRandom() {
        return random;
    }

    /**
     * Sets the random.
     *
     * @param random the new random
     */
    public void setRandom(boolean random) {
        this.random = random;
    }



    /**
     * Gets the filename.
     *
     * @return the filename
     */
    public String getFilename() {
        return filename;
    }

    /* (non-Javadoc)
     * @see com.neophob.sematrix.generator.Generator#shuffle()
     */
    @Override
    public void shuffle() {
        if (Collector.getInstance().getShufflerSelect(ShufflerOffset.BLINKEN)) {
            int nr = rand.nextInt(movieFiles.size());
            loadFile(movieFiles.get(nr));
        }
    }

}
