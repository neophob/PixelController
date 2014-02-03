/**
 * Copyright (C) 2011-2014 Michael Vogt <michu@neophob.com>
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
package com.neophob.sematrix.gui.guibuilder.eventhandler;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import processing.core.PApplet;

/**
 * Close application dialog
 * 
 * @author mvogt
 *
 */
public final class WindowHandler extends WindowAdapter {

	private static final Logger LOG = Logger.getLogger(WindowHandler.class.getName());
	
	private PApplet papplet;
	
	public WindowHandler(PApplet papplet) {
		this.papplet = papplet;
	}
	
    /**
     * 
     * @return
     */
    public boolean quitApplicationYesOrNo() {
    	if (papplet.frame==null) {
    		LOG.log(Level.WARNING, "papplet.frame==null, cannot display dialog!");
    		return false;
    	}
    	
    	//bring the papplet to front, the dialog will be displayed on top of this window
    	papplet.frame.toFront();

    	//display dialog
        int result = JOptionPane.showConfirmDialog(
        	papplet,
            "Are you sure you want to exit the application?",
            "Exit Application",
            JOptionPane.YES_NO_OPTION);
                
        if (result == JOptionPane.YES_OPTION) {
            return true;
        }
        return false;
    }
    
    
    /**
     * 
     */
    @Override
    public void windowClosing(WindowEvent e) {
        if (quitApplicationYesOrNo()) {
        	try {
        		//TODO signal shutdown to core...
                JFrame frame = (JFrame)e.getSource();                
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);        		
        	} catch (Exception exception) {
        		
        		System.exit(0);
        	}
        }

    }
 
}
