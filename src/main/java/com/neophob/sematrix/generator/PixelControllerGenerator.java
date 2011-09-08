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

package com.neophob.sematrix.generator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.neophob.sematrix.generator.Generator.GeneratorName;
import com.neophob.sematrix.glue.PixelControllerElement;
import com.neophob.sematrix.listener.ValidCommands;
import com.neophob.sematrix.properties.PropertiesHelper;

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
    private static final String DEFAULT_TEXTUREDEFORMATION = "logo.gif";
    private static final String DEFAULT_TEXT = "PixelInvaders!";
    private static final String DEFAULT_TTF = "04B_03__.TTF";
    private static final String DEFAULT_TTF_SIZE = "82";
    private static final String DEFAULT_IMAGE_ZOOMER = "bnz20.jpg";

    /** The all generators. */
    private List<Generator> allGenerators;

    /** The blinkenlights. */
    private Blinkenlights blinkenlights;

    /** The image. */
    private Image image;

    /** The image zoomer. */
    private ImageZoomer imageZoomer;

    /** The texture deformation. */
    private TextureDeformation textureDeformation;	

    /** The textwriter. */
    private Textwriter textwriter;

    private PropertiesHelper ph;

    /**
     * Instantiates a new pixel controller generator.
     */
    public PixelControllerGenerator(PropertiesHelper ph) {
        allGenerators = new CopyOnWriteArrayList<Generator>();	
        this.ph = ph;
    }



    /**
     * initialize all generators.
     */
    public void initAll() {	    		
        String fileBlinken = ph.getProperty(Blinkenlights.INITIAL_FILENAME, DEFAULT_BLINKENLIGHTS);
        blinkenlights = new Blinkenlights(this, fileBlinken);

        String fileImageSimple = ph.getProperty(Image.INITIAL_IMAGE, DEFAULT_IMAGE);
        image = new Image(this, fileImageSimple);

        new Plasma2(this);
        new PlasmaAdvanced(this);
        new SimpleColors(this);
        new Fire(this);
        new PassThruGen(this);
        new Metaballs(this);
        new PixelImage(this);
        String fileTextureDeformation = ph.getProperty(TextureDeformation.INITIAL_IMAGE, DEFAULT_TEXTUREDEFORMATION);
        textureDeformation = new TextureDeformation(this, fileTextureDeformation);

        textwriter = new Textwriter(this, 
                ph.getProperty(Textwriter.FONT_FILENAME, DEFAULT_TTF), 
                Integer.parseInt(ph.getProperty(Textwriter.FONT_SIZE, DEFAULT_TTF_SIZE)),
                ph.getProperty(Textwriter.INITIAL_TEXT, DEFAULT_TEXT)
        );

        String fileImageZoomer = ph.getProperty(ImageZoomer.INITIAL_IMAGE, DEFAULT_IMAGE_ZOOMER);
        imageZoomer = new ImageZoomer(this, fileImageZoomer);
        new Cell(this);
        new FFTSpectrum(this);
        new Geometrics(this);
    }

    /* (non-Javadoc)
     * @see com.neophob.sematrix.glue.PixelControllerElement#getCurrentState()
     */
    public List<String> getCurrentState() {
        List<String> ret = new ArrayList<String>();

        ret.add(ValidCommands.BLINKEN+" "+blinkenlights.getFilename());
        ret.add(ValidCommands.IMAGE+" "+image.getFilename());
        ret.add(ValidCommands.IMAGE_ZOOMER+" "+imageZoomer.getFilename());
        ret.add(ValidCommands.TEXTDEF_FILE+" "+textureDeformation.getFilename());
        ret.add(ValidCommands.TEXTDEF+" "+textureDeformation.getLut());
        ret.add(ValidCommands.TEXTWR+" "+textwriter.getText());

        return ret;
    }

    /* (non-Javadoc)
     * @see com.neophob.sematrix.glue.PixelControllerElement#update()
     */
    @Override
    public void update() {
        for (Generator m: allGenerators) {			
            m.update();				
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
     * @param index the index
     * @return the generator
     */
    public Generator getGenerator(int index) {
        for (Generator gen: allGenerators) {
            if (gen.getId() == index) {
                return gen;
            }
        }

        LOG.log(Level.INFO, "Invalid Generator index selected: {0}", index);
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
     * Gets the file image zoomer.
     *
     * @return the file image zoomer
     */
    public String getFileImageZoomer() {
        return imageZoomer.getFilename();
    }

    /**
     * Sets the file image zoomer.
     *
     * @param fileImageZoomer the new file image zoomer
     */
    public void setFileImageZoomer(String fileImageZoomer) {
        imageZoomer.loadImage(fileImageZoomer);
    }

    /**
     * Gets the file texture deformation.
     *
     * @return the file texture deformation
     */
    public String getFileTextureDeformation() {
        return textureDeformation.getFilename();
    }

    /**
     * Sets the file texture deformation.
     *
     * @param fileTextureDeformation the new file texture deformation
     */
    public void setFileTextureDeformation(String fileTextureDeformation) {
        textureDeformation.loadFile(fileTextureDeformation);
    }

    /**
     * Gets the texture deformation lut.
     *
     * @return the texture deformation lut
     */
    public int getTextureDeformationLut() {
        return textureDeformation.getLut();
    }

    /**
     * Sets the texture deformation lut.
     *
     * @param textureDeformationLut the new texture deformation lut
     */
    public void setTextureDeformationLut(int textureDeformationLut) {
        textureDeformation.changeLUT(textureDeformationLut);
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


}
