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
import com.neophob.sematrix.glue.ShufflerOffset;
import com.neophob.sematrix.output.gui.helper.FileUtils;
import com.neophob.sematrix.resize.PixelControllerResize;
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

    /**
     * Instantiates a new blinkenlights.
     *
     * @param controller the controller
     * @param filename the filename
     */
    public Blinkenlights(PixelControllerGenerator controller, String filename) {
        super(controller, GeneratorName.BLINKENLIGHTS, ResizeName.QUALITY_RESIZE);
        this.filename = filename;		
        this.random=false;

        //find movie files		
        movieFiles = new ArrayList<String>();

        try {
            for (String s: FileUtils.findBlinkenFiles()) {
                movieFiles.add(s);
            }
        } catch (NullPointerException e) {
            LOG.log(Level.SEVERE, "Failed to search blinken files, make sure directory 'data/blinken' exist!");
            throw new IllegalArgumentException("Failed to search blinken files, make sure directory 'data/blinken' exist!");
        }

        LOG.log(Level.INFO, "Blinkenlights, found "+movieFiles.size()+" movie files");

        blinken = new BlinkenLibrary(Collector.getInstance().getPapplet());
        blinken.loadFile(PREFIX+filename);

    }

    /**
     * load a new file.
     *
     * @param file the file
     */
    public void loadFile(String file) {
        if (StringUtils.isBlank(file)) {
            LOG.log(Level.INFO, "Empty filename provided, call ignored!");
            return;
        }

        //only load if needed
        if (!StringUtils.equals(file, this.filename)) {
            this.filename = file;
            LOG.log(Level.INFO, "Load blinkenlights file {0}.", file);
            blinken.loadFile(PREFIX+file);
            LOG.log(Level.INFO, "DONE");
            currentFrame=0;
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
            img = blinken.getFrame(currentFrame++);

            if (currentFrame>blinken.getFrameCount()) {
                currentFrame=0;
            }
        }

        PixelControllerResize res = Collector.getInstance().getPixelControllerResize();
        img.loadPixels();
        this.internalBuffer = res.resizeImage(ResizeName.PIXEL_RESIZE, img.pixels, 
                img.width, img.height, internalBufferXSize, internalBufferYSize);
        img.updatePixels();	        


        //resize image to 128x128
        /*		int ofs, dst=0, xofs, yofs=0;		
		float xSrc,ySrc=0;
		float xDiff = internalBufferXSize/(float)blinken.width;
		float yDiff = internalBufferYSize/(float)blinken.height;

		try {
			for (int y=0; y<internalBufferYSize; y++) {
				if (ySrc>yDiff) {
					if (yofs<blinken.height) {
						yofs++;				
					}
					ySrc-=yDiff;
				}
				xofs=0;
				xSrc=0;
				for (int x=0; x<internalBufferXSize; x++) {
					if (xSrc>xDiff) {
						if (xofs<blinken.width) {
							xofs++;
						}
						xSrc-=xDiff;
					}				
					ofs=xofs+yofs*blinken.width;
					this.internalBuffer[dst++]=blinken.pixels[ofs]&255;
					xSrc++;
				}
				ySrc++;
			}		
		} catch (ArrayIndexOutOfBoundsException e) {
			LOG.log(Level.SEVERE, "Failed to update internal buffer", e);
		}*/
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
        /*		if (random) {
			blinken.noLoop();
			blinken.stop();
		} else {
			blinken.loop();
		}*/
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
