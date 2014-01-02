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
package com.neophob;

import java.awt.event.WindowListener;
import java.io.File;
import java.util.Observable;
import java.util.logging.Level;
import java.util.logging.Logger;

import processing.core.PApplet;

import com.neophob.sematrix.core.api.CallbackMessageInterface;
import com.neophob.sematrix.gui.guibuilder.GeneratorGuiCreator;
import com.neophob.sematrix.gui.guibuilder.MatrixSimulatorGui;
import com.neophob.sematrix.gui.guibuilder.eventhandler.KeyboardHandler;
import com.neophob.sematrix.gui.guibuilder.eventhandler.WindowHandler;
import com.neophob.sematrix.gui.service.PixConServer;

/**
 * The Class PixelController.
 * 
 * @author michu
 */
abstract class AbstractPixelControllerP5 extends PApplet implements
        CallbackMessageInterface<String> {

    /** The log. */
    protected static final Logger LOG = Logger.getLogger(AbstractPixelControllerP5.class.getName());

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -1336765543826338205L;

    /** The Constant FPS. */
    public static final int FPS = 25;

    protected PixConServer pixelController;
    protected MatrixSimulatorGui matrixEmulator;

    /** setup gui constants */
    private static final int TEXT_Y_OFFSET = 140;
    private static final int TEXT_Y_HEIGHT = 15;

    private static final int SETUP_FONT_BIG = 20;
    private static final int SETUP_FONT_SMALL = 12;

    private static final int SETUP_WINDOW_WIDTH = 600;
    private static final int SETUP_WINDOW_HEIGHT = 500;

    protected int setupStep = 0;
    //

    protected boolean postInitDone = false;

    /**
	 * 
	 */
    public void setup() {
        try {
            LOG.log(Level.INFO, "Initialize...");
            LOG.log(Level.INFO, "\n\nPixelController Setup - http://www.pixelinvaders.ch\n\n");

            size(SETUP_WINDOW_WIDTH, SETUP_WINDOW_HEIGHT);
            background(0);
            noStroke();

            // try to display the pixelcontroller logo
            String splashimg = "setup" + File.separatorChar + "splash.jpg";
            try {
                if (new File(splashimg).canRead()) {
                    image(loadImage(splashimg), 0, 111);
                }
            } catch (Exception e) {
                LOG.log(Level.INFO, "Failed to load splash logo (" + splashimg + ")", e);
            }

            // write pixelcontroller text
            textSize(SETUP_FONT_BIG);
            fill(227, 122, 182);
            text("PixelController Setup", 10, 29);

            text("Loading...", 10, 120);
            drawProgressBar(0.0f);
            frameRate(FPS);

            this.initPixelController();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Setup() call failed!", e);
        }
    }

    /**
     * initialize gui after the core has been initialized
     */
    protected void postSetupInitialisation() {
        this.matrixEmulator = new MatrixSimulatorGui(pixelController, this);
        background(0);

        int maxWidth = pixelController.getConfig().getDebugWindowMaximalXSize();
        int maxHeight = pixelController.getConfig().getDebugWindowMaximalYSize();
        GeneratorGuiCreator ggc = new GeneratorGuiCreator(pixelController, this, maxWidth,
                maxHeight);
        // register GUI Window in the Keyhandler class, needed to do some
        // specific actions (select a visual...)
        KeyboardHandler.init(ggc.getGuiCallbackAction(), pixelController);

        try {
            // now start a little hack, remove all window listeners, so we can
            // control
            // the closing behavior ourselves.
            for (WindowListener wl : frame.getWindowListeners()) {
                frame.removeWindowListener(wl);
            }

            // add our own window listener
            frame.addWindowListener(new WindowHandler(this));
        } catch (Exception e) {
            LOG.log(Level.INFO, "failed to remove/add window listeners", e);
        }
        postInitDone = true;
    }

    public void draw() {
        if (!pixelController.isInitialized()) {
            return;
        } else if (!postInitDone) {
            postSetupInitialisation();
            return;
        }

        // update matrixEmulator instance
        long startTime = System.currentTimeMillis();

        this.matrixEmulator.update();
        pixelController.updateNeededTimeForMatrixEmulator(System.currentTimeMillis() - startTime);
    }

    public abstract void initPixelController();

    /**
     * register single keyboard handler
     */
    public void keyPressed() {
        if (keyCode == ESC) { // ignored
            key = 0;
        } else {
            KeyboardHandler.keyboardHandler(key, keyCode);
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        if (arg instanceof String) {
            String msg = (String) arg;
            handleMessage(msg);
        } else {
            LOG.log(Level.WARNING, "Ignored notification of unknown type: " + arg);
        }
    }

    @Override
    public void handleMessage(String msg) {
        if (!pixelController.isInitialized()) {
            setupStep++;
            drawProgressBar(pixelController.getSetupSteps() * setupStep);
            drawSetupText(msg, TEXT_Y_OFFSET + TEXT_Y_HEIGHT * setupStep);
            return;
        }
    }

    /**
     * 
     * @param text
     * @param textYOffset
     */
    protected void drawSetupText(String text, int textYOffset) {
        fill(240);
        textSize(SETUP_FONT_SMALL);
        text(text, 40, textYOffset);
    }

    /**
     * 
     * @param val
     */
    protected void drawProgressBar(float val) {
        fill(64);
        rect(10, 40, 580, 50);

        if (val > 1.0) {
            val = 1.0f;
        }

        fill(227, 122, 182);
        rect(10, 40, 580 * val, 50);
    }

}
