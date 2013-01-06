/**
 * Copyright (C) 2011 Michael Vogt <michu@neophob.com>
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

import java.io.File;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import processing.core.PApplet;

import com.neophob.sematrix.color.ColorSet;
import com.neophob.sematrix.glue.Collector;
import com.neophob.sematrix.glue.Shuffler;
import com.neophob.sematrix.jmx.TimeMeasureItemGlobal;
import com.neophob.sematrix.listener.KeyboardHandler;
import com.neophob.sematrix.output.ArduinoOutput;
import com.neophob.sematrix.output.Output;
import com.neophob.sematrix.output.gui.GeneratorGuiCreator;
import com.neophob.sematrix.output.gui.OutputGui;
import com.neophob.sematrix.properties.ApplicationConfigurationHelper;
import com.neophob.sematrix.properties.ConfigConstant;
import com.neophob.sematrix.setup.InitApplication;

/**
 * The Class PixelController.
 *
 * @author michu
 */
public class PixelController extends PApplet {  

	/** The log. */
	private static final Logger LOG = Logger.getLogger(PixelController.class.getName());

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -1336765543826338205L;
	
	/** The Constant FPS. */
	public static final int FPS = 25;

	/** The Constant FPS. */
	public static final String VERSION = "v1.3-SNAPSHOT";

	/** setup gui constants */
    private static final int TEXT_Y_OFFSET = 140;
    private static final int TEXT_Y_HEIGHT = 15;
    
    private static final int SETUP_FONT_BIG = 20;
    private static final int SETUP_FONT_SMALL = 12;
    
	private static final int SETUP_WINDOW_WIDTH = 600;
	private static final int SETUP_WINDOW_HEIGHT = 500;

    private Collector collector;

    /** The output. */
    private Output output;
    
    private OutputGui matrixEmulator;

    /** more setup stuff */
	private boolean initialized = false;
	private boolean initializationFailed = false;
		
	private int setupStep=0;
	private float steps = 1f/8f;
    private ApplicationConfigurationHelper applicationConfig;


	
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

	    fill(200);
	    rect(10, 40, 580*val, 50);                
	}

	/**
	 * 
	 */
	public void setup() {
        LOG.log(Level.INFO, "\n\nPixelController "+VERSION+" - http://www.pixelinvaders.ch\n\n");
        LOG.log(Level.INFO, "");

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
	    text("PixelController "+VERSION, 10, 29);
	    
	    text("Loading...", 10, 120);
	    drawProgressBar(0.0f);
	    drawSetupText("Load Configuration", TEXT_Y_OFFSET+TEXT_Y_HEIGHT*setupStep);
	}
	
	    
    /**
     * asnychronous initialize PixelController and display progress in gui
     */
    public void asyncInitApplication() {
        try {
            if (setupStep==0) {            
                applicationConfig = InitApplication.loadConfiguration(this);
                setupStep++;
                drawProgressBar(steps);
                drawSetupText("Create Collector", TEXT_Y_OFFSET+TEXT_Y_HEIGHT*setupStep);
                return;
            }
            
            if (setupStep==1) {            
                this.collector = Collector.getInstance();
                setupStep++;
                drawProgressBar(steps*setupStep);
                drawSetupText("Load Palettes", TEXT_Y_OFFSET+TEXT_Y_HEIGHT*setupStep);
                return;
            }

            if (setupStep==2) {            
                List<ColorSet> colorSets = InitApplication.getColorPalettes(this);
                this.collector.setColorSets(colorSets);
                setupStep++;
                drawProgressBar(steps*setupStep);
                drawSetupText("Initialize System", TEXT_Y_OFFSET+TEXT_Y_HEIGHT*setupStep);
                return;
            }

            if (setupStep==3) {            
                this.collector.init(this, applicationConfig);     
                frameRate(applicationConfig.parseFps());
                noSmooth();
                setupStep++;
                drawProgressBar(steps*setupStep);
                drawSetupText("Initialize TCP/OSC Server", TEXT_Y_OFFSET+TEXT_Y_HEIGHT*setupStep);
                return;
            }

            if (setupStep==4) {            
                this.collector.initDaemons(applicationConfig);     
                setupStep++;
                drawProgressBar(steps*setupStep);
                drawSetupText("Initialize Output device", TEXT_Y_OFFSET+TEXT_Y_HEIGHT*setupStep);
                return;
            }

            if (setupStep==5) {            
                this.output = InitApplication.getOutputDevice(this.collector, applicationConfig);
                this.collector.setOutput(output);
                setupStep++;
                drawProgressBar(steps*setupStep);
                drawSetupText("Initialize GUI", TEXT_Y_OFFSET+TEXT_Y_HEIGHT*setupStep);
                return;
            }
            
            if (setupStep==6) {            
                this.matrixEmulator = new OutputGui(applicationConfig, this.output);
                
                if (applicationConfig.getProperty(ConfigConstant.SHOW_DEBUG_WINDOW).equalsIgnoreCase("true")) {
                    //create GUI Window
                    GeneratorGuiCreator ggc = new GeneratorGuiCreator(applicationConfig.getDebugWindowMaximalXSize());
                    //register GUI Window in the Keyhandler class, needed to do some specific actions (select a visual...)
                    KeyboardHandler.setRegisterGuiClass(ggc.getGuiCallbackAction());
                }  
                setupStep++;
                drawProgressBar(steps*setupStep);
                drawSetupText("Apply Settings", TEXT_Y_OFFSET+TEXT_Y_HEIGHT*setupStep);
                return;
            }

            if (setupStep==7) {                 	
                //start in random mode?
                if (applicationConfig.startRandommode()) {
                    LOG.log(Level.INFO, "Random Mode enabled");
                    Shuffler.manualShuffleStuff();
                    this.collector.setRandomMode(true);
                }
                
                //load saves presets
                int presetNr = applicationConfig.loadPresetOnStart();
                if (presetNr != -1) {
                    LOG.log(Level.INFO,"Load preset "+presetNr);
                    List<String> present = this.collector.getPresent().get(presetNr).getPresent();
                    if (present!=null) { 
                        this.collector.setCurrentStatus(present);
                    }           
                }  
                setupStep++;
                drawProgressBar(steps*setupStep);
                return;
            }

            initialized = true;
            LOG.log(Level.INFO, "--- PixelController Setup END ---");
            LOG.log(Level.INFO, "---------------------------------");
            LOG.log(Level.INFO, "");

            background(0);
            
        } catch (Exception e) {
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


	/* (non-Javadoc)
	 * @see processing.core.PApplet#draw()
	 */
	public void draw() {
	    
	    if (initializationFailed) {
	        throw new IllegalArgumentException("PixelController failed to start...");
	    }

	    if (!initialized) {
	        asyncInitApplication();
	        return;
	    }
	    		
		if (Collector.getInstance().isInPauseMode()) {
			//no update here, we're in pause mode
			return;
		}
		
		// update all generators
		Collector.getInstance().updateSystem();
		// update matrixEmulator instance
		long startTime = System.currentTimeMillis();
		this.matrixEmulator.update();
		this.collector.getPixConStat().trackTime(TimeMeasureItemGlobal.MATRIX_EMULATOR_WINDOW, System.currentTimeMillis() - startTime);
		
		if (this.output != null && this.output.getClass().isAssignableFrom(ArduinoOutput.class)) {
			this.output.logStatistics();
		}
	}
	
	/**
	 * register single keyboard handler
	 */
    public void keyPressed() {
    	if (keyCode==ESC) {		//ignored
    		key=0;
    	} else {
            KeyboardHandler.keyboardHandler(key);    		
    	}
    }
    
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String args[]) {
		PApplet.main(new String[] { PixelController.class.getName().toString() });
	}
}
