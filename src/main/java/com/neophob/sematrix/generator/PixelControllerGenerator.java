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
package com.neophob.sematrix.generator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.neophob.sematrix.generator.Generator.GeneratorName;
import com.neophob.sematrix.glue.Collector;
import com.neophob.sematrix.glue.PixelControllerElement;
import com.neophob.sematrix.glue.Visual;
import com.neophob.sematrix.properties.ApplicationConfigurationHelper;
import com.neophob.sematrix.properties.ValidCommands;

/**
 * The Class PixelControllerGenerator.
 *
 * @author michu
 */
public class PixelControllerGenerator implements PixelControllerElement {

    /** The log. */
    private static final Logger LOG = Logger.getLogger(PixelControllerGenerator.class.getName());

    private static final String DEFAULT_BLINKENLIGHTS = "torus.bml";
    private static final String DEFAULT_IMAGE = "logo.gif";
    private static final String DEFAULT_TEXT = "PixelInvaders!";
    private static final String DEFAULT_TTF = "04B_03__.TTF";
    private static final String DEFAULT_TTF_SIZE = "82";

    /** The all generators. */
    private List<Generator> allGenerators;

    /** The blinkenlights. */
    private Blinkenlights blinkenlights;

    /** The image. */
    private Image image;

    /** The ColorScroller. */
    private ColorScroll colorScroll;

    /** The Color fader- */
    private ColorFade colorFade;
    
    private OscListener oscListener1;
    private OscListener oscListener2;
    
    /** The textwriter. */
    private Textwriter textwriter;
    
	private float brightness = 1.0f;	
    
    private ApplicationConfigurationHelper ph;

    private boolean isCaptureGeneratorActive = false;
    
    /**
     * Instantiates a new pixel controller generator.
     */
    public PixelControllerGenerator(ApplicationConfigurationHelper ph) {
        allGenerators = new CopyOnWriteArrayList<Generator>();	
        this.ph = ph;
    }


    /**
     * initialize all generators.
     */
    public void initAll() {
    	LOG.log(Level.INFO, "Start init");
        String fileBlinken = ph.getProperty(Blinkenlights.INITIAL_FILENAME, DEFAULT_BLINKENLIGHTS);
        blinkenlights = new Blinkenlights(this, fileBlinken);

        String fileImageSimple = ph.getProperty(Image.INITIAL_IMAGE, DEFAULT_IMAGE);
        image = new Image(this, fileImageSimple);

        new Plasma2(this);
        
        new PlasmaAdvanced(this);
        new Fire(this);
        new PassThruGen(this);
        new Metaballs(this);
        new PixelImage(this);
        
        textwriter = new Textwriter(this, 
                ph.getProperty(Textwriter.FONT_FILENAME, DEFAULT_TTF), 
                Integer.parseInt(ph.getProperty(Textwriter.FONT_SIZE, DEFAULT_TTF_SIZE)),
                ph.getProperty(Textwriter.INITIAL_TEXT, DEFAULT_TEXT)
        );

        new Cell(this);
        new FFTSpectrum(this);
        new Geometrics(this);                
        
        int screenCapureXSize = ph.parseScreenCaptureWindowSizeX();
        if (screenCapureXSize>0) {
            new ScreenCapture(this, ph.parseScreenCaptureOffset(), screenCapureXSize, ph.parseScreenCaptureWindowSizeY());
            isCaptureGeneratorActive = true;
        }
        colorScroll = new ColorScroll(this);
        colorFade = new ColorFade(this);
        
        this.oscListener1 = new OscListener(this, GeneratorName.OSC_GEN1);
        this.oscListener2 = new OscListener(this, GeneratorName.OSC_GEN2);
        
        new VisualZero(this);
        
    	LOG.log(Level.INFO, "Init finished");
    }

    /* (non-Javadoc)
     * @see com.neophob.sematrix.glue.PixelControllerElement#getCurrentState()
     */
    public List<String> getCurrentState() {
        List<String> ret = new ArrayList<String>();

        ret.add(ValidCommands.BLINKEN+" "+blinkenlights.getFilename());
        ret.add(ValidCommands.IMAGE+" "+image.getFilename());
        ret.add(ValidCommands.TEXTWR+" "+textwriter.getText());
        ret.add(ValidCommands.COLOR_SCROLL_OPT+" "+colorScroll.getScrollMode().getMode());
        ret.add(ValidCommands.COLOR_SCROLL_LENGTH+" "+colorScroll.getFade());        
        ret.add(ValidCommands.COLOR_FADE_LENGTH+" "+colorFade.getColorFadeTime());
        int brightnessInt = (int)(this.brightness*100f);
        ret.add(ValidCommands.CHANGE_BRIGHTNESS+" "+brightnessInt);
        
        return ret;
    }

    /* (non-Javadoc)
     * @see com.neophob.sematrix.glue.PixelControllerElement#update()
     */
    @Override
    public void update() {        
        //get a set with active visuals
        List<Visual> allVisuals = Collector.getInstance().getAllVisuals();
        Set<Integer> activeVisuals = new HashSet<Integer>();        
        for (Visual v: allVisuals) {
            activeVisuals.add(v.getGenerator1Idx());
            activeVisuals.add(v.getGenerator2Idx());
        }
        
        //update only selected generators
        for (Generator m: allGenerators) {
            if (activeVisuals.contains(m.getId())) {
                m.update();
                m.setActive(true);
            } else {
            	m.setActive(false);
            }
        }
    }




    /*
     * GENERATOR ======================================================
     */

    /**
     * Gets the generator.
     *
     * @param name the name
     * @return the generator
     */
    public Generator getGenerator(GeneratorName name) {
        for (Generator gen: allGenerators) {
            if (gen.getId() == name.getId()) {
                return gen;
            }
        }
        
        LOG.log(Level.WARNING, "Invalid Generator name selected: {0}", name);
        return null;
    }

    /**
     * Gets the all generators.
     *
     * @return the all generators
     */
    public List<Generator> getAllGenerators() {
        return allGenerators;
    }

    /**
     * Gets the generator.
     * 
     * return null if index is out of scope
     *
     * @param index the index
     * @return the generator
     */
    public Generator getGenerator(int index) {
        for (Generator gen: allGenerators) {
            if (gen.getId() == index) {
                return gen;
            }
        }

        LOG.log(Level.WARNING, "Invalid Generator index selected: {0}", index);
        return null;
    }

    /**
     * Gets the size.
     *
     * @return the size
     */
    public int getSize() {
        return allGenerators.size();
    }

    /**
     * Adds the input.
     *
     * @param input the input
     */
    public void addInput(Generator input) {
        allGenerators.add(input);
    }


    /**
     * Gets the file blinken.
     *
     * @return the file blinken
     */
    public String getFileBlinken() {
        return blinkenlights.getFilename();
    }

    /**
     * Sets the file blinken.
     *
     * @param fileBlinken the new file blinken
     */
    public void setFileBlinken(String fileBlinken) {
        blinkenlights.loadFile(fileBlinken);
    }

    /**
     * Gets the file image simple.
     *
     * @return the file image simple
     */
    public String getFileImageSimple() {
        return image.getFilename();
    }

    /**
     * Sets the file image simple.
     *
     * @param fileImageSimple the new file image simple
     */
    public void setFileImageSimple(String fileImageSimple) {
        image.loadFile(fileImageSimple);

    }	

    /**
     * Sets the color scroll direction.
     *
     * @param colorScrollDir the newcolor scroll direction
     */
    public void setColorScrollingDirection(int colorScrollDir) {
    	colorScroll.setScrollMode(colorScrollDir);
    }
    
    /**
     * Sets the color scroll fade length.
     *
     * @param colorScrollDir the new color scroll fade length
     */
    public void setColorScrollingFadeLength(int colorScrolFadeLength) {
        colorScroll.setFadeLength(colorScrolFadeLength);
    }
    
    /**
     * Sets the color scroll fade length.
     *
     * @param colorScrollDir the new color scroll fade length
     */
    public void setColorFadeTime(int colorFadeTime) {
        colorFade.setColorFadeTime(colorFadeTime);
    }

    /**
     * Gets the text.
     *
     * @return the text
     */
    public String getText() {
        return textwriter.getText();
    }

    /**
     * Sets the text.
     *
     * @param text the new text
     */
    public void setText(String text) {
        textwriter.createTextImage(text);
    }
    
	/**
	 * @return the brightness
	 */
	public float getBrightness() {
		return brightness;
	}

	/**
	 * @param brightness the brightness to set
	 */
	public void setBrightness(float brightness) {
		if (brightness<0f || brightness>1.0f) {
			LOG.log(Level.WARNING, "Invalid brightness value: {0}", brightness);
			return;
		}
		this.brightness = brightness;
	}

	/**
	 * 
	 * @return
	 */
	public OscListener getOscListener1() {
		return oscListener1;
	}

	/**
	 * 
	 * @return
	 */
	public OscListener getOscListener2() {
		return oscListener2;
	}


    /**
     * @return the isCaptureGeneratorActive
     */
    public boolean isCaptureGeneratorActive() {
        return isCaptureGeneratorActive;
    }

	
}
