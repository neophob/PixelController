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
package com.neophob.sematrix.output.gui;

import java.awt.Image;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;

import processing.core.PApplet;
import processing.core.PImage;

import com.neophob.sematrix.generator.Generator;
import com.neophob.sematrix.glue.Collector;
import com.neophob.sematrix.listener.WindowHandler;

/**
 * Helper class to create a new window
 *
 * @author michu
 */
public class GeneratorGuiCreator {

	/** The log. */
	private static final Logger LOG = Logger.getLogger(GeneratorGuiCreator.class.getName());

    private static final int MINIMAL_WINDOW_X_SIZE = 820;

    private PApplet gui;
    
	/**
	 * Instantiates a new internal debug window.
	 *
	 * @param displayHoriz the display horiz
	 * @param the maximal x size of the window
	 */
	public GeneratorGuiCreator(int maximalXSize, String version) {
        int nrOfScreens = Collector.getInstance().getAllVisuals().size();
        Generator g = Collector.getInstance().getPixelControllerGenerator().getGenerator(0);
        
        float aspect = (float)g.getInternalBufferXSize()/(float)g.getInternalBufferYSize();
        int singleVisualXSize,singleVisualYSize;   
        singleVisualXSize=maximalXSize/nrOfScreens;
        singleVisualYSize=(int)(maximalXSize/nrOfScreens/aspect);

        int windowXSize=singleVisualXSize*nrOfScreens;
        int windowYSize=singleVisualYSize + 350;

        //ugly boarder stuff
        windowXSize+=20;

        if (windowXSize<MINIMAL_WINDOW_X_SIZE) {
        	windowXSize = MINIMAL_WINDOW_X_SIZE;
        }
        
        //connect the new PApplet to our frame
        gui = new GeneratorGui(windowXSize, windowYSize, singleVisualXSize, singleVisualYSize);
        gui.init();        

        //create new window for child
        LOG.log(Level.INFO, "create frame with size "+windowXSize+"/"+windowYSize+", aspect: "+aspect);
        JFrame childFrame = new JFrame("PixelController Generator Window "+version);        
        childFrame.setResizable(false);
        childFrame.setIconImage(GeneratorGuiCreator.createLargeIcon());
        
        childFrame.add(gui);
        
        childFrame.setBounds(0, 0, windowXSize, windowYSize+30);
        gui.setBounds(0, 0, windowXSize, windowYSize+30);

        // important to call this whenever embedding a PApplet.
        // It ensures that the animation thread is started and
        // that other internal variables are properly set.
        childFrame.setVisible(true);
        
        //childFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        childFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);                
        childFrame.addWindowListener( new WindowHandler() );
	}	

	/**
	 * helper function to load the large pixelinvaders logo
	 * @return
	 */
	public static Image createLargeIcon() {
	    PImage img = Collector.getInstance().getPapplet().loadImage("pics/logoBig.jpg");
	    if (img!=null) {
	        return img.getImage();	        
	    }
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
