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
import com.neophob.sematrix.core.api.PixelController;
import com.neophob.sematrix.core.api.impl.PixelControllerFactory;
import com.neophob.sematrix.core.glue.Collector;
import com.neophob.sematrix.gui.GeneratorGuiCreator;
import com.neophob.sematrix.gui.OutputGui;
import com.neophob.sematrix.gui.handler.KeyboardHandler;
import com.neophob.sematrix.gui.handler.WindowHandler;


/**
 * The Class PixelController.
 *
 * @author michu
 */
public class PixelControllerP5 extends PApplet implements CallbackMessageInterface<String> {  

	/** The log. */
	private static final Logger LOG = Logger.getLogger(PixelControllerP5.class.getName());

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -1336765543826338205L;
	
	/** The Constant FPS. */
	public static final int FPS = 25;

	/** setup gui constants */
    private static final int TEXT_Y_OFFSET = 140;
    private static final int TEXT_Y_HEIGHT = 15;
    
    private static final int SETUP_FONT_BIG = 20;
    private static final int SETUP_FONT_SMALL = 12;
    
	private static final int SETUP_WINDOW_WIDTH = 600;
	private static final int SETUP_WINDOW_HEIGHT = 500;

		
	private int setupStep=0;
	private float steps = 1f/7f;

	private boolean postInitDone = false;
	
	private PixelController pixelController;
	private OutputGui matrixEmulator;
	
	/**
	 * 
	 * @param text
	 * @param textYOffset
	 */
	public void drawSetupText(String text, int textYOffset) {
	    fill(240);
	    textSize(SETUP_FONT_SMALL);
	    text(text, 40, textYOffset);
	}

	/**
	 * 
	 * @param val
	 */
	public void drawProgressBar(float val) {
	    fill(64);
	    rect(10, 40, 580, 50);

	    if (val>1.0) {
	        val = 1.0f;
	    }

	    fill(227, 122, 182);
	    rect(10, 40, 580*val, 50);                
	}

	/**
	 * 
	 */
	public void setup() {
		try {
			LOG.log(Level.INFO, "Initialize...");
			pixelController = PixelControllerFactory.initialize(this);
			LOG.log(Level.INFO, "\n\nPixelController "+pixelController.getVersion()+" - http://www.pixelinvaders.ch\n\n");                
			pixelController.start();
System.out.println("aa");
		    size(SETUP_WINDOW_WIDTH, SETUP_WINDOW_HEIGHT);
		    background(0);
		    noStroke();
		    
		    //try to display the pixelcontroller logo
		    String splashimg = "setup"+File.separatorChar+"splash.jpg";
		    try {
		        image(loadImage(splashimg), 0, 111);
		    } catch (Exception e) {
		        LOG.log(Level.INFO, "Failed to load splash logo ("+splashimg+")", e);
		    }

		    //write pixelcontroller text
		    textSize(SETUP_FONT_BIG);
		    fill(227, 122, 182);
		    text("PixelController "+pixelController.getVersion(), 10, 29);
		    
		    text("Loading...", 10, 120);
		    drawProgressBar(0.0f);
//		    drawSetupText("Load Configuration", TEXT_Y_OFFSET+TEXT_Y_HEIGHT*setupStep);		    		    		   

		} catch (Exception e) {
			LOG.log(Level.SEVERE, "Setup() call failed!", e);
		}
	}
	
	
	private void postStartInitialisation() {
		this.matrixEmulator = new OutputGui(pixelController.getConfig(), pixelController.getOutput(), this);

		int maxWidth = pixelController.getConfig().getDebugWindowMaximalXSize();
		int maxHeight = pixelController.getConfig().getDebugWindowMaximalYSize();
		GeneratorGuiCreator ggc = new GeneratorGuiCreator(this, maxWidth, maxHeight, pixelController.getVersion());
		//register GUI Window in the Keyhandler class, needed to do some specific actions (select a visual...)
		KeyboardHandler.setRegisterGuiClass(ggc.getGuiCallbackAction());
	    
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
		postInitDone = true;
	}
	    
    /**
     * Asynchronous initialize PixelController and display progress in GUI
     */
/*    public void asyncInitApplication() {
        try {
        	
        	switch (setupStep) {
        	case 0:
        		fileUtils = new FileUtils();
        		applicationConfig = InitApplication.loadConfiguration(fileUtils);
        		String rootPath = applicationConfig.getResourcePath();
        		if (StringUtils.isEmpty(rootPath)) {
        			//use processing root path
        			rootPath = this.sketchPath;
        		}		
        		
        		setupStep++;
        		drawProgressBar(steps);
        		drawSetupText("Create Collector", TEXT_Y_OFFSET+TEXT_Y_HEIGHT*setupStep);
        		return;

        	case 1:
        		this.collector = Collector.getInstance();
        		setupStep++;
        		drawProgressBar(steps*setupStep);
        		drawSetupText("Initialize System", TEXT_Y_OFFSET+TEXT_Y_HEIGHT*setupStep);
        		return;

        	case 2:
        		this.collector.init(fileUtils, applicationConfig);     
        		//frameRate(applicationConfig.parseFps());
        		noSmooth();
        		setupStep++;
        		drawProgressBar(steps*setupStep);
        		drawSetupText("Initialize OSC Server", TEXT_Y_OFFSET+TEXT_Y_HEIGHT*setupStep);
        		return;

        	case 3:
        		this.collector.initDaemons(applicationConfig);     
        		setupStep++;
        		drawProgressBar(steps*setupStep);
        		drawSetupText("Initialize Output device", TEXT_Y_OFFSET+TEXT_Y_HEIGHT*setupStep);
        		return;

        	case 4:
        		this.output = InitApplication.getOutputDevice(this.collector, applicationConfig);
        		if (this.output==null) {
        			throw new IllegalArgumentException("No output device found!");
        		}
        		this.collector.setOutput(output);
        		setupStep++;
        		drawProgressBar(steps*setupStep);
        		drawSetupText("Apply Settings", TEXT_Y_OFFSET+TEXT_Y_HEIGHT*setupStep);
        		return;

        	case 5:
        		InitApplication.setupInitialConfig(collector, applicationConfig);
        		setupStep++;
        		drawProgressBar(steps*setupStep);
        		drawSetupText("Initialize GUI", TEXT_Y_OFFSET+TEXT_Y_HEIGHT*setupStep);
        		return;

        	case 6:
        		this.matrixEmulator = new OutputGui(applicationConfig, this.output, this);

        		//create gui window
        		if (applicationConfig.getProperty(ConfigConstant.SHOW_DEBUG_WINDOW, "true").equalsIgnoreCase("true")) {
        			//create GUI Window
        			GeneratorGuiCreator ggc = new GeneratorGuiCreator(this, applicationConfig.getDebugWindowMaximalXSize(), applicationConfig.getDebugWindowMaximalYSize(), getVersion());
        			//register GUI Window in the Keyhandler class, needed to do some specific actions (select a visual...)
        			KeyboardHandler.setRegisterGuiClass(ggc.getGuiCallbackAction());
        		}  
        		setupStep++;
        		drawProgressBar(steps*setupStep);
        		drawSetupText("Finished", TEXT_Y_OFFSET+TEXT_Y_HEIGHT*setupStep);
        		
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
        		return;

        	default:
        		break;
        	}
        	

            LOG.log(Level.INFO, "--- PixelController Setup END ---");
            LOG.log(Level.INFO, "---------------------------------");
            LOG.log(Level.INFO, "");

            background(0);
            initialized = true;
            
        } catch (Exception e) {
            LOG.log(Level.INFO, "Failed to init PixelController!", e);
            textSize(SETUP_FONT_BIG);
            fill(227, 122, 182);
            
            int errorYPos = 370;
            text("PixelController Error", 10, errorYPos);
            
            drawSetupText("Failed to initialize PixelController! See log/pixelcontroller.log for more detail!s", errorYPos+20);
            drawSetupText("Error message:", errorYPos+40);
            drawSetupText("     "+e.getMessage(), errorYPos+60);
            initializationFailed = true;            
        }
    }
*/

	/* (non-Javadoc)
	 * @see processing.core.PApplet#draw()
	 */
	public void draw() {
	    
//	    if (initializationFailed) {
//	        throw new IllegalArgumentException("PixelController failed to start...");
//	    }

	    if (!pixelController.isInitialized()) {
	        return;
	    } else if (!postInitDone) {
	    	postStartInitialisation();
	    	return;
	    }
	    		
		if (frameCount %25==24) {
			System.out.println(pixelController.getFps() + " --- " + frameRate);			
		}
		
		//TODO calculate fps in pixelcontroller-core
//		this.collector.getPixConStat().setCurrentFps(frameRate);
		
		// update matrixEmulator instance
		long startTime = System.currentTimeMillis();
		this.matrixEmulator.update();
//		this.collector.getPixConStat().trackTime(TimeMeasureItemGlobal.MATRIX_EMULATOR_WINDOW, System.currentTimeMillis() - startTime);		
//		if (this.output != null && this.output.getClass().isAssignableFrom(ArduinoOutput.class)) {
//			this.output.logStatistics();
//		}
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

	@Override
	public void handleMessage(String msg) {
		if (!pixelController.isInitialized()) {
			setupStep++;
			drawProgressBar(steps*setupStep);
			drawSetupText(msg, TEXT_Y_OFFSET+TEXT_Y_HEIGHT*setupStep);			
		}
	}

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String args[]) {
		PApplet.main(new String[] { PixelControllerP5.class.getName().toString() });
	}


}
