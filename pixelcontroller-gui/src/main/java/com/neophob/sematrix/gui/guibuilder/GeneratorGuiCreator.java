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
package com.neophob.sematrix.gui.guibuilder;

import java.awt.Image;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;

import processing.core.PApplet;
import processing.core.PImage;

import com.neophob.sematrix.core.visual.MatrixData;
import com.neophob.sematrix.gui.guibuilder.eventhandler.WindowHandler;
import com.neophob.sematrix.gui.model.WindowSizeCalculator;
import com.neophob.sematrix.gui.service.PixConServer;

/**
 * Helper class to create a new window
 *
 * @author michu
 */
public class GeneratorGuiCreator {

	/** The log. */
	private static final Logger LOG = Logger.getLogger(GeneratorGuiCreator.class.getName());

    private PApplet gui;
    
	/**
	 * Instantiates a new internal debug window.
	 *
	 * @param displayHoriz the display horiz
	 * @param the maximal x size of the window
	 */
	public GeneratorGuiCreator(PixConServer pixelController, PApplet parentPapplet, int maximalXSize, int maximalYSize) {
        int nrOfScreens = pixelController.getConfig().getNrOfScreens();
        LOG.log(Level.INFO, "create GUI, nr of screens: "+nrOfScreens);
                           
        MatrixData md = pixelController.getMatrixData();
		WindowSizeCalculator wsc = new WindowSizeCalculator(md.getBufferXSize(), 
				md.getBufferYSize(), maximalXSize, maximalYSize, nrOfScreens);
        
        //connect the new PApplet to our frame
        gui = new GeneratorGui(pixelController, wsc);
        gui.init();
         
        //create new window for child
        LOG.log(Level.INFO, "Create new window: "+wsc);
        JFrame childFrame = new JFrame("PixelController Generator Window "+pixelController.getVersion());        
        childFrame.setResizable(false);
        childFrame.setIconImage(createLargeIcon(gui));
        
        childFrame.add(gui);
        
        childFrame.setBounds(0, 0, wsc.getWindowWidth(), wsc.getWindowHeight()+30);
        gui.setBounds(0, 0, wsc.getWindowWidth(), wsc.getWindowHeight()+30);

        // important to call this whenever embedding a PApplet.
        // It ensures that the animation thread is started and
        // that other internal variables are properly set.
        childFrame.setVisible(true);
        
        //childFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        childFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);                
        childFrame.addWindowListener( new WindowHandler(parentPapplet) );
	}	

	/**
	 * helper function to load the large pixelinvaders logo
	 * @return
	 */
	public static Image createLargeIcon(PApplet papplet) {
	    PImage img = papplet.loadImage("pics/logoBig.jpg");
	    if (img!=null) {
	        return img.getImage();	        
	    }
	    LOG.log(Level.WARNING, "failed to load icon image!");
	    img = new PImage(400,400);
	    return img.getImage();
	}

    /**
     * @return the gui
     */
    public GuiCallbackAction getGuiCallbackAction() {
        return (GuiCallbackAction)gui;
    }
	
	

}
