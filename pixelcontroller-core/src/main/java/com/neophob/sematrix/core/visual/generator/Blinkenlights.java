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
package com.neophob.sematrix.core.visual.generator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;

import com.neophob.sematrix.core.glue.FileUtils;
import com.neophob.sematrix.core.glue.ShufflerOffset;
import com.neophob.sematrix.core.resize.IResize;
import com.neophob.sematrix.core.resize.Resize.ResizeName;
import com.neophob.sematrix.core.visual.MatrixData;
import com.neophob.sematrix.core.visual.VisualState;
import com.neophob.sematrix.core.visual.generator.blinken.BlinkenImage;
import com.neophob.sematrix.core.visual.generator.blinken.BlinkenLibrary;

/**
 * The Blinkenlights Class
 * 
 * TODO: respect frame delay
 * 
 * @author mvogt
 */
public class Blinkenlights extends Generator {

    // list to store movie files used by shuffler
    private List<String> movieFiles;

    /** The log. */
    private static final Logger LOG = Logger.getLogger(Blinkenlights.class.getName());

    /** The blinken. */
    private BlinkenLibrary blinken;

    /** The rand. */
    private Random rand = new Random();

    /** The filename. */
    private String filename;

    private int currentFrame;

    private int frameNr;

    private IResize resize;

    private BlinkenImage img;

    private FileUtils fu;

    /**
     * Instantiates a new blinkenlights.
     * 
     * @param controller
     *            the controller
     * @param filename
     *            the filename
     */
    public Blinkenlights(MatrixData matrix, FileUtils fu, IResize resize) {
        super(matrix, GeneratorName.BLINKENLIGHTS, ResizeName.QUALITY_RESIZE);
        this.filename = null;
        this.resize = resize;
        this.fu = fu;

        // find movie files
        movieFiles = new ArrayList<String>();

        try {
            for (String s : fu.findBlinkenFiles()) {
                movieFiles.add(s);
            }
        } catch (NullPointerException e) {
            LOG.log(Level.SEVERE,
                    "Failed to search blinken files, make sure directory '" + fu.getBmlDir()
                            + "' exist!");
            throw new IllegalArgumentException(
                    "Failed to search blinken files, make sure directory '" + fu.getBmlDir()
                            + "' exist!");
        }

        LOG.log(Level.INFO, "Blinkenlights, found " + movieFiles.size() + " movie files");

        blinken = new BlinkenLibrary();
        this.loadFile(movieFiles.get(0));
    }

    /**
     * load a new file.
     * 
     * @param file
     *            the file
     */
    public synchronized void loadFile(String file) {
        if (StringUtils.isBlank(file)) {
            LOG.log(Level.INFO, "Empty filename provided, call ignored!");
            return;
        }

        // only load if needed
        if (!StringUtils.equals(file, this.filename)) {

            String fileToLoad = file;

            LOG.log(Level.INFO, "Load blinkenlights file {0} (input {1}).", new String[] {
                    fileToLoad, file });
            loadBlinken(fileToLoad);
        }
    }

    private boolean loadBlinken(String filename) {
        if (blinken.loadFile(fu.getBmlDir() + filename)) {
            this.filename = filename;
            currentFrame = 0;
            return true;
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.neophob.sematrix.core.generator.Generator#update()
     */
    @Override
    public void update(int amount) {
        frameNr++;
        if (frameNr % 2 == 0) {
            return;
        }

        img = blinken.getFrame(currentFrame);
        currentFrame += amount;
        this.internalBuffer = resize.resizeImage(img.getData(), img.getWidth(), img.getHeight(),
                internalBufferXSize, internalBufferYSize);
    }

    /**
     * Gets the filename.
     * 
     * @return the filename
     */
    public String getFilename() {
        return filename;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.neophob.sematrix.core.generator.Generator#shuffle()
     */
    @Override
    public void shuffle() {
        if (VisualState.getInstance().getShufflerSelect(ShufflerOffset.BLINKEN)) {
            int nr = rand.nextInt(movieFiles.size());
            loadFile(movieFiles.get(nr));
        }
    }

}
