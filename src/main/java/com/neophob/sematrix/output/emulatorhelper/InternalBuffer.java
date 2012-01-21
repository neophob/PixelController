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

import controlP5.ControlP5;
import controlP5.DropdownList;


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

		//Generator Test
		generatorListOne = cp5.addDropdownList(GuiElemts.GENERATOR_ONE_DROPDOWN.toString(), 20, p5GuiYOffset, 100, 120);
		themeDropdownList(generatorListOne);
		int i=0;
		for (GeneratorName gn: GeneratorName.values()) {
			generatorListOne.addItem(gn.name(), i++);
		}

		//Effect Test
		effectListOne = cp5.addDropdownList(GuiElemts.EFFECT_ONE_DROPDOWN.toString(), 130, p5GuiYOffset, 100, 120);
		themeDropdownList(effectListOne);
		i=0;
		for (EffectName gn: EffectName.values()) {
			effectListOne.addItem(gn.name(), i++);
		}
	
	}

	/**
	 * draw the whole internal buffer on screen.
	 */
	public void draw() {
		long l = System.currentTimeMillis();

		//draw gradient background
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
		ofs=0;
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

		//show mode
		displayCurrentMode(localY, col.isRandomMode());

		cp5.draw();		
		col.getPixConStat().trackTime(TimeMeasureItemGlobal.DEBUG_WINDOW, System.currentTimeMillis()-l);
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
	 */
	private void displayCurrentMode(int localY, boolean randomModeEnabled) {
		//show if random mode is enabled
		colorSelect(randomModeEnabled);
		rect(0, localY+targetYSize+SELECTED_MARKER*3, targetXSize, 10);
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
