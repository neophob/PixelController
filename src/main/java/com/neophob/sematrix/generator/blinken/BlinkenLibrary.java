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
/**
 * blinkenlights processing lib.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General
 * Public License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA  02111-1307  USA
 * 
 * @author		Michael Vogt
 * @modified	16.12.2010
 * @version		v0.5
 */

package com.neophob.sematrix.generator.blinken;


import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import processing.core.PApplet;
import processing.core.PImage;

import com.neophob.sematrix.generator.blinken.jaxb.Blm;
import com.neophob.sematrix.generator.blinken.jaxb.Header;

/**
 * Blinkenlight processing library
 * 
 * by michu / neophob.com 2010 
 *
 */
public class BlinkenLibrary {

	private static Logger log = Logger.getLogger(BlinkenLibrary.class.getName());

	// the marshalled .blm file
	private Blm blm;	

	private PApplet parent;
	
	private PImage[] frames;
	
	private JAXBContext context;
	private Unmarshaller unmarshaller;

	public final static String NAME = "blinkenlights-mini";
	public final static String VERSION = "v0.1";

	/**
	 * 
	 * @param parent
	 */
	public BlinkenLibrary(PApplet parent) {
		this.parent = parent;
		try {
			context = JAXBContext.newInstance("com.neophob.sematrix.generator.blinken.jaxb");
			unmarshaller = context.createUnmarshaller();			
		} catch (JAXBException e) {
			log.log(Level.SEVERE, "Failed to initialize Blinkenlights lib, Error: {1}" , new Object[] { e });
		}

	}

	/**
	 * load a new bml file
	 * @param filename
	 * @param maximalSize maximal height or width of an image
	 */
	public void loadFile(String filename) {
		long start = System.currentTimeMillis();			
		InputStream input = null;

		try {
			//make sure input file exist
			input = this.parent.createInput(filename);
			if (input == null) {
				//we failed to find file
				log.log(Level.WARNING, "Failed to load {0}, File not found", new Object[] { filename });
				return;
			}
			blm = (Blm) unmarshaller.unmarshal(input);
			this.frames = extractFrames(255);			
	
			long timeNeeded = System.currentTimeMillis()-start;
			log.log(Level.INFO, "Loaded file {0} / {1} frames in {2}ms", new Object[] { filename, frames.length,timeNeeded });

		} catch (Exception e) {
			log.log(Level.WARNING, "Failed to load {0}, Error: {1}", new Object[] { filename, e });
		} finally {
			try {
				if (input!=null) {
					input.close();
				}
			} catch (Exception e) {
				log.log(Level.WARNING, "Failed to close file {0}, Error: {1}" , new Object[] { filename, e });
			}
		}
	}


	/**
	 * creates a PImage-array of gif frames in a GifDecoder object 
	 * @return 
	 */
	public PImage[] extractFrames(int color) {
		int n = blm.getFrame().size();
		PImage[] framesTmp = new PImage[n];

		for (int i = 0; i < n; i++) {
			framesTmp[i] = BlinkenHelper.grabFrame(i, blm, color);
		}						
		return framesTmp;
	}


	/**
	 * total frame numbers of current movie
	 * @return how many frames this movie contains
	 */
	public int getNrOfFrames() {
		return blm.getFrame().size();
	}

	/**
	 * get meta information (title, duration...) about the loaded file
	 * @return the header object
	 */
	public Header getHeader() {
		return blm.getHeader();
	}

	/**
	 * get the marshalled object
	 * @return the marshalled blinkenlights file
	 */
	public Blm getRawObject() {
		return blm;
	}


	public PImage[] getFrames() {
		return frames;
	}
	
	public int getFrameCount() {
		return frames.length;
	}
	
	public PImage getFrame(int nr) {
		if (frames==null) {
			return null;
		}
		return frames[nr%frames.length];
	}

	/**
	 * return the version of the library.
	 * @return String
	 */
	public static String version() {
		return VERSION;
	}

}

