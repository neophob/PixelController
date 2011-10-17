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

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.neophob.sematrix.input.Sound;
import com.neophob.sematrix.resize.Resize.ResizeName;

/**
 * create some drops
 * 
 * some code is ripped from macetech.
 *
 * @author michu
 */
public class Geometrics extends Generator {

	/** The Constant LOG. */
	private static final Logger LOG = Logger.getLogger(Geometrics.class.getName());

	/** The Constant THICKNESS. */
	private static final int THICKNESS = 10;
	
	/** The drops. */
	private List<Drop> drops;
	
	/** The tmp. */
	private List<Drop> tmp;
	
	/** The drop hue. */
	private int dropHue = 0;

	/** The sound. */
	private Sound sound;
	
	/** The rnd gen. */
	private Random rndGen=new Random();

	/**
	 * Instantiates a new geometrics.
	 *
	 * @param controller the controller
	 */
	public Geometrics(PixelControllerGenerator controller) {
		super(controller, GeneratorName.GEOMETRICS, ResizeName.QUALITY_RESIZE);
		//drops = new CopyOnWriteArrayList<Drop>();
		drops = new ArrayList<Drop>();
		tmp = new ArrayList<Drop>();
		sound = Sound.getInstance();
	}

	/**
	 * Random.
	 *
	 * @param min the min
	 * @param max the max
	 * @return the int
	 */
	private int random(int min, int max) {
		int ret = rndGen.nextInt(max-min);
		return ret+min;
	}


	/* (non-Javadoc)
	 * @see com.neophob.sematrix.generator.Generator#update()
	 */
	@Override
	public void update() {

		Arrays.fill(this.internalBuffer, 0);
		//maximal 4 actice drops
		if ( (sound.isHat() || sound.isKick() || drops.size()==0) && drops.size()<5) {
			drops.add(
					new Drop(
							random(THICKNESS, internalBufferXSize), 
							random(THICKNESS, internalBufferYSize), dropHue)
					);
			dropHue += 4;
			if (dropHue > 100) {
				dropHue -= 100;
			}
		}

		tmp.clear();
		for (Drop d: drops) {
			d.update();
			if (d.done()) {
				//drops.remove(d);
				tmp.add(d);
			}
		}
		
		if (tmp.size()>0) {
			drops.removeAll(tmp);			
		}
	}



	/**
	 * Class for Raindrops effect.
	 *
	 * @author michu
	 */
	private final class Drop {

		/** The drop size. */
		int xpos, ypos, dropcolor, dropSize;
		
		/** The finished. */
		boolean finished;

		/**
		 * Instantiates a new drop.
		 *
		 * @param x the x
		 * @param y the y
		 * @param c the c
		 */
		private Drop (int x, int y, int c) {
			xpos = x;
			ypos = y;
			dropcolor = Color.HSBtoRGB(255f/(float)c, 0.98f, 0.9f);
			finished = false;
		}

		/**
		 * Update.
		 */
		private void update() {
			if (!finished) {
				drawCircle(xpos,ypos, dropSize, dropcolor);
				if (dropSize < internalBufferXSize*2) {
					dropSize ++;
				} else {
					finished = true;
				}
			}
		}

		/**
		 * Done.
		 *
		 * @return true, if successful
		 */
		private boolean done() {
			return finished;
		}

		/**
		 * Sets the pixel.
		 *
		 * @param thick the thick
		 * @param px the px
		 * @param py the py
		 * @param col the col
		 */
		private void setPixel(int thick, int px, int py, int col) {			
			int ofs=px+py*internalBufferXSize;

			for (int x=0; x<thick; x++) {
				//check boundaries
				if (x+px < internalBufferXSize && x+px>0) {

					for (int y=0; y<thick; y++) {
						
						//check boundaries
						if (y+py < internalBufferYSize && y+py>0) {
							int pos = ofs+x+y*internalBufferXSize;
							
							if (pos>0 && pos < internalBuffer.length)
							try {
								internalBuffer[pos]=col;						
							} catch (Exception e) {
								//just to be sure...
								LOG.log(Level.WARNING,
										"Very bad! {1}", new Object[] { e });
							}							
						}
					}
				}
			}

		}

		/**
		 * Bresenham Circle
		 * ripped from http://actionsnippet.com/?p=492
		 *
		 * @param xp the xp
		 * @param yp the yp
		 * @param radius the radius
		 * @param col the col
		 */
		private void drawCircle(int xp, int yp, int radius, int col) {
			int balance;
			int xoff=0;
			int yoff=radius;
			balance=- radius;

			while (xoff <= yoff) {
				setPixel(THICKNESS, xp+xoff, yp+yoff, col);
				setPixel(THICKNESS, xp-xoff, yp+yoff, col);
				setPixel(THICKNESS, xp-xoff, yp-yoff, col);
				setPixel(THICKNESS, xp+xoff, yp-yoff, col);
				setPixel(THICKNESS, xp+yoff, yp+xoff, col);
				setPixel(THICKNESS, xp-yoff, yp+xoff, col);
				setPixel(THICKNESS, xp-yoff, yp-xoff, col);
				setPixel(THICKNESS, xp+yoff, yp-xoff, col);

				balance += xoff++;
				if (balance+xoff >= 0) {
					balance-=--yoff+yoff;
				}
			}

		}
	}

}
