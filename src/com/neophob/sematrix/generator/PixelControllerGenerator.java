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
import com.neophob.sematrix.listener.MessageProcessor.ValidCommands;
import com.neophob.sematrix.properties.PropertiesHelper;

/**
 * 
 * @author michu
 *
 */
public class PixelControllerGenerator implements PixelControllerElement {

	private static Logger log = Logger.getLogger(PixelControllerGenerator.class.getName());

	
	private List<Generator> allGenerators;
	
	private Blinkenlights blinkenlights;
	private Image image;
	private ImageZoomer imageZoomer;
	private TextureDeformation textureDeformation;	
	private Textwriter textwriter;

	public PixelControllerGenerator() {
		allGenerators = new CopyOnWriteArrayList<Generator>();					
	}
	
	/**
	 * 
	 */
	public void initAll() {
		String fileBlinken = PropertiesHelper.getInstance().getProperty("initial.blinken");

		blinkenlights = new Blinkenlights(this, fileBlinken);
		String fileImageSimple = PropertiesHelper.getInstance().getProperty("initial.image.simple");
		image = new Image(this, fileImageSimple);		
		new Plasma2(this);
		new PlasmaAdvanced(this);
		new SimpleColors(this);
		new Fire(this);
		new PassThruGen(this);
		new Metaballs(this);
		new PixelImage(this);
		String fileTextureDeformation = PropertiesHelper.getInstance().getProperty("initial.texture");
		textureDeformation = new TextureDeformation(this, fileTextureDeformation);
		String text = PropertiesHelper.getInstance().getProperty("initial.text");
		textwriter = new Textwriter(this, 
				PropertiesHelper.getInstance().getProperty("font.filename"), 
				Integer.parseInt(PropertiesHelper.getInstance().getProperty("font.size")),
				text
		);
		String fileImageZoomer = PropertiesHelper.getInstance().getProperty("initial.image.zoomer");
		imageZoomer = new ImageZoomer(this, fileImageZoomer);
		new Cell(this);
		new FFTSpectrum(this);
		new Geometrics(this);
	}
	
	/**
	 * 
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

	@Override
	public void update() {
		for (Generator m: allGenerators) {			
			m.update();				
		}
	}



	
	/*
	 * GENERATOR ======================================================
	 */

	public Generator getGenerator(GeneratorName name) {
		for (Generator gen: allGenerators) {
			if (gen.getId() == name.getId()) {
				return gen;
			}
		}
		return null;
	}

	public List<Generator> getAllGenerators() {
		return allGenerators;
	}

	public Generator getGenerator(int index) {
		for (Generator gen: allGenerators) {
			if (gen.getId() == index) {
				return gen;
			}
		}
		
		log.log(Level.INFO, "Invalid Generator index selected: {0}", index);
		return null;
	}

	public int getSize() {
		return allGenerators.size();
	}

	public void addInput(Generator input) {
		allGenerators.add(input);
	}

	
	public String getFileBlinken() {
		return blinkenlights.getFilename();
	}

	public void setFileBlinken(String fileBlinken) {
		blinkenlights.loadFile(fileBlinken);
	}
	
	public String getFileImageSimple() {
		return image.getFilename();
	}

	public void setFileImageSimple(String fileImageSimple) {
		image.loadFile(fileImageSimple);

	}	

	public String getFileImageZoomer() {
		return imageZoomer.getFilename();
	}

	public void setFileImageZoomer(String fileImageZoomer) {
		imageZoomer.loadImage(fileImageZoomer);
	}

	public String getFileTextureDeformation() {
		return textureDeformation.getFilename();
	}

	public void setFileTextureDeformation(String fileTextureDeformation) {
		textureDeformation.loadFile(fileTextureDeformation);
	}

	public int getTextureDeformationLut() {
		return textureDeformation.getLut();
	}

	public void setTextureDeformationLut(int textureDeformationLut) {
		textureDeformation.changeLUT(textureDeformationLut);
	}

	public String getText() {
		return textwriter.getText();
	}

	public void setText(String text) {
		textwriter.createTextImage(text);
	}


}
