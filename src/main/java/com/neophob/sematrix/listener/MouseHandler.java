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
package com.neophob.sematrix.listener;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.neophob.sematrix.glue.Collector;

/**
 * @author mvogt
 *
 */
public class MouseHandler extends WindowAdapter {

    /**
     * 
     * @return
     */
    public static boolean quitApplicationYesOrNo() {
    	//bring the papplet to front, the dialog will be displayed on top of this window
    	Collector.getInstance().getPapplet().frame.toFront();
    	
    	//display dialog
        int result = JOptionPane.showConfirmDialog(
            Collector.getInstance().getPapplet(),
            "Are you sure you want to exit the application?",
            "Exit Application",
            JOptionPane.YES_NO_OPTION);
        
        if (result == JOptionPane.YES_OPTION) {
            return true;
        }
        
        return false;
    }
    
/*    public static void exit() {
        Collector.getInstance().getPapplet().stop();
        Collector.getInstance().getPapplet().dispose();
        Collector.getInstance().getPapplet().exit();
    }*/
    
    /**
     * 
     */
    public void windowClosing(WindowEvent e) {
        if (MouseHandler.quitApplicationYesOrNo()) {
            JFrame frame = (JFrame)e.getSource();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        }
    }
}
