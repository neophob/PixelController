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

package com.neophob.sematrix.output.emulatorhelper;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import processing.core.PApplet;
import processing.core.PImage;

import com.neophob.sematrix.effect.Effect.EffectName;
import com.neophob.sematrix.generator.Generator.GeneratorName;
import com.neophob.sematrix.glue.Collector;
import com.neophob.sematrix.glue.OutputMapping;
import com.neophob.sematrix.glue.Visual;
import com.neophob.sematrix.input.Sound;
import com.neophob.sematrix.jmx.TimeMeasureItemGlobal;
import com.neophob.sematrix.mixer.Mixer.MixerName;

import controlP5.Button;
import controlP5.ControlP5;
import controlP5.DropdownList;
import controlP5.RadioButton;
import controlP5.Toggle;


/**
 * Display the internal Visual buffers in full resolution
 * 
 * @author michu
 */
public class InternalBuffer extends PApplet {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 2344499301021L;

	private static final int SELECTED_MARKER = 10;

	/** The log. */
	private static final Logger LOG = Logger.getLogger(InternalBuffer.class.getName());

	/** The display horiz. */
	private boolean displayHoriz;

	/** The y. */
	private int x,y;

	/** The p image. */
	private PImage pImage=null;

	private ControlP5 cp5;
	private DropdownList generatorListOne, effectListOne;
	private DropdownList generatorListTwo, effectListTwo;
	private DropdownList mixerList;
	private RadioButton selectedVisualList;
	private Button randomSelection, randomPresets;
	private Toggle toggleRandom;

	/** The target y size. */
	private int targetXSize, targetYSize;
	
	private int p5GuiYOffset;

	/**
	 * Instantiates a new internal buffer.
	 *
	 * @param displayHoriz the display horiz
	 * @param x the x
	 * @param y the y
	 * @param targetXSize the target x size
	 * @param targetYSize the target y size
	 */
	public InternalBuffer(boolean displayHoriz, int x, int y, int targetXSize, int targetYSize) {
		this.displayHoriz = displayHoriz;
		this.x = x;
		this.y = y+SELECTED_MARKER;
		this.targetXSize = targetXSize;
		this.targetYSize = targetYSize;
		this.p5GuiYOffset = targetYSize + 100;
	}

	private void themeDropdownList(DropdownList ddl) {
		// a convenience function to customize a DropdownList
		ddl.setBackgroundColor(color(190));
		ddl.setItemHeight(15);
		ddl.setBarHeight(15);

		ddl.captionLabel().set("dropdown");
		ddl.captionLabel().style().marginTop = 3;
		ddl.captionLabel().style().marginLeft = 3;
		ddl.valueLabel().style().marginTop = 3;

		ddl.scroll(0);
		ddl.setColorBackground(color(60));
		ddl.setColorActive(color(255, 128));
	}
	
	void addToRadioButton(RadioButton theRadioButton, String theName, int theValue, int w) {
		  Toggle t = theRadioButton.addItem(theName,theValue);
		  t.captionLabel().setColorBackground(color(80));
		  t.captionLabel().style().movePadding(2,0,-1,2);
		  t.captionLabel().style().moveMargin(-2,0,0,-3);
		  t.captionLabel().style().backgroundWidth = w;
		}


	/* (non-Javadoc)
	 * @see processing.core.PApplet#setup()
	 */
	public void setup() {
		LOG.log(Level.INFO, "create internal buffer with size "+x+"/"+y);
		size(x,y+100);
		noSmooth();
		frameRate(Collector.getInstance().getFps());
		background(0,0,0);

		
		cp5 = new ControlP5(this);
		cp5.setAutoDraw(false);
        P5EventListener listener = new P5EventListener();
        cp5.addListener(listener);

        int i=0;        
        
        //selected visual
        int nrOfVisuals = Collector.getInstance().getAllVisuals().size();
        selectedVisualList = cp5.addRadioButton(GuiElement.CURRENT_VISUAL.toString(), 0, p5GuiYOffset-50);
        selectedVisualList.setItemsPerRow(nrOfVisuals);
        selectedVisualList.setSpacingColumn(targetXSize);

        for (i=0; i<nrOfVisuals; i++) {
            addToRadioButton(selectedVisualList, "VISUAL "+(1+i), i, targetXSize);        	
        }

        //Generator 
		generatorListOne = cp5.addDropdownList(GuiElement.GENERATOR_ONE_DROPDOWN.toString(), 
				20, p5GuiYOffset, 100, 140);
		generatorListTwo = cp5.addDropdownList(GuiElement.GENERATOR_TWO_DROPDOWN.toString(), 
				440, p5GuiYOffset, 100, 140);
		themeDropdownList(generatorListOne);
		themeDropdownList(generatorListTwo);
		i=0;
		for (GeneratorName gn: GeneratorName.values()) {
			generatorListOne.addItem(gn.name(), i);
			generatorListTwo.addItem(gn.name(), i);
			i++;
		}
		generatorListOne.setLabel(generatorListOne.getItem(1).getName());
		generatorListTwo.setLabel(generatorListTwo.getItem(0).getName());

		//Effect 
		effectListOne = cp5.addDropdownList(GuiElement.EFFECT_ONE_DROPDOWN.toString(), 
				160, p5GuiYOffset, 100, 140);
		effectListTwo = cp5.addDropdownList(GuiElement.EFFECT_TWO_DROPDOWN.toString(), 
				580, p5GuiYOffset, 100, 140);
		themeDropdownList(effectListOne);
		themeDropdownList(effectListTwo);
		i=0;
		for (EffectName gn: EffectName.values()) {
			effectListOne.addItem(gn.name(), i);
			effectListTwo.addItem(gn.name(), i);
			i++;
		}
		effectListOne.setLabel(effectListOne.getItem(0).getName());
		effectListTwo.setLabel(effectListTwo.getItem(0).getName());

		
		//Mixer 
		mixerList = cp5.addDropdownList(GuiElement.MIXER_DROPDOWN.toString(), 
				300, p5GuiYOffset, 100, 140);
		themeDropdownList(mixerList);
		i=0;
		for (MixerName gn: MixerName.values()) {
			mixerList.addItem(gn.name(), i);
			i++;
		}
		mixerList.setLabel(mixerList.getItem(0).getName());
		
		//Button
		randomSelection = cp5.addButton(GuiElement.BUTTON_RANDOM_CONFIGURATION.toString(), 0,
				720, p5GuiYOffset-15, 100, 15);
		randomSelection.setCaptionLabel("RANDOMIZE");

		randomPresets = cp5.addButton(GuiElement.BUTTON_RANDOM_PRESENT.toString(), 0,
				720, p5GuiYOffset+15, 100, 15);
		randomPresets.setCaptionLabel("RANDOM PRESENT");
		
		toggleRandom = cp5.addToggle(GuiElement.BUTTON_TOGGLE_RANDOM_MODE.toString(), true,
				720, p5GuiYOffset+45, 100, 15);
		toggleRandom.setCaptionLabel("RANDOM MODE");
		
	}

	/**
	 * draw the whole internal buffer on screen.
	 */
	public void draw() {
		long l = System.currentTimeMillis();
		
		drawGradientBackground();

		int localX=0, localY=0;
		int[] buffer;
		Collector col = Collector.getInstance();
		int currentVisual = col.getCurrentVisual();

		//set used to find out if visual is on screen
		Set<Integer> outputId = new HashSet<Integer>();
		for (OutputMapping om: col.getAllOutputMappings()) {
			outputId.add(om.getVisualId());
		}

		//draw output buffer and marker
		int ofs=0;
		for (Visual v: col.getAllVisuals()) {
			//get image
			buffer = col.getMatrix().resizeBufferForDevice(v.getBuffer(), v.getResizeOption(), targetXSize, targetYSize);

			if (pImage==null) {
				//create an image out of the buffer
				pImage = col.getPapplet().createImage(targetXSize, targetYSize, PApplet.RGB );				
			}
			pImage.loadPixels();
			System.arraycopy(buffer, 0, pImage.pixels, 0, targetXSize*targetYSize);
			pImage.updatePixels();

			//draw current input
			if (ofs==currentVisual) {
				fill(200,66,66);
			} else {
				fill(55,55,55);
			}	
			rect(localX, localY+targetYSize, targetXSize, SELECTED_MARKER);


			//draw current output
			if (outputId.contains(ofs)) {
				fill(66,200,66);
			} else {
				fill(55,55,55);
			}	
			rect(localX, localY+targetYSize+SELECTED_MARKER, targetXSize, SELECTED_MARKER);				


			//display the image
			image(pImage, localX, localY);
			if (displayHoriz) {
				localX += pImage.width;
			} else {
				localY += pImage.height;
			}

			ofs++;
		}

		//display frame progress
		int frames = col.getFrames() % targetXSize;
		fill(200,200,200);
		rect(0, localY+targetYSize+SELECTED_MARKER*2+2, frames, 5);
		fill(55,55,55);
		rect(frames, localY+targetYSize+SELECTED_MARKER*2+2, targetXSize-frames, 5);

		//beat detection
		displaySoundStats(localY);

		cp5.draw();		
		col.getPixConStat().trackTime(TimeMeasureItemGlobal.DEBUG_WINDOW, System.currentTimeMillis()-l);
	}
	

	/**
	 * draw nice gradient at the end of the screen
	 */
	private void drawGradientBackground() {
		this.loadPixels();	
		int ofs=this.width*(this.height-255);
		
		for (int y=0; y<255; y++) {
			int pink = color(y/2, y/2, y/2);
			for (int x=0; x<this.width; x++) {
				this.pixels[ofs+x] = pink;				
			}
			ofs += this.width;
		}
		this.updatePixels();		
	}
	
	
	/**
	 * 
	 * @param localY
	 */
	private void displaySoundStats(int localY) {
		Sound snd = Sound.getInstance();

		int xofs = targetXSize;
		int xx = targetXSize/3;

		colorSelect(snd.isKick());
		rect(xofs, localY+targetYSize+SELECTED_MARKER*2+2, xx, 5);

		xofs+=xx;
		colorSelect(snd.isSnare());
		rect(xofs, localY+targetYSize+SELECTED_MARKER*2+2, xx, 5);

		xofs+=xx;
		colorSelect(snd.isHat());
		rect(xofs, localY+targetYSize+SELECTED_MARKER*2+2, xx, 5);		
	}



	/**
	 * 
	 * @param b
	 */
	private void colorSelect(boolean b) {
		if (b) {
			fill(200,200,200);	
		} else {
			fill(55,55,55);	
		}		
	}

}
