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
import com.neophob.sematrix.gui.service.impl.RemoteOscServer;


/**
 * The Class PixelController.
 *
 * @author michu
 */
public class PixelControllerP5Remote extends PApplet implements CallbackMessageInterface<String> {  

	/** The log. */
	private static final Logger LOG = Logger.getLogger(PixelControllerP5Remote.class.getName());

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -1336765543826338205L;
	
	/** The Constant FPS. */
	public static final int FPS = 25;

	private PixConServer pixelController;
	private MatrixSimulatorGui matrixEmulator;
	
	/**
	 * 
	 */
	public void setup() {
		try {
			LOG.log(Level.INFO, "Initialize...");
			pixelController = new RemoteOscServer();
			pixelController.start();
			System.out.println("--------");
			LOG.log(Level.INFO, "\n\nPixelController "+pixelController.getVersion()+" - http://www.pixelinvaders.ch\n\n");                

		    frameRate(FPS);

			this.matrixEmulator = new MatrixSimulatorGui(pixelController, this);
			background(0);
			
			int maxWidth = pixelController.getConfig().getDebugWindowMaximalXSize();
			int maxHeight = pixelController.getConfig().getDebugWindowMaximalYSize();
			GeneratorGuiCreator ggc = new GeneratorGuiCreator(pixelController, this, maxWidth, maxHeight);
			//register GUI Window in the Keyhandler class, needed to do some specific actions (select a visual...)
			KeyboardHandler.init(ggc.getGuiCallbackAction(), pixelController);
		    
			try {
	    		//now start a little hack, remove all window listeners, so we can control
				//the closing behavior ourselves.
	    		for (WindowListener wl: frame.getWindowListeners()) {            			
	    			frame.removeWindowListener(wl);
	    		}
	    		
	    	    //add our own window listener
	    	    frame.addWindowListener( new WindowHandler(this) );        			
			} catch (Exception e) {
				LOG.log(Level.INFO, "failed to remove/add window listeners", e);
			}

		} catch (Exception e) {
			LOG.log(Level.SEVERE, "Setup() call failed!", e);
		}
	}
	    

	/* (non-Javadoc)
	 * @see processing.core.PApplet#draw()
	 */
	public void draw() {
	    if (!pixelController.isInitialized()) {	    	
	        return;
	    } 
	    		
		// update matrixEmulator instance
		long startTime = System.currentTimeMillis();

		this.matrixEmulator.update();
		pixelController.updateNeededTimeForMatrixEmulator(System.currentTimeMillis() - startTime);
	}
	
	/**
	 * register single keyboard handler
	 */
    public void keyPressed() {
    	if (keyCode==ESC) {		//ignored
    		key=0;
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
        	LOG.log(Level.WARNING, "Ignored notification of unknown type: "+arg);
        }
	}



	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String args[]) {
		PApplet.main(new String[] { PixelControllerP5Remote.class.getName().toString() });
	}


	@Override
	public void handleMessage(String msg) {
		System.out.println("got msg: "+msg);		
	}


}
