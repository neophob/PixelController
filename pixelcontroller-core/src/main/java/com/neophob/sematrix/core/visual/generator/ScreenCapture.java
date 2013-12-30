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

import java.awt.Color;
import java.awt.Container;
import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Window;
import java.awt.image.BufferedImage;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;

import com.neophob.sematrix.core.resize.IResize;
import com.neophob.sematrix.core.resize.Resize.ResizeName;
import com.neophob.sematrix.core.visual.MatrixData;

/**
 * this simple class capture the screen
 * 
 * @author mvogt
 */
public class ScreenCapture extends Generator {

    /** The log. */
    private static final Logger LOG = Logger.getLogger(ScreenCapture.class.getName());

    private static final int BORDER_SIZE = 20;

    private IResize resize;

    private Robot robot;
    private Rectangle rectangleCaptureArea;

    private JFrame top, bottom, left, right;

    /**
     * Instantiates a new ScreenCapture Generator.
     * 
     * @param controller
     *            the controller
     */
    public ScreenCapture(MatrixData matrix, IResize resize, final int offset, int width, int height) {
        super(matrix, GeneratorName.SCREEN_CAPTURE, ResizeName.QUALITY_RESIZE);

        this.resize = resize;

        if (width < 1) {
            width = internalBufferXSize * 2;
        }
        if (height < 1) {
            height = internalBufferYSize * 2;
        }
        rectangleCaptureArea = new Rectangle(offset, offset, width, height);

        try {
            robot = new Robot();
            LOG.log(Level.INFO, "ScreenCapture initialized, offset " + rectangleCaptureArea.x + "/"
                    + rectangleCaptureArea.y + ", size: " + rectangleCaptureArea.width + "/"
                    + rectangleCaptureArea.height);

            Frame frame = new Frame();
            frame.setName("PixelController ScreenCapture Area");

            // draw border
            left = drawRedBorder(frame, BORDER_SIZE, height, offset - BORDER_SIZE, offset);
            right = drawRedBorder(frame, BORDER_SIZE, height, offset + width, offset);
            top = drawRedBorder(frame, width + BORDER_SIZE * 2, BORDER_SIZE, offset - BORDER_SIZE,
                    offset - BORDER_SIZE);
            bottom = drawRedBorder(frame, width + BORDER_SIZE * 2, BORDER_SIZE, offset
                    - BORDER_SIZE, height + offset);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Failed to initialize ScreenCapture: {0}", e);
        }

    }

    /**
     * draw a single border
     * 
     * @param height
     * @param x
     * @param y
     */
    private static JFrame drawRedBorder(Frame frame, int width, int height, int x, int y) {
        JFrame window = new JFrame(frame.getGraphicsConfiguration());

        // Set the size and location of the window
        window.setSize(width, height);
        window.setLocation(x, y);
        window.setUndecorated(true);

        // color window
        Container container = window.getContentPane();
        container.setBackground(Color.RED);
        setOpacity(window, 0.5f);

        // update window property
        window.setAlwaysOnTop(true);
        window.setVisible(false);

        return window;
    }

    /**
     * see http://java.sun.com/developer/technicalArticles/GUI/
     * translucent_shaped_windows/
     * 
     * @param window
     * @param f
     */
    private static void setOpacity(JFrame window, float f) {
        try {
            Class<?> utils = Class.forName("com.sun.awt.AWTUtilities");
            // use reflection to check if this method is available
            Method method = utils.getMethod("setWindowOpacity", Window.class, float.class);
            method.invoke(null, window, f);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Failed to set Window Opacity: {0}", e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.neophob.sematrix.core.generator.Generator#update()
     */
    @Override
    public void update() {
        if (robot != null) {
            // get screenshot
            BufferedImage screencapture = robot.createScreenCapture(rectangleCaptureArea);

            // resize it to internalBufferSize
            int[] tmp = resize.getPixelsFromImage(screencapture, rectangleCaptureArea.width,
                    rectangleCaptureArea.height);
            this.internalBuffer = resize.resizeImage(tmp, rectangleCaptureArea.width,
                    rectangleCaptureArea.height, internalBufferXSize, internalBufferYSize);
        }
    }

    @Override
    protected void nowActive() {
        left.setVisible(true);
        right.setVisible(true);
        top.setVisible(true);
        bottom.setVisible(true);
    }

    @Override
    protected void nowInactive() {
        left.setVisible(false);
        right.setVisible(false);
        top.setVisible(false);
        bottom.setVisible(false);
    }

    @Override
    public boolean isPassThoughModeActive() {
        return true;
    }

}
